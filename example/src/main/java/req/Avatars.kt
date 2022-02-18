package req


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Avatars(
    @SerialName("allow_avatars")
    val allowAvatars: Boolean,
    @SerialName("pre-set_avatars")
    val preSetAvatars: List<String>
)