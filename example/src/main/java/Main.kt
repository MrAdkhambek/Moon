import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
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

public fun provideOkHttpClient(): OkHttpClient = with(OkHttpClient.Builder()) {
    readTimeout(1, TimeUnit.SECONDS)
    writeTimeout(1, TimeUnit.SECONDS)
    connectTimeout(1, TimeUnit.SECONDS)

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

private fun provideSocket(): Socket {
    val client = provideOkHttpClient()

    val option = IO.Options().apply {
        this.callFactory = client
        this.webSocketFactory = client
        transports = arrayOf("websocket", "polling")
    }

    return IO.socket("http://localhost:3000", option)
}

@ExperimentalSerializationApi
public fun main(): Unit = runBlocking {

    val logger = Logger {}

    val moon = Moon.Factory().create(provideSocket(), logger, provideConverterFactory())
    val api: API = moon.create(API::class.java)

    launch {
        val state: StateFlow<Moon.Status> = moon.state
    }

    moon.connect()

    api.testEvent(Message("ping request"))
    println(api.testAckEvent(Message("ping request")))

    api.helloEvent()
        .take(4)
        .collect {
            println(it)
        }

    moon.disconnect()
    exitProcess(0)
}

public interface API {

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
    public fun helloEvent(): Flow<Message>

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
    public suspend fun testEvent(message: Message)

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
    public suspend fun testAckEvent(message: Message): List<Message>
}

@Serializable
public data class Message(
    val message: String
)