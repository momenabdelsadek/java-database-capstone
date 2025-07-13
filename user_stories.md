## Admin User Stories

1. As an admin, I want to add new doctors to the platform, so that they can be available for patient bookings.
Acceptance Criteria:
-Admin can fill out a form with doctor’s details and login credentials.
-The doctor receives a confirmation email with login info.
-The doctor profile becomes immediately available to patients.

2. As an admin, I want to delete a doctor’s profile, so that they are removed from the system if they leave the clinic.
Acceptance Criteria:
-Admin can select a doctor from a list and delete their account.
-A confirmation prompt appears before deletion.
-All associated future appointments are flagged or reassigned.

3. As an admin, I want to log into the portal securely, so that I can manage platform access and data.
Acceptance Criteria:
-Admin can log in using admin credentials.
-A secure session is created with role-based access rights.
-Incorrect login attempts are logged for security auditing.

4. As an admin, I want to run a stored procedure to get appointment statistics, so that I can track platform usage.
Acceptance Criteria:
-Admin can connect to MySQL CLI and execute a named stored procedure.
-The procedure returns appointment counts grouped by month.
-The result includes a timestamp of the query execution.

5. As an admin, I want to log out of the portal, so that access to sensitive information is protected when I leave the system.
Acceptance Criteria:
-Clicking “Logout” ends the current session.
-The user is redirected to the login page.
-Session tokens or cookies are invalidated immediately.


## Patient User Stories

1. As a patient, I want to sign up with my email and password, so that I can book appointments online.
Acceptance Criteria:
-Patient can fill a registration form with email, password, and name.
-Email is validated and checked for duplicates.
-Upon successful registration, I am redirected to the login page.

2. As a patient, I want to view a list of doctors without logging in, so that I can decide before creating an account.
Acceptance Criteria:
-The doctor list is publicly accessible on the homepage.
-Each doctor profile includes name, specialization, and availability.
-A “Book Now” button redirects to the login/sign-up page.

3. As a patient, I want to log in securely, so that I can manage my appointments and health records.
Acceptance Criteria:
-Patient can log in using my registered email and password.
-Incorrect credentials trigger an error message.
-Upon successful login, I’m redirected to my dashboard.

4. As a patient, I want to book an appointment with a doctor, so that I can receive consultation for my health issue.
Acceptance Criteria:
-Patient can choose a doctor, date, and available time slot.
-Patient receive a confirmation of the booking.
-The time slot becomes unavailable for other patients.

5. As a patient, I want to view my upcoming appointments, so that I can prepare in advance.
Acceptance Criteria:
-Patient can see a list of future appointments with time, doctor name, and status.
-Patient can cancel or reschedule appointments (if allowed).
-Appointments are sorted by date in ascending order.

## Doctor User Stories

1. As a doctor, I want to view my daily appointment schedule, so that I can stay organized and prepared.
Acceptance Criteria:
-Doctor can see a calendar with time slots for booked appointments.
-Each appointment shows the patient’s name, time, and reason for visit.
-Doctor can navigate to future dates to see upcoming appointments.

2. As a doctor, I want to mark my unavailability, so that patients can only book during my available hours.
Acceptance Criteria:
-Doctor can select time slots or days where I am unavailable.
-Patients cannot see or book those slots on their end.
-Doctor can update or remove unavailability at any time.

3. As a doctor, I want to update my profile details, so that patients have the latest information about my specialization and contact info.
Acceptance Criteria:
-Doctor can edit fields like specialization, phone number, and bio.
-Changes are reflected immediately on the patient view.
-All fields are validated for correct format (e.g., phone, email).

4. As a doctor, I want to securely log in to the portal, so that I can access and manage my appointments.
Acceptance Criteria:
-Doctor can log in using my registered email and password.
-The system validates credentials and provides access upon success.
-Failed login attempts display an appropriate error message.

5. As a doctor, I want to view patient details for each appointment, so that I can be well-informed before consultations.
Acceptance Criteria:
-Doctor can click on an appointment to see patient name, age, and medical history.
-Access is restricted to appointments assigned to me.
-Sensitive patient data is protected and encrypted.

==========================================================================================================================

[Source of user stores]

As an admin, you can:

Log into the portal with your username and password to manage the platform securely
Log out of the portal to protect system access
Add doctors to the portal
Delete doctor's profile from the portal
Run a stored procedure in MySQL CLI to get the number of appointments per month and track usage statistics


As a doctor, you can:

Log into the portal to manage your appointments
Log out of the portal to protect my data
View my appointment calendar to stay organized
Mark your unavailability to inform patients only the available slots
Update your profile with specialization and contact information so that patients have up-to-date information
View the patient details for upcoming appointments so that I can be prepared

As a patient, you can:

View a list of doctors without logging in to explore options before registering
Sign up using your email and password to book appointments
Log into the portal to manage your bookings
Log out of the portal to secure your account
Log in and book an hour-long appointment to consult with a doctor
View my upcoming appointments so that I can prepare accordingly



## User Story Template
**Title:**
_As a [user role], I want [feature/goal], so that [reason]._
**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]
**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]