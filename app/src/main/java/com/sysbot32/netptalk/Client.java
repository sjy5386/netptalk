package com.sysbot32.netptalk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {
    private static Client client = new Client();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private Client() {
        socket = null;
    }

    public static Client getInstance() {
        return client;
    }

    public void connect(String host, int port) {
        if (socket != null) {
            return;
        }

        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void disconnect() {
        if (socket == null) {
            return;
        }

        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bufferedReader = null;
        bufferedWriter = null;
        socket = null;
    }

    public String read() {
        if (bufferedReader == null) {
            return null;
        }

        try {
            return bufferedReader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void write(String str) {
        if (bufferedWriter == null) {
            return;
        }

        new Thread(() -> {
            try {
                bufferedWriter.write(str);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public boolean isConnected() {
        if (socket == null) {
            return false;
        }

        return socket.isConnected();
    }
}
