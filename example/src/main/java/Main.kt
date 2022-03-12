import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.adkhambek.moon.Event
import me.adkhambek.moon.Logger
import me.adkhambek.moon.Moon
import me.adkhambek.moon.convertor.EventConvertor
import me.adkhambek.moon.convertor.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess


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
//    return GsonAdapterFactory()
}


@ExperimentalSerializationApi
fun main() = runBlocking {
    val client = provideOkHttpClient()

    val option = IO.Options().apply {
        this.callFactory = client
        this.webSocketFactory = client
        transports = arrayOf("websocket", "polling")
    }

    val socket: Socket = IO.socket("http://localhost:3000", option)
    socket.connect()

    val logger = Logger(::println)

    val moon = Moon.Factory().create(socket, logger, provideConverterFactory())
    val api: API = moon.create(API::class.java)


    println(api.testEvent(Message("ping request")))
    println(api.testAckEvent(Message("ping request")))

    api
        .helloEvent()
        .take(4)
        .collect {
            println(it)
        }

    exitProcess(0)
    Unit
}

interface API {

    /**
     * This is an example to show backend side
     *
     *  ```js
     *  socket.emit('ping', {
     *      'message': 'pong'
     *  })
     *  ```
     *
     *  @return Response (Message) from server
     *  @author by Mr. Adkhambek
     *  @see <a href="https://github.com/MrAdkhambek/Moon/blob/main/IO.Socket%20echo/app.js">Github</a>
     */
    @Event(value = "ping")
    fun helloEvent(): Flow<Message>

    /**
     * This is an example to show backend side
     *
     *  ```js
     *  socket.on('test', (arg) => {
     *          // TODO
     *  })
     *  ```
     *
     *  @param message is Request
     *  @author by Mr. Adkhambek
     *  @see <a href="https://github.com/MrAdkhambek/Moon/blob/main/IO.Socket%20echo/app.js">Github</a>
     */
    @Event(value = "test")
    suspend fun testEvent(message: Message)

    /**
     * This is an example to show backend side
     *
     *  ```js
     *  socket.on('testAck', (arg, ack) => {
     *      // TODO
     *      ack([
     *          {
     *              'message': 'pong 1'
     *          },
     *          {
     *              'message': 'pong 2'
     *          }
     *      ])
     *  })
     *  ```
     *
     *  @param message is Request
     *  @return Response (Message) from server
     *  @author by Mr. Adkhambek
     *  @see <a href="https://github.com/MrAdkhambek/Moon/blob/main/IO.Socket%20echo/app.js">Github</a>
     */
    @Event(value = "testAck")
    suspend fun testAckEvent(message: Message): List<Message>
}

@Serializable
data class Message(
    val message: String
)