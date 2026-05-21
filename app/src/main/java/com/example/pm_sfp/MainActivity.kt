package com.example.pm_sfp

import android.R
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.pm_sfp.interfaces.AudioScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.pm_sfp.interfaces.AppNav
import com.example.pm_sfp.interfaces.CameraScreen
import com.example.pm_sfp.interfaces.HomeScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET

data class Usuario(
    val id: Int,
    val nombre: String,
    val edad: String
)

class DBHelper(context: Context) : SQLiteOpenHelper(context, "crud.db", null, 1){
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE usuarios(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                edad TEXT
            )    
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun insertar(nombre: String, edad: String){
        val db = writableDatabase
        val values = ContentValues()

        values.put("nombre", nombre)
        values.put("edad", edad)

        db.insert("usuarios", null, values)
    }

    fun obtenerUsuarios() : MutableList<Usuario>{
        val lista = mutableListOf<Usuario>()

        val db = readableDatabase

        val cursor = db.rawQuery("SELECT * FROM usuarios", null)

        while (cursor.moveToNext()){
            lista.add(
                Usuario(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
                )
            )
        }

        cursor.close()

        return lista
    }

    fun eliminar(id: Int){
        writableDatabase.delete(
            "usuarios",
            "id=?",
            arrayOf(id.toString())
        )
    }

    fun actualizar(id: Int, nombre: String, edad: String){
        val values = ContentValues()

        values.put("nombre", nombre)
        values.put("edad", edad)

        writableDatabase.update(
            "usuarios",
            values,
            "id=?",
            arrayOf(id.toString())
        )
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = DBHelper(this)

        setContent {
            AppCrud(db)

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCrud(db: DBHelper){
    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var editandoId by remember { mutableStateOf(-1) }
    var usuarios by remember {
        mutableStateOf(db.obtenerUsuarios())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Crud Compose")
                }
            )
        }
    ) {padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = {nombre = it},
                label = {Text("Nombre")},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = edad,
                onValueChange = {edad = it},
                label = {Text("Edad")},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    if (nombre.isNotEmpty() && edad.isNotEmpty()){
                        if (editandoId == -1){
                            db.insertar(nombre, edad)
                        }else{
                            db.actualizar(editandoId, nombre, edad)
                            editandoId = -1
                        }

                        usuarios = db.obtenerUsuarios()

                        nombre = ""
                        edad = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (editandoId == -1)
                        "Guardar"
                    else
                        "Actualizar"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn{
                items(usuarios){usuario ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = usuario.nombre,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = "Edad: ${usuario.edad}"
                            )
                            Row {
                                IconButton(
                                    onClick = {
                                        nombre = usuario.nombre
                                        edad = usuario.edad

                                        editandoId = usuario.id
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        db.eliminar(usuario.id)

                                        usuarios = db.obtenerUsuarios()
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

