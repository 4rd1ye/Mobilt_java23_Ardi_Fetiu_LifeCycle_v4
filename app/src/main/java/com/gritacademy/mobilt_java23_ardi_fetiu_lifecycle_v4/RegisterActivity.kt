package com.gritacademy.mobilt_java23_ardi_fetiu_lifecycle_v4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var registerButton: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Find inputs from the layout
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        usernameInput = findViewById(R.id.usernameInput)
        registerButton = findViewById(R.id.registerButton)

        // Initialize Firebase Auth and Database
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        // Register button click listener
        registerButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val username = usernameInput.text.toString()

            // Validate input before proceeding
            if (validateInput(email, password, username)) {
                // Create a new user in Firebase Auth
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = firebaseAuth.currentUser!!.uid
                            val userMap = mapOf(
                                "username" to username,
                                "email" to email
                            )

                            // Store the user data in Firebase Realtime Database
                            database.child(userId).setValue(userMap).addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    // Show a toast notification that account creation was successful
                                    Toast.makeText(this, "Konto skapat framgångsrikt!", Toast.LENGTH_SHORT).show()

                                    // Navigate to the LoginActivity after successful registration
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish() // Close the registration activity
                                } else {
                                    // Show an error if saving user data fails
                                    Toast.makeText(this, "Fel vid sparning: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            // Show an error if account creation fails
                            Toast.makeText(this, "Registrering misslyckades: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Ogiltiga inmatningsdata. Kontrollera dina fält.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Validate input for email, password, and username
    private fun validateInput(email: String, password: String, username: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(Regex(emailPattern)) && password.length >= 6 && username.isNotEmpty()
    }
}
