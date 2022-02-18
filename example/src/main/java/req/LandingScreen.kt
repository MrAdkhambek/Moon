package req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LandingScreen(
    @SerialName("button_primary_color")
    val buttonPrimaryColor: String,
    @SerialName("button_secondary_color")
    val buttonSecondaryColor: String,
    @SerialName("button_text_primary_color")
    val buttonTextPrimaryColor: String,
    @SerialName("button_text_secondary_color")
    val buttonTextSecondaryColor: String,
    @SerialName("image")
    val image: String,
    @SerialName("primary_color")
    val primaryColor: String,
    @SerialName("secondary_color")
    val secondaryColor: String
)