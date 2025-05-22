package com.example.File_Image_upload.serviceImpl;

import com.example.File_Image_upload.emums.Country;
import com.example.File_Image_upload.entity.Customer;
import com.example.File_Image_upload.helperMethod.LargeObjectHelper;
import com.example.File_Image_upload.repository.CustomerRepository;
import com.example.File_Image_upload.service.CustomerService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final DataSource dataSource;
    private final LargeObjectHelper lob;
    private final ObjectMapper objectMapper;


    @Override
    public Customer createUser(
        String username,
        String phone,
        String email,
        Country country,
        String additionalInfoJson,
        MultipartFile image,
        MultipartFile cv
    ) throws IOException, SQLException {
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setCountry(country);

        // Parse additionalInfoJson into a Map and set it
        if (additionalInfoJson != null && !additionalInfoJson.isEmpty()) {
            Map<String, Object> additionalInfo = objectMapper.readValue(additionalInfoJson, Map.class);
            customer.setAdditionalInfo(additionalInfo);
        }

        // Get PostgreSQL connection
        Connection conn = dataSource.getConnection();
        try {
            // Must be in a transaction for large objects
            conn.setAutoCommit(false);

            // Get the Large Object Manager
            PGConnection pgconn = conn.unwrap(PGConnection.class);
            LargeObjectManager lom = pgconn.getLargeObjectAPI();

            // Handle CV file upload
            if (cv != null && !cv.isEmpty()) {
                // Create a new large object
                long oid = lom.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);

                // Open the large object for writing
                LargeObject obj = lom.open(oid, LargeObjectManager.WRITE);

                // Copy the file data to the large object
                try (InputStream fis = cv.getInputStream()) {
                    byte[] buf = new byte[2048];
                    int s;
                    while ((s = fis.read(buf, 0, 2048)) > 0) {
                        obj.write(buf, 0, s);
                    }
                }

                // Close the large object
                obj.close();

                // Save the oid
                customer.setCvUploadDownload(oid);
                customer.setCvName(cv.getOriginalFilename());
                customer.setCvType(cv.getContentType());
            }

            // Handle image file upload
            if (image != null && !image.isEmpty()) {
                // Create a new large object
                long oid = lom.createLO(LargeObjectManager.READ | LargeObjectManager.WRITE);

                // Open the large object for writing
                LargeObject obj = lom.open(oid, LargeObjectManager.WRITE);

                // Copy the file data to the large object
                try (InputStream fis = image.getInputStream()) {
                    byte[] buf = new byte[2048];
                    int s;
                    while ((s = fis.read(buf, 0, 2048)) > 0) {
                        obj.write(buf, 0, s);
                    }
                }

                // Close the large object
                obj.close();

                // Save the oid
                customer.setImageUploadDownload(oid);
                customer.setImageName(image.getOriginalFilename());
                customer.setImageType(image.getContentType());
            }

            // Commit the transaction
            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            // Reset to default behavior
            conn.setAutoCommit(true);
            conn.close();
        }

        return customerRepository.save(customer);
    }

    @Override
    public Customer updateUser(Long id, String username, String phone, String email,
                               Country country, String additionalInfoJson,
                               MultipartFile image, MultipartFile cv) throws IOException, SQLException {

        Optional<Customer> existingCustomerOpt = customerRepository.findById(id);
        if (existingCustomerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer with ID " + id + " not found.");
        }

        Customer existingCustomer = existingCustomerOpt.get();

        // Update text fields
        if (username != null) existingCustomer.setUsername(username);
        if (phone != null) existingCustomer.setPhone(phone);
        if (email != null) existingCustomer.setEmail(email);
        if (country != null) existingCustomer.setCountry(country);
        if (additionalInfoJson != null && !additionalInfoJson.isBlank()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> additionalInfoMap = objectMapper.readValue(additionalInfoJson, new TypeReference<>() {});
            existingCustomer.setAdditionalInfo(additionalInfoMap);
        }


        // Update files if provided
        if (image != null && !image.isEmpty()) {
            existingCustomer.setImageData(image.getBytes());  // Corrected this line
        }
        if (cv != null && !cv.isEmpty()) {
            existingCustomer.setCvData(cv.getBytes());
        }

        return customerRepository.save(existingCustomer);
    }


    @Override
    public Customer deleteUser(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer with ID " + id + " not found."));
        customerRepository.delete(customer);
        return customer;
    }

    @Override
    public Customer getUserById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer with ID " + id + " not found."));
    }

    @Override
    public List<Customer> getAllUsers() {
        return customerRepository.findAll();
    }

    public byte[] getImageData(Customer customer) throws SQLException {
        if (customer.getImageUploadDownload() != null) {
            return lob.read(customer.getImageUploadDownload()); // Use LargeObject API
        }
        return new byte[0];
    }

    public byte[] getCvData(Customer customer) throws SQLException {
        if (customer.getCvUploadDownload() != null) {
            return lob.read(customer.getCvUploadDownload()); // Same here
        }
        return new byte[0];
    }

}

