package com.jdriven.library.service

import com.jdriven.library.access.model.AuthorityEntity
import com.jdriven.library.access.model.AuthorityRepository
import com.jdriven.library.access.model.UserEntity
import com.jdriven.library.access.model.UserRepository
import com.jdriven.library.security.TokenService
import com.jdriven.library.service.model.CreateJwtRequest
import com.jdriven.library.service.model.CreateOrUpdateUserRequest
import com.jdriven.library.service.model.UserDto
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CopyOnWriteArraySet

@Service
class UserService(
	private val tokenService: TokenService,
	private val userRepository: UserRepository,
	private val authorityRepository: AuthorityRepository
) {

	val revokationList = CopyOnWriteArraySet<String>()

	@Transactional(readOnly = true)
	fun find(username: String): UserDto? = userRepository.findByUsername(username)?.let { UserDto.of(it) }

	@Transactional
	fun createJwt(request: CreateJwtRequest): String? {
		val user = userRepository.findByUsername(request.username) ?: return null
		if (!user.enabled) throw DisabledException("")
		if (user.password != request.password) throw BadCredentialsException("")
		revokationList.remove(user.username)
		return tokenService.createToken(user.username, user.authorities.map { it.authority })
	}

	@Transactional
	fun create(user: CreateOrUpdateUserRequest): UserDto? {
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
		revokationList.add(username)
		authorityRepository.save(authority)
	}

	@Transactional
	fun deleteRole(username: String, role: String): Boolean {
		val authority = authorityRepository.findByUsernameAndAuthority(username, role) ?: return false
		revokationList.add(username)
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
		revokationList.add(username)
		user.enabled = enabled
		return UserDto.of(user)
	}

	@Transactional
	fun findAllRoles(): List<String> = authorityRepository.findAllRoles()

	@Transactional
	fun update(request: CreateOrUpdateUserRequest): UserDto? {
		val user = userRepository.findByUsername(request.username) ?: return null
		request.updateEntity(user)
		return UserDto.of(user)
	}
}
