package edu.sefs.risepolylab

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth
import edu.sefs.risepolylab.api.BaseRepo
import edu.sefs.risepolylab.api.PubChemAPI
import edu.sefs.risepolylab.api.PubChemRepository
import edu.sefs.risepolylab.model.ChemMeta
import edu.sefs.risepolylab.model.MemberMeta
import edu.sefs.risepolylab.model.TrainingMeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException


class MainViewModel : ViewModel() {
    private var userName = MutableLiveData("Uninitialized")
    private var email = MutableLiveData("Uninitialized")
    private var uid = MutableLiveData("Uninitialized")
    private var userStatus = MutableLiveData("Uninitialized")

    private var trainingMetaList = MutableLiveData<List<TrainingMeta>>()
    private var chemicalMetaList = MutableLiveData<List<ChemMeta>>()

    private val dbHelp = DBHelper()
    private val api = PubChemAPI.create()
    private val repository = PubChemRepository(api)
    private var chemCID = MutableLiveData("")


    private fun userLogout() {
        userName.postValue("Uninitialized")
        email.postValue("Uninitialized")
        uid.postValue("Uninitialized")
    }


    fun updateUser() {
        // Update user data in view model
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.d("Viewmodel", "update user information")
            userName.postValue(user.displayName)
            email.postValue(user.email)
            uid.postValue(user.uid)
        }
    }

    fun observeUserName() : LiveData<String> {
        return userName
    }
    fun observeEmail() : LiveData<String> {
        return email
    }
    fun observeUid() : LiveData<String> {
        return uid
    }

    fun observeUserStatus() : LiveData<String>{
        return userStatus
    }
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        userLogout()
    }

    fun observeTrainingMetaList () : LiveData<List<TrainingMeta>>{
        return trainingMetaList
    }
    fun observeChemicalMetaList () : LiveData<List<ChemMeta>>{
        return chemicalMetaList
    }

    //get one row for VH
    fun getTrainingMeta(position: Int) : TrainingMeta {
        val note = trainingMetaList.value?.get(position)
        return note!!
    }
    fun getChemMeta(position: Int) : ChemMeta {
        val note = chemicalMetaList.value?.get(position)
        return note!!
    }

    fun fetchTrainingMetaList(resultListener:()->Unit){
        dbHelp.fetchTrainingMetaList(uid.value!!){
            trainingMetaList.postValue(it)
            resultListener.invoke()
        }
    }
    fun fetchAllChemicalMetaList(){
        dbHelp.fetchAllChemMetaList(){
            chemicalMetaList.postValue(it)
        }
    }
    fun filterChemicalMetaList(cas : String){
        dbHelp.fetchMatchingChemMetaList(cas){
            chemicalMetaList.postValue(it)
        }
    }

    fun fetchUserStatus(resultListener:()->Unit){
        dbHelp.fetchUserStatus(uid.value!!){
            userStatus.postValue(it)
            resultListener.invoke()
        }
    }

    fun addChemical(name: String, cas: String, location:String, size: String, resultListener: (String) -> Unit ){
        val chemMeta = ChemMeta(
            chemicalName = name,
            cas = cas,
            location = location,
            contentSize = size
        )
        dbHelp.createChemMeta(chemMeta){
            resultListener(it)
        }
    }

    fun removeChemical(fid : String, resultListener: () -> Unit){
        dbHelp.removeChemical(fid, resultListener)
    }

    fun refreshChemical(fid: String, resultListener: () -> Unit){
        dbHelp.refreshChemical(fid, resultListener)
    }

    fun getChemCID (cas : String){
        viewModelScope.launch(
            context = viewModelScope.coroutineContext + Dispatchers.IO) {
                val response = repository.getCID(cas).data!!.IdentifierList.cid[0].toString()
                if (response.isNotEmpty()) {
                    chemCID.postValue(response)
                    Log.d("MainViewModel", "chemCID:" + response)
                }
        }
    }

    fun observeChemCID () : LiveData<String>{
        return chemCID
    }

    fun addMember(email: String, uid : String){
        val memberMeta = MemberMeta(
            email = email,
            uid = uid,
            status = "Unauthorized"
        )
        dbHelp.createMemberMeta(memberMeta, uid)
    }

    fun searchMember(email : String, resultListener: (String) -> Unit){
        dbHelp.searchMember(email){member->
            Log.d("MainViewModel", "Found member " + member.size)
            resultListener(member[0].uid)
        }
    }

    fun setTraining(uid : String, training : String, status : String, resultListener: () -> Unit){
        dbHelp.setTraining(uid, training, status,resultListener)
    }

    fun deleteUser(uid: String, resultListener: () -> Unit){
        dbHelp.deleteUser(uid, resultListener)
    }
}