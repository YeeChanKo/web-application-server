package model;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;

public class Response {
	private static final Logger log = LoggerFactory.getLogger(Response.class);

	private DataOutputStream dos;

	public Response(OutputStream out) {
		dos = new DataOutputStream(out);
	}

	public void response200Header(String contentType, int lengthOfBodyContent) {
		response200Header(contentType, lengthOfBodyContent, null);
	}

	public void response200Header(String contentType, int lengthOfBodyContent, String cookieContents) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			if (cookieContents != null)
				dos.writeBytes("Set-Cookie: " + cookieContents + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void response302Redirect(String url) {
		response302Redirect(url, null);
	}

	public void response302Redirect(String url, String cookieContents) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			dos.writeBytes("Location: " + url + "\r\n");
			if (cookieContents != null) {
				dos.writeBytes("Set-Cookie: " + cookieContents + "\r\n");
				log.debug("cookie writing: " + cookieContents);
			}
			dos.writeBytes("\r\n");
			responseBody("".getBytes());
		} catch (IOException e) {
			log.error(e.getMessage());
		}

	}

	public void responseStatusCodeAndMessage(int statusCode, String statusMsg, String bodyMsg) {
		byte[] message = bodyMsg.getBytes();
		try {
			dos.writeBytes("HTTP/1.1 " + statusCode + " " + statusMsg + " \r\n");
			dos.writeBytes("Content-Type: text/plain;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + message.length + "\r\n");
			dos.writeBytes("\r\n");
			responseBody(message);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void response404NotFound() {
		responseStatusCodeAndMessage(404, "NOT_FOUND", "404. Page Not Found!");
	}

	public void response400WrongRequest() {
		responseStatusCodeAndMessage(400, "WRONG_REQUEST",
				"400. There is something wrong in your request!\n" + "Please retry.");
	}

	public void responseBody(byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void responseUserList() {
		try {
			HTML html = new HTML("/webapp/user/list.html");
			Element tbody = html.select("#main tbody");

			DataBase.findAll().forEach(new Consumer<User>() {
				int count = 3; // 기존 stock 요소로 1,2번이 존재해서 3번부터

				@Override
				public void accept(User user) {
					String element = "<tr><th scope='row'>" + count + "</th><td>" + user.getUserId() + "</td><td>"
							+ user.getName() + "</td><td>" + user.getEmail()
							+ "</td><td><a href='#' class='btn btn-success'role='button'>수정</a></td></tr>";
					html.appendElementAsStringAt(element, tbody);
					count++;
				}
			});

			byte[] body = html.getHTMLAsByte();
			response200Header("text/html", body.length);
			responseBody(body);

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
