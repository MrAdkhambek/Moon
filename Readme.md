Moon
-------------
[![Maven Central](https://img.shields.io/maven-central/v/me.adkhambek.moon/moon.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22me.adkhambek.moon%22)

A Retrofit inspired [Socket.io](https://socket.io) client for Kotlin (Android, JVM). </br>
For WebSocket [Scarlet](https://github.com/Tinder/Scarlet) </br>

> **⚠️   This library works only on Kotlin and Kotlin coroutines**

<p align="center">
<img src="./media/socket-moon.jpg" width="60%" alt="logo">
</p>

Dependencies
-------------

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutines_version}") // REQUIRED
    implementation("me.adkhambek.moon:moon:${latest_version}") 
}
```

Convertors
-------------

```groovy
dependencies {
    implementation("me.adkhambek.moon:convertor-gson:${latest_version}")                    // OPTIONAL
    
    implementation("me.adkhambek.moon:convertor-kotlin-serialization:${latest_version}")    // OPTIONAL
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${json_version}")      // REQUIRED if you use convertor-kotlin-serialization
}
```

Usage
-------------

```kotlin

import kotlinx.serialization.Serializable 

@Serializable
data class Message(
    val message: String
)

interface SocketAPI {

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
```

```kotlin
val logger: Logger = TODO()
val socket: io.socket.client.Socket = TODO()
val convertor: EventConvertor.Factory = TODO()

// Create moon object with Builder pattern
val moon = Moon
    .Builder()
    .with(socket)
    .logger(logger)
    .addConvertor(convertor)
    .build()

// Create moon object with Factory pattern
val moon = Moon.Factory().create(socket, logger, convertor)
moon.connect() // or socket.connect()

// Create API Interface object
val socketAPI = moon.create(SocketAPI::class.java)
// or shorter version
val socketAPI: SocketAPI = moon.create()
```

```kotlin
class ViewModel(
    private val socketAPI: SocketAPI
) {

    val events: Flow<SomeClass> = socketAPI.eventWithBody()

    fun singleEvent(body: SomeClass) {
        viewModelScope.launch {
            val eventResponse = socketAPI.singleEvent(body)
        }
    }
}
```

```kotlin
class ViewModel(
    private val moon: Moon,
) {
    /**
     * For listen socket state
     * @see <a href="https://github.com/MrAdkhambek/Moon/blob/f5dd034d25efcb5c066fa29ff4b7ca3d037890cf/moon/src/main/java/me/adkhambek/moon/Moon.kt#L193">Moon.Status</a>
     */
    val state: StateFlow<Moon.Status> get() = moon.state 
}
```

R8 / ProGuard
-------------

If you are using R8 the shrinking and obfuscation rules are included automatically.

ProGuard users must manually add the options from
[moon.pro][proguard file].



## TODO

- [x] Feature multiple convertor adapter
- [ ] Convertor adapter for acknowledgement
- [ ] Feature convert adapter for ByteArray
- [ ] Feature FILE

License
=======
    Copyright 2022 Adkhambek

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: https://search.maven.org/remote_content?g=me.adkhambek.moon&a=moon&v=LATEST
[snap]: https://s01.oss.sonatype.org/content/repositories/snapshots/
[proguard file]: https://github.com/MrAdkhambek/Moon/blob/main/moon/src/main/resources/META-INF/proguard/moon.pro
