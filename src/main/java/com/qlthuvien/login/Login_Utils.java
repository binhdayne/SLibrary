package com.qlthuvien.login;
import com.qlthuvien.controller_user.MainUserController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Login_Utils {

    public static Map<String, String> getUserInfo(String userId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Map<String, String> userInfo = new HashMap<>();

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/slibrary", "root", "password");
            preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE membership_id = ?");
            preparedStatement.setString(1, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                userInfo.put("name", resultSet.getString("name"));
                userInfo.put("email", resultSet.getString("email"));
                userInfo.put("phone", resultSet.getString("phone"));
                userInfo.put("user_name", resultSet.getString("user_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return userInfo;
    }
    public static void changeScene(ActionEvent event, String fxmlFile, String title, String membership_id, String audioFile, String name, String email, String phone) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(Login_Utils.class.getResource("/" + fxmlFile));
            root = loader.load();

            // Get the controller and pass the user ID
            MainUserController controller = loader.getController();
            controller.setmembershipId(membership_id);// Pass user ID to the controller
            controller.setname(name);
            controller.setemail(email);
            controller.setphone(phone);
            File soundFile = new File(audioFile); // Sử dụng file .wav thay vì .mp3
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);



            // Phát âm thanh khi thêm tạp chí thành công
            clip.start();

        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 1000, 700));
        stage.show();
    }

    public static void LoginUser(ActionEvent event, String username, String password) {
        Connection connection  = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/slibrary", "root", "14112005");
            preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE user_name = ?");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println("User not found");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("User not found");
                alert.show();
            } else {
                while (resultSet.next()) {
                    String retrievedPassword = resultSet.getString("password");
                    String userId = resultSet.getString("membership_id");
                    String name = resultSet.getString("name");
                    String email = resultSet.getString("email");
                    String phone = resultSet.getString("phone");

                    if (password.equals(retrievedPassword)) {
                        changeScene(event, "com/qlthuvien/giao_dien_nguoi_dung/MainLayout.fxml", "Welcome", userId, "src/main/resources/icons/preview.wav", name, email, phone);
                    } else {
                        System.out.println("Wrong password");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Wrong password");
                        alert.show();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void changeScene2(ActionEvent event, String fxmlFile, String title) {
        Parent root = null;

        try {
            FXMLLoader loader = new FXMLLoader(Login_Utils.class.getResource("/" + fxmlFile));
            root = loader.load();



        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 1000, 700));
        stage.show();
    }
    public static void LoginManager(ActionEvent event, String manager, String password) {
        Connection connection  = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/slibrary", "root", "14112005");
            preparedStatement = connection.prepareStatement("SELECT password FROM manager WHERE manager = ?");
            preparedStatement.setString(1,manager);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.isBeforeFirst()){
                System.out.println("Manager not found");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Manager not found");
                alert.show();
            } else {
                while (resultSet.next()){
                    String retrievedPassword = resultSet.getString("password");
                    if (password.equals(retrievedPassword)){
                        changeScene2(event, "com/qlthuvien/MainLayout.fxml", "Welcome");
                    } else {
                        System.out.println("Wrong password");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Wrong password");
                        alert.show();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}