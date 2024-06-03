package edu.sefs.risepolylab.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

// Firebase insists we have a no argument constructor
data class TrainingMeta(
    var uid : String = "",
    var training : String = "",
    var status: String = "",
    @DocumentId var fireStoreID: String = ""
)