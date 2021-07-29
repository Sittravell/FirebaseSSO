package com.example.templates.utils.sso

import android.app.Activity
import android.util.Log
import android.view.View
import com.example.templates.utils.inputUtils.RequiresAdvanceTextInputLayout
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class PhoneLogin: PhoneAuthProvider.OnVerificationStateChangedCallbacks(), RequiresAdvanceTextInputLayout {

    /* NOTE: If you have registered any test phone number in Firebase, insert here as well. */
    val testNumbers = listOf("+0123456789")
    val auth = FirebaseAuth.getInstance()

    lateinit var activity: Activity
    var subscriber: Subscriber? = null
    var verId: String? = null
    lateinit var phoneNumber: String
    var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    /* NOTE: Activities that implements PhoneLogin.Subscriber and passed in as subscribers can
    subscribe to these events */
    interface Subscriber{
        fun onCodeRetrieved(success: Boolean, message: String, code: String)
        fun onPhoneSignIn(success: Boolean, message: String, code: String)
        fun beforePhoneSignIn(){}
        fun onTimeOutCode(){}
    }

    /* NOTE: Optional: Implement in onCreate method. Returns a callback that you can run whenever you want to verify*/
    fun setup(activity: Activity, subscriber: Subscriber, phoneInput: TextInputLayout): View.OnClickListener{
        this.activity = activity
        this.subscriber = subscriber
        return View.OnClickListener { verify(phoneInput.value!!) }
    }

    /* NOTE: Optional: Implement to verify immediately*/
    fun verify(activity: Activity, subscriber: Subscriber, phoneNumber: String){
        this.activity = activity
        this.subscriber = subscriber
        verify(phoneNumber)
    }

    /* NOTE: If the phone number is already registered in Firebase, it will not be sent to the phone.
      Use this function to manually enter the code.*/
    fun manualLogin(code: String){
        if(verId != null || testNumbers.any { it == phoneNumber }){
            manualLogin(PhoneAuthProvider.getCredential(verId ?: "", code))
        }else{
            subscriber?.onPhoneSignIn(false, "manualLogin: false", code)
        }
    }

    /* NOTE: Use this to resend the code */
    fun resend() {
        resendVerificationCode(phoneNumber, forceResendingToken)
    }

    private fun verify(phoneNumber: String){
        this.phoneNumber = phoneNumber
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity)                 // Activity (for callback binding)
            .setCallbacks(this)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun manualLogin(c: PhoneAuthCredential){
        subscriber?.beforePhoneSignIn()
        auth.signInWithCredential(c)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful && c.smsCode != null) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("manualLogin", "signInWithCredential:success")
                    val user = task.result?.user
                    subscriber?.onPhoneSignIn(true, "signInWithCredential: success: true", c.smsCode!!)
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("manualLogin", "signInWithCredential:failure", task.exception)
                    subscriber?.onPhoneSignIn(false, "signInWithCredential: success: false", c.smsCode ?: "")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        subscriber?.onPhoneSignIn(false, "FirebaseAuthInvalidCredentialsException: success: false", c.smsCode ?: "")
                    }
                    // Update UI
                }
            }
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity)                 // Activity (for callback binding)
            .setCallbacks(this)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    override fun onVerificationCompleted(c: PhoneAuthCredential) {
        val success = c.smsCode != null && c.smsCode?.length ?: 0 == 6
        subscriber?.onCodeRetrieved(success, "onVerificationCompleted: success: $success", c.smsCode ?: "")
    }

    override fun onCodeSent(verId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
        super.onCodeSent(verId, forceResendingToken)
        this.verId = verId
        this.forceResendingToken = forceResendingToken
    }

    override fun onCodeAutoRetrievalTimeOut(p0: String) {
        super.onCodeAutoRetrievalTimeOut(p0)

    }
    override fun onVerificationFailed(e: FirebaseException) {
        subscriber?.onCodeRetrieved(false, "onVerificationFailed", "")
    }
}