package chatapp;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private ChatServerConsole server;
    private BufferedReader in;
    private PrintWriter out;
    private String name;

    public ClientHandler(Socket socket, ChatServerConsole server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            name = in.readLine();
            System.out.println(name + " connected.");
            server.addClient(this);

            Message welcome = new Message("ChatBot", "Welcome " + name + "! Type 'help' to see commands.");
            server.broadcast(welcome);

            String text;
            while ((text = in.readLine()) != null) {
                Message msg = new Message(name, text);
                server.sendMessage(msg);
            }

        } catch (IOException e) {
            System.out.println(name + " connection error: " + e.getMessage());
        } finally {
            server.remove(this);
            try { if (in != null) in.close(); } catch (IOException ignored) {}
            if (out != null) out.close();
            try { if (socket != null && !socket.isClosed()) socket.close(); } catch (IOException ignored) {}
            System.out.println(name + " disconnected.");
        }
    }

    public void send(Message msg) {
        if (out != null) out.println(msg.sender + ": " + msg.text);
    }
}
