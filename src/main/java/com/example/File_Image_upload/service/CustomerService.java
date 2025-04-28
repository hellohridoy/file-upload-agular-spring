package com.example.File_Image_upload.service;

import com.example.File_Image_upload.emums.Country;
import com.example.File_Image_upload.entity.Customer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface CustomerService {

    Customer createUser(String username,
                        String phone,
                        String email,
                        Country country,
                        String additionalInfoJson,
                        MultipartFile image,
                        MultipartFile cv)
        throws IOException, SQLException;

    Customer updateUser(Long id, String username, String phone, String email,
                        Country country, String additionalInfoJson,
                        MultipartFile image, MultipartFile cv)
        throws IOException, SQLException;

    Customer deleteUser(Long id);

    Customer getUserById(Long id);

    List<Customer> getAllUsers();

    byte[] getCvData(Customer customer) throws SQLException;

    byte[] getImageData(Customer customer) throws SQLException;


}
