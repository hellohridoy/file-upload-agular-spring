package com.example.File_Image_upload.repository;

import com.example.File_Image_upload.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
