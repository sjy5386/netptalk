package com.sysbot32.netptalk

import org.json.JSONArray
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
            println(received)
            val jsonObject: JSONObject = JSONObject(received)
            val type: String = jsonObject.getString("type")
            if (type == "chat") {
                val chatType: String = jsonObject.getString("chatType")
                val chatMessage: ChatMessage = ChatMessage(jsonObject)
                val chatMessages: MutableList<ChatMessage> =
                    getChatMessages(chatMessage.chatRoom)
                chatMessages.add(chatMessage)
                val chatMessageAdapter = chatMessageAdapterMap[chatMessage.chatRoom]
                if ((chatMessageAdapter != null) && (chatActivity.chatRoom == chatMessage.chatRoom)) {
                    val lastIndex: Int = chatMessages.size - 1
                    chatActivity.runOnUiThread {
                        chatMessageAdapter.notifyItemInserted(lastIndex)
                        chatActivity.binding.recyclerViewChat.scrollToPosition(lastIndex)
                    }
                }
                if ((!chatActivity.status) || (chatActivity.chatRoom != chatMessage.chatRoom)) {
                    notifyChatMessage(chatMessage)
                }
            } else if (type == "chatRoom") {
                if (jsonObject.getString("action") == "add") {
                    chatRooms.add(0, ChatRoom(jsonObject.getString("title")))
                    mainActivity.runOnUiThread {
                        mainActivity.chatRoomAdapter.notifyItemInserted(0)
                    }
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
