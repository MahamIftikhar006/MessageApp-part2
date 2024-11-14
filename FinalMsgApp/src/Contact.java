public class Contact implements Comparable<Contact>{
    private String contactId;
    private String name;
    private String phoneNumber;

    public Contact(String contactId, String name, String phoneNumber) {
        this.contactId = contactId;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getContactId() {
        return contactId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public int compareTo(Contact other) {
        return this.contactId.compareToIgnoreCase(other.contactId);
    }

    @Override
    public String toString() {
        return String.format("Contact ID: %s, Name: %s, Phone Number: %s", contactId, name, phoneNumber);
    }
}


