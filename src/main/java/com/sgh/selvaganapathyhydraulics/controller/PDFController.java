package com.sgh.selvaganapathyhydraulics.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import com.sgh.selvaganapathyhydraulics.Repository.MachineDetailsRepository;
import com.sgh.selvaganapathyhydraulics.entity.MachineDetails;
import com.sgh.selvaganapathyhydraulics.helper.pdfGenerator;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@CrossOrigin(origins = "*") // Allow frontend access
public class PDFController {

	@Autowired
	MachineDetailsRepository machineDetailsRepository;

	@GetMapping("/api/generate-pdf")
	public ResponseEntity<String> generatePdf(
			@RequestParam(required = true) String name,
			@RequestParam(required = true) String parentType,
			@RequestParam(required = true) String recipient) throws IOException {

		Map<String, Object> model = new HashMap<>();
		List<MachineDetails> machineDetailsList = getMachineDetailsByNameAndParent(name, parentType);

		if (machineDetailsList.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Machine details not found.");
		}

		// Assuming you take the first matching machine details
		MachineDetails machineDetails = machineDetailsList.get(0);

		Map<String, String> keySpecsMap = machineDetails.getKeySpecs();
		List<String> items = keySpecsMap.entrySet().stream()
				.map(entry -> {
					String key = entry.getKey();
					String value = entry.getValue();
					return (value == null || value.trim().isEmpty()) ? key : key + " - " + value;
				})
				.collect(Collectors.toList());


		model.put("items", items);
		// Create a NumberFormat instance for the default locale (it will format numbers with commas)
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);

		// Format the amount
		String formattedAmount = numberFormat.format( Long.parseLong(machineDetails.getTotalPrice()));

		model.put("amount", "Rs."  + formattedAmount + "/-"); // Format as in original
		model.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))); // Format date as 23/06/2025
		model.put("recipient", recipient);
		model.put("machineName", machineDetails.getName().toUpperCase()); // Dynamically set machine name

		// Add bank details from the original PDF
		model.put("bankName", "STATE BANK OF INDIA");
		model.put("accountNumber", "34567445932");
		model.put("accountHolderName", "A.Thanikai Arasu");
		model.put("ifscCode", "SBIN0014784");
		model.put("branchName", "Kunnathur.");

		// Embed images as Base64 data URIs
		try {
			model.put("sghLogoBase64", getImageBase64("static/SGHLogo.jpg"));
			model.put("signatureBase64", getImageBase64("static/signature.png"));
		} catch (IOException e) {
			// Handle error, maybe set default or null to prevent PDF generation failure
		}

		ByteArrayOutputStream baos = pdfGenerator.generate("invoice", model);

		// Convert PDF to Base64
		String base64EncodedPdf = Base64.getEncoder().encodeToString(baos.toByteArray());

		return ResponseEntity.ok()
				.contentType(MediaType.TEXT_PLAIN)
				.body(base64EncodedPdf);
	}

	public List<MachineDetails> getMachineDetailsByNameAndParent(String name, String parentType) {
		return machineDetailsRepository.findByNameIgnoreCaseAndParentTypeIgnoreCase(name, parentType);
	}

	private String getImageBase64(String imageName) throws IOException {
		ClassPathResource resource = new ClassPathResource(imageName);
		if (!resource.exists()) {
			// Handle case where image doesn't exist, e.g., log error or throw specific exception
			throw new IOException("Image not found: " + imageName);
		}
		try (InputStream inputStream = resource.getInputStream()) {
			byte[] imageBytes = StreamUtils.copyToByteArray(inputStream);
			String base64Image = Base64.getEncoder().encodeToString(imageBytes);
			// Determine the MIME type dynamically or hardcode if known
			String mimeType = "";
			if (imageName.endsWith(".png")) {
				mimeType = "image/png";
			} else if (imageName.endsWith(".jpg") || imageName.endsWith(".jpeg")) {
				mimeType = "image/jpeg";
			} else {
				mimeType = "application/octet-stream"; // Generic fallback
			}
			return "data:" + mimeType + ";base64," + base64Image;
		}
	}

	@GetMapping("/api/admin/generate-pdf")
	public ResponseEntity<String> generatePdf(
			@RequestParam(required = true) String name,
			@RequestParam(required = true) Map<String, String> keySpec,
			@RequestParam(required = true) String recipient, @RequestParam(required = true) String totalPrice) throws IOException {
		//example input in keyspec
		//{name=new machine, recipient=lnr, totalPrice=1234, firstAdvance=5, secondAdvance=6, others=machine deliveres, 12=21}

		Map<String, Object> model = new HashMap<>();
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
		String formattedAmount;
		if(!keySpec.get("firstAdvance").isEmpty()){
			// Format the amount
			formattedAmount = numberFormat.format( Long.parseLong(keySpec.get("firstAdvance")));

			model.put("firstAdvance", "First Advance: Rs."  + formattedAmount + "/-");
		}
		if(!keySpec.get("secondAdvance").isEmpty()){
			formattedAmount = numberFormat.format( Long.parseLong((keySpec.get("secondAdvance"))));
			model.put("secondAdvance",   "Second Advance: Rs."  + formattedAmount + "/-");
		}

		model.put("otherContent",  keySpec.get("others"));
		keySpec.remove("name");keySpec.remove("recipient");keySpec.remove("totalPrice");keySpec.remove("firstAdvance");
		keySpec.remove("secondAdvance");keySpec.remove("others");
		List<String> items = keySpec.entrySet().stream()
				.map(entry -> {
					String key = entry.getKey();
					String value = entry.getValue();
					return (value == null || value.trim().isEmpty()) ? key : key + " - " + value;
				})
				.collect(Collectors.toList());


		model.put("items", items);
		// Create a NumberFormat instance for the default locale (it will format numbers with commas)

		// Format the amount
		formattedAmount = numberFormat.format( Long.parseLong(totalPrice));

		model.put("amount", "Rs."  + formattedAmount + "/-"); // Format as in original
		model.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))); // Format date as 23/06/2025
		model.put("recipient", recipient);
		model.put("machineName", name.toUpperCase()); // Dynamically set machine name

		// Add bank details from the original PDF
		model.put("bankName", "STATE BANK OF INDIA");
		model.put("accountNumber", "34567445932");
		model.put("accountHolderName", "A.Thanikai Arasu");
		model.put("ifscCode", "SBIN0014784");
		model.put("branchName", "Kunnathur.");

		// Embed images as Base64 data URIs
		try {
			model.put("sghLogoBase64", getImageBase64("static/SGHLogo.jpg"));
			model.put("signatureBase64", getImageBase64("static/signature.png"));
		} catch (IOException e) {
			// Handle error, maybe set default or null to prevent PDF generation failure
		}

		ByteArrayOutputStream baos = pdfGenerator.generate("invoice", model);

		// Convert PDF to Base64
		String base64EncodedPdf = Base64.getEncoder().encodeToString(baos.toByteArray());

		return ResponseEntity.ok()
				.contentType(MediaType.TEXT_PLAIN)
				.body(base64EncodedPdf);
	}

}