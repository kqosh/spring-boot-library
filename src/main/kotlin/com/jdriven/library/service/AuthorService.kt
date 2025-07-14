package com.jdriven.library.service

import com.jdriven.library.access.model.AuthorEntity
import com.jdriven.library.access.model.AuthorRepository
import com.jdriven.library.service.model.Author
import com.jdriven.library.service.model.CreateOrUpdateAuthorRequest
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
//
//	@Transactional
//	fun findAuthorSortedByName(author: String) {
//		// Voorbeeld 2: Sorteer op meerdere kolommen (eerst op achternaam, dan op titel)
//		val multiSort = Sort.by("author").and(Sort.by("title").ascending())
//		val pageRequestMulti = PageRequest.of(0, 20, multiSort)
//
//		val sortedBooks = repository.findByAuthor(author, pageRequestMulti)
//	}
}
