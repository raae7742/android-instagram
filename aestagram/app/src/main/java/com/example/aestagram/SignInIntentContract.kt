package com.example.aestagram

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class SignInIntentContract : ActivityResultContract<String, String>() {
    var googleSignInClient : GoogleSignInClient ?= null

    // 구글 로그인 화면으로 이동하는 intent를 만드는 함수
    override fun createIntent(context: Context, clientId: String): Intent {
        // google login 초기 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)

        val signInIntent = googleSignInClient!!.signInIntent

        return signInIntent
    }

    // 구글 로그인 화면 -> LoginActivity로 돌아올 때 호출
    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return when (resultCode) {
            Activity.RESULT_OK -> getTokenId(intent)
            else -> null
        }
    }

    // tokenId 리턴 함수
    fun getTokenId(data: Intent?): String? {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google Sign In was succesful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            println("getTokenId() tokenId를 얻어옴!!")
            return account.idToken
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            println("getTokenId() tokenId를 정상적으로 얻어오지 못함 => \n${e}")
            return null
        }
    }

}