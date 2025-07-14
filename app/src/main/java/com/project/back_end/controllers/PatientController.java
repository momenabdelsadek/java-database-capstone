package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.UtilityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final UtilityService utilityService;

    @Autowired
    public PatientController(PatientService patientService, UtilityService utilityService) {
        this.patientService = patientService;
        this.utilityService = utilityService;
    }

    // 1. Get Patient Details by Token
    @GetMapping("/{token}")
    public Object getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validation =
                utilityService.validateToken(token, "patient");
        if (validation == null) {
            return patientService.getPatientDetails(token);
        }
        return validation;
    }

    // 2. Create a New Patient
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPatient(@Valid @RequestBody Patient patient) {
        if (utilityService.validatePatient(patient)) {
            return utilityService.error("Patient with email or phone already exists", 409);
        }

        int result = patientService.createPatient(patient);
        if (result == 1) {
            return utilityService.success("Signup successful");
        } else {
            return utilityService.error("Internal server error", 500);
        }
    }

    // 3. Patient Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody Login login) {
        return utilityService.validatePatientLogin(login);
    }

    // 4. Get Patient Appointments
    @GetMapping("/{id}/{token}")
    public Object getPatientAppointments(
            @PathVariable Long id,
            @PathVariable String token
    ) {
        ResponseEntity<Map<String, String>> validation =
                utilityService.validateToken(token, "patient");
        if (validation == null) {
            return patientService.getPatientAppointment(id, token);
        }
        return validation;
    }

    // 5. Filter Patient Appointments
    @GetMapping("/filter/{condition}/{name}/{token}")
    public Object filterPatientAppointments(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token
    ) {

        ResponseEntity<Map<String, String>> validation =
                utilityService.validateToken(token, "patient");
        if (validation == null) {
            return utilityService.filterPatient(condition, name, token);
        }
        return validation;
    }

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller for patient-related operations.
//    - Use `@RequestMapping("/patient")` to prefix all endpoints with `/patient`, grouping all patient functionalities under a common route.


// 2. Autowire Dependencies:
//    - Inject `PatientService` to handle patient-specific logic such as creation, retrieval, and appointments.
//    - Inject the shared `Service` class for tasks like token validation and login authentication.


// 3. Define the `getPatient` Method:
//    - Handles HTTP GET requests to retrieve patient details using a token.
//    - Validates the token for the `"patient"` role using the shared service.
//    - If the token is valid, returns patient information; otherwise, returns an appropriate error message.


// 4. Define the `createPatient` Method:
//    - Handles HTTP POST requests for patient registration.
//    - Accepts a validated `Patient` object in the request body.
//    - First checks if the patient already exists using the shared service.
//    - If validation passes, attempts to create the patient and returns success or error messages based on the outcome.


// 5. Define the `login` Method:
//    - Handles HTTP POST requests for patient login.
//    - Accepts a `Login` DTO containing email/username and password.
//    - Delegates authentication to the `validatePatientLogin` method in the shared service.
//    - Returns a response with a token or an error message depending on login success.


// 6. Define the `getPatientAppointment` Method:
//    - Handles HTTP GET requests to fetch appointment details for a specific patient.
//    - Requires the patient ID, token, and user role as path variables.
//    - Validates the token using the shared service.
//    - If valid, retrieves the patient's appointment data from `PatientService`; otherwise, returns a validation error.


// 7. Define the `filterPatientAppointment` Method:
//    - Handles HTTP GET requests to filter a patient's appointments based on specific conditions.
//    - Accepts filtering parameters: `condition`, `name`, and a token.
//    - Token must be valid for a `"patient"` role.
//    - If valid, delegates filtering logic to the shared service and returns the filtered result.

}


