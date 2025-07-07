package com.sgh.selvaganapathyhydraulics.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sgh.selvaganapathyhydraulics.Repository.DeliveredMachineRepository;
import com.sgh.selvaganapathyhydraulics.Repository.MachineDetailsRepository;
import com.sgh.selvaganapathyhydraulics.Repository.MachineRepository;
import com.sgh.selvaganapathyhydraulics.Repository.MachineVideosRepository;
import com.sgh.selvaganapathyhydraulics.entity.DeliveredMachines;
import com.sgh.selvaganapathyhydraulics.entity.Machine;
import com.sgh.selvaganapathyhydraulics.entity.MachineDetails;
import com.sgh.selvaganapathyhydraulics.entity.MachineVideos;
import com.sgh.selvaganapathyhydraulics.helper.Translator;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/machines")
@Slf4j
@CrossOrigin(origins = "*") // Allow frontend access
public class MachineController {

    private final MachineRepository machineRepository;
    private final MachineDetailsRepository machineDetailsRepository;
    private final MachineVideosRepository machineVideosRepository;
    @Autowired
    Translator langTranslator;

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MachineController.class);
    
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
    
    @GetMapping("getMachineByParentTypeByLang")
    public List<Machine> getMachinesByLang(@RequestParam(required = true) String parentType) {
    	List<Machine> parentTypeMachines = new ArrayList<>();
    	if (parentType != null) {
    		parentTypeMachines= machineRepository.findByParentType(parentType);
        }
    	return (List<Machine>) langTranslator.translateObject(parentTypeMachines, "ta");
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
