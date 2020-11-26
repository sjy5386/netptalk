package com.sysbot32.netptalk

import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

var chatClient: ChatClient? = null

val chatRooms: MutableList<ChatRoom> = mutableListOf()
private val chatMap: MutableMap<String, MutableList<ChatMessage>> = mutableMapOf()

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
                val chatMessages: MutableList<ChatMessage> =
                    getChatMessages(chatMessage.chatRoom)
                chatMessages.add(chatMessage)
                if ((chatActivity == null) || (chatActivity!!.chatRoom != chatMessage.chatRoom)) {
                    notifyChatMessage(chatMessage)
                }
            } else if (type == "chatRoom") {
                if (jsonObject.getString("action") == "add") {
                    chatRooms.add(0, ChatRoom(jsonObject.getString("title")))
                    mainActivity.chatRoomAdapter.notifyItemInserted(0)
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

    fun sendMessage(chatType: String, content: String, chatRoom: String) {
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

fun getChatMessages(chatRoom: String): MutableList<ChatMessage> {
    var chatMessages = chatMap[chatRoom]
    if (chatMessages == null) {
        chatMessages = mutableListOf()
        chatMap[chatRoom] = chatMessages
    }
    return chatMessages
}
