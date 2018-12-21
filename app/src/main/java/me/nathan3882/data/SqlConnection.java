package me.nathan3882.data;

import me.nathan3882.androidttrainparse.TaskManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class SqlConnection {

    private static long latestOpeningMilis;
    private TaskManager closeConnectionTask;
    private String host = "51.77.194.49";
    private String databaseName = "ttrainparseUserdata";
    private int port = 3307;
    private String username = "root";
    private String password = "invalid pw";
    private Connection connection;

    public SqlConnection(boolean open) {
        if (open) openConnection();
    }

    public SqlConnection(String host, int port, String databaseName, String username, String password) {
        this.host = host;
        this.databaseName = databaseName;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public static long getLatestOpeningMilis() {
        return latestOpeningMilis;
    }

    public static void setLatestOpeningMilis(long latestOpeningMilis) {
        SqlConnection.latestOpeningMilis = latestOpeningMilis;
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
                Class.forName("com.mysql.jdbc.Driver");
                this.connection = newCon();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Connection newCon() {
        try {
            // The newInstance() call is a work around for some
            // broken Java implementations
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?useSSL=false&allowMultiQueries=true", username, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeConnection() {
        close(connection);
    }

    public boolean connectionEstablished() {
        if (this.connection == null) {
            this.connection = newCon(); //updates network connection
        }
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

    public interface SqlTableName {
        String TIMETABLE_RENEWAL = "timetablerenewal";
        String TIMETABLE_LESSONS = "timetablelessons";
        String TIMETABLE_USERDATA = "timetableuserdata";
    }
}
