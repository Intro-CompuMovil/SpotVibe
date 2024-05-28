package com.example.spotvibe

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*

class DuenioCreaEvento : AppCompatActivity() {
    private lateinit var fotoImageView: ImageView
    private lateinit var fechaTextView: TextView
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private val REQUEST_LOCATION_PICK = 3
    private lateinit var storageReference: StorageReference
    private var imageUri: Uri? = null
    private var locationLatLng: Pair<Double, Double>? = null
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_duenio_crea_evento)

        val idCreador = intent.getStringExtra("user_id")
        if (idCreador.isNullOrEmpty()) {
            Toast.makeText(this, "Error: User ID not provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fotoImageView = findViewById(R.id.imageView3)
        fechaTextView = findViewById(R.id.edittextfechaa)
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

        fechaTextView.setOnClickListener {
            showDatePickerDialog()
        }

        botoncrear.setOnClickListener {
            val nombre = nombreevento.text.toString()
            val autor = autorevento.text.toString()
            val detalles = detallesevento.text.toString()
            val cantidadParticipantes = cantidadparticipevento.text.toString()
            if (imageUri != null && locationLatLng != null && !selectedDate.isNullOrEmpty()) {
                val locationAddress = "${locationLatLng!!.first},${locationLatLng!!.second}"
                uploadImageAndSaveEvent(nombre, autor, detalles, cantidadParticipantes, locationAddress, selectedDate!!, idCreador)
            } else {
                Toast.makeText(this, "Por favor, selecciona una imagen, una ubicación y una fecha", Toast.LENGTH_SHORT).show()
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

    private fun uploadImageAndSaveEvent(nombre: String, autor: String, detalles: String, cantidadParticipantes: String, location: String, selectedDate: String, idCreador: String) {
        val ref = storageReference.child("eventos/${UUID.randomUUID()}")
        imageUri?.let { uri ->
            ref.putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveEventToDatabase(nombre, autor, detalles, cantidadParticipantes, downloadUri.toString(), location, selectedDate, idCreador)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveEventToDatabase(nombre: String, autor: String, detalles: String, cantidadParticipantes: String, imageUrl: String, location: String, fecha: String, idCreador: String) {
        val evento = EventoInput(
            nombre = nombre,
            autor = autor,
            detalles = detalles,
            cantidadParticipantes = cantidadParticipantes,
            cantidadInscritos = 0,
            estado = "PENDIENTE",
            imagenUrl = imageUrl,
            localizacion = location,
            idCreador = idCreador, // Usar idCreador en lugar de emailCreador
            fecha = fecha
        )

        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference.child("eventos").push()
        myRef.setValue(evento).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@DuenioCreaEvento, "Evento guardado en Firebase", Toast.LENGTH_SHORT).show()

                // Crear y guardar la notificación
                val notificacionRef = database.reference.child("notificaciones").push()
                val notificacion = Notificacion(
                    userId = idCreador,
                    mensaje = "Tu evento '$nombre' ha sido creado exitosamente."
                )
                notificacionRef.setValue(notificacion).addOnCompleteListener { notiTask ->
                    if (notiTask.isSuccessful) {
                        // Redirigir al usuario a HomeDuenio si se creó correctamente
                        val intent = Intent(this@DuenioCreaEvento, HomeDuenio::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@DuenioCreaEvento, "Error al guardar la notificación en Firebase", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this@DuenioCreaEvento, "Error al guardar el evento en Firebase", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                fechaTextView.text = selectedDate
            },
            year, month, day
        )
        datePickerDialog.show()
    }
}
