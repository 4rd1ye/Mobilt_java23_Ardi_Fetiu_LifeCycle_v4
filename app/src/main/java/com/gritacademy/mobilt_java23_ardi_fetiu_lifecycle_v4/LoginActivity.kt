package com.gritacademy.mobilt_java23_ardi_fetiu_lifecycle_v4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        firebaseAuth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // Validate input fields
            if (email.isEmpty()) {
                Toast.makeText(this, "Vänligen ange en giltig e-postadress.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Vänligen ange ett lösenord.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase Authentication
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Save login with SharedPreferences
                        val sharedPrefs = getSharedPreferences("userPrefs", MODE_PRIVATE)
                        sharedPrefs.edit().putBoolean("isLoggedIn", true).apply()

                        // Go to ProfileActivity
                        startActivity(Intent(this, ProfileActivity::class.java))
                        finish() // Closes LoginActivity
                    } else {
                        Toast.makeText(this, "Inloggningen misslyckades: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        registerButton.setOnClickListener {
            // Go to RegistrationActivity
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}
