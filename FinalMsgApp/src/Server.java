import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    private MessageApp messageApp;
    private Scanner scanner;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        messageApp = new MessageApp();
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Server is running... Waiting for client to connect...");
        try {
            clientSocket = serverSocket.accept();
            System.out.println("Client connected from: " + clientSocket.getInetAddress());

            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);

            Thread clientListenerThread = new Thread(new ClientListener());
            clientListenerThread.start();

            handleServerCommands();
        } catch (IOException e) {
            System.out.println("Error in server operation: " + e.getMessage());
        }
    }

    private class ClientListener implements Runnable {
        @Override
        public void run() {
            listenForClientMessages();
        }
    }

    private void listenForClientMessages() {
        try {
            String clientMessage;
            while ((clientMessage = input.readLine()) != null) {
                System.out.println("Client: " + clientMessage);
                String response = handleClientMessage(clientMessage);
                output.println(response);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected or connection closed.");
        }
    }

    private String handleClientMessage(String clientMessage) {
        String[] parts = clientMessage.split(" : ", 2);
        if (parts.length < 2) {
            return "Invalid command.";
        }

        String command = parts[0].trim();
        String args = parts[1].trim();

        switch (command) {
            case "sendmessage":
                String[] messageParts = args.split(" : ", 2);
                if (messageParts.length < 2) return "Invalid message format.";
                String contactName = messageParts[0].trim();
                String messageText = messageParts[1].trim();
                return messageApp.sendMessage(contactName, messageText);

            case "displaychats":
                return "CHAT_LIST: " + messageApp.displayChats();

            case "deletemessage":
                String[] deleteParts = args.split(" : ", 2);
                if (deleteParts.length < 2) return "Invalid message format.";
                String delContactName = deleteParts[0].trim();
                String messageId = deleteParts[1].trim();
                return messageApp.deleteMessage(delContactName, messageId);

            default:
                return "Invalid command.";
        }
    }

    private void handleServerCommands() {
        while (true) {
            System.out.println("Server Menu:\n1. Add contact\n2. Display contacts\n3. Send message\n4. Delete contact\n5. Display chats\n6. Delete message\n7. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String command;
            switch (choice) {
                case 1:
                    System.out.print("Enter contact name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter contact phone: ");
                    String phone = scanner.nextLine();
                    System.out.println(messageApp.addContact(name, phone));
                    break;

                case 2:
                    System.out.println("CONTACT_LIST: " + messageApp.displayContacts());
                    break;

                case 3:
                    System.out.print("Enter contact name to message: ");
                    String contactName = scanner.nextLine();
                    System.out.print("Enter your message: (type 0 to exit)");
                    while (true) {
                        System.out.print("Message: ");
                        String messageText = scanner.nextLine();
                        if ("0".equals(messageText)) break;
                        command = "sendmessage : " + contactName + " : " + messageText;
                        processAndSendToClient(command);
                    }
                    break;

                case 4:
                    System.out.print("Enter contact name to delete: ");
                    String delName = scanner.nextLine();
                    System.out.println(messageApp.deleteContact(delName));
                    break;

                case 5:
                    System.out.println("CHAT_LIST: " + messageApp.displayChats());
                    break;

                case 6:
                    System.out.print("Enter contact name for message deletion: ");
                    String contact = scanner.nextLine();
                    System.out.print("Enter message ID to delete: ");
                    String messageId = scanner.nextLine();
                    command = "deletemessage : " + contact + " : " + messageId;
                    processAndSendToClient(command);
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

    private void processAndSendToClient(String command) {
        String response = handleClientMessage(command);
        System.out.println("Server : " + response);
        output.println(response);
    }

    private void closeResources() {
        try {
            if (scanner != null) scanner.close();
            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error closing server resources: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(12345);
            server.start();
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }
}
