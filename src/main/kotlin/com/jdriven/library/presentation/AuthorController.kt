package com.jdriven.library.presentation

import com.jdriven.library.access.model.AuthorEntity
import com.jdriven.library.presentation.model.Author
import com.jdriven.library.presentation.model.CreateAuthorRequest
import com.jdriven.library.service.AuthorService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestController
@RequestMapping("/authors")
class AuthorController(private val service: AuthorService) {

	private val logger = LoggerFactory.getLogger(this::class.java)


	@GetMapping("/{name}")
	fun findByName(@PathVariable(value = "name") name: String): Author {
		return service.find(name)?.let { Author.of(it) } ?: throw NoResourceFoundException(HttpMethod.GET, "/authors/${name}")
	}

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	fun create(@RequestBody request: CreateAuthorRequest) {
		logger.info("create $request")
		val entity = AuthorEntity()
		entity.name = request.name
		service.create(entity)
	}

	@DeleteMapping("/{name}")
	fun deleteByName(@PathVariable(value = "name") name: String) {
		service.delete(name)?.let { Author.of(it) } ?: throw NoResourceFoundException(HttpMethod.DELETE, "/authors/${name}")
	}

	//qqqq add (create), update, delete book
	//qqqq find by title xor author or both
	//qqqq borrow xor return book
}
