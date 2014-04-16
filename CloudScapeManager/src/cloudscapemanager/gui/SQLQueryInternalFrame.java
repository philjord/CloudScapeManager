/*
 * Created on 23/06/2005
 */
package cloudscapemanager.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import tools.db.DBProxy;

public abstract class SQLQueryInternalFrame extends JInternalFrame
{
	private DBProxy dBProxy;

	private JPanel centralPane = new JPanel();

	private JToolBar toolBar = new JToolBar("Toolbar");

	private JMenuBar menuBar = new JMenuBar();;

	public JMenuBar getMenuBar()
	{
		return menuBar;
	}

	public SQLQueryInternalFrame(String title, DBProxy dBProxy)
	{
		super(title);
		this.dBProxy = dBProxy;

		getContentPane().setLayout(new BorderLayout());

		centralPane.setLayout(new GridLayout(1, 1));
		getContentPane().add(centralPane, BorderLayout.CENTER);
		setResizable(true);

		setJMenuBar(menuBar);
		getContentPane().add(toolBar, BorderLayout.PAGE_START);

	}

	public JPanel getCentralPane()
	{
		return centralPane;
	}

	public DBProxy getDBProxy()
	{
		return dBProxy;
	}

	public JToolBar getToolBar()
	{
		return toolBar;
	}

}
