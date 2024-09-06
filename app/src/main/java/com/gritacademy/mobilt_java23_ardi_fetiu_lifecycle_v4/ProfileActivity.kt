package com.gritacademy.mobilt_java23_ardi_fetiu_lifecycle_v4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var ageEditText: EditText
    private lateinit var licenseCheckBox: CheckBox
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText 
    private lateinit var submitButton: Button
    private lateinit var logoutButton: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userDocumentRef: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Match ID from XML layout
        ageEditText = findViewById(R.id.ageInput)
        licenseCheckBox = findViewById(R.id.licenseCheckbox)
        genderRadioGroup = findViewById(R.id.genderGroup)
        emailEditText = findViewById(R.id.emailInput)
        phoneEditText = findViewById(R.id.phoneInput) 
        submitButton = findViewById(R.id.saveButton)
        logoutButton = findViewById(R.id.logoutButton)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val userId = firebaseAuth.currentUser!!.uid
        userDocumentRef = firestore.collection("Users").document(userId)

        // Load user data from Firestore
        loadUserData()

        // Save profile when user clicks "Spara Profil"
        submitButton.setOnClickListener {
            val age = ageEditText.text.toString()
            val hasLicense = licenseCheckBox.isChecked
            val selectedGenderId = genderRadioGroup.checkedRadioButtonId
            val email = emailEditText.text.toString()
            val phone = phoneEditText.text.toString()

            // Check if user selected a gender
            if (selectedGenderId != -1) {
                val selectedGender = findViewById<RadioButton>(selectedGenderId).text.toString()

                // Create a profile map
                val userProfile = mapOf(
                    "age" to age,
                    "hasLicense" to hasLicense,
                    "gender" to selectedGender,
                    "email" to email,
                    "phone" to phone // Add phone to profile data
                )

                // Update user's profile in Firestore
                userDocumentRef.set(userProfile).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Profil sparad", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Sparning misslyckades: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Välj kön", Toast.LENGTH_SHORT).show()
            }
        }

        // Log out when user clicks "Logga ut"
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Load user's data from Firestore
    private fun loadUserData() {
        userDocumentRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val data = document.data
                ageEditText.setText(data?.get("age")?.toString() ?: "")
                licenseCheckBox.isChecked = (data?.get("hasLicense") as? Boolean) ?: false
                val gender = data?.get("gender")?.toString()
                if (gender == "Man") {
                    genderRadioGroup.check(R.id.genderMale)
                } else if (gender == "Kvinna") {
                    genderRadioGroup.check(R.id.genderFemale)
                }
                emailEditText.setText(data?.get("email")?.toString() ?: "")
                phoneEditText.setText(data?.get("phone")?.toString() ?: "")
            }
        }
    }
}
