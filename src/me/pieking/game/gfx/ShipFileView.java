package me.pieking.game.gfx;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;

public class ShipFileView extends FileView {

	@Override
	public Icon getIcon(File f) {
		if(f.getName().toLowerCase().endsWith(".rob")){
			return new ImageIcon(Spritesheet.tiles.subTile(0, 12, 2, 2).getImage());
		}
		return super.getIcon(f);
	}
	
	@Override
	public String getName(File f) {
		if(f.getName().toLowerCase().endsWith(".rob")){
			return f.getName().substring(0, f.getName().length() - ".rob".length());
		}
		return super.getName(f);
	}
	
	
	@Override
	public String getDescription(File f) {
		if(f.getName().toLowerCase().endsWith(".rob")){
			System.out.println("This is the description.");
		}
		return super.getDescription(f);
	}
	
	@Override
	public String getTypeDescription(File f) {
		if(f.getName().toLowerCase().endsWith(".rob")){
			return "A robot file.";
		}
		return super.getTypeDescription(f);
	}
	
}
