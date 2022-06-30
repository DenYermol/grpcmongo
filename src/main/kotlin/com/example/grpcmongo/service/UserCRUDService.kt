package com.example.grpcmongo.service

import com.example.grpc.*
import com.example.grpcmongo.repository.UserRepository
import io.grpc.Status
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono
import com.example.grpcmongo.model.User as UserModel

@GrpcService
class UserCRUDService : ReactorUserCRUDServiceGrpc.UserCRUDServiceImplBase() {

    @Autowired
    lateinit var repository: UserRepository
    override fun createUser(request: Mono<User>?): Mono<ResponseMessage> =
        request!!.flatMap {
            repository.insert(UserModel.fromProtoMessage(it))
                .thenReturn(
                    ResponseMessage.newBuilder()
                        .setMessage("user ${it.username} was created")
                        .build()
                )
        }

    override fun readUser(request: Mono<Username>?): Mono<User> =
        request!!.flatMap { user ->
            findUserByUsernameElseError(user.username)
        }.map {
            User.newBuilder()
                .setUsername(it.username)
                .setFirstName(it.firstName)
                .setLastName(it.lastName)
                .setAge(it.age)
                .build()
        }

    override fun updateUser(request: Mono<User>?): Mono<ResponseMessage> =
        request!!.flatMap { user ->
            findUserByUsernameElseError(user.username)
                .flatMap {
                    repository.save(UserModel.fromProtoMessage(user, it.id))
                }.thenReturn(
                    ResponseMessage.newBuilder()
                        .setMessage("user ${user.username} was updated")
                        .build()
                )
        }

    override fun deleteUser(request: Mono<Username>?): Mono<ResponseMessage> =
        request!!.flatMap { user ->
            repository.deleteByUsername(user.username)
                .thenReturn(
                    ResponseMessage.newBuilder()
                        .setMessage("user '${user.username}' was deleted")
                        .build()
                )
        }

    private fun findUserByUsernameElseError(username: String): Mono<UserModel> {
        return repository.findByUsername(username)
            .switchIfEmpty(
                Mono.error(
                    Status.NOT_FOUND
                        .withDescription("cannot find user '${username}'")
                        .asRuntimeException()
                )
            )
    }
}