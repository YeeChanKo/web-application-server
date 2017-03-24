package model;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
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
			File file = new File(new File("").getAbsolutePath() + "/webapp/user/list.html");
			log.debug(new File("").getAbsolutePath() + "/webapp/user/list.html");

			Document doc = Jsoup.parse(file, "UTF-8", "");
			Element tbody = doc.select("#main tbody").first();

			int count = 3;
			for (User user : DataBase.findAll()) {
				String str = "<tr><th scope='row'>" + count + "</th><td>" + user.getUserId() + "</td><td>"
						+ user.getName() + "</td><td>" + user.getEmail()
						+ "</td><td><a href='#' class='btn btn-success'role='button'>수정</a></td></tr>";
				// <#root> 벗기기
				Element ele = Jsoup.parse(str, "", Parser.xmlParser()).child(0);
				tbody.appendChild(ele);
				count++;
			}

			byte[] body = doc.toString().getBytes();

			response200Header("text/html", body.length);
			responseBody(body);

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
