package lapr.project.data;


import lapr.project.utils.Updateable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exemplo de classe cujas instâncias manipulam dados de BD Oracle.
 */
public class DataHandler {
    private static final Logger LOGGER = Logger.getLogger("DataHandlerLog");
    private static final int MAX_RECONNECTION_ATTEMPTS_RECOVERABLE = 3;
    private static final int MAX_RECONNECTION_ATTEMPTS_UNRECOVERABLE = 10;
    private static final int CONNECTION_FAILURE_ORA_CODE = 17008;
    private static final int RECONNECTION_INTERVAL_MILLIS = 3000;
    private static final int QUERY_TIMEOUT_SECONDS = 10;
    private static final String NOT_CONNECTED_ERROR_MSG = "Not connected to the database";
    public static final int ORA_ERROR_CODE_NO_DATA_FOUND = 1403;

    private static DataHandler instance;

    /**
     * O URL da BD.
     */
    private final String jdbcUrl;

    /**
     * O nome de utilizador da BD.
     */
    private final String username;

    /**
     * A password de utilizador da BD.
     */
    private final String password;

    /**
     * A ligação à BD.
     */
    private Connection connection = null;

    /**
     * A invocação de "stored procedures".
     */
    private CallableStatement callStmt = null;

    /**
     * Conjunto de resultados retornados por "stored procedures".
     */
    private ResultSet rSet = null;

    /**
     * <b>Only one instance allowed at all times</b>
     * Use connection properties set on file application.properties
     */
    private DataHandler() throws IllegalAccessException, SQLException {
        this.jdbcUrl = System.getProperty("database.url");
        this.username = System.getProperty("database.username");
        this.password = System.getProperty("database.password");
        openConnection();
    }

    public static synchronized DataHandler createDataHandler() throws SQLException, IllegalAccessException {
        if (instance == null || instance.connection == null || instance.connection.isClosed())
            instance = new DataHandler();

        return instance;
    }

    /**
     * Allows running entire scripts
     *
     * @param fileName
     * @throws IOException
     * @throws SQLException
     */
    public void runSQLScriptNoCommit(String fileName) throws IOException, SQLException {
        openConnection();

        if (connection == null)
            openConnection();
        ScriptRunner runner = new ScriptRunner(connection, false, false);

        runner.runScript(new BufferedReader(new FileReader(fileName)));
    }

    /**
     * Estabelece a ligação à BD. <b>Não são efetuadas quaisquer verificações sobre o estado da conexão atual.</b>
     *
     * @throws SQLException if a database access error occurs or the url is null
     * @throws SQLTimeoutException when the driver has determined that the timeout value specified by the setLoginTimeout method has been exceeded and has at least tried to cancel the current database connection attempt
     */
    protected void openConnection() throws SQLException {
        connection = DriverManager.getConnection(
                jdbcUrl, username, password);
        connection.setAutoCommit(false);
    }

    /**
     * Fecha os objetos "ResultSet", "CallableStatement" e "Connection", e
     * retorna uma mensagem de erro se alguma dessas operações não for bem
     * sucedida. Caso contrário retorna uma "string" vazia.
     */
    public String closeAll() {
        StringBuilder message = new StringBuilder();

        try {
            rollbackTransaction();
        } catch (SQLException e) {
            // Might fail if the connection is lost/closed but when the connection is closed, it automatically performs a rollback
        }

        if (rSet != null) {
            try {
                rSet.close();
            } catch (SQLException ex) {
                message.append(ex.getMessage());
                message.append("\n");
            }
            rSet = null;
        }

        if (callStmt != null) {
            try {
                callStmt.close();
            } catch (SQLException ex) {
                message.append(ex.getMessage());
                message.append("\n");
            }
            callStmt = null;
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                message.append(ex.getMessage());
            }
            connection = null;
        }

        return message.toString();
    }

    private Updateable<Boolean> attemptingToReconnect = new Updateable<>(false);
    private synchronized void continuousReconnectAttempt() {
        if (attemptingToReconnect.getValue())
            return;

        attemptingToReconnect.setValue(true);
        new Thread(new ReconnectorRunnable(attemptingToReconnect, this, MAX_RECONNECTION_ATTEMPTS_UNRECOVERABLE, RECONNECTION_INTERVAL_MILLIS))
                .start();
    }

    private <T> T executeRecoverableSQLOperation(SQLOperation<T> operation) throws SQLException {
        int nAttempt = 1;
        while (true) {
            try {
                return operation.executeOperation();
            } catch (SQLException e) {
                if (e.getErrorCode() == CONNECTION_FAILURE_ORA_CODE && nAttempt < MAX_RECONNECTION_ATTEMPTS_RECOVERABLE && Bootstrap.isAppBootedUp()) {
                    try {
                        synchronized (this) {
                            this.wait(RECONNECTION_INTERVAL_MILLIS);
                        }
                    } catch (InterruptedException ex) {
                        LOGGER.log(Level.WARNING, "Failed to make thread wait, skipping wait period.");
                    }
                    openConnection();
                }
                else {
                    continuousReconnectAttempt();
                    throw e;
                }
                nAttempt++;
            }
        }
    }

    private <T> T executeUnrecoverableSQLOperation(SQLOperation<T> operation) throws SQLException {
        try {
            return operation.executeOperation();
        } catch (SQLException e) {
            if (e.getErrorCode() == 17008)
                continuousReconnectAttempt();
            throw e;
        }
    }

    public PreparedStatement prepareStatement(String query) throws SQLException {
        if (connection == null)
            throw new SQLException(NOT_CONNECTED_ERROR_MSG);
        SQLOperation<PreparedStatement> operation = () -> {
            PreparedStatement stm = connection.prepareStatement(query);
            stm.setQueryTimeout(QUERY_TIMEOUT_SECONDS);
            return stm;
        };
        return executeRecoverableSQLOperation(operation);
    }

    public CallableStatement prepareCall(String query) throws SQLException {
        if (connection == null)
            throw new SQLException(NOT_CONNECTED_ERROR_MSG);
        SQLOperation<CallableStatement> operation = () -> {
            CallableStatement cs = connection.prepareCall(query);
            cs.setQueryTimeout(QUERY_TIMEOUT_SECONDS);
            return cs;
        };
        return executeRecoverableSQLOperation(operation);
    }

    /**
     * Executes the SQL query in this <code>PreparedStatement</code> object
     * and returns the <code>ResultSet</code> object generated by the query.
     *
     * @return a <code>ResultSet</code> object that contains the data produced by the
     *         query; <b>never <code>null</code></b>
     * @exception SQLException if a database access error occurs;
     * this method is called on a closed  <code>PreparedStatement</code> or the SQL
     *            statement does not return a <code>ResultSet</code> object
     * @throws SQLTimeoutException when the driver has determined that the
     * timeout value that was specified by the {@code setQueryTimeout}
     * method has been exceeded and has at least attempted to cancel
     * the currently running {@code Statement}
     */
    public ResultSet executeQuery(PreparedStatement preparedStatement) throws SQLException {
        if (connection == null)
            throw new SQLException(NOT_CONNECTED_ERROR_MSG);
        SQLOperation<ResultSet> operation = () -> preparedStatement.executeQuery();
        return executeUnrecoverableSQLOperation(operation);
    }

    public Integer executeUpdate(PreparedStatement preparedStatement) throws SQLException {
        if (connection == null)
            throw new SQLException(NOT_CONNECTED_ERROR_MSG);
        SQLOperation<Integer> operation = () -> preparedStatement.executeUpdate();
        return executeUnrecoverableSQLOperation(operation);
    }

    public Boolean execute(PreparedStatement preparedStatement) throws SQLException {
        if (connection == null)
            throw new SQLException(NOT_CONNECTED_ERROR_MSG);
        SQLOperation<Boolean> operation = () -> preparedStatement.execute();
        return executeUnrecoverableSQLOperation(operation);
    }

    public int[] executeBatch(Statement statement) throws SQLException {
        if (connection == null)
            throw new SQLException(NOT_CONNECTED_ERROR_MSG);
        SQLOperation<int[]> operation = () -> statement.executeBatch();
        return executeUnrecoverableSQLOperation(operation);
    }

    public Boolean commitTransaction() throws SQLException {
        if (connection == null)
            throw new SQLException(NOT_CONNECTED_ERROR_MSG);
        SQLOperation<Boolean> operation = () -> {connection.commit(); return true;};
        return executeUnrecoverableSQLOperation(operation);
    }

    public Boolean rollbackTransaction() throws SQLException {
        if (connection == null)
            throw new SQLException(NOT_CONNECTED_ERROR_MSG);
        SQLOperation<Boolean> operation = () -> {connection.rollback(); return true;};
        return executeUnrecoverableSQLOperation(operation);
    }
}
