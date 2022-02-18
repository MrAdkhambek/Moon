package req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdditionalSettings(
    @SerialName("enable_referred_by")
    val enableReferredBy: Boolean,
    @SerialName("required_location_hometown")
    val requiredLocationHometown: Boolean
)