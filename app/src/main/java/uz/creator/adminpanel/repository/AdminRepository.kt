package uz.creator.adminpanel.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import uz.creator.adminpanel.models.User
import uz.creator.adminpanel.utils.Resource

class AdminRepository {

    private var fireStore: FirebaseFirestore = Firebase.firestore
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()

    fun addOrSetUser(user: User) = flow<Resource<Boolean>> {
        emit(Resource.Loading())
        val addUserRef: Boolean =
            user.phoneNumber?.let { fireStore.collection("users").document(it) }
                ?.set(user)?.isSuccessful == true
        emit(Resource.Success(addUserRef))
    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(Resource.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun getAllUsers() = flow<Resource<List<User>>> {
        emit(Resource.Loading())
        val snapshot = fireStore.collection("users").get().await()
        val posts = snapshot.toObjects(User::class.java)
        posts?.let {
            emit(Resource.Success(it))
        }
    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(Resource.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun addAdmin(user: User) = flow<Resource<Boolean>> {
        emit(Resource.Loading())
        fireStore.collection("admin").document("mainAdmin").set(user).await()
        emit(Resource.Success(true))
    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(Resource.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun checkAdminByPhoneNumber(phoneNumber: String) = flow<Resource<Boolean>> {
        emit(Resource.Loading())
        val admin: User? = fireStore.collection("admin").document("mainAdmin").get().await()
            .toObject(User::class.java)
        admin?.let {
            if (admin.phoneNumber == phoneNumber) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Success(false))
            }
        }
    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(Resource.Error(it.message.toString()))
    }.flowOn(Dispatchers.IO)

    fun saveUserImage() {

    }
}