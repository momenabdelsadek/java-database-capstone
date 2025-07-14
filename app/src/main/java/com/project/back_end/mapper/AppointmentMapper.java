package com.project.back_end.mapper;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;

public class AppointmentMapper {

    public static AppointmentDTO from(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        return new AppointmentDTO(
                appointment.getId(),
                appointment.getDoctor() != null ? appointment.getDoctor().getId() : null,
                appointment.getDoctor() != null ? appointment.getDoctor().getName() : null,
                appointment.getPatient() != null ? appointment.getPatient().getId() : null,
                appointment.getPatient() != null ? appointment.getPatient().getName() : null,
                appointment.getPatient() != null ? appointment.getPatient().getEmail() : null,
                appointment.getPatient() != null ? appointment.getPatient().getPhone() : null,
                appointment.getPatient() != null ? appointment.getPatient().getAddress() : null,
                appointment.getAppointmentTime(),
                appointment.getStatus()
        );
    }
}
