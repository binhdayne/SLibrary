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

import com.qlthuvien.model.Book;

@ExtendWith(MockitoExtension.class)
public class BooksDAOTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    private BookDAO bookDAO;

    /**
     * Set up the mock objects and the BookDAO instance before each test.
     * @throws SQLException
     */
    @BeforeEach
    void setUp() throws SQLException {
        bookDAO = new BookDAO(mockConnection);
        
        // Cấu hình cho câu lệnh UPDATE 
        lenient().when(mockConnection.prepareStatement(
            "UPDATE books SET title = ?, author = ?, genre = ?, status = ?, coverPath = ? WHERE id = ?"
        )).thenReturn(mockPreparedStatement);
        
        // Cấu hình cho executeUpdate
        lenient().when(mockPreparedStatement.executeUpdate()).thenReturn(1);
    }

    /**
     * Test the add method of the BookDAO class.
     * @throws SQLException
     */
    @Test
    void testAddBook() throws SQLException {
        Book book = new Book(0, "Title", "Author", "available", "Genre", "coverPath");

        bookDAO.add(book);

        verify(mockPreparedStatement, times(1)).setString(1, book.getTitle());
        verify(mockPreparedStatement, times(1)).setString(2, book.getAuthor());
        verify(mockPreparedStatement, times(1)).setString(3, book.getGenre());
        verify(mockPreparedStatement, times(1)).setString(4, book.getStatus());
        verify(mockPreparedStatement, times(1)).setString(5, book.getBookcover());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    /**
     * Test the update method of the BookDAO class.
     * @throws SQLException
     */
    @Test
    void testUpdateBook() throws SQLException {
        Book book = new Book(1, "Updated Title", "Updated Author", "available", "Updated Genre");

        when(mockConnection.prepareStatement(
            eq("UPDATE books SET title = ?, author = ?, genre = ?, status = ?, coverPath = ? WHERE id = ?")
        )).thenReturn(mockPreparedStatement);

        bookDAO.update(book);

        verify(mockPreparedStatement, times(1)).setString(1, book.getTitle());
        verify(mockPreparedStatement, times(1)).setString(2, book.getAuthor());
        verify(mockPreparedStatement, times(1)).setString(3, book.getGenre());
        verify(mockPreparedStatement, times(1)).setString(4, book.getStatus());
        verify(mockPreparedStatement, times(1)).setString(5, book.getBookcover());
        verify(mockPreparedStatement, times(1)).setInt(6, book.getId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    /**
     * Test the delete method of the BookDAO class.
     * @throws SQLException
     */
    @Test
    void testDeleteBook() throws SQLException {
        int bookId = 1;

        when(mockConnection.prepareStatement(
            eq("DELETE FROM books WHERE id = ?")
        )).thenReturn(mockPreparedStatement);

        bookDAO.delete(bookId);

        verify(mockPreparedStatement, times(1)).setInt(1, bookId);
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    /**
     * Test the getAll method of the BookDAO class.
     * @throws SQLException
     */
    @Test
    void testGetAllBooks() throws SQLException {
        List<Book> expectedBooks = List.of(
            new Book(1, "Title1", "Author1", "available", "Genre1", "coverPath1"),
            new Book(2, "Title2", "Author2", "available", "Genre2", "coverPath2")
        );

        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(eq("SELECT * FROM books"))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1, 2);
        when(mockResultSet.getString("title")).thenReturn("Title1", "Title2");
        when(mockResultSet.getString("author")).thenReturn("Author1", "Author2");
        when(mockResultSet.getString("status")).thenReturn("available", "available");
        when(mockResultSet.getString("genre")).thenReturn("Genre1", "Genre2");
        when(mockResultSet.getString("coverPath")).thenReturn("coverPath1", "coverPath2");

        List<Book> actualBooks = bookDAO.getAll();

        assertEquals(expectedBooks, actualBooks);
    }

    /**
     * Test the getById method of the BookDAO class.
     * @throws SQLException
     */
    @Test
    void testGetBookById() throws SQLException {
        int bookId = 1;
        Book expectedBook = new Book(bookId, "Title1", "Author1", "available", "Genre1");

        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(eq("SELECT id, title, author, genre, status FROM books WHERE id = ?"))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(bookId);
        when(mockResultSet.getString("title")).thenReturn("Title1");
        when(mockResultSet.getString("author")).thenReturn("Author1");
        when(mockResultSet.getString("status")).thenReturn("available");
        when(mockResultSet.getString("genre")).thenReturn("Genre1");

        Book actualBook = bookDAO.getById(bookId);

        assertEquals(expectedBook, actualBook);
    }
}
