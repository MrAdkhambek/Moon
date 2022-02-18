package req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AllowGuestAccessPreview(
    @SerialName("mobile")
    val mobile: Boolean,
    @SerialName("web")
    val web: Boolean
)