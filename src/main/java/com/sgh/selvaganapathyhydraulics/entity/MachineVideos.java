package com.sgh.selvaganapathyhydraulics.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "machineVideos") // Specify the MongoDB collection name
public class MachineVideos {

    @Id
    private String id; // MongoDB auto-generated _id field
    private String name; // Name of the machine video
    private String thumbnail; // Base64 encoded image (or you can store the image URL if needed)
    private String videoLink; // Link to the video (e.g., YouTube)
    private String duration; // Duration of the video
    private String description; // Description of the video

    // Default constructor
    public MachineVideos() {}

    // Constructor
    public MachineVideos(String id, String name, String thumbnail, String videoLink, String duration, String description) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
        this.videoLink = videoLink;
        this.duration = duration;
        this.description = description;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "MachineVideo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", videoLink='" + videoLink + '\'' +
                ", duration='" + duration + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
