package com.example.File_Image_upload.controller;

import com.example.File_Image_upload.emums.Country;
import com.example.File_Image_upload.entity.Customer;
import com.example.File_Image_upload.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerRestController {

    private final CustomerService customerService;


    @PostMapping("/api/customers/customer-infos")
    public ResponseEntity<Customer> createUser(
        @RequestParam String username,
        @RequestParam String phone,
        @RequestParam String email,
        @RequestParam Country country,
        @RequestParam(required = false) String additionalInfoJson,
        @RequestParam(required = false) MultipartFile image,
        @RequestParam(required = false) MultipartFile cv
    ) throws IOException, SQLException {
        Customer created = customerService.createUser(username, phone, email, country, additionalInfoJson, image, cv);
        return ResponseEntity.ok(created);
    }


    @PutMapping("/api/customers/customer-infos/{id}")
    public ResponseEntity<Customer> updateUser(
        @PathVariable Long id,
        @RequestParam(required = false) String username,
        @RequestParam(required = false) String phone,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) Country country,
        @RequestParam(required = false) String additionalInfoJson,
        @RequestParam(required = false) MultipartFile image,
        @RequestParam(required = false) MultipartFile cv
    ) throws IOException, SQLException {
        Customer updated = customerService.updateUser(id, username, phone, email, country, additionalInfoJson, image, cv);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/api/customers/customer-infos/{id}")
    public ResponseEntity<Customer> deleteUser(@PathVariable Long id) {
        Customer deleted = customerService.deleteUser(id);
        return ResponseEntity.ok(deleted);
    }


    @GetMapping("/api/customers/customer-infos/{id}")
    public ResponseEntity<Customer> getUserById(@PathVariable Long id) {
        Customer customer = customerService.getUserById(id);
        return ResponseEntity.ok(customer);
    }


    @GetMapping("/api/customers/customer-infos")
    public ResponseEntity<List<Customer>> getAllUsers() {
        return ResponseEntity.ok(customerService.getAllUsers());
    }

    @GetMapping("/api/customers/{id}/image")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long id) throws SQLException {
        Customer customer = customerService.getUserById(id);
        byte[] imageBytes = customerService.getImageData(customer); // Assume helper method

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + customer.getImageName() + "\"")
            .contentType(MediaType.parseMediaType(customer.getImageType()))
            .body(imageBytes);
    }

    @GetMapping("/api/customers/{id}/cv")
    public ResponseEntity<byte[]> downloadCv(@PathVariable Long id) throws SQLException {
        Customer customer = customerService.getUserById(id);
        byte[] cvBytes = customerService.getCvData(customer); // Assume helper method

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + customer.getCvName() + "\"")
            .contentType(MediaType.parseMediaType(customer.getCvType()))
            .body(cvBytes);
    }


}
