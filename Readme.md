Moon
-------------

A Retrofit inspired [Socket.io](https://socket.io) client for Kotlin (Android, JVM). </br>
For WebSocket [Scarlet](https://github.com/Tinder/Scarlet) </br>
> **⚠️   This library works only on Kotlin and Kotlin coroutines**

<p align="center">
<img src="./media/vecteezy_illustration-of-electric-plug-cartoon-sitting-on-the-half-moon_.jpg" width="60%" alt="logo">
</p>

Download
-------------

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation("me.adkhambek.moon:moon:alpha-0.0.1")
}
```

Convertors
-------------

```groovy
dependencies {
    implementation("me.adkhambek.moon:convertor-gson:alpha-0.0.1")                  // OPTIONAL
    implementation("me.adkhambek.moon:convertor-kotlin-serialization:alpha-0.0.1")  // OPTIONAL
}
```

Usage
-------------

```kotlin
interface SocketAPI {

    @Event("single_event_with_body")
    suspend fun singleEventWithBody(body: SomeClass): SomeClass

    @Event("event_as_listener")
    fun eventWithBody(): Flow<SomeClass>
}
```

```kotlin
val io: IO.socket = TODO()
val convertorAdapter = TODO()

// Create moon object with Builder pattern
val moon = Moon
    .Builder()
    .with(io)
    .convertor(convertorAdapter)
    .build()

// Create moon object with Factory pattern
val moon = Moon
    .Factory()
    .create(socket, convertorAdapter)


val socketAPI = moon.create(SocketAPI::class.java)
```

```kotlin
class ViewModel(
    private val socketAPI: SocketAPI
) {

    val events: Flow<SomeClass> = socketAPI
        .eventWithBody()

    fun singleEvent(body: SomeClass) {
        viewModelScope.launch {
            val eventResponse = socketAPI.singleEvent(body)
        }
    }
}
```

R8 / ProGuard
-------------

If you are using R8 the shrinking and obfuscation rules are included automatically.

ProGuard users must manually add the options from
[moon.pro][proguard file].



## TODO

- [ ] Feature multiple convertor adapter
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
