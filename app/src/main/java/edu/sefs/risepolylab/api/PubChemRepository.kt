package edu.sefs.risepolylab.api

import android.util.Log

class PubChemRepository (private val api: PubChemAPI): BaseRepo() {

    suspend fun getCID (cas : String) : Resource<PubChemAPI.PubChemResponse>{
        return safeApiCall { api.getCID(cas)}

    }
}

