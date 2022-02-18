package req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginScreen(
    @SerialName("image")
    val image: String
)