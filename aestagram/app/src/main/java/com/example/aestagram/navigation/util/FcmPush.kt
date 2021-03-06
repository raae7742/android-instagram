package com.example.aestagram.navigation.util

import android.util.Log
import com.example.aestagram.navigation.model.PushDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.squareup.okhttp.*
import java.io.IOException

class FcmPush {
    var JSON = MediaType.parse("application/json; charset=utf-8")
    var url = "https://fcm.googleapis.com/fcm/send"
    var serverKey = "AAAAbLpQj4g:APA91bEUqoC4AxpkRoYc_qSjzSls112bPBGDZoCy9fbuSIxxQ-J0z3fFQEQZV9L0RLU_jZ7rvSiSuhhMTAG583_xpZ2DP6w3Zt1Io9odLEAqM-pcmgGj3oplHWLmK4VRnvQPgmkrgz2M"
    var gson : Gson? = null
    var okHttpClient : OkHttpClient? = null

    companion object{
        var instance = FcmPush()
    }

    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }

    fun sendMessage(destinationUid : String, title : String, message : String) {
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid).get().addOnCompleteListener {
            task ->
            if (task.isSuccessful) {
                var token = task?.result?.get("pushToken").toString()

                var pushDTO = PushDTO()
                pushDTO.to = token
                pushDTO.notification.title = title
                pushDTO.notification.body = message

                var body = RequestBody.create(JSON,gson?.toJson(pushDTO))
                var request = Request.Builder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "key="+serverKey)
                    .url(url)
                    .post(body)
                    .build()

                okHttpClient?.newCall(request)?.enqueue(object : Callback {
                    override fun onFailure(request: Request?, e: IOException?) {
                        Log.e("FCM", "failure")
                    }

                    override fun onResponse(response: Response?) {
                        Log.d("FCM", "success")
                        println(response?.body()?.string())
                    }

                })
            }
        }
    }
}