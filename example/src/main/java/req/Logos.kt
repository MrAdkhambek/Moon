package req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Logos(
    @SerialName("landing_logo")
    val landingLogo: String,
    @SerialName("login_logo")
    val loginLogo: String
)