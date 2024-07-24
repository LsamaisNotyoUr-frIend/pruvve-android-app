package com.fluture.pruvve.data.repository

import com.fluture.pruvve.retrofittcalls.User


interface SignUpRepository {
   suspend fun createUser(user: User): User
}