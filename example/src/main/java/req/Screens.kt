package req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Screens(
    @SerialName("landing_screen")
    val landingScreen: LandingScreen,
    @SerialName("loading_screen")
    val loadingScreen: LoadingScreen,
    @SerialName("login_screen")
    val loginScreen: LoginScreen
)