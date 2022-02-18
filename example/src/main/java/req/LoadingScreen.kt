package req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoadingScreen(
    @SerialName("image")
    val image: String
)