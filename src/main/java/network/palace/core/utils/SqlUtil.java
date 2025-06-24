package network.palace.core.utils;

import network.palace.core.Core;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The SqlUtil class provides utility methods for managing SQL database connections.
 * It supports initialization of database credentials and establishing connections.
 */
public class SqlUtil {
    /**
     * Represents the URL for establishing a connection to a SQL database.
     *
     * This variable is used in conjunction with other properties such as
     * username and password to initialize a database connection. It is
     * dynamically assigned a value when the login credentials are loaded.
     */
    private String url = "";

    /**
     * Represents the username credential required to establish a connection to a SQL database.
     *
     * This variable is dynamically assigned a value during the initialization process,
     * typically loaded from a configuration file. It is used alongside the database URL
     * and password to authenticate and establish a connection to the database.
     */
    private String user = "";

    /**
     * Represents the password credential required to establish a connection to a SQL database.
     *
     * This variable is dynamically assigned a value during the initialization process,
     * typically loaded from a configuration file. It is used alongside other properties
     * such as the database URL and username to authenticate and establish a connection
     * to the database.
     */
    private String password = "";

    /**
     * Constructs an instance of the SqlUtil class and initializes the database connection credentials.
     *
     * The constructor invokes the private {@code loadLogin} method to dynamically load the database URL,
     * username, and password from the application's configuration. These credentials are used for establishing
     * SQL database connections.
     *
     * This ensures that the instance is ready to handle database connectivity immediately after it is created.
     */
    public SqlUtil() {
        loadLogin();
    }

    /**
     * Loads the database connection credentials from the application configuration.
     *
     * This method retrieves the database URL, username, and password from the
     * configuration file using the Core configuration manager. The retrieved
     * values are assigned to the instance variables {@code url}, {@code user},
     * and {@code password}. These values are used to establish SQL database
     * connections.
     *
     * The method is invoked internally during the initialization process to ensure
     * that the necessary credentials are available before any database operations
     * are attempted.
     */
    private void loadLogin() {
        url = Core.getCoreConfig().getString("sql.url");
        user = Core.getCoreConfig().getString("sql.user");
        password = Core.getCoreConfig().getString("sql.password");
    }

    /**
     * Establishes and returns a connection to the database using the configured credentials.
     *
     * The method attempts to create a database connection using the {@link DriverManager#getConnection(String, String, String)}
     * method with the URL, username, and password loaded from the instance's configuration. If the connection attempt fails,
     * an error message is logged, and an optional error handling utility is invoked.
     *
     * @return A {@link Connection} object representing the database connection, or {@code null} if the connection could not be established.
     */
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            if (!Core.isDashboardAndSqlDisabled()) {
                Core.logMessage("Core", ChatColor.RED + "Could not connect to database!");
                ErrorUtil.displayError(e);
            }
            return null;
        }
    }
}