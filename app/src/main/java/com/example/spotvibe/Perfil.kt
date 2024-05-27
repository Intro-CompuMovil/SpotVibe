package com.example.spotvibe

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class Perfil : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var profileImageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var numberEditText: EditText
    private lateinit var cedulaEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var logoutButton: Button

    private var currentName: String? = null
    private var currentEmail: String? = null
    private var currentUsername: String? = null
    private var currentNumber: String? = null
    private var currentCedula: String? = null
    private var currentProfileImageUrl: String? = null

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference

        profileImageView = findViewById(R.id.img)
        nameEditText = findViewById(R.id.fullnameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        usernameEditText = findViewById(R.id.usernameEditText)
        numberEditText = findViewById(R.id.numberEditText)
        cedulaEditText = findViewById(R.id.cedulaEditText)
        saveButton = findViewById(R.id.saveButton)
        logoutButton = findViewById(R.id.logoutButton)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = database.child("users").child(userId)

            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        currentName = snapshot.child("name").value.toString()
                        currentEmail = snapshot.child("email").value.toString()
                        currentUsername = snapshot.child("username").value.toString()
                        currentNumber = snapshot.child("number").value.toString()
                        currentCedula = snapshot.child("cedula").value.toString()
                        currentProfileImageUrl = snapshot.child("profileImageUrl").value.toString()

                        nameEditText.setText(currentName)
                        emailEditText.setText(currentEmail)
                        usernameEditText.setText(currentUsername)
                        numberEditText.setText(currentNumber)
                        cedulaEditText.setText(currentCedula)
                        if (currentProfileImageUrl?.isNotEmpty() == true) {
                            Glide.with(this@Perfil).load(currentProfileImageUrl).into(profileImageView)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors.
                }
            })
        }

        profileImageView.setOnClickListener {
            showImagePickerDialog()
        }

        saveButton.setOnClickListener {
            saveUserData()
        }

        logoutButton.setOnClickListener {
            logoutUser()
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose your profile picture")
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Take Photo" -> dispatchTakePictureIntent()
                "Choose from Gallery" -> dispatchPickPictureIntent()
                "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Camera app not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dispatchPickPictureIntent() {
        val pickPictureIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPictureIntent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    profileImageView.setImageBitmap(imageBitmap)
                    uploadImageToFirebase(imageBitmap)
                }
                REQUEST_IMAGE_PICK -> {
                    val imageUri: Uri? = data?.data
                    imageUri?.let {
                        profileImageView.setImageURI(it)
                        val imageBitmap = (profileImageView.drawable as BitmapDrawable).bitmap
                        uploadImageToFirebase(imageBitmap)
                    }
                }
            }
        }
    }

    private fun uploadImageToFirebase(bitmap: Bitmap) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
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
                                Toast.makeText(baseContext, "Profile image updated successfully.", Toast.LENGTH_SHORT).show()
                                currentProfileImageUrl = imageUrl
                            } else {
                                Toast.makeText(baseContext, "Failed to save profile image URL.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }

    private fun saveUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = database.child("users").child(userId)

            val updatedUser = mutableMapOf<String, Any>()

            val newName = nameEditText.text.toString().trim()
            if (newName != currentName) {
                updatedUser["name"] = newName
            }

            val newEmail = emailEditText.text.toString().trim()
            if (newEmail != currentEmail) {
                updatedUser["email"] = newEmail
            }

            val newUsername = usernameEditText.text.toString().trim()
            if (newUsername != currentUsername) {
                updatedUser["username"] = newUsername
            }

            val newNumber = numberEditText.text.toString().trim()
            if (newNumber != currentNumber) {
                updatedUser["number"] = newNumber
            }

            val newCedula = cedulaEditText.text.toString().trim()
            if (newCedula != currentCedula) {
                updatedUser["cedula"] = newCedula
            }

            if (updatedUser.isNotEmpty()) {
                userRef.updateChildren(updatedUser).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show()
                        // Update current values
                        currentName = newName
                        currentEmail = newEmail
                        currentUsername = newUsername
                        currentNumber = newNumber
                        currentCedula = newCedula
                    } else {
                        Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun logoutUser() {
        auth.signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
