package com.example.grpcmongo.service

import com.example.grpc.ResponseMessage
import com.example.grpc.Username
import com.example.grpcmongo.model.User
import com.example.grpcmongo.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import com.example.grpc.User as UserMessage

internal class UserCRUDServiceTest {
    private lateinit var repository: UserRepository
    private val service = UserCRUDService()

    @BeforeEach
    fun beforeEach() {
        repository = mock(UserRepository::class.java)
        service.repository = repository
    }

    @Test
    fun `should return user when readUser is called`() {
        //GIVEN
        val username = "User1"
        val user = User(
            username = username,
            firstName = "Den",
            lastName = "Yerm",
            age = 23
        )
        val request = Mono.just(
            Username.newBuilder()
                .setUsername(username)
                .build()
        )
        given(repository.findByUsername(username))
            .willReturn(Mono.just(user))

        //WHEN
        val result = service.readUser(request)

        //THEN
        StepVerifier.create(result)
            .expectNext(
                UserMessage.newBuilder()
                    .setUsername(username)
                    .setFirstName("Den")
                    .setLastName("Yerm")
                    .setAge(23)
                    .build()
            )
            .verifyComplete()

        //AND THEN
        verify(repository).findByUsername(username)
    }

    @Test
    fun `should throw error when readUser is called`() {
        //GIVEN
        val username = "User1"
        val request = Mono.just(
            Username.newBuilder()
                .setUsername(username)
                .build()
        )
        given(repository.findByUsername(username))
            .willReturn(Mono.empty())

        //WHEN
        val result = service.readUser(request)

        //THEN
        StepVerifier.create(result)
            .expectErrorMessage("NOT_FOUND: cannot find user 'User1'")
            .verify()
    }

    @Test
    fun `should return response message when createUser is called`() {
        //GIVEN
        val userMessage = UserMessage.newBuilder()
            .setUsername("Den")
            .setFirstName("Den")
            .setLastName("Yerm")
            .setAge(23)
            .build()
        val request = Mono.just(userMessage)
        val user = User.fromProtoMessage(userMessage)
        given(repository.insert(any<User>())).willReturn(Mono.just(user))

        //WHEN
        val result = service.createUser(request)

        //Then
        StepVerifier.create(result)
            .expectNext(
                ResponseMessage.newBuilder()
                    .setMessage("user Den was created")
                    .build()
            )
            .verifyComplete()

        //AND THEN
        verify(repository).insert(any<User>())
    }

    @Test
    fun `should return response message when updateUser is called`() {
        //GIVEN
        val userMessage = UserMessage.newBuilder()
            .setUsername("Den")
            .setFirstName("Den")
            .setLastName("Yerm")
            .setAge(23)
            .build()
        val request = Mono.just(userMessage)
        val user = User.fromProtoMessage(userMessage)
        given(repository.findByUsername("Den")).willReturn(Mono.just(user))
        given(repository.save(any())).willReturn(Mono.just(user))

        //WHEN
        val result = service.updateUser(request)

        //Then
        StepVerifier.create(result)
            .expectNext(
                ResponseMessage.newBuilder()
                    .setMessage("user Den was updated")
                    .build()
            )
            .verifyComplete()

        //AND THEN
        verify(repository).findByUsername("Den")
        verify(repository).save(any())
    }

    @Test
    fun `should return response message when deleteUser is called`() {
        //GIVEN
        val userMessage = UserMessage.newBuilder()
            .setUsername("Den")
            .setFirstName("Den")
            .setLastName("Yerm")
            .setAge(23)
            .build()
        val request = Mono.just(userMessage)
        given(repository.findByUsername("Den")).willReturn(Mono.empty())

        //WHEN
        val result = service.updateUser(request)

        //Then
        StepVerifier.create(result)
            .expectErrorMessage("NOT_FOUND: cannot find user 'Den'")
            .verify()

        //AND THEN
        verify(repository).findByUsername("Den")
    }

    @Test
    fun `should throw error when deleteUser is called`() {
        //GIVEN
        val username = "Den"
        val request = Mono.just(
            Username.newBuilder()
                .setUsername(username)
                .build()
        );
        given(repository.deleteByUsername(username)).willReturn(Mono.empty())

        //WHEN
        val result = service.deleteUser(request)

        //THEN
        StepVerifier.create(result)
            .expectNext(
                ResponseMessage.newBuilder()
                    .setMessage("user 'Den' was deleted")
                    .build()
            )
            .verifyComplete()

        //AND THEN
        verify(repository).deleteByUsername("Den")
    }
}