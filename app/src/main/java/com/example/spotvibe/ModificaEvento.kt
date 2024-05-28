package com.example.spotvibe

import android.app.DatePickerDialog
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ModificaEvento : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private var eventId: String? = null
    private var originalFotoUrl: String? = null

    private lateinit var nombreevento: EditText
    private lateinit var autorevento: EditText
    private lateinit var descripcionevento: EditText
    private lateinit var cantidadparticipantes: EditText
    private lateinit var imagenevento: ImageView
    private lateinit var fechaevento: EditText

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifica_evento)

        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance().reference

        nombreevento = findViewById(R.id.edittextNombreEvento)
        autorevento = findViewById(R.id.edittextAutorEvento)
        descripcionevento = findViewById(R.id.edittextDescripcionEvento)
        cantidadparticipantes = findViewById(R.id.edittextCantidadTotalParticipantes)
        imagenevento = findViewById(R.id.imageVieweventos)
        fechaevento = findViewById(R.id.edittextFechaEvento)
        val botonmodificar = findViewById<Button>(R.id.botoncambiarevento)
        val botonelimimnar = findViewById<Button>(R.id.botoneliminarevento)

        // Get extras from the intent
        eventId = intent.getStringExtra("eventId")
        val nombreEvento = intent.getStringExtra("nombreEvento")
        val autorEvento = intent.getStringExtra("autorEvento")
        val descripcionEvento = intent.getStringExtra("detalleEvento")
        val cantidadParticipantes = intent.getStringExtra("cantidadParticipantes")
        val fotoEvento = intent.getStringExtra("fotoEvento")
        val fechaEvento = intent.getStringExtra("fechaEvento")
        originalFotoUrl = fotoEvento

        // Set the values to the views
        nombreevento.setText(nombreEvento)
        autorevento.setText(autorEvento)
        descripcionevento.setText(descripcionEvento)
        cantidadparticipantes.setText(cantidadParticipantes)
        fechaevento.setText(fechaEvento)

        // Load the image using Glide
        Glide.with(this)
            .load(fotoEvento)
            .placeholder(R.drawable.ic_camera) // Use a placeholder image
            .into(imagenevento)

        // Add click listener to the image view to select a new image
        imagenevento.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Add click listener to the date field to open DatePickerDialog
        fechaevento.setOnClickListener {
            showDatePickerDialog()
        }

        botonmodificar.setOnClickListener {
            updateEvent()
        }

        botonelimimnar.setOnClickListener {
            deleteEvent()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                fechaevento.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imagenevento.setImageURI(imageUri)
        }
    }

    private fun updateEvent() {
        val updatedNombre = nombreevento.text.toString()
        val updatedAutor = autorevento.text.toString()
        val updatedDescripcion = descripcionevento.text.toString()
        val updatedCantidadParticipantes = cantidadparticipantes.text.toString()
        val updatedFecha = fechaevento.text.toString()

        val eventUpdates = hashMapOf<String, Any?>()

        if (updatedNombre != intent.getStringExtra("nombreEvento")) {
            eventUpdates["nombre"] = updatedNombre
        }
        if (updatedAutor != intent.getStringExtra("autorEvento")) {
            eventUpdates["autor"] = updatedAutor
        }
        if (updatedDescripcion != intent.getStringExtra("detalleEvento")) {
            eventUpdates["detalles"] = updatedDescripcion
        }
        if (updatedCantidadParticipantes != intent.getStringExtra("cantidadParticipantes")) {
            eventUpdates["cantidadParticipantes"] = updatedCantidadParticipantes
        }
        if (updatedFecha != intent.getStringExtra("fechaEvento")) {
            eventUpdates["fechaEvento"] = updatedFecha
        }

        if (imageUri != null) {
            uploadImage { imageUrl ->
                eventUpdates["imagenUrl"] = imageUrl
                applyUpdates(eventUpdates)
            }
        } else {
            applyUpdates(eventUpdates)
        }
    }

    private fun applyUpdates(eventUpdates: Map<String, Any?>) {
        eventId?.let {
            database.child("eventos").child(it).updateChildren(eventUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to update event", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadImage(callback: (String) -> Unit) {
        val storageRef = storage.child("eventos/${UUID.randomUUID()}.jpg")
        imageUri?.let { uri ->
            storageRef.putFile(uri).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { url ->
                    callback(url.toString())
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteEvent() {
        eventId?.let {
            database.child("eventos").child(it).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity
                } else {
                    Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
