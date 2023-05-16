package com.techpanda.techcash.csm.fragment

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.techpanda.techcash.Activity_otp
import com.techpanda.techcash.Activity_otp.randomAlphaNumeric
import com.techpanda.techcash.R
import com.techpanda.techcash.helper.AppController
import com.techpanda.techcash.helper.Constatnt
import com.techpanda.techcash.helper.Helper.getTextFromTextView
import com.techpanda.techcash.helper.Helper.isValidEmail
import com.techpanda.techcash.helper.Helper.isValidPassword
import com.techpanda.techcash.helper.JsonRequest
import com.android.volley.Response
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONException

/**
 * A simple [Fragment] subclass.
 * Use the [SingupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SingupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_singup, container, false)
        AppController.initpDialog(requireActivity() as AppCompatActivity)
        val signupBtn: AppCompatButton = view.findViewById(R.id.signupBtn)
        val passwordEditText: TextInputEditText = view.findViewById(R.id.password_edit_text)
        val confirmPasswordEditText: TextInputEditText = view.findViewById(R.id.confirm_password_edit_text)
        val emailEditText: TextInputEditText = view.findViewById(R.id.email_edit_text)
        val nameEditText: TextInputEditText = view.findViewById(R.id.name_edit_text)
        val loginTextTextView: TextView = view.findViewById(R.id.login_text)
        loginTextTextView.setOnClickListener {
            activity?.onBackPressed()
        }
        signupBtn.setOnClickListener {
            val email = getTextFromTextView(emailEditText)
            val password = getTextFromTextView(passwordEditText)
            val confirmPassword = getTextFromTextView(confirmPasswordEditText)
            val name = getTextFromTextView(nameEditText)
            val emailErrorMessage = isValidEmail(email)
            val passwordErrorMessage = isValidPassword(password)
            val confirmPasswordErrorMessage = isValidPassword(confirmPassword)
            val nameErrorMessage = isValidPassword(name)
            when {
                nameErrorMessage != "ok" -> nameEditText.apply {
                    error = nameErrorMessage
                    requestFocus()
                }
                emailErrorMessage != "ok" -> emailEditText.apply {
                    error = emailErrorMessage
                    requestFocus()
                }
                passwordErrorMessage != "ok" -> passwordEditText.apply {
                    error = passwordErrorMessage
                    requestFocus()
                }
                confirmPasswordErrorMessage != "ok" -> confirmPasswordEditText.apply {
                    error = confirmPasswordErrorMessage
                    requestFocus()
                }
                password != confirmPassword -> confirmPasswordEditText.apply {
                    error = "password not matching"
                    requestFocus()
                }
                else -> {
                    checkUser(email,password,name)
                }
            }
        }
        return view
    }

    private fun checkUser(e: String, password: String,name: String) {
        AppController.showpDialog()
        if (AppController.isConnected(requireActivity() as AppCompatActivity)) {
            val jsonReq: JsonRequest = object : JsonRequest(
                Method.POST, Constatnt.Base_Url, null,
                Response.Listener { response ->
                    try {
                        if (response.getString("error").equals("false", ignoreCase = true)) {
                            AppController.hidepDialog()
                            Toast.makeText(
                                requireActivity(),
                                "This email is already registered",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (response.getString("error").equals("true", ignoreCase = true)) {
                            register("0000000000", e.replace("@gmail.com", ""), name, e, Uri.EMPTY,password)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(requireActivity(), "Check User $e", Toast.LENGTH_LONG)
                            .show()
                        AppController.hidepDialog()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(
                        requireActivity(),
                        "Error in Check User $error",
                        Toast.LENGTH_LONG
                    ).show()
                    AppController.hidepDialog()
                }) {
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params[Constatnt.ACCESS_KEY] = Constatnt.ACCESS_Value
                    params["user_check"] = Constatnt.API
                    params["phone"] = e
                    return params
                }
            }
            AppController.getInstance().addToRequestQueue(jsonReq)
        }
    }


    fun register(
        enter_phone: String,
        username: String,
        name: String,
        email: String,
        pro: Uri,
        password: String
    ) {
        var androidId = " "
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            androidId = Settings.Secure.getString(
                requireActivity().getContentResolver(),
                Settings.Secure.ANDROID_ID
            )
            if (androidId == null) {
                androidId = " "
            }
        } else {
            val mTelephony = requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            androidId = if (mTelephony.deviceId != null) {
                mTelephony.deviceId
            } else {
                Settings.Secure.getString(
                    requireActivity().getContentResolver(),
                    Settings.Secure.ANDROID_ID
                )
            }
            if (androidId == null) {
                androidId = " "
            }
        }
        if (AppController.isConnected(requireActivity() as AppCompatActivity)) {
            AppController.showpDialog()
            val jsonReq: JsonRequest = object : JsonRequest(
                Method.POST, Constatnt.Base_Url, null,
                Response.Listener { response ->
                    try {
                        if (response.getString("error").equals("true", ignoreCase = true)) {
                            Toast.makeText(
                                requireActivity(),
                                response.getString("message"),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (response.getString("error").equals("false", ignoreCase = true)) {
                            signin(email, password)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    AppController.hidepDialog()
                },
                Response.ErrorListener { error ->
                    Toast.makeText(requireActivity(), error.toString(), Toast.LENGTH_LONG)
                        .show()
                    AppController.hidepDialog()
                }) {
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = java.util.HashMap()
                    params[Constatnt.ACCESS_KEY] = Constatnt.ACCESS_Value
                    params["user_signup"] = Constatnt.API
                    params["phone"] = enter_phone
                    params["password"] = password
                    params["username"] = username
                    params["name"] = name
                    params["email"] = email
                    params["refer"] = randomAlphaNumeric(8)
                    params["device"] = androidId
                    params["image"] = pro.toString()
                    return params
                }
            }
            AppController.getInstance().addToRequestQueue(jsonReq)
        }
    }

    fun signin(enter_phone: String, password: String) {
        if (AppController.isConnected(requireActivity() as AppCompatActivity)) {
            AppController.showpDialog()
            val jsonReq: JsonRequest = object : JsonRequest(
                Method.POST, Constatnt.Base_Url, null,
                Response.Listener { response ->
                    Log.e("RESPONSE", "signin: ${response.toString()}")
                    if (AppController.getInstance().authorize(response)) {
                        if (AppController.getInstance().state == Constatnt.ACCOUNT_STATE_ENABLED) {
                            try {
                                if (response.getString("error").equals("true", ignoreCase = true)) {
                                    Toast.makeText(
                                        requireActivity(),
                                        response.getString("message"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (response.getString("error")
                                        .equals("false", ignoreCase = true)
                                ) {
                                    Activity_otp.chkref(requireActivity() as AppCompatActivity)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } else {
                            AppController.getInstance().logout(requireActivity() as AppCompatActivity)
                            Toast.makeText(
                                requireActivity(),
                                getText(R.string.msg_account_blocked),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "Something Gone Wrong",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    AppController.hidepDialog()
                },
                Response.ErrorListener {
                    Toast.makeText(
                        requireActivity(),
                        getText(R.string.error_data_loading),
                        Toast.LENGTH_LONG
                    ).show()
                    AppController.hidepDialog()
                }) {
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = java.util.HashMap()
                    params[Constatnt.ACCESS_KEY] = Constatnt.ACCESS_Value
                    params[Constatnt.USER_LOGIN] = Constatnt.API
                    params["phone"] = enter_phone
                    params["password"] = password
                    AppController.getInstance().password = "12345"
                    AppController.getInstance().badge
                    return params
                }
            }
            AppController.getInstance().requestQueue.cache.clear()
            AppController.getInstance().addToRequestQueue(jsonReq)
        }
    }

}