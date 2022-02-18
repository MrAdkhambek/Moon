package req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Branding(
    @SerialName("logos")
    val logos: Logos,
    @SerialName("screens")
    val screens: Screens
)