package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import util.HttpRequestUtils;

public class Controller {
	private static final Logger log = LoggerFactory.getLogger(Controller.class);
	private static final String dirPath = "./webapp";
	private static final String siteRootUrl_production = "http://1.255.56.137:7070";
	private static final String siteRootUrl_development = "http://localhost:8080";
	private static final String siteRootUrl;
	static {
		siteRootUrl = siteRootUrl_production;
	}

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
			res.response404NotFound();
			return;
		}

		switch (req.getRequestPath()) {
		// 루트 페이지
		case "/": {
			setStaticFile("/index.html");
			byte[] body = readFileFromPath(staticFile.toPath());
			res.response200Header("text/html", body.length);
			res.responseBody(body);
			return;
		}

		// 회원가입
		case "/user/create": {
			Map<String, String> userParam = req.getFormRequestParamsFromBody();

			// map.get("key")는 해당 키 없으면 null 리턴함
			String userId = userParam.get("userId");
			String password = userParam.get("password");
			String name = userParam.get("name");
			String email = userParam.get("email");

			// 필요한 parameter 다 있는지 검증, 없으면 400번 응답
			if (userId != null && password != null && name != null && email != null) {
				User user = new User(userParam.get("userId"), userParam.get("password"), userParam.get("name"),
						userParam.get("email"));
				DataBase.addUser(user);
				DataBase.findAll().forEach(u -> {
					log.debug("database content: {}", u);
				});
				res.response302Redirect(siteRootUrl + "/index.html");
				return;
			}

			// 원래는 400 코드 대신 다 채우라고 경고해주는 회원가입 페이지로 리다이렉해줘야 한다
			res.response400WrongRequest();
			return;
		}

		// 로그인
		case "/user/login": {
			Map<String, String> userParam = req.getFormRequestParamsFromBody();

			// map.get("key")는 해당 키 없으면 null 리턴함
			String userId = userParam.get("userId");
			String password = userParam.get("password");

			User user = null;
			if (userId != null)
				user = DataBase.findUserById(userId);

			// 로그인 성공
			// 악의적인 리퀘스트 가능성 대비한 서버 방어코드
			if (user != null && password != null && user.getPassword().equals(password)) {
				res.response302Redirect(siteRootUrl + "/index.html", "logined=true");
				return;
			}

			// 로그인 실패
			res.response302Redirect(siteRootUrl + "/user/login_failed.html", "logined=false");
			return;
		}

		// 사용자 목록 출력
		case "/user/list.html": {
			String cookie = req.getRequestHeader().get("Cookie");
			// 쿠키 있는지 확인
			if (cookie != null && !cookie.trim().isEmpty()) {
				Map<String, String> cookieMap = HttpRequestUtils.parseCookies(cookie);
				String isLogIn = cookieMap.get("logined");
				// logined 값이 true인지 확인
				if (isLogIn != null && isLogIn.equals("true")) {
					res.responseUserList();
					return;
				}
			}

			res.response302Redirect(siteRootUrl + "/user/login");
			return;
		}

		default: {
			// static 파일인 경우
			if (staticFile.exists() && !staticFile.isDirectory()) {
				String contentType = getContentType(getExtensionFromFile(staticFile.getName()));
				byte[] body = readFileFromPath(staticFile.toPath());
				log.debug("content-type: " + contentType);
				res.response200Header(contentType, body.length);
				res.responseBody(body);
				return;
			}

			// 어디에도 속하지 않는 경우
			res.response404NotFound();
			return;
		}
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
