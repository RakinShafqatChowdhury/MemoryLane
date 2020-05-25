package model;

import com.google.firebase.Timestamp;

public class Memory {
    private String title;
    private String desc;
    private String imageUrl;
    private Timestamp timeAdded;
    private String userName;
    private String userId;

    public Memory() {
    }

    public Memory(String title, String desc, String imageUrl, Timestamp timeAdded, String userName, String userId) {
        this.title = title;
        this.desc = desc;
        this.imageUrl = imageUrl;
        this.timeAdded = timeAdded;
        this.userName = userName;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
