package com.jdriven.library.access

import com.jdriven.library.access.model.BookEntity
import com.jdriven.library.service.model.BookDto
import com.jdriven.library.service.model.PaginatedResponse
import jakarta.persistence.EntityManager
import org.hibernate.search.mapper.orm.Search
import org.hibernate.search.mapper.orm.massindexing.MassIndexer
import org.hibernate.search.mapper.orm.session.SearchSession
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.util.concurrent.atomic.AtomicBoolean

/**
 * See [Getting started with Hibernate Search in Hibernate ORM](https://docs.jboss.org/hibernate/stable/search/getting-started/orm/en-US/html_single/#getting-started-mapping)
 */
@Repository
class BookSearchRepository(
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

    fun search(
        authorTerm: String?,
        titleTerm: String?,
        pageIndex: Int,
        pageSize: Int = 20
    ): PaginatedResponse<BookDto> {
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
