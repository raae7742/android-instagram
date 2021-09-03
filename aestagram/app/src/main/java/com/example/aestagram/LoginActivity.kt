package com.example.aestagram

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
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    var launcher : ActivityResultLauncher<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        email_login_button.setOnClickListener {
            signinAndSignup()
        }

        // 구글 로그인 화면 -> LoginActivity로 돌아온 콜백 함수
        launcher = registerForActivityResult(SignInIntentContract()) { result: String? ->
            result?.let {
                firebaseAuthWithGoogle(it)  // tokenId를 이용해 firebase에 인증하는 함수
            }
        }

        google_sign_in_button.setOnClickListener {
            launcher!!.launch("466982309768-cqjv3k61vuessgimrtmq15rkuhhg1n5f.apps.googleusercontent.com")
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