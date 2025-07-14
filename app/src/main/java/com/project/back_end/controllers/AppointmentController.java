package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UtilityService utilityService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, UtilityService utilityService) {
        this.appointmentService = appointmentService;
        this.utilityService = utilityService;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, String>> getAppointments(@PathVariable String date,
                                                               @PathVariable String patientName,
                                                               @PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResponse = utilityService.validateToken(token, "doctor");
        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>(Map.of("error", "Unauthorized access"), HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> appointmentData = appointmentService.getAppointment(patientName, LocalDate.parse(date), token);
        if (appointmentData == null || appointmentData.isEmpty()) {
            return new ResponseEntity<>(Map.of("error", "No appointments found"), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(Map.of("appointments", appointmentData.toString()), HttpStatus.OK);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@RequestBody Appointment appointment,
                                                               @PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResponse = utilityService.validateToken(token, "patient");

        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>(Map.of("error", "Unauthorized access"), HttpStatus.UNAUTHORIZED);
        }

        int validationResult = utilityService.validateAppointment(appointment);
        if (validationResult == -1) {
            return new ResponseEntity<>(Map.of("error", "Doctor not found"), HttpStatus.BAD_REQUEST);
        } else if (validationResult == 0) {
            return new ResponseEntity<>(Map.of("error", "Time slot unavailable"), HttpStatus.CONFLICT);
        }

        int booked = appointmentService.bookAppointment(appointment);
        if (booked==1) {
            return new ResponseEntity<>(Map.of("message", "Appointment booked successfully"), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(Map.of("error", "Failed to book appointment"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(@RequestBody Appointment appointment,
                                                                 @PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResponse = utilityService.validateToken(token, "patient");
        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>(Map.of("error", "Unauthorized access"), HttpStatus.UNAUTHORIZED);
        }

        return appointmentService.updateAppointment(appointment);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable Long id,
                                                                 @PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResponse = utilityService.validateToken(token, "patient");
        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>(Map.of("error", "Unauthorized access"), HttpStatus.UNAUTHORIZED);
        }
        return appointmentService.cancelAppointment(id,"");
    }

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("/appointments")` to set a base path for all appointment-related endpoints.
//    - This centralizes all routes that deal with booking, updating, retrieving, and canceling appointments.


// 2. Autowire Dependencies:
//    - Inject `AppointmentService` for handling the business logic specific to appointments.
//    - Inject the general `Service` class, which provides shared functionality like token validation and appointment checks.


// 3. Define the `getAppointments` Method:
//    - Handles HTTP GET requests to fetch appointments based on date and patient name.
//    - Takes the appointment date, patient name, and token as path variables.
//    - First validates the token for role `"doctor"` using the `Service`.
//    - If the token is valid, returns appointments for the given patient on the specified date.
//    - If the token is invalid or expired, responds with the appropriate message and status code.


// 4. Define the `bookAppointment` Method:
//    - Handles HTTP POST requests to create a new appointment.
//    - Accepts a validated `Appointment` object in the request body and a token as a path variable.
//    - Validates the token for the `"patient"` role.
//    - Uses service logic to validate the appointment data (e.g., check for doctor availability and time conflicts).
//    - Returns success if booked, or appropriate error messages if the doctor ID is invalid or the slot is already taken.


// 5. Define the `updateAppointment` Method:
//    - Handles HTTP PUT requests to modify an existing appointment.
//    - Accepts a validated `Appointment` object and a token as input.
//    - Validates the token for `"patient"` role.
//    - Delegates the update logic to the `AppointmentService`.
//    - Returns an appropriate success or failure response based on the update result.


// 6. Define the `cancelAppointment` Method:
//    - Handles HTTP DELETE requests to cancel a specific appointment.
//    - Accepts the appointment ID and a token as path variables.
//    - Validates the token for `"patient"` role to ensure the user is authorized to cancel the appointment.
//    - Calls `AppointmentService` to handle the cancellation process and returns the result.


}
