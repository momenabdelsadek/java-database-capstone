## MySQL Database Design
=========================

### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id)
- patient_id: INT, Foreign Key → patients(id)
- appointment_time: DATETIME, Not Null
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)

### Table: patients
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(50), Unique, Not Null
- email: VARCHAR(100), Unique, Not Null
- password_hash: VARCHAR(255), Not Null
- full_name: VARCHAR(100), Not Null
- phone: VARCHAR(20), Nullable
- age: INT, Nullable
- status: INT (0 = Inactive, 1 = Active, 2 = Suspended), Default 1
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

### Table: doctors
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(50), Unique, Not Null
- email: VARCHAR(100), Unique, Not Null
- password_hash: VARCHAR(255), Not Null
- full_name: VARCHAR(100), Not Null
- specialization: VARCHAR(100), Not Null
- contact_number: VARCHAR(20), Nullable
- status: INT (0 = Inactive, 1 = Active, 2 = Suspended), Default 1
- available: BOOLEAN, Default TRUE
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

### Table: admin
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(50), Unique, Not Null
- email: VARCHAR(100), Unique, Not Null
- password_hash: VARCHAR(255), Not Null
- status: INT (0 = Inactive, 1 = Active), Default 1
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

### Table: clinic_locations
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100), Not Null
- address: VARCHAR(255), Not Null
- city: VARCHAR(100), Not Null
- state: VARCHAR(100), Nullable
- country: VARCHAR(100), Not Null
- contact_number: VARCHAR(20), Nullable
- status: INT (0 = Inactive, 1 = Active), Default 1
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

### Table: payments
- id: INT, Primary Key, Auto Increment
- appointment_id: INT, Foreign Key → appointments(id), Not Null
- patient_id: INT, Foreign Key → patients(id), Not Null
- amount: DECIMAL(10,2), Not Nul
- currency: VARCHAR(10), Default 'USD'
- payment_method: VARCHAR(50), Not Null (e.g., 'Credit Card', 'Cash', 'Insurance')
- status: INT (0 = Pending, 1 = Paid, 2 = Failed, 3 = Refunded), Default 0
- transaction_ref: VARCHAR(100), Nullable
- paid_at: TIMESTAMP, Nullable
- created_at: TIMESTAMP, Default CURRENT_TIMESTAMP

==================================================
==================================================

## MongoDB Collection Design
=============================

Collections:  #prescriptions, #feedback, #logs, #messages.

### Collection: prescriptions
```json
{
  "_id": "ObjectId('64abc123456')",
  "appointmentId": 51,
  "patient": {
    "patientId": "ObjectId('64pat987654')",
    "fullName": "John Smith",
    "age": 35,
    "email": "john.smith@example.com"
  },
  "doctor": {
    "doctorId": "ObjectId('64doc123456')",
    "fullName": "Dr. Emily Carter",
    "specialization": "General Practitioner",
    "email": "emily.carter@smartcare.com"
  },
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "instructions": "Take 1 tablet every 6 hours."
    },
    {
      "name": "Ibuprofen",
      "dosage": "200mg",
      "instructions": "Take after meals if pain persists."
    }
  ],
  "refillCount": 2,
  "doctorNotes": "Avoid alcohol while on medication.",
  "pharmacy": {
    "pharmacyId": "ObjectId('64xyz999888')",
    "name": "Walgreens SF",
    "location": "Market Street"
  },
  "issuedAt": "2025-07-13T09:00:00Z"
}

### Collection: feedback
```json
{
  "_id": "ObjectId('64def789101')",
  "userId": 102,
  "userRole": "patient", // or "doctor"
  "appointmentId": 51,
  "rating": 4,
  "comments": "Very helpful and friendly doctor.",
  "submittedAt": "2025-07-13T09:20:00Z"
}

### Collection: logs
```json
{
  "_id": "ObjectId('64ghi234567')",
  "userId": 1,
  "userRole": "admin", // patient / doctor / admin
  "action": "DELETE_DOCTOR_PROFILE",
  "details": {
    "doctorId": 35,
    "performedBy": "adminUser123"
  },
  "timestamp": "2025-07-13T08:15:30Z",
  "ipAddress": "192.168.1.101"
}


### Collection: messages
```json
{
  "_id": "ObjectId('64jkl345678')",
  "fromUserId": 102,
  "toUserId": 45,
  "fromRole": "patient",
  "toRole": "doctor",
  "messageText": "Can I reschedule our appointment?",
  "sentAt": "2025-07-13T10:35:00Z",
  "read": false
}


==================================================
==================================================
