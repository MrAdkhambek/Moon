# Moon
## Alpha version

This is library that look like [Scarlet](https://github.com/Tinder/Scarlet)
Wrapper [Socket.io](https://socket.io)

```kotlin
interface SocketAPI {

    @Event("single_event")
    suspend fun singleEvent(): SomeClass

    @Event("single_event_with_body")
    suspend fun singleEventWithBody(body: SomeClass): SomeClass

    @Event("event_with_body")
    fun eventWithBody(body: SomeClass): Flow<SomeClass>

    @Event("event")
    fun eventWithBody(): Flow<SomeClass>
}
```

```kotlin
val io : IO.socket = TODO()
val convertorAdapter = TODO()

// Create moon object with Builder pattern
val moon = Moon
    .Builder()
    .with(io)
    .convertor(convertorAdapter)
    .build(convertorAdapter)

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
    
    val events : Flow<SomeClass> = socketAPI
        .eventWithBody()
    
    fun singleEvent() {
        viewModelScope.launch {
            val eventResponse = socketAPI.singleEvent()
        }
    }
}
```




License
=======

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
