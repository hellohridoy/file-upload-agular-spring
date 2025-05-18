package com.example.File_Image_upload.service.impl;

import com.example.File_Image_upload.emums.Country;
import com.example.File_Image_upload.entity.Customer;
import com.example.File_Image_upload.helperMethod.LargeObjectHelper;
import com.example.File_Image_upload.repository.CustomerRepository;
import com.example.File_Image_upload.serviceImpl.CustomerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.web.multipart.MultipartFile;


import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


public class CustomerServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DataSource dataSource;

    @Mock
    private LargeObjectHelper lob;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MultipartFile imageFile;

    @Mock
    private MultipartFile cvFile;

    @Mock
    private Connection connection;

    @Mock
    private PGConnection pgConnection;

    @Mock
    private LargeObjectManager largeObjectManager;

    @Mock
    private LargeObject largeObject;

    @InjectMocks
    private CustomerServiceImpl customerService;


    //for common code this are added in the beforeEach
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.unwrap(PGConnection.class)).thenReturn(pgConnection);
        when(pgConnection.getLargeObjectAPI()).thenReturn(largeObjectManager);

        when(largeObjectManager.createLO(anyInt())).thenReturn(123L);
        when(largeObjectManager.open(anyLong(), anyInt())).thenReturn(largeObject);
        when(imageFile.getInputStream()).thenReturn(new ByteArrayInputStream("image".getBytes()));
        when(cvFile.getInputStream()).thenReturn(new ByteArrayInputStream("cv".getBytes()));

        when(imageFile.getOriginalFilename()).thenReturn("photo.jpg");
        when(imageFile.getContentType()).thenReturn("image/jpeg");
        when(imageFile.isEmpty()).thenReturn(false);

        when(cvFile.getOriginalFilename()).thenReturn("resume.pdf");
        when(cvFile.getContentType()).thenReturn("application/pdf");
        when(cvFile.isEmpty()).thenReturn(false);
    }

    @Test
    void testCreateCustomer_Success() throws Exception {
        // field for pass
        String username = "John";
        String phone = "123456";
        String email = "john@example.com";
        Country country = Country.USA;
        String additionalInfoJson = "{\"age\":30,\"job\":\"engineer\"}";


        Map<String,Object> additionalInfo = new HashMap<>();
        when(objectMapper.readValue(additionalInfoJson, Map.class)).thenReturn(additionalInfo);

        Map<String, Object> additionalInfoMap = Map.of("age", 30, "job", "engineer");
        when(objectMapper.readValue(additionalInfoJson, Map.class)).thenReturn(additionalInfoMap);

        Customer savedCustomer = new Customer();
        savedCustomer.setId(1L);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // Act
        Customer result = customerService.createUser(
            username, phone, email, country, additionalInfoJson, imageFile, cvFile
        );

        // Assert
        assertNotNull(result);
        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(largeObject, atLeastOnce()).write(any(), anyInt(), anyInt());
        verify(largeObject, times(2)).close(); // Once for image, once for cv
        verify(connection).commit();
        verify(connection).close();
    }

}
