package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.Controller;
import db.ControllerMap;
import model.Request;
import model.Response;
import model.StaticFile;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client - IP: {}, Port: {}", connection.getInetAddress(), connection.getPort());

		// java 7.0 문법 try(), finally에서 close하는 지점을 자동으로 만들어준다
		// closeable이라는 인터페이스를 상속 받고 있는 객체여야 한다
		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

			Request req = new Request(in);
			Response res = new Response(out);

			String requestPath = req.getRequestPath();

			// 있는 api인지 체크
			Controller controller = ControllerMap.getController(requestPath);
			if (controller != null) {
				controller.service(req, res);
				return;
			}

			// 루트인 경우 index.html 보내줌
			if (requestPath.equals("/")) {
				res.forwardStaticFile(new StaticFile("/index.html"));
				return;
			}

			// static file인 경우 처리
			StaticFile file = new StaticFile(requestPath);
			if (file.exists()) {
				res.forwardStaticFile(file);
				return;
			}

			// 아무것도 아닐때
			res.show404NotFound();

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
