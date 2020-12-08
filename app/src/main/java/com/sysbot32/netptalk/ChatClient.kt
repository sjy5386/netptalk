package com.sysbot32.netptalk

import org.json.JSONArray
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
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

    fun read(): String? {
        val data = client.read()
        return if (data != null)
            String(data.array(), StandardCharsets.UTF_8)
        else
            null
    }

    fun write(str: String) {
        client.write(ByteBuffer.wrap(str.toByteArray(StandardCharsets.UTF_8)))
    }

    private fun reading() {
        while (true) {
            val received: String = read() ?: break
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
        write(
            JSONObject()
                .put("type", "login")
                .put("username", username)
                .toString()
        )
    }

    fun logout() {
        write(
            JSONObject()
                .put("type", "logout")
                .put("username", username)
                .toString()
        )
    }

    fun sendMessage(chatType: String, content: String, chatRoom: String) {
        write(
            JSONObject()
                .put("type", "chat")
                .put("username", username)
                .put("chatType", chatType)
                .put("content", content)
                .put("chatRoom", chatRoom)
                .put("timestamp", System.currentTimeMillis())
                .toString()
        )
    }

    fun addChatRoom(title: String) {
        write(
            JSONObject()
                .put("type", "chatRoom")
                .put("action", "add")
                .put("title", title)
                .put("users", JSONArray().put(username))
                .put("timestamp", System.currentTimeMillis())
                .toString()
        )
    }

    fun inviteToChatRoom(chatRoom: String, invitee: String) {
        write(
            JSONObject()
                .put("type", "chatRoom")
                .put("action", "invite")
                .put("title", chatRoom)
                .put("inviter", username)
                .put("invitee", invitee)
                .put("timestamp", System.currentTimeMillis())
                .toString()
        )
    }

    fun leaveChatRoom(chatRoom: String) {
        write(
            JSONObject()
                .put("type", "chatRoom")
                .put("action", "leave")
                .put("title", chatRoom)
                .put("username", username)
                .put("timestamp", System.currentTimeMillis())
                .toString()
        )
    }

    fun sendFile(chatRoom: String, chatFile: ChatFile) {
        write(
            chatFile.toJSONObject()
                .put("type", "file")
                .put("username", username)
                .put("chatRoom", chatRoom)
                .put("timestamp", System.currentTimeMillis())
                .toString()
        )
    }
}
