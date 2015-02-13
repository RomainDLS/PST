package fr.esiea.pst.abracadabra;
import javax.swing.*;

import java.awt.*;

public class Analyseur {
	JFrame frame;
	private Panel panel;
	float[][] data;
	
	public Analyseur(float[][] data){
		 this.data = new float[data.length][data[0].length];
		 
		 for(int i=0; i<data.length; i++){
			 for(int j=0; j<data[i].length; j++){
				 this.data[i][j] = data[i][j];
			 }
		 }
		 frame = new JFrame();
		 panel = new Panel();
		 panel.setLayout(new FlowLayout());
		 frame.add(panel);
	     frame.setSize(600,600);
		 frame.setVisible(true);
		 
		 printData(this.data);
	}
	
	public void printData(float[][] data){
		for(int i=0; i<data.length; i++){
			 for(int j=0; j<data[i].length; j++){
				 System.out.println(data[i][j]+ "\n");
			 }
		 }
	}
	
	class Panel extends JPanel{
		private static final long serialVersionUID = 1L;
		
		public void paintComponent(Graphics2D g){
			for(int i = 0; i < data.length; i++) {
			    for(int j = 0; j < data[i].length; j++) {
			        double magnitude = Math.log(Math.abs(data[i][j]));
			        
			        g.setColor(new Color(0,(int)magnitude*10,(int)magnitude*20));
			        g.fillRect(i*5,(data[i].length-j)*5,5,5);
			    
			    } 
			}
		}
		
	}
}