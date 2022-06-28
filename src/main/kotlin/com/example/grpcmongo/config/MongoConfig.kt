package com.example.grpcmongo.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@EnableReactiveMongoRepositories(basePackages = ["com.example.grpcmongo.repository"])
@Configuration
class MongoConfig : AbstractReactiveMongoConfiguration() {
    override fun getDatabaseName(): String {
        return "test"
    }

    @Bean
    fun mongoClient(): MongoClient {
        val connectionString = ConnectionString("mongodb://localhost:27017/test")
        val mongoClientsSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build()
        return MongoClients.create(mongoClientsSettings)
    }

    override fun getMappingBasePackages(): MutableCollection<String> {
        return mutableListOf("com.example")
    }
}