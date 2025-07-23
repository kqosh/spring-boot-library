package com.jdriven.library.service

import com.jdriven.library.access.model.AuthorEntity
import com.jdriven.library.access.model.AuthorRepository
import com.jdriven.library.access.model.BookEntity
import com.jdriven.library.access.model.BookRepository
import com.jdriven.library.service.model.BookDto
import com.jdriven.library.service.model.PaginatedResponse
import jakarta.persistence.EntityManager
import org.hibernate.search.mapper.orm.Search
import org.hibernate.search.mapper.orm.massindexing.MassIndexer
import org.hibernate.search.mapper.orm.session.SearchSession
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.atomic.AtomicBoolean

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val entityManager: EntityManager,
    @Value("\${init.index}") private val initIndex: Boolean,
    @Value("\${search.limit}") private val searchLimit: Int,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val initializeIndexDone = AtomicBoolean(false)

    fun initializeIndex() {
        logger.info("initializeIndex start")
        val searchSession: SearchSession = Search.session(entityManager)
        val indexer: MassIndexer = searchSession.massIndexer(BookEntity::class.java).threadsToLoadObjects(7);
        indexer.startAndWait();
        logger.info("initializeIndex done")
    }

    @Transactional(readOnly = true)
    fun find(isbn: String): BookDto? {
        return bookRepository.findByIsbn(isbn)?.let { BookDto.of(it) }
    }

    @Transactional
    fun create(book: BookDto): BookDto? {
        var authorEntity = findOrCreate(book)
        val bookEntity = book.toEntity(authorEntity)
        return BookDto.of(bookRepository.save(bookEntity))
    }

    private fun findOrCreate(book: BookDto): AuthorEntity {
        var authorEntity = authorRepository.findByName(book.authorName!!)
        if (authorEntity == null) {
            authorEntity = AuthorEntity()
            authorEntity.name = book.authorName!!
            authorRepository.save(authorEntity)
        }
        return authorEntity
    }

    @Transactional
    fun update(isbn: String, book: BookDto): BookDto? {
        val bookEntity = bookRepository.findByIsbn(isbn) ?: return null
        var authorEntity = findOrCreate(book)
        return BookDto.of(book.updateEntity(bookEntity, authorEntity))
    }

    @Transactional
    fun delete(isbn: String): BookDto? {
        val entity = bookRepository.findByIsbn(isbn) ?: return null
        bookRepository.delete(entity)
        return BookDto.of(entity)
    }

    @Transactional(readOnly = true)
    fun search(
        authorName: String?,
        title: String?,
        pageIndex: Int,
        pageSize: Int = 20
    ): PaginatedResponse<BookDto> {
        if (authorName.isNullOrBlank() && title.isNullOrBlank()) throw IllegalArgumentException("authorName and title must not be both empty")
        val authorNameLength = authorName?.trim()?.length ?: 0
        val titleLength = title?.trim()?.length ?: 0
        if ((authorNameLength + titleLength) < 2) throw IllegalArgumentException("search criteria must contain at least 2 characters")

        return searchWithHibernateSearch(authorName, title, pageIndex, pageSize)
    }

    private fun searchWithJpql(
        authorName: String?,
        title: String?,
        pageIndex: Int,
        pageSize: Int = 20
    ): PaginatedResponse<BookDto> {
        val pageRequest = PageRequest.of(pageIndex, pageSize, Sort.by("author.name", "title"))
        val page = bookRepository.search(
            if (authorName.isNullOrBlank()) null else authorName,
            if (title.isNullOrBlank()) null else title, pageRequest
        )
        val books = page.content.map { it -> BookDto.of(it) }
        return PaginatedResponse(content = books, pageIndex, pageSize, page.totalElements, page.totalPages)
    }

    private fun searchWithHibernateSearch(
        authorTerm: String?,
        titleTerm: String?,
        pageIndex: Int,
        pageSize: Int = 20
    ): PaginatedResponse<BookDto> {//qqqq mv to access.BookSearcher @Service/@Component
        if (initIndex && !initializeIndexDone.get()) {
            initializeIndex()
        }

        val searchSession: SearchSession = Search.session(entityManager)

        val offset = pageIndex * pageSize

        val result = searchSession.search(BookEntity::class.java)
            .where { f -> // 'f' is the search factory
                val bool = f.bool() // Both 'must'-clauses must matchen (AND)
                if (!titleTerm.isNullOrBlank()) {
                    bool.must(
                        f.match()
                            .field("title")
                            .matching(titleTerm)
                    )
                }
                if (!authorTerm.isNullOrBlank()) {
                    bool.must(
                        f.match()
                            .field("author.name")
                            .matching(authorTerm)
                    )
                }
                bool
            }
            .fetch(searchLimit)

// TODO This code does not work
//        val result = searchSession.search(BookEntity::class.java)
//            .where { f -> // 'f' is the search factory
//                //val bool = f.bool() // Both 'must'-clauses must matchen (AND)
//                f.bool { b ->
//                    if (!titleTerm.isNullOrBlank()) {
//                        b.must(
//                            f.match()
//                                .field("title")
//                                .matching(titleTerm)
//                        )
//                    }
//                    if (!authorTerm.isNullOrBlank()) {
//                        b.must(
//                            f.match()
//                                .field("author.name")
//                                .matching(authorTerm)
//                        )
//                    }
//                }
//            }
//            .sort { f ->
//                f.composite(
//                    f.field("author.name_sort").asc(),
//                    f.field("title_sort").asc()
//                )
//            }
//            .fetch(offset, pageSize)

        val totalHits = result.total().hitCount()
        if (totalHits > searchLimit) throw IllegalArgumentException("too many hits")

        val totalPages = totalHits / pageSize + 1
        val hits: List<BookEntity> = result.hits() as List<BookEntity>
        val books = hits.map { BookDto.of(it) }.sortedWith(compareBy({ it.authorName }, { it.title }))

        return PaginatedResponse(
            content = books.subList(offset, Math.min(books.size, offset + pageSize)),
            currentPage = pageIndex,
            pageSize = pageSize,
            totalElements = totalHits,
            totalPages = totalPages.toInt(),
        )
    }
}
