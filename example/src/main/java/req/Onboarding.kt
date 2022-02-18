package req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Onboarding(

    @SerialName("allow_guest_access_preview")
    val allowGuestAccessPreview: AllowGuestAccessPreview,

    @SerialName("app_default_feature")
    val appDefaultFeature: String,

    @SerialName("avatars")
    val avatars: Avatars,

    @SerialName("branding")
    val branding: Branding,

    @SerialName("login_options")
    val loginOptions: LoginOptions,

    @SerialName("onboarding_tutorial")
    val onboardingTutorial: OnboardingTutorial,

    @SerialName("privacy_policy")
    val privacyPolicy: String,

    @SerialName("setting_name")
    val settingName: String,

    @SerialName("version")
    val version: Int
)