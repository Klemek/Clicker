package fr.klemek.clicker;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.Timer;

public class Notification extends JWindow implements ActionListener {

	private static final long serialVersionUID = 1L;

	private final Timer timer = new Timer(200, this);
	private final Timer fadeTimer = new Timer(50, this);
	private final JLabel text;
	private int offset = 50;
	private boolean bottom_left;
	
	public Notification(Color background, Color textColor, Font font, boolean bottom_left, int offset){
		text = new JLabel();
		text.setForeground(textColor);
		text.setFont(font);//
		this.add(text);
        this.setBackground(background);
        this.pack();
        this.setIconImage(null);
        this.setAlwaysOnTop(true);
        this.bottom_left = bottom_left;
        this.offset = offset;
	}
	
	public Notification(Color background, Color textColor, Font font){
		this(background,textColor,font,false,0);
	}
	
	private void updateLocation(){
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        int x = (int) rect.getMaxX() - this.getWidth();
        int y = (int) rect.getMaxY() - this.getHeight() - offset;
        this.setLocation(x, y);
        
	}
	
	public void showText(String text){
		this.text.setText(text);
		this.pack();
		if(this.bottom_left)
			this.updateLocation();
		this.fadeTimer.stop();
		this.setOpacity(1f);
		this.setVisible(true);
		this.timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(this.timer)){
			this.timer.stop();
			this.fadeTimer.start();
		}
		
		if(e.getSource().equals(this.fadeTimer)){
			float opacity = this.getOpacity()-0.05f;
			if(opacity <= 0f){
				this.setVisible(false);
				this.fadeTimer.stop();
			}else{
				this.setOpacity(opacity);
			}
		}
	}
	
	
}
