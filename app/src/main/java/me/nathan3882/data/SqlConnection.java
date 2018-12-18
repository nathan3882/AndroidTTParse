package me.nathan3882.data;

import me.nathan3882.testingapp.MainActivity;
import me.nathan3882.testingapp.TaskManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class SqlConnection {

    private MainActivity main;
    private boolean open;

    private static long latestOpeningMilis;
    private TaskManager closeConnectionTask;

    public interface SqlTableName {
        String TIMETABLE_RENEWAL = "timetablerenewal";
        String TIMETABLE_LESSONS = "timetablelessons";
        String TIMETABLE_USERDATA = "timetableuserdata";
    }

    private String host = "localhost";
    private String databaseName = "userdata";
    private int port = 3306;
    private String username = "root";
    private String password = "";

    private Connection connection;

    public SqlConnection(MainActivity main) {
        this.main = main;
        this.connection = newCon();
    }

    public SqlConnection(MainActivity main, String host, int port, String databaseName, String username, String password) {
        this.main = main;
        this.host = host;
        this.databaseName = databaseName;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * executeQuery("SELECT * FROM users WHERE databaseName = 'Nathan'");
     *
     * @return success or not
     */
    public void openConnection() {
        if (!connectionEstablished()) {
            newCon();
        }
        if (connectionEstablished()) {
            if (closeConnectionTask == null) {
                closeConnectionTask = new TaskManager(new Timer()) {
                    @Override
                    public void run() {
                        long current = System.currentTimeMillis();
                        long lastOpen = SqlConnection.getLatestOpeningMilis();
                        if (connectionEstablished() && isOpen() &&
                                current - lastOpen >= TimeUnit.SECONDS.toMillis(150)) { //Close after 2 and 1/2 minutes
                            closeConnection();
                            SqlConnection.setLatestOpeningMilis(current); //Will be 0, condition will fail unless has been opened again
                        }
                    }
                };
                closeConnectionTask.runTaskSynchronously(closeConnectionTask, 5000L, 5000L);
            }
            try {
                latestOpeningMilis = System.currentTimeMillis();
                if (open) return;
                Class.forName("com.mysql.jdbc.Driver");
                this.connection = newCon();
                open = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Connection newCon() {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?allowMultiQueries=true", username, password);
        } catch (Exception e) {
            System.out.println("Could not connect to SQL Database, are the details correct?");
            return null;
        }
    }

    public void closeConnection() {
        if (open) {
            close(connection);
            open = false;
        }
    }

    public boolean connectionEstablished() {
        return this.connection != null;
    }

    public Connection getConnection() {
        return connection;
    }

    private void close(AutoCloseable resource) {
        try {
            resource.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void setLatestOpeningMilis(long latestOpeningMilis) {
        SqlConnection.latestOpeningMilis = latestOpeningMilis;
    }

    public MainActivity getMainActivity() {
        return this.main;
    }

    public boolean isClosed() {
        try {
            return this.connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isOpen() {
        return !isClosed();
    }

    public static long getLatestOpeningMilis() {
        return latestOpeningMilis;
    }
}