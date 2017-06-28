package fr.klemek.clicker;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.Timer;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseMotionListener;


public class Clicker implements ActionListener, NativeKeyListener, NativeMouseMotionListener{

	private static final String VERSION = "1.0";
	
	private static final int MAX_DIST = 50;

	private Timer t;
	private Robot robot;
	
	private int hz = 200;
	
	private Image handRed, handGreen;
	private TrayIcon trayIcon;
	private Notification notif;
	
	private Point startpoint, mousepoint;
	
	public static void main(String[] args) {
		new Clicker();
	}
	
	public Clicker(){
		if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        
        handGreen = createImage("/hand_green32.png", "tray icon");
        handRed = createImage("/hand_red32.png", "tray icon");
        trayIcon = new TrayIcon(handGreen);
        trayIcon.setImageAutoSize(true);

        final PopupMenu popup = new PopupMenu();
        
        MenuItem info = new MenuItem("ALT+F1 : launch");
        info.setEnabled(false);
        popup.add(info);
        info = new MenuItem("ALT+PAGE UP : speed +100Hz");
        info.setEnabled(false);
        popup.add(info);
        info = new MenuItem("ALT+PAGE DOWN : speed -100Hz");
        info.setEnabled(false);
        popup.add(info);
        popup.addSeparator();
        popup.add(new MenuItem("Exit (ALT+F2)"));
        popup.addActionListener(this);
        
        trayIcon.setPopupMenu(popup);

        
        final SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
        
        notif = new Notification(new Color(0,0,0,0), Color.WHITE, new Font("Impact", Font.BOLD, 20));
        
        notif.showText("Auto clicker !");
        
        try {
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(this);
			GlobalScreen.addNativeMouseMotionListener(this);
		} catch (NativeHookException e1) {
			System.out.println("Could not register native hook");
			return;
		}
        
        try {
			
			robot = new Robot();
			
			
		} catch (AWTException e) {
			System.out.println("Could not register robot");
			return;
		}
        
        t = new Timer(1000/hz,this);
		t.addActionListener(this);
		
		trayIcon.setToolTip("AutoClicker by Klemek (version "+VERSION+")\n"+hz+" Hz ("+(t.isRunning()?"running":"stopped")+")");
		
	}

	//Obtain the image URL
    protected static Image createImage(String path, String description) {
        URL imageURL = Clicker.class.getResource(path);
         
        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == t){
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		}else{
			System.exit(0);
		}
		
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		if(e.getModifiers() == NativeKeyEvent.ALT_L_MASK || e.getModifiers() == NativeKeyEvent.ALT_R_MASK){
			switch(e.getKeyCode()){
			case NativeKeyEvent.VC_F1:
				if(t.isRunning()){
					t.stop();
					trayIcon.setImage(handGreen);
					notif.showText("   Stopped");
				}
				else{
					startpoint = (Point) mousepoint.clone();
					t.start();
					trayIcon.setImage(handRed);
					notif.showText("   Started");
				}
				break;
			case NativeKeyEvent.VC_F2:
				System.exit(0);
				break;
			case NativeKeyEvent.VC_PAGE_UP:
				if(hz<1000){
					hz+=100;
					if(hz==101)
						hz = 100;
					t.setDelay(1000/hz);
					notif.showText("   "+hz+"Hz ");
				}
				break;
			case NativeKeyEvent.VC_PAGE_DOWN:
				if(hz>1){
					hz-=100;
					if(hz==0)
						hz = 1;
					t.setDelay(1000/hz);
					notif.showText("   "+hz+"Hz ");
				}
				break;
			}
			trayIcon.setToolTip("AutoClicker by Klemek\n"+hz+" Hz ("+(t.isRunning()?"running":"stopped")+")");
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {}

	@Override
	public void nativeMouseDragged(NativeMouseEvent arg0) {}

	@Override
	public void nativeMouseMoved(NativeMouseEvent e) {
		mousepoint = e.getPoint();
		notif.setLocation(mousepoint);
		if(t.isRunning() && startpoint.distance(mousepoint)>=MAX_DIST){
			t.stop();
			trayIcon.setImage(handGreen);
			notif.showText("   Stopped");
			trayIcon.setToolTip("AutoClicker by Klemek\n"+hz+" Hz ("+(t.isRunning()?"running":"stopped")+")");
		}
	}

}
