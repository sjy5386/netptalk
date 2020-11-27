package com.sysbot32.netptalk

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sysbot32.netptalk.databinding.ActivityMainBinding

lateinit var mainActivity: MainActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var client: Client
    lateinit var chatRoomAdapter: ChatRoomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainActivity = this

        val sharedPreferences = getSharedPreferences("com.sysbot32.netptalk", MODE_PRIVATE)
        username = sharedPreferences.getString("username", username).toString()
        host = sharedPreferences.getString("host", host).toString()
        port = sharedPreferences.getInt("port", port)
        val login: Boolean = sharedPreferences.getBoolean("login", false)

        client = Client.getInstance()
        if (!client.isConnected) {
            if (login) {
                client.connect(host, port)
                if (client.waitForConnection(1000)) {
                    val chatClient = ChatClient(client)
                    chatClient.login(username)
                    chatClient.start()
                } else {
                    getSharedPreferences("com.sysbot32.netptalk", MODE_PRIVATE).edit()
                        .putBoolean("login", false)
                        .apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        chatRoomAdapter = ChatRoomAdapter(this, chatRooms)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = chatRoomAdapter

        binding.floatingActionButton.setOnClickListener {
            val editTitle: EditText = EditText(this)
            AlertDialog.Builder(this).setTitle("채팅방 만들기").setMessage("채팅방 제목 입력")
                .setView(editTitle)
                .setPositiveButton("확인") { dialogInterface: DialogInterface, i: Int ->
                    chatClient?.addChatRoom(editTitle.text.toString())
                }.setNegativeButton("취소") { dialogInterface: DialogInterface, i: Int ->
                }.create().show()
        }

        createNotificationChannel("default")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuLogout) {
            getSharedPreferences("com.sysbot32.netptalk", MODE_PRIVATE).edit()
                .putBoolean("login", false)
                .apply()
            chatClient?.logout()
            chatClient?.stop()
            client.disconnect()
            chatClient = null
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
