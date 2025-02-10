package `is`.hbv601.hugbunadarverk2.model

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val user: User?
)