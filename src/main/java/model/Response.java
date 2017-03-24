package model;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Response {
	private static final Logger log = LoggerFactory.getLogger(Response.class);

	private DataOutputStream dos;

	public Response(OutputStream out) {
		dos = new DataOutputStream(out);
	}

	public void response200Header(String contentType, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void responseRedirect(String url) {
		String msg = //simple html with script 
				"alert('회원가입 성공!');\n" + "document.location.href='" + url + "';";
		byte[] msgByte = msg.getBytes();
		response200Header("text/html", msgByte.length);
		responseBody(msgByte);
	}

	public void response404() {
		byte[] message = "404. Page Not Found!".getBytes();
		try {
			dos.writeBytes("HTTP/1.1 404 NOT_FOUND \r\n");
			dos.writeBytes("Content-Type: text/plain;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + message.length + "\r\n");
			dos.writeBytes("\r\n");
			responseBody(message);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void responseBody(byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

}
