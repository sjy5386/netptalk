package com.sysbot32.netptalk

import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

var chatClient: ChatClient? = null

class ChatClient(client: Client) {
    private val client: Client = client
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    private var username: String = ".anonymous"

    init {
        chatClient = this
    }

    fun start() {
        if (!client.isConnected) {
            return
        }
        executorService.submit(this::reading)
    }

    fun stop() {
        executorService.shutdownNow()
    }

    private fun reading() {
        while (true) {
            val received: String = client.read()
            println(received)
            val jsonObject = JSONObject(received)
            when (jsonObject.getString("type")) {
                "chat" -> {
                    val chatType: String = jsonObject.getString("chatType")
                    val chatMessage = ChatMessage(jsonObject)
                    val chatMessages: MutableList<ChatMessage> =
                        getChatMessages(chatMessage.chatRoom)
                    val lastIndex: Int = chatMessages.size
                    chatMessages.add(lastIndex, chatMessage)
                    val chatMessageAdapter = chatMessageAdapterMap[chatMessage.chatRoom]
                    val chatActivity = chatActivity
                    if ((chatMessageAdapter != null) && (chatActivity.chatRoom == chatMessage.chatRoom)) {
                        chatActivity.runOnUiThread {
                            chatMessageAdapter.notifyItemInserted(lastIndex)
                            chatActivity.binding.recyclerViewChat.scrollToPosition(lastIndex)
                        }
                    }
                    if ((!chatActivity.status) || (chatActivity.chatRoom != chatMessage.chatRoom)) {
                        notifyChatMessage(chatMessage)
                    }
                }
                "chatRoom" -> {
                    when (jsonObject.getString("action")) {
                        "add", "invite" -> {
                            chatRooms.add(0, ChatRoom(jsonObject.getString("title")))
                            mainActivity.runOnUiThread {
                                mainActivity.chatRoomAdapter.notifyItemInserted(0)
                            }
                        }
                    }
                }
            }
        }
    }

    fun login(username: String) {
        this.username = username
        client.write(
            JSONObject()
                .put("type", "login")
                .put("username", username)
                .toString()
        )
    }

    fun logout() {
        client.write(
            JSONObject()
                .put("type", "logout")
                .put("username", username)
                .toString()
        )
    }

    fun sendMessage(chatType: String, content: String, chatRoom: String) {
        client.write(
            JSONObject()
                .put("type", "chat")
                .put("username", username)
                .put("chatType", chatType)
                .put("content", content)
                .put("chatRoom", chatRoom)
                .toString()
        )
    }

    fun addChatRoom(title: String) {
        client.write(
            JSONObject()
                .put("type", "chatRoom")
                .put("action", "add")
                .put("title", title)
                .put("users", JSONArray().put(username))
                .toString()
        )
    }

    fun inviteToChatRoom(chatRoom: String, invitee: String) {
        client.write(
            JSONObject()
                .put("type", "chatRoom")
                .put("action", "invite")
                .put("title", chatRoom)
                .put("inviter", username)
                .put("invitee", invitee)
                .toString()
        )
    }

    fun leaveChatRoom(chatRoom: String) {
        client.write(
            JSONObject()
                .put("type", "chatRoom")
                .put("action", "leave")
                .put("title", chatRoom)
                .put("username", username)
                .toString()
        )
    }
}
