import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;

public class MessageApp {
    private Contact[] contacts = new Contact[200];
    private Sms[][] chats = new Sms[200][200];
    private final String senderName = "Maham";
    private final String senderNumber = "03008812484";
    private static int contactCount = 0;
    private int[] chatCount = new int[200];

    {
        preaddContact("Komal", "03221836653");
        preaddContact("Sadaf", "03239884711");
        preaddContact("Abeera", "03219348471");
        preaddContact("Iqra", "03218384692");
        preaddContact("Huma", "03008162665");
    }

    public void preaddContact(String name, String phoneNumber) {
        contacts[contactCount] = new Contact(Integer.toString(contactCount + 1), name, phoneNumber);
        contactCount++;
    }

    public String addContact(String name, String phoneNumber) {
        contacts[contactCount] = new Contact(Integer.toString(contactCount + 1), name, phoneNumber);
        contactCount++;
        return "Contact added: " + name;
    }

    private int findContactIndexByName(String name) {
        for (int i = 0; i < contactCount; i++) {
            if (contacts[i].getName().equalsIgnoreCase(name))
                return i;
        }
        return -1;
    }

    public String displayContacts() {
        if (contactCount == 0) {
            return "No Contact Found";
        }

        Arrays.sort(contacts, 0, contactCount, Comparator.comparing(Contact::getContactId));
        StringBuilder contactList = new StringBuilder();
        contactList.append("========================================\n")
                .append("              Contact List\n")
                .append("========================================\n");

        for (int i = 0; i < contactCount; i++) {
            contactList.append(contacts[i].toString()).append("\n");
        }
        contactList.append("========================================");
        return contactList.toString();
    }

    public String deleteContact(String name) {
        int index = findContactIndexByName(name);
        if (index != -1) {
            for (int i = index; i < contactCount - 1; i++) {
                contacts[i] = contacts[i + 1];
            }
            contacts[--contactCount] = null;
            return "Contact deleted successfully.";
        } else {
            return "Contact not found.";
        }
    }

    public String sendMessage(String name, String messageText) {
        int index = findContactIndexByName(name);
        if (index != -1) {
            Sms text = new Sms(messageText);
            chats[index][chatCount[index]++] = text;
            return "Message sent to " + name + ":\n  ID: " + text.getMessageId() + " | Message: " + messageText;
        } else {
            return "Contact not found.";
        }
    }

    private void sortMessagesByTime(Sms[] messages) {
        Arrays.sort(messages, Comparator.comparing(Sms::getDateTime).reversed());
    }

    public String displayChats() {
        if (contactCount == 0) {
            return "No chats available.";
        }

        StringBuilder allChats = new StringBuilder();
        allChats.append("========================================\n")
                .append("               All Chats\n")
                .append("========================================\n");

        for (int i = 0; i < contactCount; i++) {
            allChats.append("Contact: ").append(contacts[i].getName()).append("\n");
            Sms[] messages = Arrays.copyOf(chats[i], chatCount[i]);
            sortMessagesByTime(messages);

            for (Sms message : messages) {
                allChats.append("  ID: ").append(message.getMessageId())
                        .append(" | Message: ").append(message.getMessageContent())
                        .append(" | Status: ").append(message.messagestatus())
                        .append(" | Sent: ").append(message.getDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                        .append("\n");
                message.markAsRead();
            }
            allChats.append("----------------------------------------\n");
        }
        allChats.append("========================================");
        return allChats.toString();
    }

    public String deleteMessage(String name, String messageId) {
        int index = findContactIndexByName(name);
        if (index != -1) {
            for (int i = 0; i < chatCount[index]; i++) {
                if (chats[index][i].getMessageId().equals(messageId)) {
                    for (int j = i; j < chatCount[index] - 1; j++) {
                        chats[index][j] = chats[index][j + 1];
                    }
                    chats[index][--chatCount[index]] = null;
                    return "Message with ID " + messageId + " deleted successfully.";
                }
            }
            return "Message ID not found.";
        } else {
            return "Contact not found.";
        }
    }
}
