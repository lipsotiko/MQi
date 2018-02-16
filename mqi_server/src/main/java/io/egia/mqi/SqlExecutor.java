package io.egia.mqi;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

@Component
public class SqlExecutor {

	private Logger log = LoggerFactory.getLogger(SqlExecutor.class);

	@Autowired
	private DataSource ds;

	private Connection conn;

	private Statement stmt;

	private String url;
	
	public void execute(String sql) {
		
		if ((sql != null) && (!sql.isEmpty())) {
			try {
				conn = ds.getConnection();
				conn.setAutoCommit(false);
				url = conn.getMetaData().getURL();
				if(!url.contains("hsqldb")){
					log.debug(sql);
					stmt = conn.createStatement();
					stmt.executeUpdate(sql);
				}
				stmt.close();
		        conn.commit();
		        conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}