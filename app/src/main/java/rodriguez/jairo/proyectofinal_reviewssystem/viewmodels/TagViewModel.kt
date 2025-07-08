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
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Tag
import java.util.UUID

class TagViewModel: ViewModel() {
    private val db = Firebase.firestore

    private var _listaTags = MutableLiveData<List<Tag>>(emptyList())
    val listaTags: LiveData<List<Tag>> = _listaTags

    init {
        obtenerTags()
    }

    fun obtenerTags(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resultado = db.collection("tags").get().await()

                val tags = resultado.documents.mapNotNull { it.toObject(Tag::class.java) }
                _listaTags.postValue(tags)
            } catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun agregarTags(tag: Tag){
        tag.id = UUID.randomUUID().toString()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("tags").document(tag.id).set(tag).await()
                _listaTags.postValue(_listaTags.value?.plus(tag))
            } catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun actualizarTags(tag: Tag){
        viewModelScope.launch(Dispatchers.IO){
            try{
                db.collection("tags").document(tag.id).update(tag.toMap()).await()
                _listaTags.postValue(_listaTags.value?.map{ if (it.id == tag.id)tag else it })
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun borrarTags(id: String) {
        viewModelScope.launch(Dispatchers.IO){
            try{
                db.collection("tags").document(id).delete().await()
                _listaTags.postValue(_listaTags.value?.filter { it.id != id})
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

}