package com.sysbot32.netptalk

import org.json.JSONObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

var chatClient: ChatClient? = null

class ChatClient(client: Client) {
    private val client: Client = client
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    private var username: String = "User"

    init {
        chatClient = this
    }

    fun start() {
        if (!client.isConnected) {
            return
        }
        executorService.submit(this::reading)
    }

    private fun reading() {
        while (true) {
            val received: String = client.read()
            val jsonObject: JSONObject = JSONObject(received)
            val type: String = jsonObject.getString("type")
            if (type == "chat") {
                val chatType: String = jsonObject.getString("chatType")
                val chatMessage: ChatMessage = ChatMessage(jsonObject)
                if (chatType == "text") {
                    notifyChatMessage(chatMessage)
                }
            }
        }
    }

    fun login(username: String) {
        this.username = username
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("type", "login")
        jsonObject.put("username", username)
        client.write(jsonObject.toString())
    }

    fun sendMessage(text: String) {
        sendMessage(text, "text")
    }

    private fun sendMessage(chatType: String, content: String) {
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("type", "chat")
        jsonObject.put("username", username)
        jsonObject.put("chatType", chatType)
        jsonObject.put("content", content)
        client.write(jsonObject.toString())
    }
}
