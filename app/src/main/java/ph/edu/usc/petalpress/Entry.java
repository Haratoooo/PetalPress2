package ph.edu.usc.petalpress;

public class Entry {
    private String title;
    private String content;
    private String dateLabel;
    private String timeAgo;
    private int mood1ResId;
    private int mood2ResId;
    private String moodTag;

    public Entry(String title, String content, String dateLabel, String timeAgo, int mood1ResId, int mood2ResId, String moodTag) {
        this.title = title;
        this.content = content;
        this.dateLabel = dateLabel;
        this.timeAgo = timeAgo;
        this.mood1ResId = mood1ResId;
        this.mood2ResId = mood2ResId;
        this.moodTag = moodTag;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getDateLabel() { return dateLabel; }
    public String getTimeAgo() { return timeAgo; }
    public int getMood1ResId() { return mood1ResId; }
    public int getMood2ResId() { return mood2ResId; }
    public String getMoodTag() { return moodTag; }
}
