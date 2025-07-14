package com.jdriven.library.service

import com.jdriven.library.access.model.AuthorEntity
import com.jdriven.library.access.model.AuthorRepository
import com.jdriven.library.service.model.Author
import com.jdriven.library.service.model.CreateOrUpdateAuthorRequest
import com.jdriven.library.service.model.PaginatedResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorService(private val repository: AuthorRepository)  {

	@Transactional(readOnly = true)
	fun find(name: String): Author? = repository.findByName(name)?.let { Author.of(it) }

	@Transactional
	fun create(request: CreateOrUpdateAuthorRequest): Author? {
		val entity = AuthorEntity()
		entity.name = request.name
		return Author.of(repository.save(entity))
	}

	@Transactional
	fun delete(name: String): Author? {
		val authorEntity = repository.findByName(name) ?: return null
		repository.deleteById(authorEntity.id!!)
		return Author.of(authorEntity)
	}

	@Transactional(readOnly = true)
	fun search(authorName: String?, pageIndex: Int, pageSize: Int = 20): PaginatedResponse<Author> {
		if (authorName.isNullOrEmpty()) throw IllegalArgumentException("authorName must not be empty")
		val pageRequest = PageRequest.of(pageIndex, pageSize, Sort.by("name"))
		val page = repository.search(authorName, pageRequest)
		val authors = page.content.map { it -> Author.of(it)}
		return PaginatedResponse(content = authors, pageIndex, pageSize, page.totalElements, page.totalPages)
	}
}
