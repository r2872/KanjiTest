package com.autopro.kanjitest.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.autopro.kanjitest.BillingManager
import com.autopro.kanjitest.MainActivity
import com.autopro.kanjitest.R
import com.autopro.kanjitest.databinding.FragmentMenuBinding
import com.autopro.kanjitest.datas.GlobalData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import java.util.regex.Pattern

class MenuFragment : BaseFragment() {

    private lateinit var binding: FragmentMenuBinding
    private lateinit var mClient: GoogleSignInClient
    private lateinit var mBillingManager: BillingManager
    private var mSkuDetailsList = listOf<SkuDetails>()
    private lateinit var mDatabase: DatabaseReference

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
        setUpBilling()
        setValues()
        setupEvents()
    }

    override fun onResume() {
        super.onResume()
        mBillingManager.checkIfPurchasedButNotAcknowledged()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBillingManager.endConnect()
    }

    override fun setupEvents() {

        binding.billingBtn.setOnClickListener {
            if (mAuth.currentUser != null) {
                Log.d("MenuFragment", "결제시작")
                runBlocking {
                    purchase()
                }
                checkPremium()
            } else {
                Snackbar.make(binding.root, "로그인이 필요한 서비스 입니다.", Snackbar.LENGTH_SHORT).show()
                Log.d("MenuFragment", "로그인필요")
                return@setOnClickListener
            }
        }

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
                                mDatabase.child("Users")
                                    .child(inputEmail.text.toString().split("@")[0])
                                    .child("isPremium")
                                    .setValue(false)
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
                                GlobalData.checkPremium = true
                                binding.signUserTxt.text = mAuth.currentUser?.email
                                Snackbar.make(binding.root, "로그인에 성공하였습니다.", Snackbar.LENGTH_SHORT)
                                    .show()
                                myAlert.dismiss()
                                isSignIn()
                                checkPremium()
                            } else {
                                Snackbar.make(
                                    it,
                                    "아이디 또는 비밀번호를 다시 확인 해 주세요.",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
                signInGoogle.setOnClickListener {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("1089583564175-9bd4ko8vjbqtrd2tmeluenut51gen446.apps.googleusercontent.com")
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
                        GlobalData.checkPremium = false
                        isSignIn()
                        checkPremium()

                    }
                    .setNegativeButton("취소", null)
                    .show()
            }
        }
    }

    override fun setValues() {

        showBillingButton()

        isSignIn()
        mDatabase = Firebase.database.reference
    }

    @SuppressLint("SetTextI18n")
    private fun isSignIn() {
        if (mAuth.currentUser != null) {
            binding.signInOutBtn.text = "로그아웃"
            binding.signUserTxt.text =
                "${mAuth.currentUser?.email.toString().split("@")[0]} 님 환영합니다."
            binding.signUpBtn.isVisible = false
        } else {
            binding.signInOutBtn.text = "로그인"
            binding.signUserTxt.text = "로그인 해 주세요"
            binding.signUpBtn.isVisible = true
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
                    GlobalData.checkPremium = true
                    isSignIn()
                    checkPremium()
                    Snackbar.make(binding.root, "구글 아이디로 로그인 하였습니다.", Snackbar.LENGTH_SHORT).show()
                } else {

                }
            }
    }

    private fun setUpBilling() {
        mBillingManager =
            BillingManager(mContext as MainActivity, object : BillingManager.Callback {
                override fun onBillingManagerReady() {

                    // 구매 가능한 제품 목록 조회
                    mBillingManager.querySkuDetails(
                        BillingClient.SkuType.INAPP,
                        skuId
                    ) { skuDetailsList: List<SkuDetails> ->
                        // 조회한 제품 목록 저장
                        mSkuDetailsList = skuDetailsList
                    }

                    // 이미 구매했는지 확인
                    mBillingManager.checkIfAlreadyPurchased(skuId) { isAlreadyPurchased ->
                        if (isAlreadyPurchased) {

                        } else {

                        }
                    }
                }

                override fun onSuccess(purchase: Purchase) {

                }

                override fun onFailure(errorCode: Int) {

                    when (errorCode) {
                        BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                            Toast.makeText(mContext, "Cannot find item", Toast.LENGTH_SHORT).show()
                        }
                        BillingClient.BillingResponseCode.USER_CANCELED -> {
                            Toast.makeText(mContext, "구매가 취소되었습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                        else -> {
                            Toast.makeText(mContext, "Error: $errorCode", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }

    private fun purchase() {
        for (skuDetail in mSkuDetailsList) {
            if (skuDetail.sku == skuId) {
                mBillingManager.purchase(skuDetail)
            } else {
                Toast.makeText(mContext, "Cannot find item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPremium() {
        if (mAuth.currentUser != null) {
            val userEmail = mAuth.currentUser?.email.toString().split("@")[0]
            mDatabase = Firebase.database.getReference("Users").child(userEmail)
            mDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        val getIsPremium = data.value
                        GlobalData.checkPremium = getIsPremium == true
                    }
                    (mContext as MainActivity).setAdBanner()
                    showBillingButton()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        } else {
            (mContext as MainActivity).showAdBanner()
            binding.billingBtn.visibility = View.VISIBLE
        }
    }

    private fun showBillingButton() {
        if (GlobalData.checkPremium) binding.billingBtn.visibility = View.GONE
        else binding.billingBtn.visibility = View.VISIBLE
    }

    companion object {
        const val REQUEST_GOOGLE_SIGN_IN = 1000
        const val skuId = "remove_ad_test"
    }

}