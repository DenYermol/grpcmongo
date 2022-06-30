import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.*

val protobufVersion by extra("3.21.1")
val protobufPluginVersion by extra("0.8.18")
val grpcVersion by extra("1.47.0")

plugins {
    id("idea")
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.google.protobuf") version "0.8.18"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive:2.7.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("net.devh:grpc-server-spring-boot-starter:2.13.1.RELEASE")
    implementation("io.grpc:grpc-kotlin-stub:1.3.0")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")
    implementation("com.salesforce.servicelibs:reactor-grpc-stub:1.2.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }

        id("reactor") {
            artifact = "com.salesforce.servicelibs:reactor-grpc:1.2.3"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("reactor")
            }
        }
    }
    generatedFilesBaseDir = "$projectDir/src/generated"
}

idea {
    module {
        sourceDirs.add(file("$projectDir/src/generated/main/java"))
        sourceDirs.add(file("$projectDir/src/generated/main/grpc"))
    }
}
tasks {
    clean {
        delete("$projectDir/src/generated")
    }
}
