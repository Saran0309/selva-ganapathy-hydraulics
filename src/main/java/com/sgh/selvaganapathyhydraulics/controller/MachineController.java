package com.sgh.selvaganapathyhydraulics.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sgh.selvaganapathyhydraulics.Repository.DeliveredMachineRepository;
import com.sgh.selvaganapathyhydraulics.Repository.MachineDetailsRepository;
import com.sgh.selvaganapathyhydraulics.Repository.MachineVideosRepository;
import com.sgh.selvaganapathyhydraulics.entity.DeliveredMachines;
import com.sgh.selvaganapathyhydraulics.entity.MachineDetails;
import com.sgh.selvaganapathyhydraulics.entity.MachineVideos;

@RestController
@RequestMapping("/api/machines")

@CrossOrigin(origins = "*") // Allow frontend access
public class MachineController {

    private final MachineDetailsRepository machineDetailsRepository;
    private final MachineVideosRepository machineVideosRepository;
    private final DeliveredMachineRepository deliveredMachineRepository;

    public MachineController(MachineDetailsRepository machineDetailsRepository, MachineVideosRepository machineVideosRepository, DeliveredMachineRepository deliveredMachineRepository) {
        this.machineDetailsRepository = machineDetailsRepository;
        this.machineVideosRepository = machineVideosRepository;
        this.deliveredMachineRepository = deliveredMachineRepository;
    }

    @GetMapping("getMachineByParentType")
    public List<MachineDetails> getMachines(@RequestParam(required = true) String parentType) {
        if (parentType != null) {
            return machineDetailsRepository.findByParentType(parentType);
        }
        return null;
    }

    @GetMapping("getMachineNamesByParentType")
    public List<String> getAllMachineNamesForFilter(@RequestParam(required = true) String parentType) {
        List<MachineDetails> machines = new ArrayList<>();

        if (parentType != null && !parentType.isEmpty()) {
            machines = machineDetailsRepository.findByParentType(parentType);
        }
        // Extract and return only the machine names
        return machines.stream()
                .map(MachineDetails::getType) // Assuming the name field is name
                .collect(Collectors.toList());
    }

    @GetMapping("/getMachineDetails")
    public List<MachineDetails> getMachineDetails(@RequestParam(required = true) String name, @RequestParam(required = true) String parentType) {
        if (name != null && parentType != null) {
            return machineDetailsRepository.findByNameIgnoreCaseAndParentTypeIgnoreCase(name, parentType);
        }
        return new ArrayList<>();
    }

    @GetMapping("/getAllMachineVideos")
    public List<MachineVideos> getAllMachineVideos() {
        return machineVideosRepository.findAll();
    }

    @GetMapping("/getAllDeliveredMachine")
    public List<DeliveredMachines> getAllDeliveredMachines() {
        return deliveredMachineRepository.findAll();
    }

    @PostMapping("/addNewMachineVideo")
    public MachineVideos saveNewMachineVideo(@RequestBody MachineVideos machineVideo) {

        return machineVideosRepository.save(machineVideo);
    }

    @PutMapping("/updateMachineVideo/{id}")
    public MachineVideos updateMachineVideo(@PathVariable String id, @RequestBody MachineVideos machineVideo) {

        Optional<MachineVideos> existingVideo = machineVideosRepository.findById(id);

        if (existingVideo.isPresent()) {
            MachineVideos videoToUpdate = existingVideo.get();
            videoToUpdate.setName(machineVideo.getName());
            videoToUpdate.setThumbnail(machineVideo.getThumbnail());
            videoToUpdate.setVideoLink(machineVideo.getVideoLink());
            videoToUpdate.setDuration(machineVideo.getDuration());
            videoToUpdate.setDescription(machineVideo.getDescription());

            return machineVideosRepository.save(videoToUpdate);
        }
        throw new RuntimeException("Video with ID " + id + " not found");
    }

    @DeleteMapping("/deleteMachineVideo/{id}")
    public ResponseEntity<String> deleteMachineVideo(@PathVariable String id) {
        Optional<MachineVideos> existingVideo = machineVideosRepository.findById(id);

        if (existingVideo.isPresent()) {
            machineVideosRepository.deleteById(id);  // Deletes the video
            return new ResponseEntity<>("Video with ID " + id + " has been deleted.", HttpStatus.OK);
        }

        return new ResponseEntity<>("Video with ID " + id + " not found.", HttpStatus.NOT_FOUND);
    }


    @PostMapping("/addDeliveredMachine")
    public ResponseEntity<String> saveNewDeliveredMachine(@RequestBody DeliveredMachines deliveredMachine) {
        deliveredMachineRepository.save(deliveredMachine);
        return new ResponseEntity<>("Delivered Machine saved successfully.", HttpStatus.CREATED);
    }

    // Method to update an existing DeliveredMachine
    @PutMapping("/updateDeliveredMachine/{id}")
    public ResponseEntity<String> updateDeliveredMachine(@PathVariable String id, @RequestBody DeliveredMachines updatedMachine) {
        Optional<DeliveredMachines> existingMachine = deliveredMachineRepository.findById(id);

        if (existingMachine.isPresent()) {
            DeliveredMachines machine = existingMachine.get();
            machine.setCustomerName(updatedMachine.getCustomerName());
            machine.setLocation(updatedMachine.getLocation());
            machine.setMachineName(updatedMachine.getMachineName());
            machine.setImage(updatedMachine.getImage());
            machine.setDeliveryDate(updatedMachine.getDeliveryDate());
            machine.setDescription(updatedMachine.getDescription());
            machine.setWorkingMechanism(updatedMachine.getWorkingMechanism());

            deliveredMachineRepository.save(machine);  // Save updated machine
            return new ResponseEntity<>("Delivered Machine updated successfully.", HttpStatus.OK);
        }

        return new ResponseEntity<>("Delivered Machine not found.", HttpStatus.NOT_FOUND);
    }

    // Method to delete a DeliveredMachines by ID
    @DeleteMapping("/deleteDeliveredMachine/{id}")
    public ResponseEntity<String> deleteDeliveredMachine(@PathVariable String id) {
        Optional<DeliveredMachines> existingMachine = deliveredMachineRepository.findById(id);

        if (existingMachine.isPresent()) {
            deliveredMachineRepository.deleteById(id);  // Delete the machine
            return new ResponseEntity<>("Delivered Machine with ID " + id + " deleted.", HttpStatus.OK);
        }

        return new ResponseEntity<>("Delivered Machine not found.", HttpStatus.NOT_FOUND);
    }


    // Update an existing machine detail
    @PutMapping("/updateMachine/{id}")
    public ResponseEntity<MachineDetails> updateMachine(@PathVariable String id, @RequestBody MachineDetails machineDetails) {
        if(getMachineDetails(machineDetails.getName(), machineDetails.getParentType()).size() > 1){
            return  new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        if (machineDetailsRepository.existsById(id)) {
            machineDetails.setId(id);
            MachineDetails updatedMachine = machineDetailsRepository.save(machineDetails);
            return new ResponseEntity<>(updatedMachine, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Delete a machine by ID
    @DeleteMapping("/deleteMachine/{id}")
    public ResponseEntity<HttpStatus> deleteMachine(@PathVariable String id) {
        if (machineDetailsRepository.existsById(id)) {
            machineDetailsRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    // Get a machine detail by ID
    @GetMapping("/get/{id}")
    public ResponseEntity<MachineDetails> getMachineById(@PathVariable String id) {
        Optional<MachineDetails> machine = machineDetailsRepository.findById(id);
        return machine.map(machineDetails -> new ResponseEntity<>(machineDetails, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/addNewMachine")
    public ResponseEntity<MachineDetails> addMachine(@RequestBody MachineDetails machineDetails) {
        MachineDetails savedMachine = machineDetailsRepository.save(machineDetails);
        return new ResponseEntity<>(savedMachine, HttpStatus.CREATED);
    }

}