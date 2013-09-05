package cloudscapemanager.gui;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import javax.swing.DropMode;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import tools.swing.TableStringTransferHandler;

/**
 * @author pj
 *
 */
public class ResultsTable extends JTable
{
	private ResultSetMetaData md;

	public ResultsTable(ResultSet rs)
	{
		setAutoCreateRowSorter(true);

		setDropMode(DropMode.ON);

		setTransferHandler(new TableStringTransferHandler());
		getTableHeader().setReorderingAllowed(true);

		if (rs != null)
		{
			try
			{
				this.md = rs.getMetaData();
				int colCount = md.getColumnCount();
				int rowCount = 0;
				//now set up the model
				DefaultTableModel dtm = new DefaultTableModel(1, colCount);
				this.setModel(dtm);

				for (int c = 0; c < colCount; c++)
				{
					getColumnModel().getColumn(c).setHeaderValue(md.getColumnName(c + 1));
				}

				while (rs.next())
				{
					for (int i = 1; i <= colCount; i++)
					{
						((DefaultTableModel) getModel()).setNumRows(rowCount + 1);

						int type = md.getColumnType(i);
						if (type == Types.BLOB || type == Types.CLOB || type == Types.VARBINARY)
						{
							byte[] bs = (byte[]) rs.getObject(i);
							if (bs.length < 512)
							{
								setValueAt(new String(bs), rowCount, i - 1);
							}
							else
							{
								setValueAt(bs, rowCount, i - 1);
							}
						}
						else
						{
							setValueAt(rs.getObject(i), rowCount, i - 1);
						}
					}
					rowCount++;
				}

			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	public String getUpdateString()
	{
		try
		{
			String insert = " INSERT INTO " + md.getSchemaName(1) + "." + md.getTableName(1);

			insert += " ( ";
			int colCount = md.getColumnCount();
			if (colCount != getColumnCount())
			{
				new Exception("Col counts don't match " + colCount + " != " + getColumnCount()).printStackTrace();
				return "";
			}

			for (int c = 0; c < colCount; c++)
			{
				insert += md.getColumnName(c + 1);
				insert += (c < colCount - 1) ? "," : "";
			}

			insert += ") VALUES ";

			for (int r = 0; r < getRowCount(); r++)
			{
				insert += "(";
				for (int c = 0; c < colCount; c++)
				{
					boolean isChar = md.getColumnType(c + 1) == Types.VARCHAR || md.getColumnType(c + 1) == Types.CHAR
							|| md.getColumnType(c + 1) == Types.LONGVARCHAR;
					insert += isChar ? "'" : "";
					insert += getValueAt(r, c);
					insert += isChar ? "'" : "";
					insert += (c < colCount - 1) ? "," : "";
				}
				insert += ")";
				insert += (r < getRowCount() - 1) ? ",\n" : "\n";

			}
			return insert;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * @return a delete SQL statement
	 */
	public String getDeleteAllString()
	{
		try
		{
			return " DELETE FROM " + md.getSchemaName(1) + "." + md.getTableName(1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return "no SQL";
		}
	}
}
