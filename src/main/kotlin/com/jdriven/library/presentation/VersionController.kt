package com.jdriven.library.presentation

import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/version")
class VersionController(@Value("\${app.version}") private val appVersion: String) {

	@Operation(summary = "Does NOT require an authorization header.", security = [])
	@GetMapping
	fun version(): String = appVersion
}
