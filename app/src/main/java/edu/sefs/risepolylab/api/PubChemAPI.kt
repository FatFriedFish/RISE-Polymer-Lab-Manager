package edu.sefs.risepolylab.api

import android.text.SpannableString
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.lang.reflect.Type

interface PubChemAPI {
    @GET("/rest/pug/compound/name/{cas}/cids/json")
    suspend fun getCID(@Path("cas") cas: String) : Response<PubChemResponse>

    data class PubChemResponse(
        @SerializedName("IdentifierList")
        val IdentifierList : CID
    )
    data class CID(
        @SerializedName("CID")
        val cid: List<Int>
    )


    class SpannableDeserializer : JsonDeserializer<SpannableString> {
        // @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): SpannableString {
            return SpannableString(json.asString)
        }
    }

    companion object {
        // base URL
        var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host("pubchem.ncbi.nlm.nih.gov")
            .build()
        fun create(): PubChemAPI = create(httpurl)
        private fun create(httpUrl: HttpUrl): PubChemAPI {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    // Enable basic HTTP logging to help with debugging.
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PubChemAPI::class.java)
        }
    }
}