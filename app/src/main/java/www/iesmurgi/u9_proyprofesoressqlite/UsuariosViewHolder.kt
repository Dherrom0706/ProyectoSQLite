package www.iesmurgi.u9_proyprofesoressqlite

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import www.iesmurgi.u9_proyprofesoressqlite.databinding.UsuariosEsqueletoBinding

class UsuariosViewHolder (vista: View):RecyclerView.ViewHolder(vista){
  //  private val miBinding=UsuariosLayoutBinding.bind(vista)
    private val miBinding=UsuariosEsqueletoBinding.bind(vista)
    var imagen: Bitmap? = null
    fun inflar(profesor:Usuarios,
        onItemDelete:(Int)->Unit,
        onItemUpdate:(Usuarios)->Unit)
    {
        miBinding.tvId.text=profesor.id.toString()
        miBinding.tvNombre.text=profesor.nombre
        miBinding.tvAsignatura.text=profesor.asig
        miBinding.tvEmail.text=profesor.email
        println("Hola, estoy en viewHolder ${profesor.imagen}")
        imagen = profesor.imagen?.let { BitmapFactory.decodeByteArray(profesor.imagen, 0, it.size) }
        miBinding.ivPerfil.setImageBitmap(imagen)
        miBinding.btnBorrar.setOnClickListener{
        onItemDelete(adapterPosition)
        }
        itemView.setOnClickListener { onItemUpdate(profesor) }
    }


}