package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // 1. Extend JpaRepository:
//    - The repository extends JpaRepository<Patient, Long>, which provides basic CRUD functionality.
//    - This allows the repository to perform operations like save, delete, update, and find without needing to implement these methods manually.
//    - JpaRepository also includes features like pagination and sorting.

// Example: public interface PatientRepository extends JpaRepository<Patient, Long> {}

    /**
     * Find a patient by their email address.
     *
     * @param email the patient's email
     * @return the matching Patient entity or null if not found
     */
    Patient findByEmail(String email);

    /**
     * Find a patient using either email or phone number.
     *
     * @param email the patient's email
     * @param phone the patient's phone number
     * @return the matching Patient entity or null if not found
     */
    Patient findByEmailOrPhone(String email, String phone);


// 2. Custom Query Methods:

//    - **findByEmail**:
//      - This method retrieves a Patient by their email address.
//      - Return type: Patient
//      - Parameters: String email

//    - **findByEmailOrPhone**:
//      - This method retrieves a Patient by either their email or phone number, allowing flexibility for the search.
//      - Return type: Patient
//      - Parameters: String email, String phone

// 3. @Repository annotation:
//    - The @Repository annotation marks this interface as a Spring Data JPA repository.
//    - Spring Data JPA automatically implements this repository, providing the necessary CRUD functionality and custom queries defined in the interface.


}

