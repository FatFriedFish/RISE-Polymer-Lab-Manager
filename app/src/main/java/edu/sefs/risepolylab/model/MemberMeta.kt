package edu.sefs.risepolylab.model

import com.google.firebase.firestore.DocumentId
data class MemberMeta(
    var email : String = "",
    var uid : String = "",
    var status : String = "",
    @DocumentId var fireStoreID: String = ""
)
