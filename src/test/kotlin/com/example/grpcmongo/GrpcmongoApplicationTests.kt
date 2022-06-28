package com.example.grpcmongo

import com.example.grpcmongo.model.User
import com.example.grpcmongo.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GrpcmongoApplicationTests {

	@Autowired
	lateinit var userRepository: UserRepository

	@Test
	fun contextLoads() {
		userRepository.insert(User(name="It is working!!!"))
	}

}
