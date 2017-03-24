package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;

public class Controller {
	private static final Logger log = LoggerFactory.getLogger(Controller.class);
	private static final String dirPath = "./webapp";

	private Request req;
	private Response res;

	private File staticFile = null;

	public Controller(Request req, Response res) {
		super();
		this.req = req;
		this.res = res;
		setStaticFile(req.getRequestPath());
	}

	// 요청에 따른 응답 해주기
	public void makeResponseFromRequest() {
		if (req.getRequestPath() == null) {
			log.debug("requestPath is null!");
			res.response404();
			return;
		}

		// url이 static file로 존재하는 경우
		if (staticFile.exists() && !staticFile.isDirectory()) {
			String contentType = getContentType(getExtensionFromFile(staticFile.getName()));
			byte[] body = readFileFromPath(staticFile.toPath());
			log.debug("content-type: " + contentType);
			res.response200Header(contentType, body.length);
			res.responseBody(body);
			return;
		}
		log.debug("not a static file!");

		// static file이 아닌 경우
		switch (req.getRequestPath()) {
		case "/":
			setStaticFile("/index.html");
			byte[] body = readFileFromPath(staticFile.toPath());
			res.response200Header("text/html", body.length);
			res.responseBody(body);
			break;

		case "/user/create":
			Map<String, String> userParam = req.getParams();
			User user = new User(userParam.get("userId"), userParam.get("password"), userParam.get("name"),
					userParam.get("email"));
			DataBase.addUser(user);
			res.responseRedirect("www.google.com");

			break;

		default:
			res.response404();
		}
	}

	// path는 "/*/*.*" 형식
	public void setStaticFile(String path) {
		this.staticFile = new File(dirPath + path);
	}

	private byte[] readFileFromPath(Path path) {
		try {
			return Files.readAllBytes(path);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getContentType(String extension) {
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
		}

		return null;
	}

	public static String getExtensionFromFile(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			return fileName.substring(i + 1);
		}
		return null;
	}

}
