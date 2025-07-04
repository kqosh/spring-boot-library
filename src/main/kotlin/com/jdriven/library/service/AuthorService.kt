package com.jdriven.library.service

import com.jdriven.library.access.model.AuthorEntity
import com.jdriven.library.access.model.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
public class AuthorService(private val repository: AuthorRepository)  {

	@Transactional
	fun create(author: AuthorEntity) = repository.save(author)
	@Transactional(readOnly = true)

	fun find(name: String): AuthorEntity? = repository.findByName(name)
//
//	@Transactional
//	fun update(author: AuthorEntity): AuthorEntity? {
//		val author = find(author.name)
//		if (author == null) return null
//		repository.deleteById(author.id!!)
//		return author
//	}qqqq

	@Transactional
	fun delete(name: String): AuthorEntity? {
		val author = find(name)
		if (author == null) return null
		repository.deleteById(author.id!!)
		return author
	}
}
