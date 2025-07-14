## MySQL Database Design (4 tables)

tables: #appointments, #patients, #doctors, , #admin, optainl  #clinic_locations, #payments
For each table:

Define columns, Specify data types, Mark primary keys and any foreign key relationships, Consider constraints:

Should some fields be NOT NULL, UNIQUE, or AUTO_INCREMENT?
Should we validate email or phone formats later via code?
Ask yourself: What happens if a patient is deleted? Should appointments also be deleted?


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



## MongoDB Collection Design

Collections:  #prescriptions, #feedback, #logs, #messages.

### Collection: prescriptions
```json
{
  "_id": "ObjectId('64abc123456')",
  "patientName": "John Smith",
  "appointmentId": 51,
  "medication": "Paracetamol",
  "dosage": "500mg",
  "doctorNotes": "Take 1 tablet every 6 hours.",
  "refillCount": 2,
  "pharmacy": {
    "name": "Walgreens SF",
    "location": "Market Street"
  }
}

Should MongoDB documents include the full patient object or just an ID?
What would a chat message document look like?
What happens if the schema needs to evolve – will your design support that?
