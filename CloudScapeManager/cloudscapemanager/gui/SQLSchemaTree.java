package cloudscapemanager.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import tools.db.DBProxy;

/**
 * @author pj
 *
 */
public class SQLSchemaTree extends SQLQueryInternalFrame
{
	private DefaultMutableTreeNode root;

	private JTree tree;

	private JSplitPane splitter;

	private SQLQueryPanel sQLQueryPanel;

	public SQLSchemaTree(String title, DBProxy dBProxy)
	{
		super(title, dBProxy);

		root = new DefaultMutableTreeNode("All");
		tree = new JTree(root);
		splitter = new JSplitPane();

		this.getCentralPane().add(splitter);
		splitter.setLeftComponent(new JScrollPane(tree));
		splitter.setRightComponent(new JPanel());
		tree.setShowsRootHandles(true);
		loadTree();
		tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				showTableContents();
			}
		});

		setUpMenu();
		setToolBar();

	}

	private void setToolBar()
	{
		JButton b0 = new JButton("Run");
		b0.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				runQuery();
			}
		});
		this.getToolBar().add(b0);

	}

	protected void setUpMenu()
	{

		JMenu runMenu = new JMenu("Run");
		runMenu.setMnemonic(KeyEvent.VK_D);
		this.getMenuBar().add(runMenu);

		JMenuItem menuItem1 = new JMenuItem("Query");
		menuItem1.setActionCommand("Selected Text");
		menuItem1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				runQuery();
			}
		});
		runMenu.add(menuItem1);

		JMenuItem menuItem3 = new JMenuItem("Update Table");
		menuItem3.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateTable();
			}
		});
		runMenu.add(menuItem3);

	}

	private void updateTable()
	{
		if (sQLQueryPanel != null)
		{
			sQLQueryPanel.updateTable();
		}
	}

	private void runQuery()
	{
		if (sQLQueryPanel != null)
		{
			sQLQueryPanel.runQuery();
		}
	}

	void showTableContents()
	{
		if (tree.getSelectionPath() != null && tree.getSelectionPath().getLastPathComponent() instanceof TableNode)
		{
			TableNode tn = (TableNode) tree.getSelectionPath().getLastPathComponent();
			TreeNode parentTn = tn.getParent();
			String query = " SELECT * FROM " + parentTn.toString() + "." + tn.toString();
			sQLQueryPanel = new SQLQueryPanel(getDBProxy());
			sQLQueryPanel.setAndRunText(query);
			splitter.setRightComponent(sQLQueryPanel);
		}
	}

	private void loadTree()
	{
		root.removeAllChildren();

		DatabaseMetaData dbmd = getDBProxy().getMetaData();
		if (dbmd != null)
		{
			ResultSet rs;
			try
			{
				rs = dbmd.getSchemas();
				while (rs.next())
				{
					root.add(new SchemaNode(rs.getString(1)));
				}

				Enumeration<?> enumeration = root.children();
				while (enumeration.hasMoreElements())
				{
					SchemaNode sn = (SchemaNode) enumeration.nextElement();
					rs = dbmd.getTables(null, sn.toString(), null, new String[]
					{ "TABLE" });

					while (rs.next())
					{
						sn.add(new TableNode(rs.getString(3)));
					}

					// now to go through the roots children and get table info for them
					Enumeration<?> enum2 = sn.children();
					while (enum2.hasMoreElements())
					{
						TableNode tn = (TableNode) enum2.nextElement();
						rs = dbmd.getColumns(null, null, tn.toString(), null);

						while (rs.next())
						{
							tn.add(new ColumnNode(rs.getString(4)));
						}
					}
				}

				// and expand the root
				tree.expandRow(0);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

	}

	private class SchemaNode extends DefaultMutableTreeNode
	{
		public SchemaNode(Object userObject)
		{
			super(userObject);
		}
	}

	private class TableNode extends DefaultMutableTreeNode
	{
		public TableNode(String tableName)
		{
			super(tableName);
		}
	}

	private class ColumnNode extends DefaultMutableTreeNode
	{
		public ColumnNode(Object userObject)
		{
			super(userObject);
		}
	}

}
