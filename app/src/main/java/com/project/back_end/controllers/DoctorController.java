package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.DTO.Login;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")

public class DoctorController {

    private final DoctorService doctorService;
    private final UtilityService utilityService;

    @Autowired
    public DoctorController(DoctorService doctorService, UtilityService utilityService) {
        this.doctorService = doctorService;
        this.utilityService = utilityService;
    }

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST controller that serves JSON responses.
//    - Use `@RequestMapping("${api.path}doctor")` to prefix all endpoints with a configurable API path followed by "doctor".
//    - This class manages doctor-related functionalities such as registration, login, updates, and availability.


// 2. Autowire Dependencies:
//    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
//    - Inject the shared `Service` class for general-purpose features like token validation and filtering.


// 3. Define the `getDoctorAvailability` Method:
//    - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
//    - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
//    - First validates the token against the user type.
//    - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.

    // 1. Get Doctor Availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Object> getDoctorAvailability(@PathVariable String user,
                                                        @PathVariable Long doctorId,
                                                        @PathVariable String date,
                                                        @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = utilityService.validateToken(token, user);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized access"));
        }

        try {
            return ResponseEntity.ok(doctorService.getDoctorAvailability(doctorId, LocalDate.parse(date)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal error occurred"));
        }
    }

    // 2. Get List of Doctors
    @GetMapping
    public ResponseEntity<Object> getDoctors() {
        try {
            return ResponseEntity.ok(doctorService.getDoctors());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal error occurred"));
        }
    }

    // 3. Add New Doctor
    @PostMapping("/{token}")
    public ResponseEntity<Object> saveDoctor(@PathVariable String token,
                                             @RequestBody Doctor doctor) {
        ResponseEntity<Map<String, String>> validation = utilityService.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized access"));
        }

        if (doctorService.validateDoctorByEmail(doctor.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Doctor already exists"));
        }

        try {
            doctorService.saveDoctor(doctor);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Doctor added to db"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Some internal error occurred"));
        }
    }

    // 4. Doctor Login
    @PostMapping("/login")
    public ResponseEntity<Object> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    // 5. Update Doctor
    @PutMapping("/{token}")
    public ResponseEntity<Object> updateDoctor(@PathVariable String token,
                                               @RequestBody Doctor doctor) {
        ResponseEntity<Map<String, String>> validation = utilityService.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized access"));
        }

        try {
            int updated = doctorService.updateDoctor(doctor);
            if (updated == 1) {
                return ResponseEntity.ok(Map.of("message", "Doctor updated"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Doctor not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Some internal error occurred"));
        }
    }

    // 6. Delete Doctor
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Object> deleteDoctor(@PathVariable Long id,
                                               @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = utilityService.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Unauthorized access"));
        }

        try {
            int deleted = doctorService.deleteDoctor(id);
            if (deleted == 1) {
                return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Doctor not found with id"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Some internal error occurred"));
        }
    }

    // 7. Filter Doctors
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Object> filterDoctors(@PathVariable String name,
                                                @PathVariable String time,
                                                @PathVariable String speciality) {
        try {
            List<Doctor> filtered = utilityService.filterDoctor(name, time, speciality);
            if (filtered == null || filtered.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No matching doctors found"));
            }

            return ResponseEntity.ok(filtered);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Some internal error occurred"));
        }
    }


// 4. Define the `getDoctor` Method:
//    - Handles HTTP GET requests to retrieve a list of all doctors.
//    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.


// 5. Define the `saveDoctor` Method:
//    - Handles HTTP POST requests to register a new doctor.
//    - Accepts a validated `Doctor` object in the request body and a token for authorization.
//    - Validates the token for the `"admin"` role before proceeding.
//    - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.


// 6. Define the `doctorLogin` Method:
//    - Handles HTTP POST requests for doctor login.
//    - Accepts a validated `Login` DTO containing credentials.
//    - Delegates authentication to the `DoctorService` and returns login status and token information.


// 7. Define the `updateDoctor` Method:
//    - Handles HTTP PUT requests to update an existing doctor's information.
//    - Accepts a validated `Doctor` object and a token for authorization.
//    - Token must belong to an `"admin"`.
//    - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.


// 8. Define the `deleteDoctor` Method:
//    - Handles HTTP DELETE requests to remove a doctor by ID.
//    - Requires both doctor ID and an admin token as path variables.
//    - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.


// 9. Define the `filter` Method:
//    - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
//    - Accepts `name`, `time`, and `speciality` as path variables.
//    - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.


}
