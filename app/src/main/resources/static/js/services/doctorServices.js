/*
  Import the base API URL from the config file
  Define a constant DOCTOR_API to hold the full endpoint for doctor-related actions


  Function: getDoctors
  Purpose: Fetch the list of all doctors from the API

   Use fetch() to send a GET request to the DOCTOR_API endpoint
   Convert the response to JSON
   Return the 'doctors' array from the response
   If there's an error (e.g., network issue), log it and return an empty array


  Function: deleteDoctor
  Purpose: Delete a specific doctor using their ID and an authentication token

   Use fetch() with the DELETE method
    - The URL includes the doctor ID and token as path parameters
   Convert the response to JSON
   Return an object with:
    - success: true if deletion was successful
    - message: message from the server
   If an error occurs, log it and return a default failure response


  Function: saveDoctor
  Purpose: Save (create) a new doctor using a POST request

   Use fetch() with the POST method
    - URL includes the token in the path
    - Set headers to specify JSON content type
    - Convert the doctor object to JSON in the request body

   Parse the JSON response and return:
    - success: whether the request succeeded
    - message: from the server

   Catch and log errors
    - Return a failure response if an error occurs


  Function: filterDoctors
  Purpose: Fetch doctors based on filtering criteria (name, time, and specialty)

   Use fetch() with the GET method
    - Include the name, time, and specialty as URL path parameters
   Check if the response is OK
    - If yes, parse and return the doctor data
    - If no, log the error and return an object with an empty 'doctors' array

   Catch any other errors, alert the user, and return a default empty result
*/
// js/services/doctorServices.js

import { API_BASE_URL } from "../config/config.js";

// Base endpoint for doctor-related API operations
const DOCTOR_API = `${API_BASE_URL}/doctor`;

/**
 * Fetch all doctors from the backend
 * @returns {Promise<Array>} Array of doctor objects, or empty array on failure
 */
export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);
    const data = await response.json();
    return data.doctors || [];
  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}

/**
 * Delete a doctor by ID
 * @param {string} id - Doctor's unique ID
 * @param {string} token - Admin's authentication token
 * @returns {Promise<Object>} { success: boolean, message: string }
 */
export async function deleteDoctor(id, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/delete/${id}/${token}`, {
      method: "DELETE",
    });

    const data = await response.json();

    return {
      success: response.ok,
      message: data.message || "Doctor deleted successfully.",
    };
  } catch (error) {
    console.error("Error deleting doctor:", error);
    return {
      success: false,
      message: "Failed to delete doctor. Please try again later.",
    };
  }
}

/**
 * Save a new doctor to the backend
 * @param {Object} doctor - Doctor object with fields: name, email, specialization, etc.
 * @param {string} token - Admin authentication token
 * @returns {Promise<Object>} { success: boolean, message: string }
 */
export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/save/${token}`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(doctor),
    });

    const data = await response.json();

    return {
      success: response.ok,
      message: data.message || "Doctor saved successfully.",
    };
  } catch (error) {
    console.error("Error saving doctor:", error);
    return {
      success: false,
      message: "Failed to save doctor. Please try again.",
    };
  }
}

/**
 * Filter doctors by name, time, and specialization
 * @param {string} name - Name or partial name of doctor
 * @param {string} time - Time availability (e.g., "10:00 AM")
 * @param {string} specialty - Doctor's specialization
 * @returns {Promise<Object>} { doctors: Array }
 */
export async function filterDoctors(name, time, specialty) {
  try {
    const response = await fetch(
      `${DOCTOR_API}/filter/${name}/${time}/${specialty}`
    );

    if (response.ok) {
      const data = await response.json();
      return data;
    } else {
      console.error("Failed to filter doctors:", response.statusText);
      return { doctors: [] };
    }
  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("An error occurred while filtering doctors. Please try again.");
    return { doctors: [] };
  }
}
