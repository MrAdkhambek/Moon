import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.adkhambek.moon.Event
import me.adkhambek.moon.Moon
import me.adkhambek.moon.convertor.EventConvertor
import me.adkhambek.moon.convertor.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

fun provideOkHttpClient(): OkHttpClient = with(OkHttpClient.Builder()) {
    readTimeout(10, TimeUnit.MINUTES)
    writeTimeout(10, TimeUnit.MINUTES)
    connectTimeout(40, TimeUnit.SECONDS)

    addInterceptor(HttpLoggingInterceptor(::println))

    build()
}

private val json = Json {
    prettyPrint = true
    encodeDefaults = true
}

@ExperimentalSerializationApi
private fun provideConverterFactory(): EventConvertor.Factory {
    val contentType = "application/json".toMediaType()
    return json.asConverterFactory(contentType)
}

@Serializable
data class TestEvent(
    val name: String
)


@ExperimentalSerializationApi
fun main() = runBlocking {
    val client = provideOkHttpClient()

    val option = IO.Options().apply {
        this.callFactory = client
        this.webSocketFactory = client
        transports = arrayOf("websocket", "polling")
    }

    val socket: Socket = IO.socket("http://192.168.0.100:3000", option)
    socket.connect()

    socket.on(Socket.EVENT_CONNECT) {
        println(Socket.EVENT_CONNECT)
        it.map(Any::toString).forEach(::println)
    }

    socket.on(Socket.EVENT_DISCONNECT) {
        println(Socket.EVENT_DISCONNECT)
        it.forEach(::println)
    }

    socket.on(Socket.EVENT_CONNECT_ERROR) {
        println(Socket.EVENT_CONNECT_ERROR)
        it.map(Any::toString).forEach(::println)
    }

    val moon = Moon.Factory().create(socket, provideConverterFactory())
    val api: API = moon.create(API::class.java)

    println(api.testEvent(Message("test event")))

    println("=".repeat(40))

    api.helloEvent()
        .catch {
            println(it.toString())
        }
        .collect {
            println(it)
        }
}

interface API {

    @Event(value = "ping")
    fun helloEvent(): Flow<Message>

    @Event(value = "test")
    suspend fun testEvent(message: Message): List<Message>
}

@Serializable
data class Message(
    val message: String
)