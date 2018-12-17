package me.nathan3882.testingapp;

import me.nathan3882.data.SqlConnection;
import me.nathan3882.data.SqlQuery;

public class LoginAttempt {

    private final SqlConnection connection;
    private String emailText;
    private boolean wasSuccessful;
    private String unsuccessfulReason;

    public LoginAttempt(SqlConnection connection, long currentTimeMillis, String emailText, int localEnteredPassword) {
        this.connection = connection;
        this.emailText = emailText;

        if (!connection.connectionEstablished()) {
            setSuccessful(false, "Database connection not established");
        } else {
            String sqlPassword = getPasswordFromSql();
            if (sqlPassword.equals("invalid email")) { //= 'invalid email' when record / email doesnt exists
                setSuccessful(false, "Incorrect email!");
                return;
            } else if (!sqlPassword.equals(sqlPassword)) {
                setSuccessful(false, "Incorrect password");
                return;
            }
            setSuccessful(true);
        }
    }

    public String getPasswordFromSql() throws UnsupportedOperationException {
        if (hasSqlEntry(SqlConnection.SqlTableName.TIMETABLE_USERDATA)) {
            SqlQuery query = new SqlQuery(getConnection());
            query.executeQuery("SELECT password from {table} WHERE userEmail = '" + getEmailText() + "'",
                    SqlConnection.SqlTableName.TIMETABLE_USERDATA);
            query.next(false);
            int one = query.getInt(1);
            System.out.println("pw = " + one);
            return String.valueOf(one);
        }
        return "invalid email";
    }

    public boolean hasSqlEntry(String table) {
        if (!getConnection().connectionEstablished()) return false;
        SqlQuery query = new SqlQuery(getConnection());
        query.executeQuery("SELECT * FROM {table} WHERE userEmail = '" + getEmailText() + "'", table);
        boolean has = query.next(false);

        return has;
    }

    private void setSuccessful(boolean wasSuccessful, String... unsuccessfulReason) {
        this.wasSuccessful = wasSuccessful;
        if (!wasSuccessful) setUnsuccessfulReason(unsuccessfulReason[0]);
    }

    public boolean wasSuccessful() {
        return this.wasSuccessful;
    }

    public String getUnsuccessfulReason() {
        return this.unsuccessfulReason;
    }

    public void setUnsuccessfulReason(String unsuccessfulReason) {
        this.unsuccessfulReason = unsuccessfulReason;
    }

    public SqlConnection getConnection() {
        return connection;
    }

    public String getEmailText() {
        return emailText;
    }
}
