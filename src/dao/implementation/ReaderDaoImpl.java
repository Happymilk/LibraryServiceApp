package dao.implementation;

import dao.AuthorDao;
import dao.ReaderDao;
import dao.util.DbUtil;
import domain.Book;
import domain.Reader;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReaderDaoImpl implements ReaderDao {

    private static final ReaderDao instance = new ReaderDaoImpl();

    private ReaderDaoImpl() {}

    public static ReaderDao getInstance() {
        return instance;
    }

    @Override
    public int create(Reader reader) {
        int id = 0;
        if (!isEmailExist(reader.getEmail())) {
            try (Connection connection = DbUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement("insert into `reader` (iduser, email) " +
                         "values (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, reader.getIdUser());
                statement.setString(2, reader.getEmail());
                statement.execute();
                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        id = resultSet.getInt(1);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    @Override
    public Reader read(int idUser) {
        Reader reader = null;
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("select * from `reader` where iduser=?")) {
            statement.setInt(1, idUser);

            try(ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    reader = new Reader();
                    reader.setIdUser(idUser);
                    reader.setEmail(resultSet.getString("email"));
                    reader.setId(resultSet.getInt("idReader"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reader;
    }

    @Override
    public void update(Reader reader) {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("update `reader` set iduser=?, email=? "+
                     "where idreader=?")) {
            statement.setInt(1, reader.getIdUser());
            statement.setString(2, reader.getEmail());
            statement.setInt(3, reader.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Reader reader) {
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("delete from `reader` where idreader=?")){
            statement.setInt(1, reader.getId());
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Reader> getReadersList() {
        List<Reader> readers = new ArrayList<>();

        try(Connection connection = DbUtil.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from reader");
            while (resultSet.next()) {
                Reader reader = new Reader();
                reader.setId(resultSet.getInt("idreader"));
                reader.setIdUser(resultSet.getInt("iduser"));
                reader.setEmail(resultSet.getString("email"));
                readers.add(reader);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return readers;
    }

    @Override
    public List<Book> getBookCollection(int idReader) {
        AuthorDao authorDao = AuthorDaoImpl.getInstance();
        List<Book> bookCollectionOfReader = new ArrayList<>();
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("select * from book as b join bookcollectionofreader as br on b.idbook = br.idbook join reader as r on br.idreader = r.idreader where r.idreader=?")) {
            statement.setInt(1, idReader);

            try(ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Book book = new Book();
                    book.setId(resultSet.getInt("idbook"));
                    book.setName(resultSet.getString("title"));
                    book.setDescription(resultSet.getString("description"));
                    book.setAuthor(authorDao.getAuthorByBook(book).getName() + " " + authorDao.getAuthorByBook(book).getSurname());
                    bookCollectionOfReader.add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookCollectionOfReader;
    }

    @Override
    public int getReaderIdByUserId(int userId) {
        int readerId = -1;
        try (Connection connection = DbUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT idreader FROM reader WHERE iduser=?")) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    readerId = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return readerId;
    }


    @Override
    public boolean isEmailExist(String email) {
        boolean emailExist = false;

        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT email FROM `reader` WHERE email=?")) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    emailExist = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return emailExist;
    }
}
