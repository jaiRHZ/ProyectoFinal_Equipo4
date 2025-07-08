package rodriguez.jairo.proyectofinal_reviewssystem.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Content
import java.util.UUID

class ContentViewModel: ViewModel() {
    private val db = Firebase.firestore

    private var _listaContenidos = MutableLiveData<List<Content>>(emptyList())
    val listaContenidos: LiveData<List<Content>> = _listaContenidos

    init {
        obtenerContenidos()
    }

    fun obtenerContenidos(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resultado = db.collection("contenidos").get().await()

                val contenidos = resultado.documents.mapNotNull { it.toObject(Content::class.java) }
                _listaContenidos.postValue(contenidos)
            } catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun agregarContenidos(contenido: Content){
        contenido.id = UUID.randomUUID().toString()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("contenidos").document(contenido.id).set(contenido).await()
                _listaContenidos.postValue(_listaContenidos.value?.plus(contenido))
            } catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun actualizarContenidos(contenido: Content){
        viewModelScope.launch(Dispatchers.IO){
            try{
                db.collection("contenidos").document(contenido.id).update(contenido.toMap()).await()
                _listaContenidos.postValue(_listaContenidos.value?.map{ if (it.id == contenido.id)contenido else it })
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun borrarContenidos(id: String) {
        viewModelScope.launch(Dispatchers.IO){
            try{
                db.collection("contenidos").document(id).delete().await()
                _listaContenidos.postValue(_listaContenidos.value?.filter { it.id != id})
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

}