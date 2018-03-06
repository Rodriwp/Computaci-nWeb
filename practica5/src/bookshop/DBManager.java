package bookshop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;


public class DBManager implements AutoCloseable {

    private Connection connection;

    public DBManager() throws SQLException {
        connect();
    }

    private void connect() throws SQLException {
        // TODO: program this method
        String  url = "jdbc:mysql://localhost:8080/18_comweb_14b";
        connection = DriverManager.getConnection(url , "18_comweb_14", "Rw1e1Rpn");
    }

    /**
     * Close the connection to the database if it is still open.
     *
     */
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
        connection = null;
    }

    /**
     * Return the number of units in stock of the given book.
     *
     * @param book The book object.
     * @return The number of units in stock, or 0 if the book does not
     *         exist in the database.
     * @throws SQLException If somthing fails with the DB.
     */
    public int getStock(Book book) throws SQLException {
        return getStock(book.getId());
    }

    /**
     * Return the number of units in stock of the given book.
     *
     * @param bookId The book identifier in the database.
     * @return The number of units in stock, or 0 if the book does not
     *         exist in the database.
     */
    public int getStock(int bookId) throws SQLException {
        // TODO: program this method
        ResultSet rs;
        String  query = "SELECT * FROM Stock WHERE libro=?";
        try (PreparedStatement  st = connection.prepareStatement(query)) {

            st.setInt(1, bookId);
            rs = st.executeQuery();

            if(rs == null) return 0;
            if(!rs.next()) {
                return 0;
            }else{

                return rs.getInt("cantidad");
            }
        }
    }

    /**
     * Search book by ISBN.
     *
     * @param isbn The ISBN of the book.
     * @return The Book object, or null if not found.
     * @throws SQLException If somthing fails with the DB.
     */
    public Book searchBook(String isbn) throws SQLException {
        // TODO: program this method
        ResultSet rs;
        String  query = "SELECT * FROM Libros WHERE isbn=?";
        try (PreparedStatement  st = connection.prepareStatement(query)) {

            st.setInt(1, Integer.parseInt(isbn));
            rs = st.executeQuery();

            if(rs == null) return null;
            if(!rs.next()) {
                return null;
            }else{
                Book book = new Book();

                book.setTitle(rs.getString("titulo"));
                book.setIsbn(rs.getString("isbn"));
                book.setYear(rs.getInt("ano"));
                book.setId(rs.getInt("id"));

                return book;
            }
        }
    }

    /**
     * Sell a book.
     *
     * @param book The book.
     * @param units Number of units that are being sold.
     * @return True if the operation succeeds, or false otherwise
     *         (e.g. when the stock of the book is not big enough).
     * @throws SQLException If somthing fails with the DB.
     */
    public boolean sellBook(Book book, int units) throws SQLException {
        return sellBook(book.getId(), units);
    }

    /**
     * Sell a book.
     *
     * @param book The book's identifier.
     * @param units Number of units that are being sold.
     * @return True if the operation succeeds, or false otherwise
     *         (e.g. when the stock of the book is not big enough).
     * @throws SQLException If something fails with the DB.
     */
    public boolean sellBook(int book, int units) throws SQLException {
        // TODO: program this method
        boolean  success = false;
        connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        connection.setAutoCommit(false);
        String query = "SELECT * FROM Stock WHERE libro=? AND cantidad >= ?";
        try (PreparedStatement st = connection.prepareStatement(query)) {
            st.setInt(1, book);
            st.setInt(2, units);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                String query2 = "UPDATE Stock SET cantidad=cantidad-? WHERE libro=?";
                try (PreparedStatement  st2 = connection.prepareStatement(query2)) {
                    st2.setInt(1, units);
                    st2.setInt(2, book);
                    st2.executeUpdate();
                }
                String query3 = "INSERT INTO Ventas (date,libro,cantidad) VALUES (NOW(),?,?)";
                try (PreparedStatement  st3 = connection.prepareStatement(query2)) {
                    st3.setInt(1, book);
                    st3.setInt(2, units);
                    st3.executeUpdate();
                }
                success = true;

            }else{
                success = false;
            }
        } finally {
            if (success) {
                connection.commit ();
            } else {
                connection.rollback ();
            }
            connection.setAutoCommit(true);
        }
        return success;
    }

    /**
     * Return a list with all the books in the database.
     *
     * @return List with all the books.
     * @throws SQLException If something fails with the DB.
     */
    public List<Book> listBooks() throws SQLException {
        // TODO: program this method
        ResultSet rs;
        ArrayList<Book> booklist = new ArrayList<Book>();
        String  query = "SELECT * FROM Libros";
        try (PreparedStatement  st = connection.prepareStatement(query)) {
            rs = st.executeQuery();

            if(rs == null) return null;
            while(rs.next()){
                Book book = new Book();
                book.setTitle(rs.getString("titulo"));
                book.setIsbn(rs.getString("isbn"));
                book.setYear(rs.getInt("ano"));
                book.setId(rs.getInt("id"));

                booklist.add(book);
            }
        }
        return booklist ;
    }
}
