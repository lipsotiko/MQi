package io.egia.mqi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class SqlExecutor {
    private Logger log = LoggerFactory.getLogger(SqlExecutor.class);
    private DataSource dataSource;
    private Connection connection;
    private Statement statement;
    private String url;

    public SqlExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void execute(String sql) {
        if ((sql != null) && (!sql.isEmpty())) {
            try {
                connection = dataSource.getConnection();
                connection.setAutoCommit(false);
                url = connection.getMetaData().getURL();
                if (!url.contains("hsqldb")) {
                    log.debug(sql);
                    statement = connection.createStatement();
                    statement.executeUpdate(sql);
                    statement.close();
                }
                connection.commit();
                connection.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
