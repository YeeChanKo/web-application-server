package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class StaticFile {
	private static final String dirPath = "./webapp";

	private File file;
	private String name; // 확장자 포함 이름
	private String extension;
	private String contentType;

	// path는 "/*/*.*" 형식
	public StaticFile(String path) {
		this.file = new File(dirPath + path);
		if (checkIfExistAndNotDirectory()) {
			name = file.getName();
			extension = getExtensionFromFile();
			contentType = setContentTypeFromExtension();
		}
	}

	// 이름 설정되었으면 null이 아니므로 파일이 존재한다 true 리턴
	public boolean exists() {
		if (name != null)
			return true;
		return false;
	}

	public byte[] readFile() {
		try {
			return Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getContentType() {
		return this.contentType;
	}

	public File getFile() {
		return this.file;
	}

	private boolean checkIfExistAndNotDirectory() {
		return file.exists() && !file.isDirectory();
	}

	private String getExtensionFromFile() {

		int i = name.lastIndexOf('.');
		if (i > 0) {
			return name.substring(i + 1);
		}
		return null;
	}

	private String setContentTypeFromExtension() {
		switch (extension) {
		case "html":
		case "css":
			return "text/" + extension;
		case "js":
			return "text/javascript";
		case "ico":
			return "image/x-icon";
		case "woff":
			return "application/font-woff";
		default:
			return "text/plain";
		}
	}
}
