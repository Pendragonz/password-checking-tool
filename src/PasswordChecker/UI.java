package PasswordChecker;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.security.NoSuchAlgorithmException;

import java.net.*;


/**
 * 
 * @author pendragonz
 *
 */
public class UI {
	public static final String PWNED_PASSWORDS_URL = "https://api.pwnedpasswords.com/range/";
	
	private JPasswordField passwordField;
	private JFrame frame;
	private JTextArea text;
	
	private SHA1 sha1;
	
	
	public UI() {
		sha1 = new SHA1();
		createWindow();
	}
	
	
	private void createWindow() {
			
		frame = new JFrame("Password Checker");
		JButton submit = new JButton();
		text = new JTextArea("", 10, 40);
		JPanel pwPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(text);
		passwordField = new JPasswordField();
		
		
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		submit.setText("Check Password");
		frame.setBounds(100,50,480,300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		text.setEditable(true);
			
		
		
		pwPanel.setLayout(new BorderLayout());
		frame.setLayout(new FlowLayout());
		
		
		pwPanel.add(passwordField, BorderLayout.NORTH);
		pwPanel.add(submit, BorderLayout.SOUTH);
		
		
		frame.add(pwPanel);
		frame.add(scrollPane);
		
		
		frame.setVisible(true);
		
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bttnClick();
			}

		});
	}

	private void bttnClick() {
		char[] pwArr = passwordField.getPassword();
		String pw = "";
		
		for(int i = 0; i<pwArr.length; i++) {
			pw+=pwArr[i];
		}
		
		text.setText("Hashing Password... \n");
		String hash = "";
		
		try {
			hash = sha1.sha1(pw);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("err");
			e.printStackTrace();
		}
		
		text.append("Hash: \n" + hash);
		text.append("\nQuerying hash against DB... \n");
		
		int appearances = 0;
		
		try {
			appearances = checkHash(hash.toUpperCase());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		text.append("Appearances of Hash in DB : \n" + appearances);
		
	}
	
	private int checkHash(String hash) throws IOException {
		
		hash = new String(hash);
		
		URL url = new URL(PWNED_PASSWORDS_URL + hash.substring(0, 5));
		
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		
		String match = hash.substring(5, hash.length());
		String line;
		int numAppearances = 0;
		
		
		while((line = in.readLine()) != null) {
			String first = line.split(":")[0];
			String num = line.split(":")[1];			
			
			if(first.equals(match)) {
				numAppearances = Integer.parseInt(num);
			}
		}
		
		
		in.close();
		
		
		return numAppearances;
	}
	
	
	
}
