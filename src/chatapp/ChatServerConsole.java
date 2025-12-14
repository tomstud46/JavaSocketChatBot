package chatapp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServerConsole {

    private List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private ChatDAO dao = new ChatDAO();

    public static void main(String[] args) {
        try {
            new ChatServerConsole().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(6000);
            System.out.println("Server started on port 6000");

            ServerSocket finalServer = server;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (!finalServer.isClosed()) finalServer.close();
                    synchronized (clients) {
                        for (ClientHandler c : clients) {
                            c.send(new Message("Server", "Server is shutting down."));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            while (true) {
                Socket s = server.accept();
                ClientHandler ch = new ClientHandler(s, this);
                new Thread(ch).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null && !server.isClosed()) {
                try { server.close(); } catch (IOException e) { e.printStackTrace(); }
            }
        }
    }

    public void addClient(ClientHandler ch) { clients.add(ch); }
    public void remove(ClientHandler ch) { clients.remove(ch); }

    public void sendMessage(Message msg) {
        dao.saveMessage(msg);
        broadcast(msg);
        Message botReply = ChatBot.reply(msg);
        if (botReply != null) broadcast(botReply);
    }

    public void broadcast(Message msg) {
        synchronized (clients) {
            for (ClientHandler c : clients) c.send(msg);
        }
    }
}


