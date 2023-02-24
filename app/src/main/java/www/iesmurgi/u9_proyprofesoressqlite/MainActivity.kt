package www.iesmurgi.u9_proyprofesoressqlite

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import www.iesmurgi.u9_proyprofesoressqlite.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding
    lateinit var conexion: BaseDatosProfes
    lateinit var miAdapter: UsuariosAdapter
    var lista = mutableListOf<Usuarios>()
    private lateinit var drawer: DrawerLayout
    private lateinit var toogle:ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        conexion = BaseDatosProfes(this)
        setRecycler()
        setListeners() //Cdo pulsemos el boton flotante
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_opciones, menu)
        return super.onCreateOptionsMenu(menu)
    }



    private fun setListeners() {
        binding.fabAdd.setOnClickListener {
           startActivity(Intent(this, AddUpdateActivity::class.java))
        }
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)

        toogle = ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawer.addDrawerListener(toogle)

        toogle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        navigationView.bringToFront() //importante esta linea, sino no hace nada
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.menu_aniadir -> {
                startActivity(Intent(this, AddUpdateActivity::class.java))
            }
            R.id.menu_buscar -> {
                Toast.makeText(this,"Item 2 seleccionado",Toast.LENGTH_SHORT).show()
            }
            R.id.menu_borrar -> {
                Toast.makeText(this,"ha seleccionado borrar",Toast.LENGTH_SHORT).show()
                val alertDialog: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
                alertDialog.setMessage("¿Esta seguro que desea borrar todos los datos?")
                alertDialog.setPositiveButton("Si"){ dialog, which ->
                    for (i in 0 until lista.size){
                        onItemDelete(i)
                    }
                }
                alertDialog.setNegativeButton("Cancelar",null)
                alertDialog.show()
            }
        }

        drawer.closeDrawer(GravityCompat.START)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toogle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toogle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toogle.onConfigurationChanged(newConfig)
    }


    private fun setRecycler() {
        lista = conexion.leerTodos()
        binding.tvNo.visibility = View.INVISIBLE
        if (lista.size == 0) {
            binding.tvNo.visibility = View.VISIBLE
            return
        }
        val layoutManager = LinearLayoutManager(this)
        binding.recUsuarios.layoutManager = layoutManager
        miAdapter = UsuariosAdapter(lista, { onItemDelete(it) }) {
                usuario->onItemUpdate(usuario)
        }
        binding.recUsuarios.adapter = miAdapter
    }

    private fun onItemUpdate(usuario: Usuarios) {
        //pasamos el usuario al activity updatecreate
        val i = Intent(this, AddUpdateActivity::class.java).apply {
            putExtra("USUARIO", usuario)
        }
        startActivity(i)
    }

    private fun onItemDelete(position: Int) {
        val usuario = lista[position]
        conexion.borrar(usuario.id)
        //borramos de la lista e indicamos al adapter que hemos
        //eliminado un registro
        lista.removeAt(position)
        if (lista.size == 0) {
            binding.tvNo.visibility = View.VISIBLE
        }
        miAdapter.notifyItemRemoved(position)
    }

    override fun onResume() {
        super.onResume()
        setRecycler()
    }


}