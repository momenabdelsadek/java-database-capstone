package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@Service
public class AppointmentService {
// 1. **Add @Service Annotation**:
//    - To indicate that this class is a service layer class for handling business logic.
//    - The `@Service` annotation should be added before the class declaration to mark it as a Spring service component.
//    - Instruction: Add `@Service` above the class definition.

// 2. **Constructor Injection for Dependencies**:
//    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`.
//    - These dependencies should be injected through the constructor.
//    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.

// 3. **Add @Transactional Annotation for Methods that Modify Database**:
//    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
//    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.

// 4. **Book Appointment Method**:
//    - Responsible for saving the new appointment to the database.
//    - If the save operation fails, it returns `0`; otherwise, it returns `1`.
//    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.

// 5. **Update Appointment Method**:
//    - This method is used to update an existing appointment based on its ID.
//    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
//    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
//    - Instruction: Ensure proper validation and error handling is included for appointment updates.

// 6. **Cancel Appointment Method**:
//    - This method cancels an appointment by deleting it from the database.
//    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
//    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.

// 7. **Get Appointments Method**:
//    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
//    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
//    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.

// 8. **Change Status Method**:
//    - This method updates the status of an appointment by changing its value in the database.
//    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
//    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.


    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        logger.info("Booking appointment for patient ID: {}, doctor ID: {}",
                appointment.getPatient().getId(), appointment.getDoctor().getId());
        try {
            appointmentRepository.save(appointment);
            logger.info("Appointment booked successfully at {}", appointment.getAppointmentTime());
            return 1;
        } catch (Exception e) {
            logger.error("Error booking appointment: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        logger.info("Updating appointment ID: {}", appointment.getId());
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> optional = appointmentRepository.findById(appointment.getId());

        if (optional.isEmpty()) {
            logger.warn("Appointment ID {} not found.", appointment.getId());
            response.put("message", "Appointment not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Appointment existing = optional.get();

        if (!existing.getPatient().getId().equals(appointment.getPatient().getId())) {
            logger.warn("Unauthorized update attempt by patient ID: {}", appointment.getPatient().getId());
            response.put("message", "Unauthorized to update this appointment.");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctor().getId());
        if (doctorOpt.isEmpty()) {
            logger.warn("Doctor ID {} not found.", appointment.getDoctor().getId());
            response.put("message", "Doctor not found.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        LocalDateTime start = appointment.getAppointmentTime();
        LocalDateTime end = start.plusMinutes(59);

        List<Appointment> conflictingAppointments =
                appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                        appointment.getDoctor().getId(), start, end);

        conflictingAppointments.removeIf(a -> a.getId().equals(appointment.getId()));

        if (!conflictingAppointments.isEmpty()) {
            logger.warn("Time conflict detected for doctor ID: {} at {}", appointment.getDoctor().getId(), start);
            response.put("message", "Doctor already has an appointment at this time.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        existing.setDoctor(appointment.getDoctor());
        existing.setAppointmentTime(appointment.getAppointmentTime());
        existing.setStatus(appointment.getStatus());

        appointmentRepository.save(existing);
        logger.info("Appointment ID {} updated successfully.", appointment.getId());
        response.put("message", "Appointment updated successfully.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        logger.info("Attempting to cancel appointment ID: {}", id);
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> optional = appointmentRepository.findById(id);

        if (optional.isEmpty()) {
            logger.warn("Appointment ID {} not found.", id);
            response.put("message", "Appointment not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Appointment appointment = optional.get();
        String patientIdentifierFromToken = tokenService.extractIdentifier(token);

        if (!appointment.getPatient().getEmail().equals(patientIdentifierFromToken)) {
            logger.warn("Unauthorized cancellation attempt by patient ID: {}", patientIdentifierFromToken);
            response.put("message", "Unauthorized: You can only cancel your own appointments.");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        appointmentRepository.delete(appointment);
        logger.info("Appointment ID {} canceled successfully.", id);
        response.put("message", "Appointment canceled successfully.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        logger.info("Fetching appointments for doctor (from token) on date: {}", date);
        Map<String, Object> result = new HashMap<>();
        String doctorEmail = tokenService.extractIdentifier(token);
        Long doctorId = doctorRepository.findByEmail(doctorEmail).getId();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<Appointment> appointments;

        if (pname == null || pname.equalsIgnoreCase("null") || pname.trim().isEmpty()) {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        } else {
            appointments = appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                            doctorId, pname, start, end);
        }

        logger.info("Appointments fetched: {}", appointments.size());
        result.put("appointments", appointments);
        return result;
    }
}
