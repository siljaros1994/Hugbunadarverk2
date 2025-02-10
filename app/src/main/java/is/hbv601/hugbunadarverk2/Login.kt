package `is`.hbv601.hugbunadarverk2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import `is`.hbv601.hugbunadarverk2.model.LoginRequest
import `is`.hbv601.hugbunadarverk2.model.LoginResponse
import `is`.hbv601.hugbunadarverk2.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var messageTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Here we initialize the views
        emailEditText = findViewById(R.id.login_email)
        passwordEditText = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        registerLink = findViewById(R.id.login_register_link)
        messageTextView = findViewById(R.id.message_text_view)

        // Here we set the click listeners
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            // Here we call the login function
            login(email, password)
        }

        registerLink.setOnClickListener {
            // Navigate to the registration activity
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }
    }

    private fun login(email: String, password: String) {
        val call = ApiClient.apiService.loginUser(email, password)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        if (!loginResponse.error) {
                            // Login successful
                            val user = loginResponse.user
                            val message = loginResponse.message
                            runOnUiThread {
                                Toast.makeText(this@Login, message, Toast.LENGTH_SHORT).show()
                                showMessage(message)
                                Log.d("LoginActivity", "User ID: ${user?.id}, Username: ${user?.username}, Email: ${user?.email}")

                                // Here we navigate to different home pages based on the user type
                                val intent = when (user?.usertype) {
                                    "Donor" -> {
                                        Log.d("LoginActivity", "Navigating to DonorHomeActivity") //An Log to debug the home pages

                                        Intent(this@Login, DonorHomeActivity::class.java)
                                    }
                                    "Recipient" -> {
                                        Log.d("LoginActivity", "Navigating to RecipientHomeActivity") //An log to debug the home pages
                                        Intent(this@Login, RecipientHomeActivity::class.java)
                                    }
                                    else -> {
                                        Log.e("LoginActivity", "Unknown user type: ${user?.usertype}")
                                        null
                                    }
                                }

                                if (intent != null) {
                                    startActivity(intent)
                                    finish() // Closes the login activity
                                } else {
                                    // Handle cases where intent is null, like unknown user type.
                                    Toast.makeText(this@Login, "Unknown user type", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            // here we have if login as failed
                            val message = loginResponse.message
                            runOnUiThread {
                                Toast.makeText(this@Login, message, Toast.LENGTH_SHORT).show()
                                showMessage(message)
                            }
                            Log.e("LoginActivity", "Login failed: $message")
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@Login, "Login failed: Empty response", Toast.LENGTH_SHORT).show()
                            showMessage("Login failed: Empty response")
                        }
                        Log.e("LoginActivity", "Login failed: Empty response")
                    }
                } else {
                    val message = response.message()
                    runOnUiThread {
                        Toast.makeText(this@Login, "Login failed: $message", Toast.LENGTH_SHORT).show()
                        showMessage("Login failed: $message")
                    }
                    Log.e("LoginActivity", "Login failed: $message")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // Handle for network errors
                val errorMessage = t.message ?: "Network error"
                runOnUiThread {
                    Toast.makeText(this@Login, "Login failed: $errorMessage", Toast.LENGTH_SHORT).show()
                    showMessage("Login failed: $errorMessage")
                }
                Log.e("LoginActivity", "Login failed: $errorMessage", t)
            }
        })
    }

    private fun showMessage(message: String) {
        messageTextView.text = message
        messageTextView.visibility = View.VISIBLE
    }
}