package dao;

import domain.Author;
import domain.Book;

import java.util.List;

public interface AuthorDao {
    int create(Author author);
    Author read(int idAuthor);
    void update(Author author);
    void delete(Author author);
    List<Author> getAuthorsList();
    Author getAuthorByBook(Book book);
    boolean isSurnameExist(String surname);
    boolean isNameExist(String name);
}
