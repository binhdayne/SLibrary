package com.qlthuvien.controller_user;

import com.qlthuvien.model.User;
import com.qlthuvien.utils.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;

public class FirstScreenController {

    @FXML
    private ImageView avatarImageView;
    @FXML
    private Label userIdLabel;
    @FXML
    private Label userNameLabel;
    @FXML
    private ImageView timeImageView;

    public void initialize() {
        // Display the time-based image in timeImageView
        updateTimeImage();
    }

    // Load user details into the UI
    public void setUserDetails(String membershipId) {
        try {
            // Fetch user details from the database
            User user = getUserFromDatabase(membershipId);
            if (user != null) {
                // Display the user details
                userIdLabel.setText(user.getMembershipId());
                userNameLabel.setText(user.getName());
                setAvatar(user.getAvatar());
            } else {
                // Default values if user is not found
                userIdLabel.setText("Unknown ID");
                userNameLabel.setText("Unknown User");
                setAvatar(null);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user details: " + e.getMessage());
            userIdLabel.setText("Error");
            userNameLabel.setText("Error");
            setAvatar(null);
        }
    }

    private User getUserFromDatabase(String membershipId) throws SQLException {
        String query = "SELECT membership_id, name, email, phone, avatar FROM users WHERE membership_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, membershipId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("membership_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("avatar")
                    );
                }
            }
        }
        return null;
    }

    private void setAvatar(String avatarPath) {
        if (avatarPath != null && !avatarPath.isEmpty()) {
            try {
                // Load the avatar from the given file path
                Image avatarImage = new Image(new File(avatarPath).toURI().toString());
                avatarImageView.setImage(avatarImage);
            } catch (Exception e) {
                // Load a default avatar if there is an error
                avatarImageView.setImage(new Image(getClass().getResourceAsStream("/icons/default-avatar.png")));
                System.err.println("Error loading avatar: " + e.getMessage());
            }
        } else {
            // Load a default avatar if no avatar is available
            avatarImageView.setImage(new Image(getClass().getResourceAsStream("/icons/default-avatar.png")));
        }
    }

    // Method to determine the appropriate image path based on the current time
    private String getImagePath(LocalTime time) {
        if (time.isAfter(LocalTime.of(4, 59)) && time.isBefore(LocalTime.of(12, 0))) {
            return getClass().getResource("/icons/Gmning.gif").toExternalForm();
        } else if (time.isAfter(LocalTime.of(11, 59)) && time.isBefore(LocalTime.of(18, 0))) {
            return getClass().getResource("/icons/Gatn.gif").toExternalForm();
        } else if (time.isAfter(LocalTime.of(17, 59)) && time.isBefore(LocalTime.of(23, 0))) {
            return getClass().getResource("/icons/Gev.gif").toExternalForm();
        } else {
            return getClass().getResource("/icons/Gn.gif").toExternalForm();
        }
    }

    // Update the timeImageView based on the current time
    private void updateTimeImage() {
        LocalTime now = LocalTime.now(); // Get the current time
        String imagePath = getImagePath(now); // Get the corresponding image path
        timeImageView.setImage(new Image(imagePath)); // Set the image in the ImageView
    }
}
