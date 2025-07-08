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
import rodriguez.jairo.proyectofinal_reviewssystem.entities.User

class UserViewModel : ViewModel() {
    private val db = Firebase.firestore

    private var _usuario = MutableLiveData<User?>(null)
    val usuario: LiveData<User?> = _usuario

    // Carga el usuario actual por su uid
    fun cargarUsuario(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val doc = db.collection("users").document(uid).get().await()
                val user = doc.toObject(User::class.java)
                _usuario.postValue(user)
            } catch (e: Exception) {
                e.printStackTrace()
                _usuario.postValue(null)
            }
        }
    }

    // Crea o actualiza el usuario en Firestore
    fun guardarUsuario(uid: String, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("users").document(uid).set(user).await()
                _usuario.postValue(user)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Actualiza solo algunos campos del usuario
    fun actualizarCamposUsuario(uid: String, campos: Map<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("users").document(uid).update(campos).await()
                // Opcional: recargar usuario
                cargarUsuario(uid)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}