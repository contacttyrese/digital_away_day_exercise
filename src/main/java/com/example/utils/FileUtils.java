package com.example.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class FileUtils {
	private FileUtils(){}
	
	public static BufferedReader getBufferedReader(File file){
	BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
		}
		return br;
	}
	

}