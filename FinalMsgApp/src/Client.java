import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private Scanner sc;
    private MessageApp messageApp;

    public Client(String serverAddress, int port) throws IOException {
        socket = new Socket(serverAddress, port);
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
        sc = new Scanner(System.in);
        messageApp = new MessageApp();
    }

    public void start() {
        System.out.println("Client connected to server!");

        Thread serverListenerThread = new Thread(new ServerListener());
        serverListenerThread.start();

        handleClientCommands();
    }

    private class ServerListener implements Runnable {
        @Override
        public void run() {
            listenForServerMessages();
        }
    }

    private void listenForServerMessages() {
        try {
            String serverMessage;
            while ((serverMessage = input.readLine()) != null) {
                System.out.println("\n" + serverMessage);
            }
        } catch (IOException e) {
            System.out.println("Connection closed.");
        }
    }

    private void handleClientCommands() {
        while (true) {
            System.out.println("\nClient Menu:\n1. Add contact\n2. Display contacts\n3. Send message\n4. Delete contact\n5. Display chats\n6. Delete message\n7. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            String command;
            switch (choice) {
                case 1:
                    System.out.print("Enter contact name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter contact phone: ");
                    String phone = sc.nextLine();
                    System.out.println(messageApp.addContact(name, phone));
                    break;
                case 2:
                    System.out.println("CONTACT_LIST: " + messageApp.displayContacts());
                    break;
                case 3:
                    System.out.print("Enter contact name to message: ");
                    String contactName = sc.nextLine();
                    System.out.print("Enter your message: (type 0 to exit)");
                    while (true) {
                        System.out.print("Message: ");
                        String messageText = sc.nextLine();
                        if ("0".equals(messageText)) break;
                        command = "sendmessage : " + contactName + " : " + messageText;
                        sendCommandToServer(command);
                    }
                    break;

                case 4:
                    System.out.print("Enter contact name to delete: ");
                    String delName = sc.nextLine();
                    System.out.println(messageApp.deleteContact(delName));
                    break;

                case 5:
                    command = "displaychats : ";
                    sendCommandToServer(command);
                    break;
                case 6:
                    System.out.print("Enter contact name for message deletion: ");
                    String contact = sc.nextLine();
                    System.out.print("Enter message ID to delete: ");
                    String messageId = sc.nextLine();
                    command = "deletemessage : " + contact + " : " + messageId;
                    sendCommandToServer(command);
                    break;
                case 7:
                    System.out.println("Exiting...");
                    closeResources();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void sendCommandToServer(String command) {
        output.println(command);
    }

    private void closeResources() {
        try {
            if (sc != null) sc.close();
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.out.println("Error closing client resources: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("Enter server IP address: ");
            Scanner scanner = new Scanner(System.in);
            String serverIp = scanner.nextLine();
            Client client = new Client(serverIp, 12345);
            client.start();
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }
}
