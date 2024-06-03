package edu.sefs.risepolylab

import android.util.Log
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.sefs.risepolylab.model.ChemMeta
import edu.sefs.risepolylab.model.MemberMeta
import edu.sefs.risepolylab.model.TrainingMeta
import org.checkerframework.checker.units.qual.C
import java.time.LocalDate

class DBHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val chemCollection = "Chemicals"
    private val memberCollection = "Members"
    private val trainingCollection = "Training"

    private fun limitAndGet(query: Query,
                             resultListener: (List<ChemMeta>)->Unit) {
        query
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "Chemicals fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                resultListener(result.documents.mapNotNull {
                    it.toObject(ChemMeta::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "Chemicals fetch FAILED ", it)
                resultListener(listOf())
            }
    }
    fun fetchAllChemMetaList(
        resultListener: (List<ChemMeta>) -> Unit
    ) {
        val query = db.collection(chemCollection)
        limitAndGet(query, resultListener)
    }

    fun fetchMatchingChemMetaList(cas : String, resultListener: (List<ChemMeta>) -> Unit){
        val query = db.collection(chemCollection)
        query
            .whereEqualTo("cas", cas)
            .get()
            .addOnSuccessListener { result->
                Log.d(javaClass.simpleName, "Matching chemicals found:: ${result!!.documents.size}")
                resultListener(result.documents.mapNotNull {
                    it.toObject(ChemMeta::class.java)
                })
            }
    }
    fun fetchTrainingMetaList(uid: String, resultListener: (List<TrainingMeta>) -> Unit) {

        Log.d(javaClass.simpleName, uid)
        val query = db.collection(trainingCollection)
        query
            .whereEqualTo("uid",uid)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "Trainings fetched: ${result!!.documents.size}")
                // NB: This is done on a background thread
                resultListener(result.documents.mapNotNull {
                    it.toObject(TrainingMeta::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "Trainings fetch FAILED ", it)
                resultListener(listOf())
            }
    }

    fun fetchUserStatus(uid: String, resultListener: (String) -> Unit){
        val query = db.collection(memberCollection).document(uid)
        query
            .get()
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "SUCCESS to fetch user status")
                resultListener(it.get("status").toString())
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "FAILED to fetch user status")
            }
    }

    fun createChemMeta(chemMeta: ChemMeta, resultListener: (String)->Unit) {
        db.collection(chemCollection)
            .add(chemMeta)
            .addOnSuccessListener {
                Log.d("Database", "ChemMeta added successfully")
                resultListener(it.id)
            }
            .addOnFailureListener { e ->
                Log.w("Database", "Error adding ChemMeta", e)
            }
    }

    fun createMemberMeta(memberMeta: MemberMeta, uid : String) {
        db.collection(memberCollection)
            .document(uid)
            .set(memberMeta)
            .addOnSuccessListener {
                Log.d("Database", "MemberMeta added successfully")
            }
            .addOnFailureListener { e ->
                Log.w("Database", "Error adding MemberMeta", e)
            }
    }

    fun searchMember(email : String, resultListener: (List<MemberMeta>) -> Unit){
        db.collection(memberCollection)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    resultListener(it.documents.mapNotNull { doc->
                        doc.toObject(MemberMeta::class.java)
                    })
                }
            }
    }

    fun setTraining(uid: String, training: String, status: String, resultListener: () -> Unit){
        db.collection(trainingCollection)
            .whereEqualTo("uid", uid)
            .whereEqualTo("training", training)
            .get()
            .addOnSuccessListener {
                if (it.documents.isEmpty()){
                    val trainingMeta = TrainingMeta(
                        uid = uid,
                        training = training,
                        status = status
                    )
                    db.collection(trainingCollection)
                        .add(trainingMeta)
                        .addOnSuccessListener {
                            resultListener()
                        }
                }else{
                    db.collection(trainingCollection).document(it.documents[0].id).update("Status", status)
                }
            }
    }

    fun removeChemical(fid: String, resultListener: () -> Unit) {
        db.collection(chemCollection).document(fid)
            .delete()
            .addOnSuccessListener {
                Log.d("Database", "Chemical Meta successfully deleted!")
                resultListener.invoke()
            }
            .addOnFailureListener { e -> Log.w("Database", "Error deleting Chemical Meta", e) }
    }

    fun refreshChemical(fid: String, resultListener: () -> Unit){
        db.collection(chemCollection).document(fid)
            .update("lastDate", LocalDate.now().toString())
            .addOnSuccessListener {
                Log.d("Database", "Chemical lastDate successfully deleted!")
                resultListener.invoke()
            }
            .addOnFailureListener { e -> Log.w("Database", "Error updating chemical lastDate", e) }
    }

    fun deleteUser(uid: String, resultListener: () -> Unit){
        db.collection(memberCollection).document(uid)
            .delete()
            .addOnSuccessListener {
                Log.d("Database", "Member successfully deleted!")
                resultListener.invoke()
            }
            .addOnFailureListener { e -> Log.w("Database", "Error deleting member", e) }

        val query = db.collection(trainingCollection).whereEqualTo("uid", uid)
        query
            .get()
            .addOnSuccessListener {
                it.forEach {doc ->
                    doc.reference.delete()
                }
                Log.d("Database", "Training successfully deleted!")
            }
            .addOnFailureListener { e -> Log.w("Database", "Error deleting training", e)  }
    }
}