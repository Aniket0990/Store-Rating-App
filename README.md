# Store-Rating-App

# Introduction -
- Developed a full-stack web application using Spring Boot, MySQL, and ReactJS that enables users to submit ratings (1–5) for registred stores. 
- Implemented a role-based login system supporting System Administrators, Normal Users, and Store Owners with tailored dashboards and functionalities. 
- Admins can manage users and stores, view detailed listings, and apply filters. 
- Users can register, browse stores, submit or update ratings, and owners can monitor ratings and view users who rated their stores. 
- The application features responsive UI, search, and secure password management.

# Functionalities -
# 1.System Administrator 
- Can add new stores, normal users, and admin users. 
- Has access to a dashboard displaying: 
    -Total number of users
    -Total number of stores 
    -Total number of submitted ratings 
- Can add new users with the following details: 
    - Name 
    - Email 
    - Password 
    - Address 
- Can view a list of stores with the following details:
    - Name, Email, Address, Rating 
- Can view a list of normal and admin users with: 
    - Name, Email, Address, Role 
- Can apply filters on all listings based on Name, Email, Address, and Role. 
- Can view details of all users, including Name, Email, Address, and Role. 
    - If the user is a Store Owner, their Rating should also be displayed. 
- Can log out from the system.
  
# 2.Normal User 
- Can sign up and log in to the platform. 
- Signup form fields: 
    - Name 
    - Email 
    - Address 
    - Password 
- Can update their password after logging in. 
- Can view a list of all registered stores. 
- Can search for stores by Name and Address. 
- Store listings should display: 
    - Store Name 
    - Address 
    - Overall Rating 
    - User's Submitted Rating 
    - Option to submit a rating 
    - Option to modify their submitted rating 
- Can submit ratings (between 1 to 5) for individual stores. 
- Can log out from the system.
   
# 3.Store Owner 
- Can log in to the platform. 
- Can update their password after logging in. 
- Dashboard functionalities: 
    - View a list of users who have submitted ratings for their store. 
    - See the average rating of their store. 
- Can log out from the system.

# Technology Stack - 
- Backend: Java, SpringBoot
- Database: MySQL 
- Frontend: ReactJs , HTML , CSS , JavaScript

# Demo Images - 
![IMG_20251205_164157](https://github.com/user-attachments/assets/e09d2ee5-ec3d-4d65-90e2-8d511639fe6d)
