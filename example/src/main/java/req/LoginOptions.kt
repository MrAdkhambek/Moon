package req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginOptions(
    @SerialName("additional_settings")
    val additionalSettings: AdditionalSettings,
    @SerialName("apple_login_signup")
    val appleLoginSignup: Boolean,
    @SerialName("custom_login")
    val customLogin: String,
    @SerialName("email_login_signup")
    val emailLoginSignup: Boolean,
    @SerialName("facebook_login_signup")
    val facebookLoginSignup: Boolean,
    @SerialName("google_login_signup")
    val googleLoginSignup: Boolean,
    @SerialName("required_login_info")
    val requiredLoginInfo: RequiredLoginInfo,
    @SerialName("sms_login_signup")
    val smsLoginSignup: Boolean
)