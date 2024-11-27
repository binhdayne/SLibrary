package com.qlthuvien.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.qlthuvien.model.BorrowReturn;

@ExtendWith(MockitoExtension.class)
public class BorrowReturnDAOTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private Statement mockStatement;

    private BorrowReturnDAO borrowReturnDAO;

    @BeforeEach
    void setUp() throws SQLException {
        borrowReturnDAO = new BorrowReturnDAO(mockConnection);
        
        // Setup common PreparedStatement mocks
        lenient().when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        lenient().when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        lenient().when(mockConnection.createStatement()).thenReturn(mockStatement);
    }

    /**
     * Test borrowing a document.
     * @throws SQLException
     */
    @Test
    void testBorrowDocument() throws SQLException {
        // Setup test data
        LocalDate borrowDate = LocalDate.now();
        BorrowReturn borrowReturn = new BorrowReturn(
            "M001", 1, "BOOK", borrowDate, null, "borrowed"
        );

        // Execute test
        borrowReturnDAO.borrowDocument(borrowReturn);

        // Verify các tương tác với PreparedStatement cho câu lệnh INSERT
        verify(mockPreparedStatement).setString(1, borrowReturn.getMembershipId());
        verify(mockPreparedStatement).setInt(2, borrowReturn.getDocumentId());
        verify(mockPreparedStatement).setString(3, borrowReturn.getDocumentType());
        verify(mockPreparedStatement).setDate(4, Date.valueOf(borrowReturn.getBorrowDate()));
        verify(mockPreparedStatement).setNull(5, Types.DATE);
        verify(mockPreparedStatement).setString(6, borrowReturn.getStatus());
        
        // Verify executeUpdate() được gọi 2 lần
        verify(mockPreparedStatement, times(2)).executeUpdate();

        // Verify transaction management
        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection).commit();
        verify(mockConnection).setAutoCommit(true);
    }
    
    /**
     * Test returning a document.
     * @throws SQLException
     */
    @Test
    void testReturnDocument() throws SQLException {
        // Setup test data
        LocalDate borrowDate = LocalDate.now().minusDays(7);
        LocalDate returnDate = LocalDate.now();
        BorrowReturn borrowReturn = new BorrowReturn(
            "M001", 1, "BOOK", borrowDate, returnDate, "returned"
        );

        // Execute test
        borrowReturnDAO.returnDocument(borrowReturn);

        // Verify các tương tác với PreparedStatement cho câu lệnh UPDATE borrow_return
        verify(mockPreparedStatement).setDate(1, Date.valueOf(borrowReturn.getReturnDate()));
        verify(mockPreparedStatement).setString(2, borrowReturn.getStatus());
        verify(mockPreparedStatement).setString(3, borrowReturn.getMembershipId());
        verify(mockPreparedStatement).setInt(4, borrowReturn.getDocumentId());
        verify(mockPreparedStatement).setString(5, borrowReturn.getDocumentType());
        
        // Verify executeUpdate() được gọi 2 lần
        verify(mockPreparedStatement, times(2)).executeUpdate();

        // Verify transaction management
        verify(mockConnection).setAutoCommit(false);
        verify(mockConnection).commit();
        verify(mockConnection).setAutoCommit(true);
    }

    /**
     * Test getting all borrow returns.
     * @throws SQLException
     */
    @Test
    void testGetAllBorrowReturns() throws SQLException {
        // Setup test data
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        
        LocalDate borrowDate = LocalDate.of(2024, 1, 1);
        LocalDate returnDate = LocalDate.of(2024, 1, 8);
        
        when(mockResultSet.getString("membership_id")).thenReturn("M001", "M002");
        when(mockResultSet.getInt("document_id")).thenReturn(1, 2);
        when(mockResultSet.getString("document_type")).thenReturn("BOOK", "THESIS");
        when(mockResultSet.getDate("borrow_date")).thenReturn(Date.valueOf(borrowDate));
        when(mockResultSet.getDate("return_date")).thenReturn(Date.valueOf(returnDate));
        when(mockResultSet.getString("status")).thenReturn("returned", "borrowed");

        // Execute test
        List<BorrowReturn> results = borrowReturnDAO.getAll();

        // Verify results
        assertEquals(2, results.size());
        
        // Verify first record
        assertEquals("M001", results.get(0).getMembershipId());
        assertEquals(1, results.get(0).getDocumentId());
        assertEquals("BOOK", results.get(0).getDocumentType());
        assertEquals(borrowDate, results.get(0).getBorrowDate());
        assertEquals(returnDate, results.get(0).getReturnDate());
        assertEquals("returned", results.get(0).getStatus());
        
        // Verify second record
        assertEquals("M002", results.get(1).getMembershipId());
        assertEquals(2, results.get(1).getDocumentId());
        assertEquals("THESIS", results.get(1).getDocumentType());

        // Verify Statement was used
        verify(mockConnection).createStatement();
        verify(mockStatement).executeQuery("SELECT * FROM borrow_return");
    }

    /**
     * Test getting borrow returns by status.
     * @throws SQLException
     */
    @Test
    void testGetByStatus() throws SQLException {
        // Setup test data
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        
        String status = "borrowed";
        LocalDate borrowDate = LocalDate.now();
        
        when(mockResultSet.getString("membership_id")).thenReturn("M001");
        when(mockResultSet.getInt("document_id")).thenReturn(1);
        when(mockResultSet.getString("document_type")).thenReturn("BOOK");
        when(mockResultSet.getDate("borrow_date")).thenReturn(Date.valueOf(borrowDate));
        when(mockResultSet.getDate("return_date")).thenReturn(null);
        when(mockResultSet.getString("status")).thenReturn(status);

        // Execute test
        List<BorrowReturn> results = borrowReturnDAO.getByStatus(status);

        // Verify results
        assertEquals(1, results.size());
        assertEquals(status, results.get(0).getStatus());
        verify(mockPreparedStatement).setString(1, status);
    }
}

