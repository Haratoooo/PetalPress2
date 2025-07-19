package ph.edu.usc.petalpress;

public class Entry {
    private String title;
    private String dateLabel;
    private String timeAgo;
    private int mood1ResId;
    private int mood2ResId;

    public Entry(String title, String dateLabel, String timeAgo, int mood1ResId, int mood2ResId) {
        this.title = title;
        this.dateLabel = dateLabel;
        this.timeAgo = timeAgo;
        this.mood1ResId = mood1ResId;
        this.mood2ResId = mood2ResId;
    }

    public String getTitle() { return title; }
    public String getDateLabel() { return dateLabel; }
    public String getTimeAgo() { return timeAgo; }
    public int getMood1ResId() { return mood1ResId; }
    public int getMood2ResId() { return mood2ResId; }
}

