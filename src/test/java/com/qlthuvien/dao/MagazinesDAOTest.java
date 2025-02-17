package com.qlthuvien.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.qlthuvien.model.Magazine;

@ExtendWith(MockitoExtension.class)
public class MagazinesDAOTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    private MagazineDAO magazineDAO;

    /**
     * Set up the mock objects and the MagazineDAO instance before each test.
     * @throws SQLException
     */
    @BeforeEach
    void setUp() throws SQLException {
        magazineDAO = new MagazineDAO(mockConnection);
        lenient().when(mockConnection.prepareStatement(
                eq("INSERT INTO magazines (title, author, publisher, issue_number, status, coverPath) VALUES (?, ?, ?, ?, ?, ?)")
        )).thenReturn(mockPreparedStatement);
    }

    /**
     * Test the add method of the MagazineDAO class.
     * @throws SQLException
     */
    @Test
    void testAddMagazine() throws SQLException {
        Magazine magazine = new Magazine(0, "Title", "Author", "available", 1, "Publisher", null);

        magazineDAO.add(magazine);

        verify(mockPreparedStatement, times(1)).setString(1, magazine.getTitle());
        verify(mockPreparedStatement, times(1)).setString(2, magazine.getAuthor());
        verify(mockPreparedStatement, times(1)).setString(3, magazine.getPublisher());
        verify(mockPreparedStatement, times(1)).setInt(4, magazine.getIssueNumber());
        verify(mockPreparedStatement, times(1)).setString(5, magazine.getStatus());
        verify(mockPreparedStatement, times(1)).setString(6, magazine.getCoverPath());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    /**
     * Test the update method of the MagazineDAO class.
     * @throws SQLException
     */
    @Test
    void testUpdateMagazine() throws SQLException {
        Magazine magazine = new Magazine(1, "Updated Title", "Updated Author", "available", 2, "Updated Publisher", null);

        when(mockConnection.prepareStatement(
                eq("UPDATE magazines SET title = ?, author = ?, publisher = ?, issue_number = ?, status = ?, coverPath = ? WHERE id = ?")
        )).thenReturn(mockPreparedStatement);

        magazineDAO.update(magazine);

        verify(mockPreparedStatement, times(1)).setString(1, magazine.getTitle());
        verify(mockPreparedStatement, times(1)).setString(2, magazine.getAuthor());
        verify(mockPreparedStatement, times(1)).setString(3, magazine.getPublisher());
        verify(mockPreparedStatement, times(1)).setInt(4, magazine.getIssueNumber());
        verify(mockPreparedStatement, times(1)).setString(5, magazine.getStatus());
        verify(mockPreparedStatement, times(1)).setString(6, magazine.getCoverPath());
        verify(mockPreparedStatement, times(1)).setInt(7, magazine.getId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    /**
     * Test the delete method of the MagazineDAO class.
     * @throws SQLException
     */
    @Test
    void testDeleteMagazine() throws SQLException {
        int magazineId = 1;

        when(mockConnection.prepareStatement(
                eq("DELETE FROM magazines WHERE id = ?")
        )).thenReturn(mockPreparedStatement);

        magazineDAO.delete(magazineId);

        verify(mockPreparedStatement, times(1)).setInt(1, magazineId);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    /**
     * Test the getAll method of the MagazineDAO class.
     * @throws SQLException
     */
    @Test
    void testGetAllMagazines() throws SQLException {
        List<Magazine> expectedMagazines = List.of(
                new Magazine(1, "Title1", "Author1", "available", 1, "Publisher1"),
                new Magazine(2, "Title2", "Author2", "available", 2, "Publisher2")
        );

        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement("SELECT * FROM magazines"))
            .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery())
            .thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("title")).thenReturn("Title1", "Title2");
        when(mockResultSet.getString("author")).thenReturn("Author1", "Author2");
        when(mockResultSet.getString("status")).thenReturn("available", "available");
        when(mockResultSet.getInt("issue_number")).thenReturn(1, 2);
        when(mockResultSet.getString("publisher")).thenReturn("Publisher1", "Publisher2");

        List<Magazine> actualMagazines = magazineDAO.getAll();

        assertEquals(expectedMagazines, actualMagazines);
    }

    /**
     * Test the getById method of the MagazineDAO class.
     * @throws SQLException
     */
    @Test
    void testGetMagazineById() throws SQLException {
        int magazineId = 1;
        Magazine expectedMagazine = new Magazine(magazineId, "Title1", "Author1", "available", 1, "Publisher1");

        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(eq("SELECT id, title, author, issue_number, publisher, status FROM magazines WHERE id = ?"))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(magazineId);
        when(mockResultSet.getString("title")).thenReturn("Title1");
        when(mockResultSet.getString("author")).thenReturn("Author1");
        when(mockResultSet.getString("status")).thenReturn("available");
        when(mockResultSet.getInt("issue_number")).thenReturn(1);
        when(mockResultSet.getString("publisher")).thenReturn("Publisher1");

        Magazine actualMagazine = magazineDAO.getById(magazineId);

        assertEquals(expectedMagazine, actualMagazine);
    }
}
