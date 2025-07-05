package com.sgh.selvaganapathyhydraulics.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "deliveredMachines")
public class DeliveredMachines {
    @Id
    private String id; // MongoDB auto-generated _id field
    private String customerName; // Customer name
    private String location; // Location of the customer
    private String machineName; // The machine being delivered
    private String image; // Base64 encoded image
    private String deliveryDate; // Delivery date
    private String description; // Description of the machine
    private String workingMechanism; // How the machine works

    // Default constructor
    public DeliveredMachines() {
    }

    // Constructor
    public DeliveredMachines(String id, String customerName, String location, String machine, String image, String deliveryDate, String description, String workingMechanism) {
        this.id = id;
        this.customerName = customerName;
        this.location = location;
        this.machineName = machine;
        this.image = image;
        this.deliveryDate = deliveryDate;
        this.description = description;
        this.workingMechanism = workingMechanism;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
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

}