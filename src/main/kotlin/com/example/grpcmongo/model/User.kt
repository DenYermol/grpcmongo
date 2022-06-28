package com.example.grpcmongo.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import com.example.grpc.User as UserMessage

@Document
data class User(
        @Id
        val id: ObjectId = ObjectId.get(),
        val username: String,
        val firstName: String,
        val lastName: String,
        val age: Int
) {
    companion object {
        fun fromProtoMessage(userMessage: UserMessage): User {
            return User(
                    username = userMessage.username,
                    firstName = userMessage.firstName,
                    lastName = userMessage.lastName,
                    age = userMessage.age
            )
        }
    }
}
