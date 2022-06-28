package com.example.grpcmongo.repository

import com.example.grpcmongo.model.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface UserRepository : ReactiveMongoRepository<User, String> {
    fun findByUsername(username: String): Mono<User>
    fun deleteByUsername(username: String): Mono<Void>
}