package com.jdriven.library.service

import com.jdriven.library.access.model.AuthorityEntity
import com.jdriven.library.access.model.AuthorityRepository
import com.jdriven.library.access.model.UserEntity
import com.jdriven.library.access.model.UserRepository
import com.jdriven.library.service.model.Author
import com.jdriven.library.service.model.PaginatedResponse
import com.jdriven.library.service.model.UserDto
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository, private val authorityRepository: AuthorityRepository)  {

	@Transactional(readOnly = true)
	fun find(username: String): UserDto? = userRepository.findByUsername(username)?.let { UserDto.of(it) }//qqqq ut

	@Transactional
	fun create(user: UserDto): UserDto? {
		val userEntity = user.toEntity()
		userRepository.save(userEntity)

		user.roles.forEach { userRepository}

		val autorityEntities = user.roles.map { it -> createAuthorityEntity(userEntity, it) }
		autorityEntities.forEach { authorityRepository.save(it)}

		return user
	}

	private fun createAuthorityEntity(userEntity: UserEntity, role: String): AuthorityEntity {
		val entity = AuthorityEntity()
		entity.user = userEntity
		entity.authority = role
		return entity
	}

	@Transactional
	fun enable(username: String, enabled: Boolean): UserDto? {
		val user = userRepository.findByUsername(username) ?: return null
		user.enabled = enabled
		return UserDto.of(user)
	}
}
