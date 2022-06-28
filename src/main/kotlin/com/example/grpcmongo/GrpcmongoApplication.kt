package com.example.grpcmongo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GrpcmongoApplication

fun main(args: Array<String>) {
    runApplication<GrpcmongoApplication>(*args)
}
