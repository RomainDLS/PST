package fr.esiea.pst.abracadabra;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.NumberFormat;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class importWindow extends JFrame {
	  private JPanel container = new JPanel();
	  private JTextField jtf = new JTextField();
	  private JLabel label = new JLabel("Copier ici le lien de la musique à identifier");
	  private JLabel Get = new JLabel();
	  private JButton b = new JButton ("START");

	public importWindow() {
		this.setTitle("Animation");
	    this.setSize(500, 200);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo(null);
	    container.setBackground(Color.white);
	    container.setLayout(new BorderLayout());
	    JPanel top = new JPanel();
	    jtf.setPreferredSize(new Dimension(400, 30));
	    jtf.setForeground(Color.GRAY);
	    b.addActionListener(new ActionListener(){
		    public void actionPerformed(ActionEvent e) {
					try {
						
						Get.setText(ImportToDb.Recognize(reorganize(jtf.getText())));
					
					} catch (UnsupportedAudioFileException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			    }
			  });
	    top.add(label);
	    top.add(jtf);
	    top.add(b);
	    top.add(Get);
	    this.setContentPane(top);
	    this.setVisible(true);            
	  }
	
	private String reorganize(String string){
		return string.replace("\\", "/");
	}

}
