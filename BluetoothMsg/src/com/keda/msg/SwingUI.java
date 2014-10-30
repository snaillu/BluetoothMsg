package com.keda.msg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SwingUI implements ConnectionProtocol.EventHandler {

	private ConnectionProtocol protocol;

	private JTextArea chatArea = new JTextArea();

	public SwingUI() {
		// Swing operations are good to execute in Swing's own thread.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame.setDefaultLookAndFeelDecorated(true);
				JFrame frame = new JFrame("Bluetooth Chat!");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().setLayout(new BorderLayout());
				frame.setSize(new Dimension(400, 400));

				JPanel margin = new JPanel(new BorderLayout());
				margin.setBorder(BorderFactory
						.createEmptyBorder(15, 15, 15, 15));
				frame.getContentPane().add(margin);

				chatArea.setLineWrap(true);
				JScrollPane pane = new JScrollPane(chatArea,
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				margin.add(pane);

				final JTextField msgField = new JTextField("Hi!");
				msgField.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER) {
							protocol.broadcastMessage(msgField.getText());
							msgField.setText("");
						}
					}
				});
				margin.add(msgField, BorderLayout.SOUTH);

				frame.setVisible(true);
			}
		});
	}

	private void addMessage(String userName, String msg) {
		Log.log("Chat msg from user: " + userName + ", " + msg);
		StringBuffer buff = new StringBuffer();
		Calendar cal = Calendar.getInstance();
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int m = cal.get(Calendar.MINUTE);
		buff.append("<");
		if (h < 10)
			buff.append("0");
		buff.append(h);
		buff.append(":");
		if (m < 10)
			buff.append("0");
		buff.append(m);
		buff.append("> ");
		if (userName != null) {
			buff.append(userName);
			buff.append(": ");
		}
		buff.append(msg);
		buff.append("\n");
		chatArea.insert(buff.toString(), chatArea.getDocument().getLength());
		// TODO: move scroll pane window.
	}

	public void chatMessage(String userName, String msg) {
		addMessage(userName, msg);
	}

	public void chatLeave(String userName) {
		Log.log("Chatter leaving: " + userName);
		addMessage(null, userName + " is leaving.");
	}

	public void chatEnter(String userName) {
		Log.log("Chatter entering: " + userName);
		addMessage(null, userName + " has joined.");
	}

	public void setProtocol(ConnectionProtocol protocol) {
		this.protocol = protocol;
	}

}
