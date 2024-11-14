import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sms {
    private static int messageCounter = 1;
    private final String messageId;
    private final String messageContent;
    private final LocalDateTime dateTime;
    private String status;

    public Sms(String messageContent) {
        this.messageId = String.format("MSG%04d", messageCounter++);
        this.messageContent = messageContent;
        this.dateTime = LocalDateTime.now();
        this.status = "UNREAD";
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String messagestatus() {
        return status;
    }

    public void markAsRead() {
        if (status.equals("READ")) {
            status = "READ";
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return "Message ID: " + messageId +
                " | Status: " + status +
                " | Sent: " + dateTime.format(formatter) +
                "\nContent: " + messageContent;
    }
}
