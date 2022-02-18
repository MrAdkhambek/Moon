package req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequiredLoginInfo(
    @SerialName("email_required")
    val emailRequired: Boolean,
    @SerialName("sms_required")
    val smsRequired: Boolean
)