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
import rodriguez.jairo.proyectofinal_reviewssystem.entities.Review
import java.util.UUID

class ReviewViewModel: ViewModel() {
    private val db = Firebase.firestore

    private var _listaReviews = MutableLiveData<List<Review>>(emptyList())
    val listaReviews: LiveData<List<Review>> = _listaReviews

    init {
        obtenerReviews()
    }

    fun obtenerReviews(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resultado = db.collection("reviews").get().await()

                val reviews = resultado.documents.mapNotNull { it.toObject(Review::class.java) }
                _listaReviews.postValue(reviews)
            } catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun agregarReviews(review: Review){
        review.id = UUID.randomUUID().toString()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("reviews").document(review.id).set(review).await()
                _listaReviews.postValue(_listaReviews.value?.plus(review))
            } catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun actualizarReviews(review: Review){
        viewModelScope.launch(Dispatchers.IO){
            try{
                db.collection("reviews").document(review.id).update(review.toMap()).await()
                _listaReviews.postValue(_listaReviews.value?.map{ if (it.id == review.id)review else it })
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun borrarReviews(id: String) {
        viewModelScope.launch(Dispatchers.IO){
            try{
                db.collection("reviews").document(id).delete().await()
                _listaReviews.postValue(_listaReviews.value?.filter { it.id != id})
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

}