package me.pieking.game.gfx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import me.pieking.game.Game;
import me.pieking.game.Location;
import me.pieking.game.robot.Robot;

public class ShipFileAccessory extends JComponent implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private ImageIcon icon;
	
	private int width = 150;
	private int height = 200;
	
	public ShipFileAccessory(JFileChooser jfc) {
		jfc.addPropertyChangeListener(this);
		setPreferredSize(new Dimension(width, height));
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		// Extract property name from event object.

		String propName = e.getPropertyName();

		// Erase any displayed image if user moves up the directory hierarchy.

		if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(propName)) {
			icon = null;
			repaint();
			return;
		}

		// Display selected file. If a directory is selected, erase any
		// displayed image.

		if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(propName)) {
			// Extract selected file's File object.

			File file = (File) e.getNewValue();

			// If file is null, a directory was selected -- the user is moving
			// between directories. In response, any displayed image in the
			// accessory area must be erased.

			if (file == null) {
				icon = null;
				repaint();
				return;
			}
			
			// Obtain the selected file's icon.
			BufferedImage img = getPreview(file);
			System.out.println(img.getWidth() + " " + img.getHeight());
			icon = new ImageIcon(img);
			System.out.println(icon.getIconWidth() + " " + icon.getIconHeight());

			// The ImageIcon constructor invokes a Toolkit getImage() method to
			// obtain the image identified by file.getPath(). The image is read
			// from the file unless the image (together with file path/name
			// information) has been cached (for performance reasons). Suppose
			// the user has specified the name of a file and that file does not
			// exist. Toolkit's getImage() method will return an Image with the
			// width and height each set to -1. The "image" associated with this
			// Image will be cached. Suppose the user activates the open file
			// chooser and selects the file to which the image was saved. The
			// previous ImageIcon() constructor will execute, but the image
			// will not be read from the file -- it will be read from the cache
			// (with -1 as the width and as the height). No image will appear in 
			// the preview window; the user will be confused. The solution to
			// this problem is to test the Image's width for -1. If this value
			// is present, Image's flush() method is called on the Image, and a
			// new ImageIcon is created. Internally, Toolkit's getImage() method
			// will read the image from the file -- not from the cache.

			if (icon.getIconWidth() == -1) {
				icon.getImage().flush();
				icon = new ImageIcon(file.getPath());
			}

			// Scale icon to fit accessory area if icon too big.

			if (icon.getIconWidth() > width) icon = new ImageIcon(icon.getImage().getScaledInstance(width, -1, Image.SCALE_DEFAULT));

			// Display image.

			repaint();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		// When this method is called, the background has already been painted.
		// If icon is null, do nothing. This action causes the current image
		// thumbnail to disappear when the user selects a directory, for example.

//		g.setColor(Color.BLUE);
//		g.fillRect(0, 0, getWidth(), getHeight());
		
		
		if (icon != null) {
			System.out.println(icon.getIconWidth() + " " + icon.getIconHeight());
			// Paint a white background behind the pixels so that a GIF image's
			// transparent pixel causes white (instead of gray or whatever color
			// is appropriate for this look and feel) to show through.

			Graphics2D g2d = (Graphics2D) g;
			Rectangle bounds = new Rectangle(0, 0, icon.getIconWidth(), icon.getIconHeight());
			g.setColor(Color.white);
			g2d.fill(bounds);

			// Paint the image -- (0, 0) is the image's upper-left corner, and
			// the upper-left corner of the accessory area.
			
			icon.paintIcon(this, g, 0, 0);
		}
	}
	
	public BufferedImage getPreview(File f){
		try {
			
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			
			Graphics2D g = img.createGraphics();
			
			g.setColor(getBackground());
			g.fillRect(0, 0, width, height);
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, width, width);
			
			System.out.println("loading");
			Robot s = Robot.load(f, Game.getWorld().getSelfPlayer());
			System.out.println("done");
			s.construct(new Location(0, 0), 0);
			
			List<String> info = new ArrayList<String>();
			
			info.add(s.getGridSize() + " x " + s.getGridSize());
			info.add("there");
			
			g.setFont(new Font("Arial", Font.PLAIN, 14));
			g.setColor(Color.BLACK);
			for(int i = 0; i < info.size(); i++){
				g.drawString(info.get(i), width/2 - g.getFontMetrics().stringWidth(info.get(i))/2, width + 20 + 20*i);
			}
			
			g.translate(width/2, width/2);
			double scale = 2.5/s.getGridSize();
			g.scale(scale, scale);
//			g2d.translate((s.getGridSize()/2) * Component.unitSize * GameObject.SCALE, s.getGridSize()/2 * Component.unitSize * GameObject.SCALE);
			s.renderNoTransform(g);
//			if(false){
//			}
			
			g.dispose();
			return img;
		}catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
			e.printStackTrace();
		}
		return Images.errorS.getImage();
	}
	
}
