package com.sysbot32.netptalk

import org.json.JSONObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ChatClient(client: Client) {
    private val client: Client = client
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

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
        }
    }
}
