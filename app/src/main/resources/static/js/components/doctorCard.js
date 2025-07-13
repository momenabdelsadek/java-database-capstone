/*
Import the overlay function for booking appointments from loggedPatient.js

  Import the deleteDoctor API function to remove doctors (admin role) from docotrServices.js

  Import function to fetch patient details (used during booking) from patientServices.js

  Function to create and return a DOM element for a single doctor card
    Create the main container for the doctor card
    Retrieve the current user role from localStorage
    Create a div to hold doctor information
    Create and set the doctor’s name
    Create and set the doctor's specialization
    Create and set the doctor's email
    Create and list available appointment times
    Append all info elements to the doctor info container
    Create a container for card action buttons
    === ADMIN ROLE ACTIONS ===
      Create a delete button
      Add click handler for delete button
     Get the admin token from localStorage
        Call API to delete the doctor
        Show result and remove card if successful
      Add delete button to actions container
   
    === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
      Create a book now button
      Alert patient to log in before booking
      Add button to actions container
  
    === LOGGED-IN PATIENT ROLE ACTIONS === 
      Create a book now button
      Handle booking logic for logged-in patient   
        Redirect if token not available
        Fetch patient data with token
        Show booking overlay UI with doctor and patient info
      Add button to actions container
   
  Append doctor info and action buttons to the car
  Return the complete doctor card element
*/
// doctorCard.js

import { deleteDoctorById } from "../services/doctorServices.js";
import { getPatientDetails } from "../services/patientServices.js";
import { openBookingOverlay } from "../services/loggedPatient.js";

// Define the Function
export function createDoctorCard(doctor) {
  // Create the Main Card Container
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  // Fetch the User’s Role
  const role = localStorage.getItem("userRole");

  // Create Doctor Info Section
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialization = document.createElement("p");
  specialization.textContent = `Specialization: ${doctor.specialty}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;

  const availability = document.createElement("p");
  availability.textContent = `Available Times: ${doctor.availableTimes.join(", ")}`;

  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  // Create Button Container
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // Conditionally Add Buttons Based on Role

  // === ADMIN ROLE ===
  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.classList.add("adminBtn");

    removeBtn.addEventListener("click", async () => {
      const confirmed = confirm(`Are you sure you want to delete Dr. ${doctor.name}?`);
      if (!confirmed) return;

      const token = localStorage.getItem("token");

      try {
        const response = await deleteDoctorById(doctor.id, token);
        if (response.success) {
          alert("Doctor deleted successfully.");
          card.remove();
        } else {
          alert("Failed to delete doctor.");
        }
      } catch (error) {
        console.error("Error deleting doctor:", error);
        alert("An error occurred while trying to delete the doctor.");
      }
    });

    actionsDiv.appendChild(removeBtn);
  }

  // === PATIENT (NOT LOGGED-IN) ROLE ===
  else if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("book-btn");

    bookNow.addEventListener("click", () => {
      alert("Please log in to book an appointment.");
    });

    actionsDiv.appendChild(bookNow);
  }

  // === LOGGED-IN PATIENT ROLE ===
  else if (role === "loggedPatient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("book-btn");

    bookNow.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");

      try {
        const patientData = await getPatientDetails(token);
        openBookingOverlay(e, doctor, patientData);
      } catch (error) {
        console.error("Booking failed:", error);
        alert("Unable to book appointment. Please try again.");
      }
    });

    actionsDiv.appendChild(bookNow);
  }

  // Final Assembly
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  // Return the final card
  return card;
}
