package com.zipper.fetch.miniprogram.mtaotai

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.zipper.fetch.miniprogram.mtaotai.databinding.MtActivityLoginBinding
import kotlinx.coroutines.launch

fun Activity.toLogin(config: LoginConfig) {
    val intent = Intent(this, LoginActivity::class.java)
    intent.putExtra("LoginConfig", config)
    startActivity(intent)
}

fun Intent.getLoginConfig(): LoginConfig? {
    return getParcelableExtra("LoginConfig")
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: MtActivityLoginBinding

    private lateinit var loginConfig: LoginConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MtActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val config = intent.getLoginConfig()
        if (config == null) {
            finish()
            Toast.makeText(this, "参数错误", Toast.LENGTH_LONG).show()
            return
        }
        loginConfig = config

        binding.btnSendCode.setOnClickListener {
            if (binding.etPhone.text.toString().matches(Regex("^1\\d{10}$"))) {
                toast("请输入正确的手机号")
                return@setOnClickListener
            }
            lifecycleScope.launch {

            }
        }

        binding.btnLogin.setOnClickListener {

        }
    }
}