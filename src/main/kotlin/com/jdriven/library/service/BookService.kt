package com.jdriven.library.service

import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.resource.NoResourceFoundException

@Service
public class BookService {

	@Transactional(readOnly = true)
	fun find(isbn: String): String? {
		return if (isbn == "123NotFound") null else "qqqq $isbn" //qqqq find in db
	}
}
