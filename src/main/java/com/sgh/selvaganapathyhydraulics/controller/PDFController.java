package com.sgh.selvaganapathyhydraulics.controller;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sgh.selvaganapathyhydraulics.Repository.MachineDetailsRepository;
import com.sgh.selvaganapathyhydraulics.entity.MachineDetails;
import com.sgh.selvaganapathyhydraulics.helper.pdfGenerator;


@Controller
public class PDFController {
	
	@Autowired
	MachineDetailsRepository machineDetailsRepository;

	@GetMapping("/api/generate-pdf")
	public ResponseEntity<String> generatePdf(
	        @RequestParam(required = true) String name,
	        @RequestParam(required = true) String parentType,
	        @RequestParam(required = true) String recipient) throws IOException {

	    Map<String, Object> model = new HashMap<>();
	    Optional<MachineDetails> machineDetails = getMachineDetailsByNameAndParent(name, parentType);

	    if (machineDetails.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Machine details not found.");
	    }

	    Map<String, String> map = machineDetails.get().getKeySpecs();
	    List<String> items = map.entrySet().stream()
	            .map(entry -> entry.getKey() + " - " + entry.getValue())
	            .collect(Collectors.toList());

	    model.put("items", items);
	    model.put("amount", machineDetails.get().getTotalPrice());
	    model.put("date", LocalDate.now());
	    model.put("bankDetails", "STATE BANK OF INDIA, A/C NO: 34567494532 ...");
	    model.put("recipient", recipient);

	    ByteArrayOutputStream baos = pdfGenerator.generate("invoice", model);

	    // Convert PDF to Base64
	    String base64EncodedPdf = Base64.getEncoder().encodeToString(baos.toByteArray());

	    return ResponseEntity.ok()
	            .contentType(MediaType.TEXT_PLAIN) 
	            .body(base64EncodedPdf);
	}

	
	public Optional<MachineDetails> getMachineDetailsByNameAndParent(String name, String parentType) {
		return machineDetailsRepository.findByNameAndParentType(name,parentType);
		
		
	}

}