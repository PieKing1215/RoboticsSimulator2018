package me.pieking.game;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

public class FileSystem {

	public static File getSavesFolder(){
		return getFolder("saves");
	}
	
	public static File getFolder(String name){
		return new File(getUserFolder(), name + "/");
	}
	
	public static File getFile(String name){
		return new File(getUserFolder(), name);
	}
	
	public static File getUserFolder(){
		return new File(FileSystemView.getFileSystemView().getHomeDirectory(), "robo2018/");
	}
	
}
