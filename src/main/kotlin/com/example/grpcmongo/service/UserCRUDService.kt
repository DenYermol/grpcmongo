package com.example.grpcmongo.service

import com.example.grpc.*
import com.example.grpcmongo.repository.UserRepository
import io.grpc.Status
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.beans.factory.annotation.Autowired
import com.example.grpcmongo.model.User as UserModel

@GrpcService
class UserCRUDService : UserCRUDServiceGrpc.UserCRUDServiceImplBase() {

    @Autowired
    lateinit var repository: UserRepository

    override fun createUser(request: User?, responseObserver: StreamObserver<ResponseMessage>?) {
        repository.insert(UserModel.fromProtoMessage(request!!))

        val responseMessage = ResponseMessage.newBuilder().setMessage("user ${request.username} was created").build()
        responseObserver!!.onNext(responseMessage)
        responseObserver.onCompleted()
    }

    override fun readUser(request: Username?, responseObserver: StreamObserver<User>?) {
        val userModel = repository.findByUsername(request!!.username)
        if (userModel == null) {
            responseObserver!!.onError(
                Status.NOT_FOUND.withDescription("user '${request.username}' is not found")
                    .asRuntimeException()
            )
            return
        }
        val user = User.newBuilder()
            .setUsername(userModel.username)
            .setFirstName(userModel.firstName)
            .setLastName(userModel.lastName)
            .setAge(userModel.age)
            .build()
        responseObserver!!.onNext(user)
        responseObserver.onCompleted()
    }

    override fun updateUser(request: User?, responseObserver: StreamObserver<ResponseMessage>?) {
        val user = repository.findByUsername(request!!.username)
        if (user == null) {
            responseObserver!!.onError(
                Status.NOT_FOUND.withDescription("user '${request.username}' is not found")
                    .asRuntimeException()
            )
            return
        }
        repository.save(
            UserModel(
                id = user.id,
                username = request.username,
                firstName = request.firstName,
                lastName = request.lastName,
                age = request.age
            )
        )
        val responseMessage = ResponseMessage.newBuilder().setMessage("update user was called").build()
        responseObserver!!.onNext(responseMessage)
        responseObserver.onCompleted()
    }

    override fun deleteUser(request: Username?, responseObserver: StreamObserver<ResponseMessage>?) {
        repository.deleteByUsername(request!!.username)
        val responseMessage = ResponseMessage.newBuilder().setMessage("user '${request.username}' was deleted").build()
        responseObserver!!.onNext(responseMessage)
        responseObserver.onCompleted()
    }
}