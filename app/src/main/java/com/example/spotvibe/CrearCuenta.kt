package com.example.spotvibe

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class CrearCuenta : AppCompatActivity() {
    private lateinit var takePictureImageView: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var nameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var numberInput: EditText
    private lateinit var cedulaInput: EditText
    private lateinit var accountTypeRadioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference

        takePictureImageView = findViewById(R.id.imageViewPhoto)
        emailInput = findViewById(R.id.emailEditText)
        passwordInput = findViewById(R.id.passwordEditText)
        nameInput = findViewById(R.id.fullnameEditText)
        lastNameInput = findViewById(R.id.usernameEditText)
        usernameInput = findViewById(R.id.usernameEditText)
        numberInput = findViewById(R.id.numberEditText)
        cedulaInput = findViewById(R.id.cedulaEditText)
        accountTypeRadioGroup = findViewById(R.id.accountTypeRadioGroup)

        val btn = findViewById<Button>(R.id.createAccountButton)
        btn.setOnClickListener {
            if (validateInputs(emailInput, passwordInput)) {
                createAccount(emailInput.text.toString(), passwordInput.text.toString())
            }
        }

        takePictureImageView.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            } else {
                setupImageView()
            }
        }
    }

    private fun validateInputs(email: EditText, password: EditText): Boolean {
        val emailText = email.text.toString().trim()
        val passwordText = password.text.toString().trim()

        if (emailText.isEmpty()) {
            email.error = "Email is required"
            email.requestFocus()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.error = "Please enter a valid email"
            email.requestFocus()
            return false
        }

        if (passwordText.isEmpty()) {
            password.error = "Password is required"
            password.requestFocus()
            return false
        }

        if (passwordText.length < 6) {
            password.error = "Password must be at least 6 characters long"
            password.requestFocus()
            return false
        }

        return true
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        uploadUserData(it)
                    }
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun uploadUserData(user: FirebaseUser) {
        val userId = user.uid
        val email = user.email ?: ""
        val name = nameInput.text.toString().trim()
        val lastName = lastNameInput.text.toString().trim()
        val username = usernameInput.text.toString().trim()
        val number = numberInput.text.toString().trim()
        val cedula = cedulaInput.text.toString().trim()

        // Get selected role
        val selectedRoleId = accountTypeRadioGroup.checkedRadioButtonId
        val role = if (selectedRoleId == R.id.personRadioButton) "Persona" else "Dueño"

        val userMap = User(
            name = name,
            lastName = lastName,
            username = username,
            number = number,
            cedula = cedula,
            email = email,
            rol = role,
            profileImageUrl = ""
        )

        database.child("users").child(userId).setValue(userMap)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    uploadProfileImage(userId)
                } else {
                    Toast.makeText(baseContext, "Failed to save user data.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun uploadProfileImage(userId: String) {
        takePictureImageView.isDrawingCacheEnabled = true
        takePictureImageView.buildDrawingCache()
        val bitmap = (takePictureImageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val profileImageRef = storageReference.child("profileImages/$userId.jpg")
        val uploadTask = profileImageRef.putBytes(data)

        uploadTask.addOnFailureListener {
            Toast.makeText(baseContext, "Failed to upload profile image.", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                database.child("users").child(userId).child("profileImageUrl").setValue(imageUrl)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(baseContext, "User account created successfully.", Toast.LENGTH_SHORT).show()
                            // Redirect to another activity if needed
                        } else {
                            Toast.makeText(baseContext, "Failed to save profile image URL.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun setupImageView() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Camera app not available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    takePictureImageView.setImageBitmap(imageBitmap)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    setupImageView()
                } else {
                    Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_CAMERA_PERMISSION = 2
    }
}
