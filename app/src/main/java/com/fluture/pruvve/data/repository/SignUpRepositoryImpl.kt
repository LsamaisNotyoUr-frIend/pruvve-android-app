package com.fluture.pruvve.data.repository


import com.fluture.pruvve.data.api.UserService
import com.fluture.pruvve.retrofittcalls.User

import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(
    private val userService: UserService,
): SignUpRepository {
    override suspend fun createUser(user: User): User {
        TODO("Not yet implemented")
    }

}