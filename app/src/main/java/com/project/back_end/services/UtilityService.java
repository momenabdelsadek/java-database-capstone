package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class UtilityService {

    private static final Logger logger = LoggerFactory.getLogger(UtilityService.class);

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;


    public UtilityService(TokenService tokenService,
                          AdminRepository adminRepository,
                          AppointmentRepository appointmentRepository,
                          DoctorRepository doctorRepository,
                          PatientRepository patientRepository,
                          DoctorService doctorService,
                          PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        logger.info("Validating token for user: {}", user);
        if (!tokenService.validateToken(token, user)) {
            logger.warn("Unauthorized: Token is invalid or expired for user: {}", user);
            return new ResponseEntity<>(Collections.singletonMap("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin admin) {
        try {
            Admin admin1 = adminRepository.findByUsername(admin.getUsername());
            if (admin1 != null) {
                if (admin.getPassword().equals(admin.getPassword())) {
                    String token = tokenService.generateToken(admin.getUsername());
                    logger.info("Admin login successful for username: {}", admin.getUsername());
                    return new ResponseEntity<>(Collections.singletonMap("token", token), HttpStatus.OK);
                } else {
                    logger.warn("Unauthorized: Incorrect password for admin username: {}", admin.getUsername());
                    return new ResponseEntity<>(Collections.singletonMap("error", "Invalid Password"), HttpStatus.UNAUTHORIZED);
                }
            } else {
                logger.warn("Unauthorized: Admin not found for username: {}", admin.getUsername());
                return new ResponseEntity<>(Collections.singletonMap("error", "Invalid User"), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logger.error("Error validating admin: {}", e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("error", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Doctor> filterDoctor(String name, String spec, String amOrPm) {
        logger.info("Filtering doctors by name: {}, specialty: {}, time: {}", name, spec, amOrPm);

        if (name != null && spec != null && amOrPm != null) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, spec, amOrPm);
        } else if (name != null && spec != null) {
            return doctorService.filterDoctorByNameAndSpecility(name, spec);
        } else if (name != null && amOrPm != null) {
            return doctorService.filterDoctorByNameAndTime(name, amOrPm);
        } else if (spec != null && amOrPm != null) {
            return doctorService.filterDoctorByTimeAndSpecility(spec, amOrPm);
        } else if (name != null) {
            return doctorService.findDoctorByName(name);
        } else if (spec != null) {
            return doctorService.filterDoctorBySpecility(spec);
        } else if (amOrPm != null) {
            return doctorService.filterDoctorsByTime(amOrPm);
        } else {
            return doctorService.getDoctors();
        }
    }

    @Transactional
    public int validateAppointment(Appointment appointment) {
        Optional<Doctor> doctorOptional = doctorRepository.findById(appointment.getDoctor().getId());
        if (doctorOptional.isPresent()) {
            Doctor doctor = doctorOptional.get();
            LocalDate date = appointment.getAppointmentTime().toLocalDate();
            List<String> availableSlots = doctorService.getDoctorAvailability(doctor.getId(), date);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String requestedTime = appointment.getAppointmentTime().format(formatter);
            boolean match = availableSlots.stream()
                    .anyMatch(slot -> slot.startsWith(requestedTime.substring(0, 16)));
            logger.info("Appointment validation result for doctor {} on {}: {}", doctor.getId(), requestedTime, match);
            return match ? 1 : 0;
        }
        logger.warn("Appointment validation failed: Doctor not found");
        return -1;
    }

    public boolean validatePatient(Patient patient) {
        boolean exists = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()) != null;
        logger.info("Patient validation result for email: {}, phone: {} - exists: {}", patient.getEmail(), patient.getPhone(), exists);
        return exists;
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        try {
            Patient patient = patientRepository.findByEmail(login.getEmail());
            if (patient != null) {
                if (patient.getPassword().equals(login.getPassword())) {
                    String token = tokenService.generateToken(patient.getEmail());
                    logger.info("Patient login successful for email: {}", patient.getEmail());
                    return new ResponseEntity<>(Collections.singletonMap("token", token), HttpStatus.OK);
                } else {
                    logger.warn("Unauthorized: Incorrect password for patient email: {}", login.getEmail());
                    return new ResponseEntity<>(Collections.singletonMap("error", "Invalid Password"), HttpStatus.UNAUTHORIZED);
                }
            } else {
                logger.warn("Unauthorized: Patient not found for email: {}", login.getEmail());
                return new ResponseEntity<>(Collections.singletonMap("error", "Invalid User"), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logger.error("Error validating patient: {}", e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("error", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(String token, String condition, String name) {
        try {
            String email = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(email);
            if (condition != null && name != null) {
                return patientService.filterByDoctorAndCondition(condition, name, patient.getId());
            } else if (condition != null) {
                return patientService.filterByCondition(condition, patient.getId());
            } else if (name != null) {
                return patientService.filterByDoctor(name, patient.getId());
            } else {
                return patientService.getPatientAppointment(patient.getId(), token);
            }
        } catch (Exception e) {
            logger.error("Error filtering patient appointments: {}", e.getMessage());
            return new ResponseEntity<>(Collections.singletonMap("error", "Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 4. Return Success Response
    public ResponseEntity<Map<String, Object>> success(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 5. Return Error Response
    public ResponseEntity<Map<String, Object>> error(String message, int status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return new ResponseEntity<>(response, HttpStatus.valueOf(status));
    }

// 1. **@Service Annotation**
// The @Service annotation marks this class as a service component in Spring. This allows Spring to automatically detect it through component scanning
// and manage its lifecycle, enabling it to be injected into controllers or other services using @Autowired or constructor injection.

// 2. **Constructor Injection for Dependencies**
// The constructor injects all required dependencies (TokenService, Repositories, and other Services). This approach promotes loose coupling, improves testability,
// and ensures that all required dependencies are provided at object creation time.

// 3. **validateToken Method**
// This method checks if the provided JWT token is valid for a specific user. It uses the TokenService to perform the validation.
// If the token is invalid or expired, it returns a 401 Unauthorized response with an appropriate error message. This ensures security by preventing
// unauthorized access to protected resources.

// 4. **validateAdmin Method**
// This method validates the login credentials for an admin user.
// - It first searches the admin repository using the provided username.
// - If an admin is found, it checks if the password matches.
// - If the password is correct, it generates and returns a JWT token (using the admin’s username) with a 200 OK status.
// - If the password is incorrect, it returns a 401 Unauthorized status with an error message.
// - If no admin is found, it also returns a 401 Unauthorized.
// - If any unexpected error occurs during the process, a 500 Internal Server Error response is returned.
// This method ensures that only valid admin users can access secured parts of the system.

// 5. **filterDoctor Method**
// This method provides filtering functionality for doctors based on name, specialty, and available time slots.
// - It supports various combinations of the three filters.
// - If none of the filters are provided, it returns all available doctors.
// This flexible filtering mechanism allows the frontend or consumers of the API to search and narrow down doctors based on user criteria.

// 6. **validateAppointment Method**
// This method validates if the requested appointment time for a doctor is available.
// - It first checks if the doctor exists in the repository.
// - Then, it retrieves the list of available time slots for the doctor on the specified date.
// - It compares the requested appointment time with the start times of these slots.
// - If a match is found, it returns 1 (valid appointment time).
// - If no matching time slot is found, it returns 0 (invalid).
// - If the doctor doesn’t exist, it returns -1.
// This logic prevents overlapping or invalid appointment bookings.

// 7. **validatePatient Method**
// This method checks whether a patient with the same email or phone number already exists in the system.
// - If a match is found, it returns false (indicating the patient is not valid for new registration).
// - If no match is found, it returns true.
// This helps enforce uniqueness constraints on patient records and prevent duplicate entries.

// 8. **validatePatientLogin Method**
// This method handles login validation for patient users.
// - It looks up the patient by email.
// - If found, it checks whether the provided password matches the stored one.
// - On successful validation, it generates a JWT token and returns it with a 200 OK status.
// - If the password is incorrect or the patient doesn't exist, it returns a 401 Unauthorized with a relevant error.
// - If an exception occurs, it returns a 500 Internal Server Error.
// This method ensures only legitimate patients can log in and access their data securely.

// 9. **filterPatient Method**
// This method filters a patient's appointment history based on condition and doctor name.
// - It extracts the email from the JWT token to identify the patient.
// - Depending on which filters (condition, doctor name) are provided, it delegates the filtering logic to PatientService.
// - If no filters are provided, it retrieves all appointments for the patient.
// This flexible method supports patient-specific querying and enhances user experience on the client side.


}
