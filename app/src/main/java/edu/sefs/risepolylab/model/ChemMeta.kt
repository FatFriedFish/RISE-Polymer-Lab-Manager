package edu.sefs.risepolylab.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.time.LocalDate
import java.util.Date

data class ChemMeta(
    // Chem information
    var chemicalName: String = "",
    var cas: String = "",
    var location : String = "",
    var contentSize: String = "",
    var lastDate: String  = LocalDate.now().toString(),
    @DocumentId var firestoreID: String = ""
)