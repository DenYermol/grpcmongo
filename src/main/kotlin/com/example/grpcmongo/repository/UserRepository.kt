package com.example.grpcmongo.repository

import com.example.grpcmongo.model.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String> {
    fun findByUsername(username: String): User?
    fun deleteByUsername(username: String)
}