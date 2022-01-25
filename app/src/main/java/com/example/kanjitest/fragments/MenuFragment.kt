package com.example.kanjitest.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.kanjitest.MainActivity
import com.example.kanjitest.R
import com.example.kanjitest.databinding.FragmentMenuBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.util.regex.Pattern


class MenuFragment : BaseFragment() {

    private lateinit var binding: FragmentMenuBinding
    private lateinit var mClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        setValues()
        setupEvents()
    }

    override fun setupEvents() {

        binding.signUpBtn.setOnClickListener {

            val customView =
                LayoutInflater.from(mContext).inflate(R.layout.custom_alert_signup, null)
            val myAlert = AlertDialog.Builder(mContext)
                .setView(customView)
                .show()
            val inputEmail = customView.findViewById<EditText>(R.id.email_edt)
            val inputPassword = customView.findViewById<EditText>(R.id.password_edt)
            val okButton = customView.findViewById<Button>(R.id.ok_btn)
            okButton.setOnClickListener {
                val pattern: Pattern = android.util.Patterns.EMAIL_ADDRESS
                val isEmail = pattern.matcher(inputEmail.text.toString().trim()).matches()
                if (inputEmail.text.isEmpty() || inputPassword.text.isEmpty()) {
                    Snackbar.make(it, "사용 할 아이디 및 비밀번호를 입력 해 주세요", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if (isEmail) {
                    mAuth.createUserWithEmailAndPassword(
                        inputEmail.text.toString(),
                        inputPassword.text.toString()
                    )
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Snackbar.make(binding.root, "회원가입에 성공하였습니다.", Snackbar.LENGTH_SHORT)
                                    .show()
                                myAlert.dismiss()
                            } else {
                                Snackbar.make(binding.root, "회원가입에 실패하였습니다.", Snackbar.LENGTH_SHORT)
                                    .show()
                                myAlert.dismiss()
                            }
                        }
                } else {
                    Snackbar.make(it, "이메일 형식에 맞게 입력 해 주세요", Snackbar.LENGTH_SHORT).show()
                }

            }
        }

        binding.signInOutBtn.setOnClickListener {

            if (binding.signInOutBtn.text == "로그인") {
                val customView =
                    LayoutInflater.from(mContext).inflate(R.layout.custom_alert_signup, null)
                val myAlert = AlertDialog.Builder(mContext)
                    .setView(customView)
                    .show()
                val inputEmail = customView.findViewById<EditText>(R.id.email_edt)
                val inputPassword = customView.findViewById<EditText>(R.id.password_edt)
                val okButton = customView.findViewById<Button>(R.id.ok_btn)
                val signInGoogle =
                    customView.findViewById<com.google.android.gms.common.SignInButton>(R.id.signInGoogle_btn)
                signInGoogle.isVisible = true
                okButton.setOnClickListener {
                    if (inputEmail.text.isEmpty() || inputPassword.text.isEmpty()) {
                        Snackbar.make(it, "아이디 및 비밀번호를 입력 해 주세요.", Snackbar.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    mAuth.signInWithEmailAndPassword(
                        inputEmail.text.toString(),
                        inputPassword.text.toString()
                    )
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                binding.signUserTxt.text = mAuth.currentUser?.email
                                Snackbar.make(binding.root, "로그인에 성공하였습니다.", Snackbar.LENGTH_SHORT)
                                    .show()
                                myAlert.dismiss()
                                binding.signInOutBtn.text = "로그아웃"
                            } else {
                                Snackbar.make(
                                    it,
                                    "아이디 또는 비밀번호를 다시 확인 해 주세요.",
                                    Snackbar.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                }
                signInGoogle.setOnClickListener {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail().build()
                    mClient = GoogleSignIn.getClient(mContext, gso)

                    startActivityForResult(mClient.signInIntent, REQUEST_GOOGLE_SIGN_IN)
                    myAlert.dismiss()
                }
            } else {
                val myAlert = AlertDialog.Builder(mContext)
                    .setTitle("로그아웃 하시겠습니까?")
                    .setPositiveButton("확인") { _, _ ->
                        mAuth.signOut()
                        binding.signInOutBtn.text = "로그인"
                        binding.signUserTxt.text = "로그인 해 주세요"
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }
        }
    }

    override fun setValues() {

        if (mAuth.currentUser != null) {
            binding.signInOutBtn.text = "로그아웃"
            binding.signUserTxt.text = mAuth.currentUser?.email
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            var account: GoogleSignInAccount
            try {
                account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {

            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                (mContext as MainActivity)
            ) { task ->
                if (task.isSuccessful) {
                    binding.signUserTxt.text = mAuth.currentUser?.email
                    binding.signInOutBtn.text = "로그아웃"
                } else {

                }
            }
    }

    companion object {
        const val REQUEST_GOOGLE_SIGN_IN = 1000
    }
}