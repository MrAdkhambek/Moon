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
import mr.adkhambek.moon.convertor.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import req.Onboarding
import java.util.concurrent.TimeUnit

private fun buildRequest(request: Request, auth: String?): Request.Builder {
    return request.newBuilder()
        .header("Content-type", "application/json")
        .header("Accept", "application/json")
        .header("Authorization", requireNotNull(auth))
}

fun provideOkHttpClient(): OkHttpClient = with(OkHttpClient.Builder()) {
    readTimeout(10, TimeUnit.MINUTES)
    writeTimeout(10, TimeUnit.MINUTES)
    connectTimeout(40, TimeUnit.SECONDS)

    addInterceptor(HttpLoggingInterceptor(::println))

//    addInterceptor { chain ->
//        val request = chain.request();
//        chain.proceed(
//            buildRequest(
//                request = request,
//                auth = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InJhbTE1NUB5b3BtYWlsLmNvbSIsImlhdCI6MTY0NDkwNDU2NH0.FDuZgocrWOsgDzVG0GmhuUzJELDzLcIytcGl-6vUVCY"
//            ).build()
//        )
//    }

    build()
}

private val json = Json { encodeDefaults = true }

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

    val socket: Socket = IO.socket("http://csa-rc.dev2.skylab.world", option)
    socket.connect()

//    val moon = Moon
//        .Builder()
//        .with(socket)
//        .convertor(provideConverterFactory())
//        .build()


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

    api.helloEvent()
        .catch {
            println(it.toString())
        }
        .collect {
            println(it)
        }

    Unit
}

interface API {

    @Event(value = "app.configuration.onboarding")
    fun helloEvent(): Flow<Onboarding>
}