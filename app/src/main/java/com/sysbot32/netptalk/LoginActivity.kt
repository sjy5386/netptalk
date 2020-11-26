package com.sysbot32.netptalk

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sysbot32.netptalk.databinding.ActivityLoginBinding

var username: String = ".anonymous"
var host: String = "netptalk.sysbot32.com"
var port: Int = 30001

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var client: Client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editTextUserName.setText(username)
        binding.editTextServerHost.setText(host)
        binding.editTextServerPort.setText(port.toString())

        client = Client.getInstance()

        binding.buttonLogin.setOnClickListener {
            username = binding.editTextUserName.text.toString()
            host = binding.editTextServerHost.text.toString()
            port = binding.editTextServerPort.text.toString().toInt()

            client.connect(host, port)
            if (client.waitForConnection(1000)) {
                getSharedPreferences("com.sysbot32.netptalk", MODE_PRIVATE).edit()
                    .putString("username", username)
                    .putString("host", host)
                    .putInt("port", port)
                    .putBoolean("login", true)
                    .apply()
                val chatClient = ChatClient(client)
                chatClient.login(username)
                chatClient.start()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, R.string.toast_failed_connect, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
