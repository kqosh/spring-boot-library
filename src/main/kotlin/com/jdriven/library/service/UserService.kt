package com.jdriven.library.service

import com.jdriven.library.access.model.AuthorityEntity
import com.jdriven.library.access.model.AuthorityRepository
import com.jdriven.library.access.model.UserEntity
import com.jdriven.library.access.model.UserRepository
import com.jdriven.library.service.model.CreateUserRequest
import com.jdriven.library.service.model.UserDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository, private val authorityRepository: AuthorityRepository)  {

	@Transactional(readOnly = true)
	fun find(username: String): UserDto? = userRepository.findByUsername(username)?.let { UserDto.of(it) }//qqqq ut

	@Transactional
	fun create(user: CreateUserRequest): UserDto? {
		val userEntity = user.toEntity()
		userRepository.save(userEntity)
		return UserDto.of(userEntity)
	}

	@Transactional
	fun addRole(username: String, role: String) {
		if (role !in listOf("ROLE_ADMIN", "ROLE_USER")) throw IllegalArgumentException("non existing role $role")
		val existing = authorityRepository.findByUsernameAndAuthority(username, role)
		if (existing != null) throw IllegalStateException("authority already exists for $username $role")
		val user = userRepository.findByUsername(username) ?: throw IllegalArgumentException("user does not exist $username")

		val authority = createAuthorityEntity(user, role)
		authorityRepository.save(authority)
	}

	@Transactional
	fun deleteRole(username: String, role: String): Boolean {
		val authority = authorityRepository.findByUsernameAndAuthority(username, role) ?: return false
		authorityRepository.delete(authority)
		return true
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
