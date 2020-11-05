package com.sysbot32.netptalk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String host;
    private int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() {
        try {
            socket = new Socket(host, port);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
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

        try {
            bufferedWriter.write(str);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
