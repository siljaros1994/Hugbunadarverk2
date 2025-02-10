package `is`.hbv601.hugbunadarverk2.model

data class RegisterRequest(
    val fullname: String,
    val username: String,
    val password: String,
    val email: String,
    val usertype: String
)