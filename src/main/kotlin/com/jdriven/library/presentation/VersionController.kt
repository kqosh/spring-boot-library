package com.jdriven.library.presentation

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/version")
class VersionController(@Value("\${app.version}") val appVersion: String) {//qqqq private val

	@GetMapping
	fun version(): String = appVersion
}
