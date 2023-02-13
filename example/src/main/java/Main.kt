import com.adkhambek.moon.Event
import com.adkhambek.moon.Logger
import com.adkhambek.moon.Moon
import com.adkhambek.moon.convertor.EventConvertor
import com.adkhambek.moon.convertor.GsonAdapterFactory
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

public fun provideOkHttpClient(): OkHttpClient = with(OkHttpClient.Builder()) {
    readTimeout(1, TimeUnit.SECONDS)
    writeTimeout(1, TimeUnit.SECONDS)
    connectTimeout(1, TimeUnit.SECONDS)

    addInterceptor(HttpLoggingInterceptor(::println))
    build()
}

// private val json = Json {
//    prettyPrint = true
//    encodeDefaults = true
// }
//
// @ExperimentalSerializationApi
private fun provideConverterFactory(): EventConvertor.Factory {
    val contentType = "application/json".toMediaType()
//    return json.asConverterFactory(contentType)
    return GsonAdapterFactory()
}

private fun provideSocket(): Socket {
    val client = provideOkHttpClient()

    val option = IO.Options().apply {
        this.callFactory = client
        this.webSocketFactory = client
        transports = arrayOf("polling", "websocket")
    }

    return IO.socket("http://95.46.96.49:4000", option)
}

public fun main(): Unit = runBlocking {
    val moon = Moon.Factory().create(
        socket = provideSocket(),
        logger = Logger { log ->
            println(log)
        },
        provideConverterFactory()
    )

    moon.connect()
    delay(1000L)

    val api: API = moon.create()

    val executor = Executors
        .newSingleThreadExecutor()
        .asCoroutineDispatcher()

    api
        .helloEvent("practical_socket_ex_21_car_777")
        .onEach {
            launch(
                executor
            ) {
                // TODO
            }
        }.launchIn(this)

    delay(1_000L)
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
    @Event(value = "{event}")
    public fun helloEvent(
        @Event("event") event: String
    ): Flow<Message>

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
    @Event(value = "{event}")
    public suspend fun testEvent(@Event("event") event: String, message: Message)

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
    @Event(value = "{event}")
    public suspend fun testAckEvent(@Event("event") event: String, message: Message): List<Message>
}

public data class Message(
    val message: String
)
