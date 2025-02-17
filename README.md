# Java Librabry Management System (Java LiMS)  <a name="JLMS"></a>

This project is a library management system developed in Java. The project is a part of the Object-Oriented Programming course at the University of Engineering and Technology, VNU. In this project, we have implemented a library management system that includes the documents and users management, GUI and additional features.

# Introduction <a name="introduction"></a>
* This application helps administrators easily manage books in the library.
* The application helps users borrow books online and helps users easily search for books available in the library.
* The application has a section for borrowing books through QR codes, a rank section, and a chart to help users become more interested in reading books.
* The application has a game to choose the correct answer so that users can learn more about knowledge related to VNU library and knowledge related to books

![yrNpBwo](https://github.com/user-attachments/assets/637eae22-00b3-4ae0-8d88-1e93f0290ab0)

## Built with 

- Language: Java
- Database: MySQL Server
- Build tool: Maven
- Design pattern: MVC Model, UML Diagram
- Operating system: Windows
- Source version control: Git


## Contributors:
### 1. Dương Thanh Bình 23020516

- Create User's functions: User's Controllers , Update Dao.java and model for Users,
- Edit fxml-files for Admin(manager) and User
- 

### 2. Nguyễn Hải Dương 23020523 

- Fix and update User's controllers, edit Dao.java and model for Users
- Additional function: Charts management in admin's view, game, animations, sounds
- Create and Update Login systems.

### 3. Nguyễn Huy Hiệp 23020534

- Create Manager's functions : Manager's contrillers,...
- Edit DAOS And Models For Managers
- Create Function (QR Code, Star Animation,...), Unit Test


## Features
**For managers**
- Chart: helps managers easily manage books and users
- Document: helps managers easily add, edit, delete books in the library
- Manage users: add, edit, delete users
- Loans: Borrow and return by entering information or scanning QR

**For users**
- Search for books and borrow on the google book API repository
- Select and save avatars
- Dashboard: count the number of borrowed books, the number of returned books, the number of unreturned books along with the user's book borrowing and returning history table
- The chart is inspired by github to show your diligence in reading books
- Table of books waiting and can generate QR codes and delete when in waiting status
- Document: shows all books in the library. Combines a search bar for users.
- Game: there will be 10 questions to choose the answer related to the library and books

  ## Demo Videos
  **For managers**

https://github.com/user-attachments/assets/16eec470-ad9b-405e-81a2-5eada91800f6


https://github.com/user-attachments/assets/00798199-2337-427a-8824-74327524f7b6


  **For Users**
  





https://github.com/user-attachments/assets/df70a145-053d-437d-9a48-9274faad9cf7




  ## UML Diagram
![java](https://github.com/user-attachments/assets/ccc16ab6-f813-4852-8eab-19bc996fc002)



  ## How to install ?
- Step 1: First, you need to download our project and connect it to your MySQL database. In MySQL, you need to create a new schema and run the db_ql_thu_vien.sql script.
- Step 2: After running and connecting, please modify the MySQL connection part in our code at the following files: src/main/java/com/qlthuvien/utils/DBConnection.java,
src/main/java/com/qlthuvien/login/Login_Utils.java,
src/main/java/com/qlthuvien/controller_user/HomeTabUser.java.
- Step 3: Enter the account you want to create in MySQL and start using Slibrary!
  
