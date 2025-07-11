package com.jdriven.library.service

import com.jdriven.library.access.model.AuthorEntity
import com.jdriven.library.access.model.AuthorRepository
import com.jdriven.library.service.model.Author
import com.jdriven.library.service.model.CreateAuthorRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorService(private val repository: AuthorRepository)  {

	@Transactional
	fun create(request: CreateAuthorRequest): Author? {
		val entity = AuthorEntity()
		entity.name = request.name
		return Author.of(repository.save(entity))
	}

	@Transactional(readOnly = true)
	fun find(name: String): Author? = repository.findByName(name)?.let { Author.of(it) }

	@Transactional
	fun delete(name: String): Author? {
		val authorEntity = repository.findByName(name)
		if (authorEntity == null) return null
		repository.deleteById(authorEntity.id!!)
		return Author.of(authorEntity)
	}
}
