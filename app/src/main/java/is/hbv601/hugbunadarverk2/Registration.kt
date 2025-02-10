package `is`.hbv601.hugbunadarverk2

import `is`.hbv601.hugbunadarverk2.model.RegisterRequest
import `is`.hbv601.hugbunadarverk2.model.RegisterResponse
import `is`.hbv601.hugbunadarverk2.network.ApiClient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Registration : AppCompatActivity() {

    private lateinit var fullnameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var userTypeSpinner: Spinner
    private lateinit var registerButton: Button
    private lateinit var loginLink: TextView
    private lateinit var messageTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Here we initialize the views
        fullnameEditText = findViewById(R.id.register_fullname)
        usernameEditText = findViewById(R.id.register_username)
        emailEditText = findViewById(R.id.register_email)
        passwordEditText = findViewById(R.id.register_password)
        confirmPasswordEditText = findViewById(R.id.register_confirm_password)
        userTypeSpinner = findViewById(R.id.register_user_type)
        registerButton = findViewById(R.id.register_button)
        loginLink = findViewById(R.id.register_login_link)
        messageTextView = findViewById(R.id.message_text_view)

        // Set up the user type for the register page
        val userTypes = arrayOf("", "Donor", "Recipient")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, userTypes)
        userTypeSpinner.adapter = adapter

        // Here we set the click listeners
        registerButton.setOnClickListener {
            val fullname = fullnameEditText.text.toString()
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val userType = userTypeSpinner.selectedItem.toString()

            //Here we check if password and confirm password are the same.
            if (password == confirmPassword) {
                register(fullname,username, password, email, userType)
            } else {
                showMessage("Passwords do not match")
                Toast.makeText(this@Registration, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }

        loginLink.setOnClickListener {
            // Navigate to the login activity
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    private fun register(fullname: String, username: String, password: String, email: String, userType: String) {
        Log.d("RegistrationActivity", "Fullname: $fullname")
        Log.d("RegistrationActivity", "Username: $username")
        Log.d("RegistrationActivity", "Password: $password")
        Log.d("RegistrationActivity", "Email: $email")
        Log.d("RegistrationActivity", "UserType: $userType")

        val call = ApiClient.apiService.registerUser(fullname, username, password, email, userType)

        Log.d("RegistrationActivity", "Request URL: ${call.request().url}")
        Log.d("RegistrationActivity", "Request Body: ${call.request().body}")

        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null) {
                        if (!registerResponse.error) {
                            // Registration is successful
                            val message = registerResponse.message
                            runOnUiThread {
                                Toast.makeText(this@Registration, message, Toast.LENGTH_SHORT).show()
                                showMessage(message)
                                // Here we clear the input fields.
                                fullnameEditText.text.clear()
                                usernameEditText.text.clear()
                                emailEditText.text.clear()
                                passwordEditText.text.clear()
                                confirmPasswordEditText.text.clear()
                                userTypeSpinner.setSelection(0)
                            }
                            // Here we navigate to the login activity
                            val intent = Intent(this@Registration, Login::class.java)
                            startActivity(intent)
                        } else {
                            // Registration has failed
                            val message = registerResponse.message
                            runOnUiThread {
                                Toast.makeText(this@Registration, message, Toast.LENGTH_SHORT).show()
                                showMessage(message)
                            }
                            Log.e("RegistrationActivity", "Registration failed: $message")
                        }
                    } else {
                        // Handle for null response
                        runOnUiThread {
                            Toast.makeText(this@Registration, "Registration failed: Empty response", Toast.LENGTH_SHORT).show()
                            showMessage("Registration failed: Empty response")
                        }
                        Log.e("RegistrationActivity", "Registration failed: Empty response")
                    }
                } else {
                    // Handle for unsuccessful HTTP responses.
                    val message = response.message()
                    runOnUiThread {
                        Toast.makeText(this@Registration, "Registration failed: $message", Toast.LENGTH_SHORT).show()
                        showMessage("Registration failed: $message")
                    }
                    Log.e("RegistrationActivity", "Registration failed: $message")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                // Handle for network errors
                val errorMessage = t.message ?: "Network error"
                runOnUiThread {
                    Toast.makeText(this@Registration, "Registration failed: $errorMessage", Toast.LENGTH_SHORT).show()
                    showMessage("Registration failed: $errorMessage")
                }
                Log.e("RegistrationActivity", "Registration failed: $errorMessage", t)
            }
        })
    }

    private fun showMessage(message: String) {
        messageTextView.text = message
        messageTextView.visibility = View.VISIBLE
    }
}