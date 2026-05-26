package dataaccess;

import dataaccess.exceptions.ResponseException;

import java.sql.Connection;
import java.sql.SQLException;

public class DataAccessSelector {
    private static final boolean USE_SQL = true;

    private static final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  authList (
              `id` int NOT NULL AUTO_INCREMENT,
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(authToken),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  userList (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(username),
              UNIQUE KEY (`username`),
              INDEX(email),
              UNIQUE KEY (`email`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  gameList (
              `id` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) NOT NULL,
              `blackUsername` varchar(256) NOT NULL,
              `gameName` varchar(256) NOT NULL,
              `game` JSON,
              PRIMARY KEY (`id`),
              INDEX(whiteUsername),
              INDEX(blackUsername),
              INDEX(gameName)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    static {
        if (USE_SQL){
            configureDatabase();
        }
    }

    public static UserDAO getUserDAO() {
        return USE_SQL ? new SQLUserDAO() : new MemoryUserDAO();
    }

    public static AuthDAO getAuthDAO() {
        return USE_SQL ? new SQLAuthDAO() : new MemoryAuthDAO();
    }

    public static GameDAO getGameDAO() {
        return USE_SQL ? new SQLGameDAO() : new MemoryGameDAO();
    }

    private static void configureDatabase() throws ResponseException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

}
