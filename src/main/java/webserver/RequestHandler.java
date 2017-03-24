package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Controller;
import model.Request;
import model.Response;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		// java 7.0 문법 try(), finally에서 close하는 지점을 자동으로 만들어준다
		// closeable이라는 인터페이스를 상속 받고 있는 객체여야 한다
		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

			Request req = new Request(in);
			Response res = new Response(out);

			new Controller(req, res).makeResponseFromRequest();

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
