package dao;

import domain.Book;
import domain.Path;

import java.util.List;

public interface PathDao {
    int create(Path path);
    Path read(int idPath);
    void update(Path path);
    void delete(Path path);
    List<Path> getPathsList(Book book);
    Path getPathsList(String format, Book book);
    int getPathIdByFormatAndBookId(String format, int bookId);
    boolean isPathExist(String path);
}
