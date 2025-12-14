package chatapp;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClientConsole {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter username: ");
        String name = sc.nextLine().trim();

        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            socket = new Socket("localhost", 6000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // send username
            out.println(name);

            BufferedReader reader = in; // safe final reference

            // Thread to receive messages
            Thread receiver = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = reader.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            });
            receiver.start();

            // Main send loop
            while (true) {
                String text = sc.nextLine();
                if (text.equalsIgnoreCase("exit")) break;
                out.println(text);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // CLEAN shutdown (no leaks)
            try {
                if (in != null) in.close();
            } catch (IOException ignored) {}

            if (out != null) out.close();

            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException ignored) {}

            sc.close();
            System.out.println("Client disconnected.");
        }
    }
}
