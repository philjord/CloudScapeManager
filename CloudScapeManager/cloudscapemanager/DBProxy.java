package cloudscapemanager;

import java.awt.GridLayout;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class DBProxy
{
	private Connection connection;

	public DBProxy()
	{
	}

	public boolean connectCS(String fileLocation)
	{
		String connectionString = "jdbc:db2j:" + fileLocation + ";create=true";
		return connect("com.ibm.db2j.jdbc.DB2jDriver", connectionString);
	}

	public boolean connectDerby(String fileLocation)
	{
		String connectionString = "jdbc:derby:" + fileLocation + ";create=true";
		return connect("org.apache.derby.jdbc.EmbeddedDriver", connectionString);
	}

	private boolean connect(String driverName, String connectionString)
	{

		try
		{
			// Step 1: Load the JDBC driver. 
			//Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

			// Step 2: Establish the connection to the database. 
			//String url = "jdbc:derby:" + fileLocation + ";create=true";
			//connection = DriverManager.getConnection(url, "", "");

			Class.forName(driverName);

			connection = DriverManager.getConnection(connectionString);

		}
		catch (SQLException e)
		{
			System.out.println("Unable to connect to db");

			// have we got a either a "does not exist" or "already connected" exception
			if (e.getErrorCode() == 40000)
			{
				SQLException next = e.getNextException();

				if (next != null && next.getErrorCode() == 45000)
				{
					// we got a "already connected"		
					System.out.println("There is another app already connected, cloudscape is single user");
					System.out.println("Remember that Websphere in Data Perspective can make connections");
				}
				else
				{
					// we got a "does not exist"		
					System.out.println("The DB doesn't exist (use a better string)");
				}
			}
			else
			{
				//otherwise just print it out
				e.printStackTrace();
			}
			return false;
		}
		catch (ClassNotFoundException e)
		{
			displayException("", e);
			e.printStackTrace();
		}
		return true;

	}

	protected void finalize() throws Throwable
	{
		shutDown();
	}

	public void shutDown()
	{
		try
		{
			if (connection != null)
			{
				connection.close();
			}
		}
		catch (SQLException e)
		{
			//ignore, as we are shutting down
		}
	}

	public int execute(String executeStatement)
	{
		try
		{
			CallableStatement cs = connection.prepareCall(executeStatement);
			cs.execute();
			return cs.getUpdateCount();
		}
		catch (SQLException e)
		{
			displayException("Error running SQL " + executeStatement, e);

		}
		return -1;
	}

	public ResultSet getResults(String query)
	{
		try
		{
			CallableStatement cs = connection.prepareCall(query);
			cs.executeQuery();
			return cs.getResultSet();
		}
		catch (SQLException e)
		{
			displayException("Error running SQL " + query, e);

		}
		return null;
	}

	public DatabaseMetaData getMetaData()
	{
		try
		{
			return connection.getMetaData();
		}
		catch (SQLException e)
		{
			displayException("", e);
			e.printStackTrace();
			return null;
		}
	}

	private void displayException(String header, Exception e)
	{
		JFrame f = new JFrame("Exception output");
		f.getContentPane().setLayout(new GridLayout(1, 1));
		f.setSize(600, 200);
		JTextArea text = new JTextArea(header + "\n" + e.getMessage());
		f.getContentPane().add(text);
		f.setVisible(true);

		if (e instanceof SQLException)
		{

		}
		else
		{
			System.out.println(header);
			e.printStackTrace();
		}

	}

	public Connection getConnection()
	{
		return connection;
	}
}
