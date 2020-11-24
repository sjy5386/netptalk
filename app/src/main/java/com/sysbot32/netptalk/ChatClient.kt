package com.sysbot32.netptalk

import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

var chatClient: ChatClient? = null

val chatRooms: MutableList<ChatRoom> = mutableListOf()

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
            println(received)
            val jsonObject: JSONObject = JSONObject(received)
            val type: String = jsonObject.getString("type")
            if (type == "chat") {
                val chatType: String = jsonObject.getString("chatType")
                val chatMessage: ChatMessage = ChatMessage(jsonObject)
                if (chatType == "text") {
                    notifyChatMessage(chatMessage)
                }
            } else if (type == "chatRoom") {
                if (jsonObject.getString("action") == "add") {
                    chatRooms.add(ChatRoom(jsonObject.getString("title")))
                }
            }
        }
    }

    fun login(username: String) {
        this.username = username
        val jsonObject: JSONObject = JSONObject()
            .put("type", "login")
            .put("username", username)
        client.write(jsonObject.toString())
    }

    fun sendMessage(text: String, chatRoom: String) {
        sendMessage(text, "text", chatRoom)
    }

    private fun sendMessage(chatType: String, content: String, chatRoom: String) {
        val jsonObject: JSONObject = JSONObject()
            .put("type", "chat")
            .put("username", username)
            .put("chatType", chatType)
            .put("content", content)
            .put("chatRoom", chatRoom)
        client.write(jsonObject.toString())
    }

    fun addChatRoom(title: String) {
        val jsonObject: JSONObject = JSONObject()
            .put("type", "chatRoom")
            .put("action", "add")
            .put("title", title)
            .put("users", JSONArray().put(username))
        client.write(jsonObject.toString())
    }
}
