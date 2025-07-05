package com.sgh.selvaganapathyhydraulics.controller;

import com.sgh.selvaganapathyhydraulics.Repository.DeliveredMachineRepository;
import com.sgh.selvaganapathyhydraulics.Repository.MachineVideosRepository;
import com.sgh.selvaganapathyhydraulics.Repository.MachineDetailsRepository;
import com.sgh.selvaganapathyhydraulics.Repository.MachineRepository;
import com.sgh.selvaganapathyhydraulics.entity.DeliveredMachines;
import com.sgh.selvaganapathyhydraulics.entity.MachineVideos;
import com.sgh.selvaganapathyhydraulics.entity.Machine;
import com.sgh.selvaganapathyhydraulics.entity.MachineDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/machines")
@CrossOrigin(origins = "*") // Allow frontend access
public class MachineController {

    private final MachineRepository machineRepository;
    private final MachineDetailsRepository machineDetailsRepository;
    private final MachineVideosRepository machineVideosRepository;

    private final DeliveredMachineRepository deliveredMachineRepository;
    public MachineController(MachineRepository machineRepository, MachineDetailsRepository machineDetailsRepository, MachineVideosRepository machineVideosRepository, DeliveredMachineRepository deliveredMachineRepository) {
        this.machineRepository = machineRepository;
        this.machineDetailsRepository =machineDetailsRepository;
        this.machineVideosRepository = machineVideosRepository;
        this.deliveredMachineRepository = deliveredMachineRepository;
    }

    @GetMapping("getMachineByParentType")
    public List<Machine> getMachines(@RequestParam(required = true) String parentType) {
        if (parentType != null) {
            return machineRepository.findByParentType(parentType);
        }
        return machineRepository.findAll();
    }

    @GetMapping("getMachineNamesByParentType")
    public List<String> getAllMachineNamesForFilter(@RequestParam(required = true) String parentType) {
        List<Machine> machines = new ArrayList<>();

        if (parentType != null && !parentType.isEmpty()) {
            machines = machineRepository.findByParentType(parentType);
        }
        // Extract and return only the machine names
        return machines.stream()
                .map(Machine::getName) // Assuming the name field is `name`
                .collect(Collectors.toList());
    }

    @GetMapping("/getMachineDetails")
    public List<MachineDetails> getMachineDetails(@RequestParam(required = true) String name, @RequestParam(required = true) String parentType) {
        if (name != null && parentType != null) {

            return machineDetailsRepository.findByNameContainingIgnoreCaseAndParentTypeContainingIgnoreCase(name, parentType);
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

}
