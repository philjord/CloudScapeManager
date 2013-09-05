package cloudscapemanager.gui;

import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import cloudscapemanager.DBProxy;

/**
 * @author pj
 *
 */
public class SQLQueryPanel extends JPanel
{
	private DBProxy dBProxy;

	protected JTextArea queryInputArea = new JTextArea("\n");

	private JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

	public SQLQueryPanel(DBProxy dBProxy)
	{
		this.dBProxy = dBProxy;

		setLayout(new GridLayout(1, 1));
		splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitter.add(new JScrollPane(queryInputArea), JSplitPane.TOP);
		add(splitter);
	}

	public void updateTable()
	{
		if (splitter.getRightComponent() != null)
		{
			ResultsTable resultsTable = (ResultsTable) ((JScrollPane) splitter.getRightComponent()).getViewport().getComponent(0);
			String deleteAllQuery = resultsTable.getDeleteAllString();
			dBProxy.execute(deleteAllQuery);
			String updateQuery = resultsTable.getUpdateString();
			dBProxy.execute(updateQuery);
		}
	}

	/**
	 * @param resetText
	 */
	public void setText(String resetText)
	{
		queryInputArea.setText(resetText);
	}

	public void setAndRunText(String query)
	{
		queryInputArea.setText(query);
		runText(query);
	}

	public void runQuery()
	{

		if (queryInputArea.getSelectedText() != null)
		{
			runText(queryInputArea.getSelectedText());
		}
		else
		{
			runText(queryInputArea.getText());
		}
	}

	private void runText(String text)
	{

		if (text != null)
		{
			// lets parse the text into semi colon'ed chunks
			Vector<String> querys = parseText(text);
			// and run each one
			for (int i = 0; i < querys.size(); i++)
			{
				String query = querys.elementAt(i);

				if (query.trim().toLowerCase().startsWith("select"))
				{
					loadResultsToTable(dBProxy.getResults(query));
				}
				else
				{
					dBProxy.execute(query);
				}
			}
		}
	}

	private void loadResultsToTable(ResultSet rs)
	{
		splitter.setRightComponent(new JScrollPane(new ResultsTable(rs)));
	}

	private Vector<String> parseText(String text)
	{
		Vector<String> returnVector = new Vector<String>();
		while (text.indexOf(";") != -1)
		{
			// add everything up to the semi
			returnVector.add(text.substring(0, text.indexOf(";")));
			text = text.substring(text.indexOf(";") + 1);
		}

		//finally add the remianer so the last one does not require a semi
		returnVector.add(text);
		return returnVector;
	}

	/**
	 * This is a sepcial area to run custom sql because I can' figure out how to set a varbinary field using sql
	 * @param dBProxy
	 */
	public static void oneOffCustom(DBProxy dBProxy)
	{
		String s = "UPDATE ST.SUBRECORD set subrecord_data = ? where form_id = 34 and subrecord_type = 'TYPE'";

		PreparedStatement ps;
		try
		{
			ps = dBProxy.getConnection().prepareStatement(s);

			ps.setBytes(1, "CONSOLE".getBytes());
			ps.execute();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

}
