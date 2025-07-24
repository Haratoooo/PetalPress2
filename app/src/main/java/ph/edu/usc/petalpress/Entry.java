package ph.edu.usc.petalpress;

public class Entry {
    public String id;
    public String journalId;
    public String title;
    public String content;
    public String createdAt;
    public String mood1;
    public String mood2;
    public String moodTag;

    public String dateLabel;
    public String timeAgo;
    public int mood1ResId;
    public int mood2ResId;

    public Entry() {}

    public Entry(String title, String content, String dateLabel, String timeAgo,
                 int mood1ResId, int mood2ResId, String moodTag) {
        this.title = title;
        this.content = content;
        this.dateLabel = dateLabel;
        this.timeAgo = timeAgo;
        this.mood1ResId = mood1ResId;
        this.mood2ResId = mood2ResId;
        this.moodTag = moodTag;
    }

    public String getId() {
        return id;
    }

    public String getJournalId() {
        return journalId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getMood1() {
        return mood1;
    }

    public String getMood2() {
        return mood2;
    }

    public String getMoodTag() {
        return moodTag;
    }

    public String getDateLabel() {
        return dateLabel;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public int getMood1ResId() {
        return mood1ResId;
    }

    public int getMood2ResId() {
        return mood2ResId;
    }

    // You can add getters if needed for use in adapters
}
