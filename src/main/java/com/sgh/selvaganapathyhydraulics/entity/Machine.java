package com.sgh.selvaganapathyhydraulics.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Map;

@Document(collection = "machines")
public class Machine {
    @Id
    private String id;
    private String name;
    private List<String> images; // Base64 encoded strings
    private Map<String, String> keySpecs;
    private String description;
    private String parentType;
    private String type;
    public Map<String, String> getKeySpecs() {
        return keySpecs;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setKeySpecs(Map<String, String> keySpecs) {
        this.keySpecs = keySpecs;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
