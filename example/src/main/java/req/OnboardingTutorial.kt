package req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OnboardingTutorial(
    @SerialName("turn_on")
    val turnOn: Boolean,
    @SerialName("tutorial_button_and_swiping_color")
    val tutorialButtonAndSwipingColor: String,
    @SerialName("tutorial_pages")
    val tutorialPages: List<String>
)