package com.example.aestagram

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_login.*
import android.content.pm.PackageManager

import android.content.pm.PackageInfo
import android.util.Base64.encode
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.*
import com.google.zxing.qrcode.encoder.Encoder.encode
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.Arrays.asList


class LoginActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    var launcher : ActivityResultLauncher<String>? = null
    var callbackManager : CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        email_login_button.setOnClickListener {
            signinAndSignup()
        }

        google_sign_in_button.setOnClickListener {
            launcher!!.launch("466982309768-cqjv3k61vuessgimrtmq15rkuhhg1n5f.apps.googleusercontent.com")
        }

        facebook_login_button.setOnClickListener {
            facebookLogin()
        }

        // 구글 로그인 화면 -> LoginActivity로 돌아온 콜백 함수
        launcher = registerForActivityResult(SignInIntentContract()) { result: String? ->
            result?.let {
                firebaseAuthWithGoogle(it)  // tokenId를 이용해 firebase에 인증하는 함수
            }
        }

        callbackManager = CallbackManager.Factory.create()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun facebookLogin() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    handleFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException?) {

                }

            })

    }


    private fun handleFacebookAccessToken(token: AccessToken?) {
        var credential = FacebookAuthProvider.getCredential(token?.token!!)

        auth!!.signInWithCredential(credential)
            .addOnCompleteListener(this@LoginActivity) {
                    task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    // Login
                    Toast.makeText(
                        this@LoginActivity,
                        "로그인 성공",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                } else {
                    // Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    // tokenId로 firebase에 인증하는 함수
   private fun firebaseAuthWithGoogle(idToken: String) {
        // it가 tokenId, credential은 Firebase 사용자 인증 정보
        var credential = GoogleAuthProvider.getCredential(idToken, null)

        // credential로 Firebase 인증
        auth!!.signInWithCredential(credential)
            .addOnCompleteListener(this@LoginActivity) {
                    task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    // Login
                    Toast.makeText(
                        this@LoginActivity,
                        "로그인 성공",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                } else {
                    // Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun signinAndSignup() {
        auth?.createUserWithEmailAndPassword(email_editText.text.toString(), password_editText.text.toString())
            ?.addOnCompleteListener {
            task ->
                if (task.isSuccessful) {
                    //Creating a user account
                    moveMainPage(task.result?.user)

                } else if (task.exception?.message.isNullOrEmpty()) {
                    // Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                } else {
                    // Login if you have account
                    signinEmail()
                }
            }
    }

    fun signinEmail() {
        auth?.signInWithEmailAndPassword(email_editText.text.toString(), password_editText.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    // Login
                    moveMainPage(task.result?.user)

                } else {
                    // Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun moveMainPage (user:FirebaseUser?) {
        if (user != null) {
            startActivity(Intent( this, MainActivity::class.java));
        }
    }

}