object Deps {

    object Kotlin {
        const val version = "1.6.10"
        const val stdLib = "org.jetbrains.kotlin:kotlin-stdlib:$version"
    }

    object KotlinX {
        const val serializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2"
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0"
    }

    object Squareup {
        private const val version = "4.9.1"
        const val okhttp3 = "com.squareup.okhttp3:okhttp:$version"
        const val logging = "com.squareup.okhttp3:logging-interceptor:$version"
    }

    object Google {
        const val findbugs = "com.google.code.findbugs:jsr305:3.0.2"
        const val gson = "com.google.code.gson:gson:2.9.0"
    }

    object SocketIO {
        const val client = "io.socket:socket.io-client:2.0.1"
    }

    object Test {
        const val junitAPI = "org.junit.jupiter:junit-jupiter-api:5.6.0"
        const val junitEngine = "org.junit.jupiter:junit-jupiter-engine"
    }
}