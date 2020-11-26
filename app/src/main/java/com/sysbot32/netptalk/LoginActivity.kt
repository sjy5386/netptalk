package com.sysbot32.netptalk

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sysbot32.netptalk.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var client: Client

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("com.sysbot32.netptalk", MODE_PRIVATE)
        binding.editTextUserName.setText(
            sharedPreferences.getString(
                "username",
                getString(R.string.edit_username)
            )
        )
        binding.editTextServerHost.setText(
            sharedPreferences.getString(
                "host",
                getString(R.string.edit_server_host)
            )
        )
        binding.editTextServerPort.setText(sharedPreferences.getInt("port", 30001).toString())

        client = Client.getInstance()

        binding.buttonLogin.setOnClickListener {
            val username: String = binding.editTextUserName.text.toString()
            val host: String = binding.editTextServerHost.text.toString()
            val port: Int = binding.editTextServerPort.text.toString().toInt()

            val editor = getSharedPreferences("com.sysbot32.netptalk", MODE_PRIVATE).edit()
            editor.putString("username", username)
            editor.putString("host", host)
            editor.putInt("port", port)
            editor.apply()

            client.connect(host, port)
            val start: Long = System.currentTimeMillis()
            while (!client.isConnected && (System.currentTimeMillis() - start <= 3000));
            if (client.isConnected) {
                val chatClient: ChatClient = ChatClient(client)
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
