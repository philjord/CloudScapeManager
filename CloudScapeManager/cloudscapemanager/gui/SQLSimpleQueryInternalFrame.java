/*
 * Created on 23/06/2005
 */
package cloudscapemanager.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cloudscapemanager.DBProxy;

/**
 * @author pj
 *
 */
public class SQLSimpleQueryInternalFrame extends SQLQueryInternalFrame
{
	private SQLQueryPanel sQLQueryPanel;

	public SQLSimpleQueryInternalFrame(String title, DBProxy dBProxy)
	{
		super(title, dBProxy);

		sQLQueryPanel = new SQLQueryPanel(dBProxy);

		this.getCentralPane().add(sQLQueryPanel);

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
		sQLQueryPanel.updateTable();
	}

	private void runQuery()
	{
		sQLQueryPanel.runQuery();
	}

}
