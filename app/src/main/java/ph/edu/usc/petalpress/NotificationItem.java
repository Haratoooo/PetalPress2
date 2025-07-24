package ph.edu.usc.petalpress;

// NotificationItem.java
public class NotificationItem {
    private final String title;
    private final String description;
    private final String timestamp;

    public NotificationItem(String title, String description, String timestamp) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getTimestamp() { return timestamp; }
}
