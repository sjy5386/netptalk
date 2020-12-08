package com.sysbot32.netptalk;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    private static Client client = new Client();

    private SocketChannel socket;

    private Client() {
        socket = null;
    }

    public static Client getInstance() {
        return client;
    }

    public void connect(String host, int port) {
        connect(new InetSocketAddress(host, port));
    }

    public void connect(SocketAddress socketAddress) {
        if ((socket == null) || !socket.isOpen()) {
            try {
                socket = SocketChannel.open();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        new Thread(() -> {
            try {
                socket.connect(socketAddress);
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
        socket = null;
    }

    public ByteBuffer read() {
        if ((socket == null) || !socket.isConnected()) {
            return null;
        }

        ByteBuffer buf = ByteBuffer.allocate(4);
        ByteBuffer data;
        try {
            if (socket.read(buf) < 0) {
                return null;
            }
            buf.flip();
            int size = buf.getInt();
            data = ByteBuffer.allocate(size);
            while (data.hasRemaining()) {
                socket.read(data);
            }
            data.flip();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return data;
    }

    public void write(ByteBuffer data) {
        if ((socket == null) || !socket.isConnected()) {
            return;
        }

        new Thread(() -> writing(data)).start();
    }

    private synchronized void writing(ByteBuffer data) {
        int size = data.capacity();
        ByteBuffer buf = ByteBuffer.allocate(4 + size);
        buf.putInt(size);
        buf.put(data);
        buf.flip();
        try {
            socket.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        if (socket == null) {
            return false;
        }

        return socket.isConnected();
    }

    public boolean waitForConnection(int timeout) {
        long start = System.currentTimeMillis();
        while (!client.isConnected() && (System.currentTimeMillis() - start <= timeout)) ;
        return client.isConnected();
    }
}
