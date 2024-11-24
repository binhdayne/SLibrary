#### Table of Contents
1.[Java Librabry Management System] (#JLMS)
2.[Introduction] (
# Java Librabry Management System (Java LiMS)  <a name="JLMS"></a>

This project is a library management system developed in Java. The project is a part of the Object-Oriented Programming course at the University of Engineering and Technology, VNU. In this project, we have implemented a library management system that includes the documents and users management, GUI and additional features.

# Introduction <a name="introduction"></a>
*This application helps administrators easily manage books in the library.
*The application helps users borrow books online and helps users easily search for books available in the library.
*The application has a section for borrowing books through QR codes, a rank section, and a chart to help users become more interested in reading books.
*The application has a game to choose the correct answer so that users can learn more about knowledge related to VNU library and knowledge related to books

<p align="center">
<img width="640" height="480" src="https://i.imgur.com/64c19ON.png">
</p>


## Built with 

- [Java](https://www.java.com/en/) - The programming language used
- [JavaFX](https://openjfx.io/) - The GUI library used
- [MySQL](https://dev.mysql.com/) - The database used
- [JDBC](https://www.oracle.com/java/technologies/jdbc.html) - The Java database connectivity used

## Team members  <a name="author"></a>

- **Dương Thanh Bình** 
- **Nguyễn Hải Dương**
- **Nguyễn Huy Hiệp**

  ## How to install ?
- Step 1: First, you need to download our project and connect it to your MySQL database. In MySQL, you need to create a new schema and run the db_ql_thu_vien.sql script.
- Step 2: After running and connecting, please modify the MySQL connection part in our code at the following files: src/main/java/com/qlthuvien/utils/DBConnection.java,
src/main/java/com/qlthuvien/login/Login_Utils.java,
src/main/java/com/qlthuvien/controller_user/HomeTabUser.java.
- Step 3: Enter the account you want to create in MySQL and start using Slibrary!
