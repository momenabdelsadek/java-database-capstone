/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/

// adminDashboard.js

import { openModal } from "../components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "../services/doctorServices.js";
import { createDoctorCard } from "../components/doctorCard.js";

// ========== DOM ELEMENTS ==========
const contentDiv = document.getElementById("content");
const searchInput = document.getElementById("searchBar");
const timeFilter = document.getElementById("filterTime");
const specialtyFilter = document.getElementById("filterSpecialty");
const addDoctorBtn = document.getElementById("addDocBtn");

// ========== EVENT BINDING ==========

// Open add doctor modal
if (addDoctorBtn) {
  addDoctorBtn.addEventListener("click", () => {
    openModal("addDoctor");
  });
}

// Search and filter events
if (searchInput) {
  searchInput.addEventListener("input", filterDoctorsOnChange);
}
if (timeFilter) {
  timeFilter.addEventListener("change", filterDoctorsOnChange);
}
if (specialtyFilter) {
  specialtyFilter.addEventListener("change", filterDoctorsOnChange);
}

// ========== LOAD DOCTORS ON PAGE LOAD ==========
window.addEventListener("DOMContentLoaded", loadDoctorCards);

async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Error loading doctor cards:", error);
  }
}

// ========== FILTER FUNCTION ==========
async function filterDoctorsOnChange() {
  const name = searchInput?.value?.trim() || null;
  const time = timeFilter?.value || null;
  const specialty = specialtyFilter?.value || null;

  try {
    const data = await filterDoctors(name, time, specialty);
    if (data?.doctors?.length > 0) {
      renderDoctorCards(data.doctors);
    } else {
      contentDiv.innerHTML = `<p class="noPatientRecord">No doctors found with the given filters.</p>`;
    }
  } catch (error) {
    console.error("Filtering error:", error);
    alert("Something went wrong while filtering doctors.");
  }
}

// ========== RENDER FUNCTION ==========
function renderDoctorCards(doctors) {
  contentDiv.innerHTML = "";
  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// ========== ADD DOCTOR FUNCTION ==========
export async function adminAddDoctor() {
  const name = document.getElementById("doctorName")?.value?.trim();
  const email = document.getElementById("doctorEmail")?.value?.trim();
  const phone = document.getElementById("doctorPhone")?.value?.trim();
  const password = document.getElementById("doctorPassword")?.value;
  const specialty = document.getElementById("doctorSpecialty")?.value?.trim();
  const availabilityInputs = document.querySelectorAll('input[name="availability"]:checked');
  const availableTimes = Array.from(availabilityInputs).map((input) => input.value);

  const token = localStorage.getItem("token");
  if (!token) {
    alert("Session expired. Please log in again.");
    return;
  }

  const doctor = {
    name,
    email,
    phone,
    password,
    specialization: specialty,
    availableTimes,
  };

  try {
    const result = await saveDoctor(doctor, token);
    if (result.success) {
      alert("Doctor added successfully!");
      document.getElementById("addDoctorModal")?.classList.remove("active");
      loadDoctorCards();
    } else {
      alert(result.message || "Failed to add doctor.");
    }
  } catch (error) {
    console.error("Error adding doctor:", error);
    alert("An unexpected error occurred.");
  }
}
