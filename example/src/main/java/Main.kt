import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import mr.adkhambek.moon.Event
import mr.adkhambek.moon.Moon
import mr.adkhambek.moon.adapter.EventAdapter
import mr.adkhambek.moon.adapter.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess


fun provideOkHttpClient(): OkHttpClient = with(OkHttpClient.Builder()) {
    readTimeout(10, TimeUnit.MINUTES)
    writeTimeout(10, TimeUnit.MINUTES)
    connectTimeout(40, TimeUnit.SECONDS)

    build()
}

@ExperimentalSerializationApi
private fun provideConverterFactory(): EventAdapter.Factory {
    val contentType = "application/json".toMediaType()
    return Json.asConverterFactory(contentType)
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
        transports = arrayOf("polling", "websocket")
    }

    val socket: Socket = IO.socket("http://localhost:3000", option)
    socket.connect()

//    val moon = Moon
//        .Builder()
//        .with(socket)
//        .convertor(provideConverterFactory())
//        .build()

    val moon = Moon.Factory().create(socket, provideConverterFactory())

    val f: API = moon.create(API::class.java)

    println(f.helloEvent(listOf(TestEvent("Hi"), TestEvent("By"))))

    f
        .flowEvent("")
        .first().let {
            println(it)
        }

    socket.disconnect()
    exitProcess(0)

    Unit
}

interface API {

    @Event(value = "test")
    suspend fun helloEvent(arg: List<TestEvent>): List<Message>

    @Event(value = "ping")
    fun flowEvent(arg: String): Flow<Message>
}

@Serializable
data class Message(
    val message: String
)