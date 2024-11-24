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

import com.qlthuvien.model.BookFromAPI;

@ExtendWith(MockitoExtension.class)
public class BooksFromAPIDAOTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    private BookFromAPIDAO bookFromAPIDAO;

    /**
     * Set up the mock objects and the BookFromAPIDAO instance before each test.
     * @throws SQLException
     */
    @BeforeEach
    void setUp() throws SQLException {
        bookFromAPIDAO = new BookFromAPIDAO(mockConnection);
        lenient().when(mockConnection.prepareStatement(
            eq("INSERT INTO books_from_api (isbn, title, author, publisher, published_date, description, status) VALUES (?, ?, ?, ?, ?, ?, ?)"),
            eq(Statement.RETURN_GENERATED_KEYS)
        )).thenReturn(mockPreparedStatement);
    }

    /**
     * Test the add method of the BookFromAPIDAO class.
     * @throws SQLException
     */
    @Test
    void testAddBookFromAPI() throws SQLException {
        BookFromAPI book = new BookFromAPI(0, "Title", "Author", "available", "ISBN", "Publisher", "2023-01-01", "Description");

        bookFromAPIDAO.add(book);

        verify(mockPreparedStatement, times(1)).setString(1, book.getIsbn());
        verify(mockPreparedStatement, times(1)).setString(2, book.getTitle());
        verify(mockPreparedStatement, times(1)).setString(3, book.getAuthor());
        verify(mockPreparedStatement, times(1)).setString(4, book.getPublisher());
        verify(mockPreparedStatement, times(1)).setString(5, book.getPublishedDate());
        verify(mockPreparedStatement, times(1)).setString(6, book.getDescription());
        verify(mockPreparedStatement, times(1)).setString(7, book.getStatus());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    /**
     * Test the update method of the BookFromAPIDAO class.
     * @throws SQLException
     */
    @Test
    void testUpdateBookFromAPI() throws SQLException {
        BookFromAPI book = new BookFromAPI(1, "Updated Title", "Updated Author", "available", "ISBN", "Updated Publisher", "2023-01-01", "Updated Description");

        when(mockConnection.prepareStatement(
            eq("UPDATE books_from_api SET title = ?, author = ?, publisher = ?, published_date = ?, description = ?, status = ? WHERE isbn = ?")
        )).thenReturn(mockPreparedStatement);

        bookFromAPIDAO.update(book);

        verify(mockPreparedStatement, times(1)).setString(1, book.getTitle());
        verify(mockPreparedStatement, times(1)).setString(2, book.getAuthor());
        verify(mockPreparedStatement, times(1)).setString(3, book.getPublisher());
        verify(mockPreparedStatement, times(1)).setString(4, book.getPublishedDate());
        verify(mockPreparedStatement, times(1)).setString(5, book.getDescription());
        verify(mockPreparedStatement, times(1)).setString(6, book.getStatus());
        verify(mockPreparedStatement, times(1)).setString(7, book.getIsbn());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    /**
     * Test the delete method of the BookFromAPIDAO class.
     * @throws SQLException
     */
    @Test
    void testDeleteBookFromAPI() throws SQLException {
        int bookId = 1;

        when(mockConnection.prepareStatement(
            eq("DELETE FROM books_from_api WHERE id = ?")
        )).thenReturn(mockPreparedStatement);

        bookFromAPIDAO.delete(bookId);

        verify(mockPreparedStatement, times(1)).setInt(1, bookId);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    /**
     * Test the getAll method of the BookFromAPIDAO class.
     * @throws SQLException
     */
    @Test
    void testGetAllBooksFromAPI() throws SQLException {
        List<BookFromAPI> expectedBooks = List.of(
            new BookFromAPI(1, "Title1", "Author1", "available", "ISBN1", "Publisher1", "2023-01-01", "Description1"),
            new BookFromAPI(2, "Title2", "Author2", "available", "ISBN2", "Publisher2", "2023-01-02", "Description2")
        );

        Statement mockStatement = mock(Statement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(eq("SELECT id, isbn, title, author, publisher, published_date, description, status FROM books_from_api"))).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("isbn")).thenReturn("ISBN1", "ISBN2");
        when(mockResultSet.getString("title")).thenReturn("Title1", "Title2");
        when(mockResultSet.getString("author")).thenReturn("Author1", "Author2");
        when(mockResultSet.getString("status")).thenReturn("available", "available");
        when(mockResultSet.getString("publisher")).thenReturn("Publisher1", "Publisher2");
        when(mockResultSet.getString("published_date")).thenReturn("2023-01-01", "2023-01-02");
        when(mockResultSet.getString("description")).thenReturn("Description1", "Description2");

        List<BookFromAPI> actualBooks = bookFromAPIDAO.getAll();

        assertEquals(expectedBooks, actualBooks);
    }

    /**
     * Test the getById method of the BookFromAPIDAO class.
     * @throws SQLException
     */
    @Test
    void testGetBookFromAPIById() throws SQLException {
        int bookId = 1;
        BookFromAPI expectedBook = new BookFromAPI(bookId, "Title1", "Author1", "available", "ISBN1", "Publisher1", "2023-01-01", "Description1");

        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(eq("SELECT id, isbn, title, author, publisher, published_date, description, status FROM books_from_api WHERE id = ?"))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(bookId);
        when(mockResultSet.getString("isbn")).thenReturn("ISBN1");
        when(mockResultSet.getString("title")).thenReturn("Title1");
        when(mockResultSet.getString("author")).thenReturn("Author1");
        when(mockResultSet.getString("status")).thenReturn("available");
        when(mockResultSet.getString("publisher")).thenReturn("Publisher1");
        when(mockResultSet.getString("published_date")).thenReturn("2023-01-01");
        when(mockResultSet.getString("description")).thenReturn("Description1");

        BookFromAPI actualBook = bookFromAPIDAO.getById(bookId);

        assertEquals(expectedBook, actualBook);
    }
}
