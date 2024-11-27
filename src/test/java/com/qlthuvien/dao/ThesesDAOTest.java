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
import org.mockito.InOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.inOrder;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.qlthuvien.model.Thesis;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
        
        // Cấu hình mock cho câu lệnh INSERT
        lenient().when(mockConnection.prepareStatement(
            eq("INSERT INTO theses (title, author, supervisor, university, status, coverPath) VALUES (?, ?, ?, ?, ?, ?)")
        )).thenReturn(mockPreparedStatement);

        // Cấu hình mock cho câu lệnh SELECT ALL
        lenient().when(mockConnection.prepareStatement(
            eq("SELECT * FROM theses")
        )).thenReturn(mockPreparedStatement);

        // Cấu hình mock cho câu lệnh UPDATE
        lenient().when(mockConnection.prepareStatement(
            eq("UPDATE theses SET title = ?, author = ?, supervisor = ?, university = ?, status = ?, coverPath = ? WHERE id = ?")
        )).thenReturn(mockPreparedStatement);

        // Cấu hình mock cho câu lệnh SELECT BY ID - sửa lại câu SQL
        lenient().when(mockConnection.prepareStatement(
            eq("SELECT id, title, author, supervisor, university, status, coverPath FROM theses WHERE id = ?")
        )).thenReturn(mockPreparedStatement);

        // Cấu hình mock cho câu lệnh DELETE
        lenient().when(mockConnection.prepareStatement(
            eq("DELETE FROM theses WHERE id = ?")
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

        thesisDAO.update(thesis);

        verify(mockPreparedStatement, times(1)).setString(1, thesis.getTitle());
        verify(mockPreparedStatement, times(1)).setString(2, thesis.getAuthor());
        verify(mockPreparedStatement, times(1)).setString(3, thesis.getSupervisor());
        verify(mockPreparedStatement, times(1)).setString(4, thesis.getUniversity());
        verify(mockPreparedStatement, times(1)).setString(5, thesis.getStatus());
        verify(mockPreparedStatement, times(1)).setString(6, thesis.getCoverPath());
        verify(mockPreparedStatement, times(1)).setInt(7, thesis.getId());
        verify(mockPreparedStatement, times(1)).executeUpdate();

        // Thêm kiểm tra thứ tự thực hiện
        InOrder inOrder = inOrder(mockPreparedStatement);
        inOrder.verify(mockPreparedStatement).setString(1, thesis.getTitle());
        inOrder.verify(mockPreparedStatement).setString(2, thesis.getAuthor());
        inOrder.verify(mockPreparedStatement).setString(3, thesis.getSupervisor());
        inOrder.verify(mockPreparedStatement).setString(4, thesis.getUniversity());
        inOrder.verify(mockPreparedStatement).setString(5, thesis.getStatus());
        inOrder.verify(mockPreparedStatement).setString(6, thesis.getCoverPath());
        inOrder.verify(mockPreparedStatement).setInt(7, thesis.getId());
        inOrder.verify(mockPreparedStatement).executeUpdate();
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
            new Thesis(
                1,              // id
                "Title1",       // title
                "Author1",      // author
                "Supervisor1",  // supervisor
                "University1",  // university
                "available",    // status
                null           // coverPath
            ),
            new Thesis(
                2,              // id
                "Title2",       // title
                "Author2",      // author
                "Supervisor2",  // supervisor
                "University2",  // university
                "available",    // status
                null           // coverPath
            )
        );

        // Create mocks
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Setup ResultSet behavior - match constructor parameter order
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("title")).thenReturn("Title1", "Title2");
        when(mockResultSet.getString("author")).thenReturn("Author1", "Author2");
        when(mockResultSet.getString("supervisor")).thenReturn("Supervisor1", "Supervisor2");
        when(mockResultSet.getString("university")).thenReturn("University1", "University2");
        when(mockResultSet.getString("status")).thenReturn("available", "available");
        when(mockResultSet.getString("coverPath")).thenReturn(null, null);

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
            assertEquals(expected.getStatus(), actual.getStatus(), 
                String.format("Thesis status mismatch - Expected: %s, Actual: %s", 
                expected.getStatus(), actual.getStatus()));
            assertEquals(expected.getSupervisor(), actual.getSupervisor(), "Supervisor should match");
            assertEquals(expected.getUniversity(), actual.getUniversity(), "University should match");
            assertEquals(expected.getCoverPath(), actual.getCoverPath(), "CoverPath should match");
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
        // Tạo dữ liệu mong đợi
        Thesis expectedThesis = createTestThesis();

        // Mock ResultSet
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        
        // Mock dữ liệu từ ResultSet
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(expectedThesis.getId());
        when(mockResultSet.getString("title")).thenReturn(expectedThesis.getTitle());
        when(mockResultSet.getString("author")).thenReturn(expectedThesis.getAuthor());
        when(mockResultSet.getString("supervisor")).thenReturn(expectedThesis.getSupervisor());
        when(mockResultSet.getString("university")).thenReturn(expectedThesis.getUniversity());
        when(mockResultSet.getString("status")).thenReturn(expectedThesis.getStatus());
        when(mockResultSet.getString("coverPath")).thenReturn(expectedThesis.getCoverPath());

        // Thực thi test và verify
        Thesis actualThesis = thesisDAO.getById(1);

        // Verify từng trường riêng lẻ
        assertEquals(expectedThesis.getId(), actualThesis.getId(), "ID should match");
        assertEquals(expectedThesis.getTitle(), actualThesis.getTitle(), "Title should match");
        assertEquals(expectedThesis.getAuthor(), actualThesis.getAuthor(), "Author should match");
        assertEquals(expectedThesis.getSupervisor(), actualThesis.getSupervisor(), "Supervisor should match");
        assertEquals(expectedThesis.getUniversity(), actualThesis.getUniversity(), "University should match");
        assertEquals(expectedThesis.getStatus(), actualThesis.getStatus(), "Status should match");
        assertEquals(expectedThesis.getCoverPath(), actualThesis.getCoverPath(), "CoverPath should match");

        // Verify các tương tác
        verify(mockPreparedStatement).setInt(1, 1);
        verify(mockPreparedStatement).executeQuery();
    }

    // Tạo phương thức nàynày để tái sử dụng dữ liệu test
    private Thesis createTestThesis() {
        return new Thesis(
            1,                  // id
            "Test Thesis",      // title 
            "Test Author",      // author
            "available",        // status - phải đặt trước supervisor và university
            "Test Supervisor",  // supervisor
            "Test University",  // university
            "test_cover.jpg"    // coverPath
        );
    }

    @Test
    void testAddThesisWithNullValues() throws SQLException {
        // Test thêm luận văn với giá trị null
        Thesis invalidThesis = new Thesis(0, null, null, null, null, null);
        
        // Cấu hình mock để ném ra SQLException khi thực thi executeUpdate
        when(mockConnection.prepareStatement(
            eq("INSERT INTO theses (title, author, supervisor, university, status, coverPath) VALUES (?, ?, ?, ?, ?, ?)")
        )).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Cannot insert null values"));
        
        // Kiểm tra xem có ném ra SQLException không
        SQLException exception = assertThrows(SQLException.class, () -> {
            thesisDAO.add(invalidThesis);
        });
        
        // Kiểm tra thông báo lỗi không null
        assertNotNull(exception.getMessage());
    }

}
