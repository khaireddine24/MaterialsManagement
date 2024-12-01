# Materials Management System

An Android application for managing materials and classes within an educational or organizational context. This app provides a streamlined experience for users to navigate between features like material and class management while ensuring secure access via Firebase Authentication.

## Features

- **User Authentication**:
  - Login/logout functionality using Firebase Authentication.
  - Automatic fetching of user profile data from Firestore (e.g., username, email).

- **User Profile**:
  - Displays user details, including profile picture, username, and email address.

- **Navigation**:
  - Easy access to material and class management functionalities through intuitive cards.

- **Secure Logout**:
  - Securely logs out the user and redirects them to the login screen.

## Technologies Used

- **Android (Java)**: Core application development.
- **Firebase Authentication**: User authentication and session management.
- **Firestore**: Storing and retrieving user profile data.
- **ConstraintLayout**: Modern and responsive UI layout.
- **CardView**: Clean and organized navigation interface.

## Screenshots

<img src='https://github.com/user-attachments/assets/0d3400f9-76dd-45bc-8adf-ff5a4dd22ddc'/>

<img src='https://github.com/user-attachments/assets/85c39148-68a2-462d-94f3-2590044c2893'/>

<img src='https://github.com/user-attachments/assets/3bcec4a2-2367-4c5d-b54d-2f5bcd19d09f'/>

<img src='https://github.com/user-attachments/assets/0881241f-31fa-44bc-b183-1f14ad6684ac'/>




<img src='https://github.com/user-attachments/assets/fc2eab39-7339-4fe8-85fc-5e7a97f16f03'/>
<img src='https://github.com/user-attachments/assets/d2511686-e2bc-43fd-925f-f1dd1aa1aa7e' width='30%' height='30%'/>

<img src='https://github.com/user-attachments/assets/799788c8-eb98-4d59-8fb5-49b8b3ceaf54'/>
<img src='https://github.com/user-attachments/assets/984a9719-5cbb-41a7-89ca-a28b79fda980'/>
<img src='https://github.com/user-attachments/assets/2eb45a0a-1220-4282-aa7e-53609f17de95'/>








## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/materials-management.git
2. Open the project in Android Studio.<br/>

3. Add your Firebase configuration file (google-services.json) to the app/ directory.<br/>
4. Sync the project with Gradle files.<br/>
5. Run the app on an emulator or physical device.<br/>

## How to Use


+Login: Users can log in using their Firebase credentials.<br/>
+Home Screen:<br/>
-View profile information (username, email).<br/>
-Navigate to "Manage Materials" or "Manage Classes" sections.<br/>
+Logout:<br/>
-Use the "Logout" button to securely end the session and return to the login screen.<br/>
+Future Enhancements:<br/>
-Add real-time updates for materials and classes.<br/>
-Integrate push notifications for new materials or updates.<br/>
-Include a role-based system for administrators and general users.<br/>
