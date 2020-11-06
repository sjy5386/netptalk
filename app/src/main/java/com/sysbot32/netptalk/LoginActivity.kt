package com.sysbot32.netptalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sysbot32.netptalk.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var client: Client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        client = Client.getInstance()

        binding.buttonLogin.setOnClickListener {
            val username: String = binding.editTextUserName.text.toString()
            val host: String = binding.editTextServerHost.text.toString()
            val port: Int = binding.editTextServerPort.text.toString().toInt()

            client.connect(host, port)
            finish()
        }
    }
}
