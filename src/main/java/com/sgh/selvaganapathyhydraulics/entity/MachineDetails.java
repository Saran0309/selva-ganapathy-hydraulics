package com.sgh.selvaganapathyhydraulics.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Map;

@Document(collection = "machinesDetails") // Specify the MongoDB collection
public class MachineDetails {

    @Id
    private String id; // This maps to the "id" field in the JSON (electric-woodbreaker-standard)

    private String name;
    private List<String> images; // List of base64 images
    private Map<String, String> keySpecs; // Key specifications with dynamic key-value pairs
    private String description;
    private String workingMechanism;

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    private String parentType; // Assuming parentType is the machine category/type
    private String type;
    private String totalPrice;
    private Map<String, String> videoLink;// base 64 , video link
    // Constructors, getters, and setters

    public MachineDetails() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(Map<String, String> videoLink) {
        this.videoLink = videoLink;
    }

    public MachineDetails(String id, String name, String parentType, List<String> images, Map<String, String> keySpecs, String description, String workingMechanism) {
        this.id = id;
        this.name = name;
        this.parentType = parentType;
        this.images = images;
        this.keySpecs = keySpecs;
        this.description = description;
        this.workingMechanism = workingMechanism;
    }

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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Map<String, String> getKeySpecs() {
        return keySpecs;
    }

    public void setKeySpecs(Map<String, String> keySpecs) {
        this.keySpecs = keySpecs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkingMechanism() {
        return workingMechanism;
    }

    public void setWorkingMechanism(String workingMechanism) {
        this.workingMechanism = workingMechanism;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    @Override
    public String toString() {
        return "MachineDetails{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", images=" + images +
                ", keySpecs=" + keySpecs +
                ", description='" + description + '\'' +
                ", workingMechanism='" + workingMechanism + '\'' +
                ", parentType='" + parentType + '\'' +
                '}';
    }
}