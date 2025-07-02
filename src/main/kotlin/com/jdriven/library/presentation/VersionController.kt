package com.jdriven.library.presentation

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/version")
class VersionController {

	@GetMapping
	fun version(): String {
		return "version qqqq get from app.props?" //qqqq also use mvn replace
	}
}
