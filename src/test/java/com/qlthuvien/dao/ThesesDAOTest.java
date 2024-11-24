package com.qlthuvien.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

import com.qlthuvien.model.Thesis;

@ExtendWith(MockitoExtension.class)
public class ThesesDAOTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    private ThesisDAO thesisDAO;

    /**
     * Set up the mock objects and the ThesisDAO instance before each test.
     * @throws SQLException
     */
    @BeforeEach
    void setUp() throws SQLException {
        thesisDAO = new ThesisDAO(mockConnection);
        lenient().when(mockConnection.prepareStatement(
            eq("INSERT INTO theses (title, author, supervisor, university, status) VALUES (?, ?, ?, ?, ?)"),
            eq(Statement.RETURN_GENERATED_KEYS)
        )).thenReturn(mockPreparedStatement);

        lenient().when(mockConnection.prepareStatement(
            eq("SELECT id, title, author, supervisor, university, status FROM theses")
        )).thenReturn(mockPreparedStatement);

        lenient().when(mockConnection.prepareStatement(
            eq("UPDATE theses SET title = ?, author = ?, supervisor = ?, university = ?, status = ? WHERE id = ?")
        )).thenReturn(mockPreparedStatement);
    }

    /**
     * Test the add method of the ThesisDAO class.
     * @throws SQLException
     */
    @Test
    void testAddThesis() throws SQLException {
        Thesis thesis = new Thesis(0, "Title", "Author", "available", "Supervisor", "University");

        thesisDAO.add(thesis);

        verify(mockPreparedStatement, times(1)).setString(1, thesis.getTitle());
        verify(mockPreparedStatement, times(1)).setString(2, thesis.getAuthor());
        verify(mockPreparedStatement, times(1)).setString(3, thesis.getSupervisor());
        verify(mockPreparedStatement, times(1)).setString(4, thesis.getUniversity());
        verify(mockPreparedStatement, times(1)).setString(5, thesis.getStatus());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    /**
     * Test the update method of the ThesisDAO class.
     * @throws SQLException
     */
    @Test
    void testUpdateThesis() throws SQLException {
        Thesis thesis = new Thesis(1, "Updated Title", "Updated Author", "available", "Updated Supervisor", "Updated University");

        when(mockConnection.prepareStatement(
            eq("UPDATE theses SET title = ?, author = ?, supervisor = ?, university = ?, status = ? WHERE id = ?")
        )).thenReturn(mockPreparedStatement);

        thesisDAO.update(thesis);

        verify(mockPreparedStatement, times(1)).setString(1, thesis.getTitle());
        verify(mockPreparedStatement, times(1)).setString(2, thesis.getAuthor());
        verify(mockPreparedStatement, times(1)).setString(3, thesis.getSupervisor());
        verify(mockPreparedStatement, times(1)).setString(4, thesis.getUniversity());
        verify(mockPreparedStatement, times(1)).setString(5, thesis.getStatus());
        verify(mockPreparedStatement, times(1)).setInt(6, thesis.getId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    /**
     * Test the delete method of the ThesisDAO class.
     * @throws SQLException
     */
    @Test
    void testDeleteThesis() throws SQLException {
        int thesisId = 1;

        when(mockConnection.prepareStatement(
            eq("DELETE FROM theses WHERE id = ?")
        )).thenReturn(mockPreparedStatement);

        thesisDAO.delete(thesisId);

        verify(mockPreparedStatement, times(1)).setInt(1, thesisId);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    /**
     * Test the getAll method of the ThesisDAO class.
     * @throws SQLException
     */
    @Test
    void testGetAllTheses() throws SQLException {
        // Setup expected data
        List<Thesis> expectedTheses = List.of(
            new Thesis(1, "Title1", "Author1", "available", "Supervisor1", "University1"),
            new Thesis(2, "Title2", "Author2", "available", "Supervisor2", "University2")
        );

        // Create mocks
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Setup ResultSet behavior - match the order of fields in constructor
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("title")).thenReturn("Title1", "Title2");
        when(mockResultSet.getString("author")).thenReturn("Author1", "Author2");
        when(mockResultSet.getString("status")).thenReturn("available", "available");
        when(mockResultSet.getString("supervisor")).thenReturn("Supervisor1", "Supervisor2");
        when(mockResultSet.getString("university")).thenReturn("University1", "University2");

        // Execute test
        List<Thesis> actualTheses = thesisDAO.getAll();

        // Verify results
        assertEquals(expectedTheses.size(), actualTheses.size(), "List sizes should match");
        for (int i = 0; i < expectedTheses.size(); i++) {
            Thesis expected = expectedTheses.get(i);
            Thesis actual = actualTheses.get(i);
            assertEquals(expected.getId(), actual.getId(), "ID should match");
            assertEquals(expected.getTitle(), actual.getTitle(), "Title should match");
            assertEquals(expected.getAuthor(), actual.getAuthor(), "Author should match");
            assertEquals(expected.getStatus(), actual.getStatus(), "Status should match");
            assertEquals(expected.getSupervisor(), actual.getSupervisor(), "Supervisor should match");
            assertEquals(expected.getUniversity(), actual.getUniversity(), "University should match");
        }
        
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet, times(3)).next();
    }

    /**
     * Test the getById method of the ThesisDAO class.
     * @throws SQLException
     */
    @Test
    void testGetThesisById() throws SQLException {
        int thesisId = 1;
        Thesis expectedThesis = new Thesis(thesisId, "Title1", "Author1", "available", "Supervisor1", "University1");

        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(
            eq("SELECT id, title, author, supervisor, university, status FROM theses WHERE id = ?"))
        ).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(thesisId);
        when(mockResultSet.getString("title")).thenReturn("Title1");
        when(mockResultSet.getString("author")).thenReturn("Author1");
        when(mockResultSet.getString("status")).thenReturn("available");
        when(mockResultSet.getString("supervisor")).thenReturn("Supervisor1");
        when(mockResultSet.getString("university")).thenReturn("University1");

        Thesis actualThesis = thesisDAO.getById(thesisId);

        // Compare individual fields instead of the entire object
        assertEquals(expectedThesis.getId(), actualThesis.getId(), "ID should match");
        assertEquals(expectedThesis.getTitle(), actualThesis.getTitle(), "Title should match");
        assertEquals(expectedThesis.getAuthor(), actualThesis.getAuthor(), "Author should match");
        assertEquals(expectedThesis.getStatus(), actualThesis.getStatus(), "Status should match");
        assertEquals(expectedThesis.getSupervisor(), actualThesis.getSupervisor(), "Supervisor should match");
        assertEquals(expectedThesis.getUniversity(), actualThesis.getUniversity(), "University should match");

        // Verify that the prepared statement was called correctly
        verify(mockPreparedStatement).setInt(1, thesisId);
        verify(mockPreparedStatement).executeQuery();
    }

}
