package cloudscapemanager.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import cloudscapemanager.DBProxy;

/**
 * @author pj
 *
 */
public class BinaryColumnUpdater extends JPanel
{
	private DBProxy dBProxy;

	protected JTextArea queryInputArea = new JTextArea(
			"UPDATE ST.SUBRECORD set subrecord_data = ? where form_id = -123 and subrecord_type = 'freddy foo'");

	private JTextField binaryData = new JTextField("binary string here");

	public BinaryColumnUpdater(DBProxy dBProxy)
	{
		this.dBProxy = dBProxy;

		setLayout(new GridLayout(-1, 1));

		add(queryInputArea);
		add(binaryData);

		JButton runButton = new JButton("Run");
		add(runButton);

		runButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				run();

			}
		});
	}

	/**
	 * This is a sepcial area to run custom sql because I can' figure out how to set a varbinary field using sql
	 * @param dBProxy
	 */
	public void run()
	{

		PreparedStatement ps;
		try
		{
			ps = dBProxy.getConnection().prepareStatement(queryInputArea.getText());

			ps.setBytes(1, binaryData.getText().getBytes());
			ps.execute();

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

}
