/*
 * Created on 23/06/2005
 */
package cloudscapemanager.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import tools.db.DBProxy;

/**
 * @author pj
 * 
 */
public class DBGui extends JFrame implements ActionListener
{
	private JDesktopPane desktop = new JDesktopPane();

	private DBProxy dBProxy;

	private Preferences prefs;

	public DBGui()
	{
		prefs = Preferences.userNodeForPackage(this.getClass());
		setSize(1000, 800);
		// Set up the GUI.
		setContentPane(desktop);
		setJMenuBar(createMenuBar());

	}

	private void init()
	{
		String defaultDir = prefs.get("defaultDir", "c:\\");
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setCurrentDirectory(new File(defaultDir));
		fc.showOpenDialog(this);

		if (fc.getSelectedFile() != null)
		{
			prefs.put("defaultDir", fc.getSelectedFile().getAbsolutePath());
			DBProxy dbp = new DBProxy();
			dbp.connectDerby(fc.getSelectedFile().getPath());
			setDbProxy(dbp);
		}
		else
		{
			System.exit(0);
		}

	}

	public void setDbProxy(DBProxy dbp)
	{
		dBProxy = dbp;

		SQLSchemaTree sQLSchemaTree = new SQLSchemaTree("SQLSchemaTree", dBProxy);
		sQLSchemaTree.setBounds(0, 0, 800, 600);
		sQLSchemaTree.setVisible(true);
		desktop.add(sQLSchemaTree);

		SQLSimpleQueryInternalFrame sQLSimpleQueryInternalFrame = new SQLSimpleQueryInternalFrame("SQLQueryPanel", dBProxy);
		sQLSimpleQueryInternalFrame.setBounds(40, 40, 800, 600);
		sQLSimpleQueryInternalFrame.setVisible(true);
		desktop.add(sQLSimpleQueryInternalFrame);
	}

	/**
	 * Currently this does very little
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		if ("SQLSchemaTree".equals(e.getActionCommand()))
		{
			SQLSchemaTree sQLSchemaTree = new SQLSchemaTree("SQLSchemaTree", dBProxy);
			sQLSchemaTree.setVisible(true);
			desktop.add(sQLSchemaTree);
			sQLSchemaTree.requestFocusInWindow();
		}
		else if ("SQLSimpleQueryInternalFrame".equals(e.getActionCommand()))
		{
			SQLSimpleQueryInternalFrame sQLSimpleQueryInternalFrame = new SQLSimpleQueryInternalFrame("SQLSimpleQueryInternalFrame",
					dBProxy);
			sQLSimpleQueryInternalFrame.setVisible(true);
			desktop.add(sQLSimpleQueryInternalFrame);
			sQLSimpleQueryInternalFrame.requestFocusInWindow();
		}
		else if ("quit".equals(e.getActionCommand()))
		{
			dispose();
		}
	}

	private JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();

		JMenu viewMenu = new JMenu("Views");
		viewMenu.setMnemonic(KeyEvent.VK_D);
		menuBar.add(viewMenu);

		JMenuItem menuItem = new JMenuItem("SQLSchemaTree");
		menuItem.setActionCommand("SQLSchemaTree");
		menuItem.addActionListener(this);
		viewMenu.add(menuItem);

		menuItem = new JMenuItem("SQLSimpleQueryInternalFrame");
		menuItem.setActionCommand("SQLSimpleQueryInternalFrame");
		menuItem.addActionListener(this);
		viewMenu.add(menuItem);

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_D);
		menuBar.add(fileMenu);

		JMenuItem varbinMenu = new JMenuItem("BinaryColumnUpdater");
		varbinMenu.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFrame f = new JFrame("BinaryColumnUpdater");
				f.setSize(600, 300);
				f.getContentPane().add(new BinaryColumnUpdater(dBProxy));
				f.setVisible(true);
			}
		});
		viewMenu.add(varbinMenu);

		// Set up the second menu item.
		menuItem = new JMenuItem("Quit");
		menuItem.setMnemonic(KeyEvent.VK_Q);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
		menuItem.setActionCommand("quit");
		menuItem.addActionListener(this);
		fileMenu.add(menuItem);

		return menuBar;
	}

	public static void main(String[] args)
	{

		DBGui DBGui = new DBGui();
		DBGui.setVisible(true);
		DBGui.init();

		DBGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
