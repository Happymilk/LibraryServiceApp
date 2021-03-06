package dao.implementation;

import dao.UserDao;
import dao.util.DbUtil;
import domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    private static final UserDao instance = new UserDaoImpl();

    private UserDaoImpl() {}

    public static UserDao getInstance() {
        return instance;
    }

    @Override
    public int create(User user) {
        int id = 0;

        if (!isLoginExist(user.getLogin())) {
            try (Connection connection = DbUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO `user` (login, `password`) " +
                         "VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, user.getLogin());
                statement.setString(2, user.getPassword());
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
    public User read(int idUser) {
        User user = null;
        try (Connection connection = DbUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("select * from `user` where iduser=?")) {
            statement.setInt(1, idUser);

            try(ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User();
                    user.setLogin(resultSet.getString("login"));
                    user.setPassword(resultSet.getString("password"));
                    user.setId(idUser);
                    user.setAdmin(resultSet.getBoolean("isadmin"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public void update(User user) {
        try (Connection connection = DbUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement("update `user` set isadmin=? where iduser=?")) {
            statement.setBoolean(1, user.isAdmin());
            statement.setInt(2, user.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(User user) {
        try (Connection connection = DbUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement("delete from `user` where iduser=?")){
            statement.setInt(1, user.getId());
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> getUsersList() {
        List<User> users = new ArrayList<>();

        try(Connection connection = DbUtil.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from user");
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("iduser"));
                user.setAdmin(resultSet.getBoolean("isadmin"));
                user.setLogin(resultSet.getString("login"));
                user.setPassword(resultSet.getString("password"));

                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public boolean isLoginExist(String login) {
        boolean loginExist = false;

        try (Connection connection = DbUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT login FROM `user` WHERE login=?"))
        {
            statement.setString(1, login);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    loginExist = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return loginExist;
    }

    @Override
    public int findIdUser(User user){
        int id = -1;

        try(
                Connection connection = DbUtil.getConnection();
                PreparedStatement statement = setStatement(connection, "SELECT iduser FROM `user` where " +
                        "login = ? and password = ?", user.getLogin(), user.getPassword());
                ResultSet resultSet = statement.executeQuery()
        )
        {
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }


    private PreparedStatement setStatement(Connection connection, String sqlRequest, String login, String password) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sqlRequest);
        statement.setString(1, login);
        statement.setString(2, password);
        return statement;
    }
}
