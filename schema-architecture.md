This Spring Boot application uses both MVC and REST controllers. Thymeleaf templates are used for the Admin and Doctor dashboards, while REST APIs serve all other modules. The application interacts with two databases—MySQL (for patient, doctor, appointment, and admin data) and MongoDB (for prescriptions). All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories. MySQL uses JPA entities while MongoDB uses document models.


1. User accesses AdminDashboard or Appointment pages.
2. The action is routed to the appropriate Thymeleaf or REST controller.
3. The controller calls the service layer to fulfil run the the user action
4. The service layer prepare and pass the data SQL repos layer 
5. The SQL repos layer access the db Tables of Patiant and Appointment and doctor presist or modify of delete or read the spesified data
6. The SQL repos layer access return the result to service layer 
7. The service layer prepare the data for presentation and pass to the controller layer 
8. the controller layer return the data to view layer or the AdminDashboard or Appointment pages to display the operation/task outcome.
