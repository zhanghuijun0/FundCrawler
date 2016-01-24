package com.zhj;


import java.io.File;
import java.io.IOException;

/**
 * 该类封装了一些创建文件、文件夹的实现
 * 
 * @author zhj
 *
 */
public class CreateFolderHelper {
	/**
	 * 创建文件夹
	 * 
	 * @param path
	 *            log
	 */
	public void createFolder(String path) {
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	/**
	 * 创建文件
	 * 
	 * @param file
	 *            log/command.log
	 */
	public void createFile(String file) {
		if (file.contains("/")) {
			createFolder(file.split("/")[0]);
		}
		File f = new File(file);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		CreateFolderHelper mCreateFolderHelper = new CreateFolderHelper();
		String path = "log/text.log";
		mCreateFolderHelper.createFile(path);
	}
}
