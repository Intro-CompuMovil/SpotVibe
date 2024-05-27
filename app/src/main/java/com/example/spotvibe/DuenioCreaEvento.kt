package com.example.spotvibe

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID

class DuenioCreaEvento : AppCompatActivity() {
    private lateinit var fotoImageView: ImageView
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private val REQUEST_LOCATION_PICK = 3
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private var locationLatLng: Pair<Double, Double>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_duenio_crea_evento)

        fotoImageView = findViewById(R.id.imageView3)
        val nombreevento = findViewById<EditText>(R.id.textviewnombreevento)
        val autorevento = findViewById<EditText>(R.id.textviewautordetalles)
        val detallesevento = findViewById<EditText>(R.id.textviewdescripcion)
        val cantidadparticipevento = findViewById<EditText>(R.id.edittextcantidaddeparticipantes)
        val botoncrear = findViewById<Button>(R.id.botoninscribirsealevento)
        val localizacionevento = findViewById<ImageView>(R.id.imageViewlocation)

        // Inicializar Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference

        // Solicitar permisos si no están otorgados
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 0)
        }

        fotoImageView.setOnClickListener {
            val options = arrayOf<CharSequence>("Tomar Foto", "Elegir de la Galería", "Cancelar")
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Seleccionar Imagen")
            builder.setItems(options) { dialog, item ->
                when (options[item]) {
                    "Tomar Foto" -> {
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (takePictureIntent.resolveActivity(packageManager) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    }
                    "Elegir de la Galería" -> {
                        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK)
                    }
                    "Cancelar" -> {
                        dialog.dismiss()
                    }
                }
            }
            builder.show()
        }

        localizacionevento.setOnClickListener {
            val intent = Intent(this, MapaCreaEvento::class.java)
            startActivityForResult(intent, REQUEST_LOCATION_PICK)
        }

        botoncrear.setOnClickListener {
            val nombre = nombreevento.text.toString()
            val autor = autorevento.text.toString()
            val detalles = detallesevento.text.toString()
            val cantidadParticipantes = cantidadparticipevento.text.toString()

            if (imageUri != null && locationLatLng != null) {
                val locationAddress = "${locationLatLng!!.first},${locationLatLng!!.second}"
                uploadImageAndSaveEvent(nombre, autor, detalles, cantidadParticipantes, locationAddress)
            } else {
                Toast.makeText(this, "Por favor, selecciona una imagen y una ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val uri = getImageUri(imageBitmap)
                    imageUri = uri
                    fotoImageView.setImageURI(uri)
                }
                REQUEST_IMAGE_PICK -> {
                    val selectedImage: Uri? = data?.data
                    imageUri = selectedImage
                    fotoImageView.setImageURI(selectedImage)
                }
                REQUEST_LOCATION_PICK -> {
                    val selectedLat = data?.getDoubleExtra("selected_lat", 0.0)
                    val selectedLng = data?.getDoubleExtra("selected_lng", 0.0)
                    if (selectedLat != null && selectedLng != null) {
                        locationLatLng = Pair(selectedLat, selectedLng)
                        Toast.makeText(this, "Ubicación seleccionada: $selectedLat, $selectedLng", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getImageUri(inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    private fun uploadImageAndSaveEvent(nombre: String, autor: String, detalles: String, cantidadParticipantes: String, location: String) {
        val ref = storageReference.child("eventos/${UUID.randomUUID()}")
        imageUri?.let { uri ->
            ref.putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveEventToDatabase(nombre, autor, detalles, cantidadParticipantes, downloadUri.toString(), location)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveEventToDatabase(nombre: String, autor: String, detalles: String, cantidadParticipantes: String, imageUrl: String, location: String) {
       var emailcreador = intent.getStringExtra("user_email").toString()
        val evento = EventoInput(
            nombre = nombre,
            autor = autor,
            detalles = detalles,
            cantidadParticipantes = cantidadParticipantes,
            cantidadInscritos = 0,
            estado = "PENDIENTE",
            imagenUrl = imageUrl,
            localizacion = location,
            emailCreador = emailcreador
        )
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child("eventos").push()
        myRef.setValue(evento).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@DuenioCreaEvento, "Evento guardado en Firebase", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@DuenioCreaEvento, "Error al guardar el evento", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
