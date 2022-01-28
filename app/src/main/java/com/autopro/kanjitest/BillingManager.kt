package com.autopro.kanjitest

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BillingManager(
    private val activity: Activity,
    private val callback: Callback
) {

    private val mAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    interface Callback {
        fun onBillingManagerReady()
        fun onSuccess(purchase: Purchase)
        fun onFailure(errorCode: Int)
    }

    private val purchaseUpdateListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            // 구매 성공 시
            for (purchase in purchases) {
                // 비소모품은 정상적으로 구매 완료 시 인정(Acknowledge)해야합니다.
                // 3일 이내 인정(Acknowledge)하지 않으면 자동으로 환불됩니다.
                // 소모품은 정상적으로 구매 완료 시 구매 확인 처리를 해야합니다.
                // 3일 이내 구매 확인 처리를 하지 않으면 자동으로 환불됩니다.
                handlePurchase(purchase)
            }
        } else {
            // 구매 실패 시
            callback.onFailure(billingResult.responseCode)
        }
    }

    private val billingClient = BillingClient.newBuilder(activity)
        .setListener(purchaseUpdateListener)
        .enablePendingPurchases()
        .build()

    init {
        billingClient.startConnection(object : BillingClientStateListener {

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        // Google Play 와의 연결이 성공했을 때
                        callback.onBillingManagerReady()
                    }
                    else -> {
                        // Google Play 와의 연결이 실패했을 때
                        callback.onFailure(billingResult.responseCode)
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Google Play 와 연결이 끊어졌을 때 재시도하는 로직
            }
        })
    }

    fun querySkuDetails(
        type: String = BillingClient.SkuType.INAPP,
        vararg skuIDs: String,
        onResult: (List<SkuDetails>) -> Unit
    ) {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuIDs.toList())
            .setType(type)
            .build()

        billingClient.querySkuDetailsAsync(params) { billingReusult: BillingResult, skuDetailList: List<SkuDetails>? ->
            if (billingReusult.responseCode == BillingClient.BillingResponseCode.OK) {
                onResult(skuDetailList ?: emptyList())
            } else {
                callback.onFailure(billingReusult.responseCode)
            }
        }
    }

    fun purchase(skuDetails: SkuDetails) {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()

        //  구매화면 표시
        val responseCode = billingClient.launchBillingFlow(activity, flowParams).responseCode

        if (responseCode != BillingClient.BillingResponseCode.OK) {
            // 구매 화면을 여는데 실패하면 이 부분이 호출
            callback.onFailure(responseCode)
            Log.d("billing", "결제 실패")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
//        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
//            if (!purchase.isAcknowledged) {
//                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
//                    .setPurchaseToken(purchase.purchaseToken)
//                    .build()
//                CoroutineScope(Dispatchers.Default).launch {
//                    billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
//                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                            // 구매 인정(Acknowledge)에 성공했을 때
//                            val database = Firebase.database.reference
//                            val userEmail = mAuth.currentUser?.email
//                            database.child("Users").child(userEmail.toString().split("@")[0])
//                                .child("isPremium")
//                                .setValue(true)
//                            callback.onSuccess(purchase)
//                        } else {
//                            callback.onFailure(billingResult.responseCode)
//                        }
//                    }
//                }
//            }
//        }
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {

            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            // 소모품은 소비(Consume)을 해 주어야 결제가 완료되며, 재구매 할 수 있습니다.
            billingClient.consumeAsync(consumeParams) { billingResult, purchaseToken ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // 소비 성공
                    val database = Firebase.database.reference
                    val userEmail = mAuth.currentUser?.email
                    database.child("Users").child(userEmail.toString().split("@")[0])
                        .child("isPremium")
                        .setValue(true)
                    Snackbar.make((activity as MainActivity).binding.root, "구매가 완료되었습니다.", Snackbar.LENGTH_SHORT).show()
                    callback.onSuccess(purchase)
                } else {
                    // 소비 실패
                    callback.onFailure(billingResult.responseCode)
                }
            }

        } else {
            // 3일 후 자동으로 취소
            return
        }
    }

    fun checkIfPurchasedButNotAcknowledged(type: String = BillingClient.SkuType.INAPP) {
        if (billingClient.isReady) {
            // 모든 구매 조회하기
            billingClient.queryPurchasesAsync(type) { _: BillingResult, purchases: List<Purchase> ->
                if (purchases.isNotEmpty()) {
                    for (purchase in purchases) {
                        // 구매 되었다면
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            // 구매되었으나 인정되지 않았다면
                            if (!purchase.isAcknowledged) {
                                handlePurchase(purchase)
                            }
                        }
                    }
                }
            }
        }
    }

    fun checkIfAlreadyPurchased(
        sku: String,
        type: String = BillingClient.SkuType.INAPP,
        onResult: (isAlreadyPurchased: Boolean) -> Unit
    ) {
        // queryPurchaseAsync()는 이전에 샀던 구매를 반환
        // 구매하지 않았거나 환불된 제품은 queryPurchasesAsync()에서 조회되지 않습니다.
        billingClient.queryPurchasesAsync(type) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchasesList.isNotEmpty()) {
                    for (purchase in purchasesList) {
                        purchase.skus.forEach { it ->
                            if (it == sku) {
                                return@forEach onResult(true)
                            }
                            return@forEach onResult(false)
                        }
                    }
                } else {
                    return@queryPurchasesAsync onResult(false)
                }
            } else {
                onResult(false)
            }
        }
    }

    fun endConnect() {
        billingClient.endConnection()
    }
}