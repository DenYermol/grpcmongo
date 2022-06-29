package com.example.grpcmongo.service

import com.example.grpc.*
import com.example.grpcmongo.repository.UserRepository
import io.grpc.Status
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.beans.factory.annotation.Autowired
import com.example.grpcmongo.model.User as UserModel

@GrpcService
class UserCRUDService : UserCRUDServiceGrpcKt.UserCRUDServiceCoroutineImplBase() {

    @Autowired
    lateinit var repository: UserRepository

    override suspend fun createUser(request: User): ResponseMessage {
        repository.save(UserModel.fromProtoMessage(request)).subscribe()

        return ResponseMessage.newBuilder()
            .setMessage("user ${request.username} was created")
            .build()
    }

    override suspend fun readUser(request: Username): User {
        val userModel = repository.findByUsername(request.username).block()
            ?: throw Status.NOT_FOUND
                .withDescription("user '${request.username}' is not found")
                .asRuntimeException()

        return User.newBuilder()
            .setUsername(userModel.username)
            .setFirstName(userModel.firstName)
            .setLastName(userModel.lastName)
            .setAge(userModel.age)
            .build()
    }

    override suspend fun updateUser(request: User): ResponseMessage {
        val user = repository.findByUsername(request.username).block()
            ?: throw Status.NOT_FOUND
                .withDescription("user '${request.username}' is not found")
                .asRuntimeException()
        repository.save(
            UserModel.fromProtoMessage(request, user.id)
        ).subscribe()
        return ResponseMessage.newBuilder().setMessage("update user was called").build()
    }

    override suspend fun deleteUser(request: Username): ResponseMessage {
        repository.deleteByUsername(request.username).subscribe()
        return ResponseMessage.newBuilder()
            .setMessage("user '${request.username}' was deleted")
            .build()
    }
}