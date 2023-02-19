package www.iesmurgi.u9_proyprofesoressqlite

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.drawToBitmap
import www.iesmurgi.u9_proyprofesoressqlite.databinding.ActivityAddUpdateBinding
import java.io.ByteArrayOutputStream

class AddUpdateActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddUpdateBinding
    var nombre=""
    var asignatura=""
    var email=""
    var id: Int? = null
    var imagen: Bitmap? = null
    lateinit var conexion: BaseDatosProfes
    var editar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //faltaba el binding y el setContentview
        binding = ActivityAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //
        conexion= BaseDatosProfes(this)
        cogerDatos()
        setListeners()
    }
    private fun cogerDatos() {
        val datos = intent.extras
        if(datos!=null){
            editar= true
            binding.btnCrear.text="EDITAR"
            val usuario = datos.getSerializable("USUARIO") as Usuarios
            imagen = usuario.imagen?.let { BitmapFactory.decodeByteArray(usuario.imagen, 0, it.size) }
            id=usuario.id
            binding.etNombre.setText(usuario.nombre)
            binding.etAsignatura.setText(usuario.asig)//No esta en el esqueleto
            binding.etEmail.setText(usuario.email)
            binding.ivAddUpdate.setImageBitmap(imagen)
        }
    }
    private fun setListeners() {
        binding.btnVolver.setOnClickListener {
            finish()
        }
        binding.btnCrear.setOnClickListener {
            crearRegistro()
        }
        binding.btnFoto.setOnClickListener {
            abrirFoto()
        }
    }

    private val PICK_IMAGE_REQUEST_GALERIA = 20
    private val PICK_IMAGE_REQUEST_CAMERA = 75

    private fun abrirFoto() {

        val options = listOf(
            CustomOption("Tomar foto", R.drawable.camara),
            CustomOption("Elegir de la galería", R.drawable.galeria)
        )
        val adapter = object : ArrayAdapter<CustomOption>(this, R.layout.custom_icon_esqueleto, options) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_icon_esqueleto, parent, false)
                val option = getItem(position)
                val iconView = view.findViewById<ImageView>(R.id.option_icon)
                val nameView = view.findViewById<TextView>(R.id.option_name)
                option?.let {
                    iconView.setImageResource(it.iconRes)
                    nameView.text = it.name
                }
                return view
            }
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Elige una opción")
        builder.setAdapter(adapter) { _, item ->
            when {
                options[item].name == "Tomar foto" -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, PICK_IMAGE_REQUEST_CAMERA)
                }
                options[item].name == "Elegir de la galería" -> {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, PICK_IMAGE_REQUEST_GALERIA)
                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == PICK_IMAGE_REQUEST_GALERIA && resultCode == Activity.RESULT_OK && data != null -> {
                val imagenUri = data.data
                val stream = imagenUri?.let { contentResolver.openInputStream(it) }
                val datos = stream?.readBytes() ?: return
                imagen = BitmapFactory.decodeByteArray(datos, 0, datos.size)
                binding.ivAddUpdate.setImageBitmap(imagen)
            }
            requestCode == PICK_IMAGE_REQUEST_CAMERA && resultCode == Activity.RESULT_OK && data != null -> {
                val imagen = data.extras?.get("data") as Bitmap
                binding.ivAddUpdate.setImageBitmap(imagen)
            }
        }
    }

    private fun crearRegistro() {
        nombre=binding.etNombre.text.toString().trim()
        email=binding.etEmail.text.toString().trim()
        //falta la zona de asignatura
        asignatura = binding.etAsignatura.text.toString().trim()
        imagen = binding.ivAddUpdate.drawToBitmap()
        if(nombre.length<3){
            binding.etNombre.setError("El campo nombre debe tener al menos 3 caracteres")
            return
        }
        if(email.length<6){
            binding.etEmail.setError("El campo email debe tener al menos 6 caracteres")
            binding.etEmail.requestFocus()
            return
        }
        //el email no esta duplicado
        if(conexion.existeEmail(email, id)){
            binding.etEmail.setError("El email YA está registrado.")
            binding.etEmail.requestFocus()
            return
        }
        val stream = ByteArrayOutputStream()
        imagen?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        if(!editar){
            val usuario=Usuarios(1, nombre,asignatura,email,byteArray)
            if(conexion.crear(usuario)>-1){
                finish()
            }
            else{
                Toast.makeText(this, "NO se pudo guardar el registro!!!", Toast.LENGTH_SHORT).show()
            }
        }else{
            val usuario=Usuarios(id, nombre,asignatura, email,byteArray)
            if(conexion.update(usuario)>-1){
                finish()
            }
            else{
                Toast.makeText(this, "NO se pudo editar el registro!!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
