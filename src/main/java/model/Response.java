package model;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class Response {
	private static final Logger log = LoggerFactory.getLogger(Response.class);
	private static final String httpVersion = "HTTP/1.1";

	private DataOutputStream dos;
	private Map<String, String> headers;

	public Response(OutputStream out) {
		dos = new DataOutputStream(out);
		headers = Maps.newHashMap();
	}

	private void writeLineInDos(String line) {
		try {
			dos.writeBytes(line + "\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void writeBodyInDos(byte[] body) {
		try {
			dos.write(body, 0, body.length);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void flushDos() {
		try {
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void writeStatusCodeAndMessage(int statusCode, String statusMsg) {
		writeLineInDos(httpVersion + " " + statusCode + " " + statusMsg);
	}

	private void writeStatusCode200() {
		writeStatusCodeAndMessage(200, "OK");
	}

	public void addHeader(String key, String value) {
		headers.put(key, value);
	}

	private void writeHeaderAndEmptyLine() {
		headers.keySet().stream().forEach(key -> {
			writeLineInDos(key + ": " + headers.get(key));
		});
		// 헤더 종료후 구분선 넣어줌
		writeLineInDos("");
	}

	private void addContentType(String contentType) {
		addHeader("Content-Type", contentType + "; charset=utf-8");
	}

	private void addContentLength(int contentLength) {
		addHeader("Content-Length", Integer.toString(contentLength));
	}

	private void addSetCookie(String cookieContents) {
		addHeader("Set-Cookie", cookieContents);
	}

	public void redirect(String url, String cookieContents) {
		writeStatusCodeAndMessage(302, "Found");
		addHeader("Location", url);
		if (cookieContents != null)
			addSetCookie(cookieContents);
		writeHeaderAndEmptyLine();
		flushDos();
	}

	public void show200WithBody(String contentType, byte[] body, String cookieContents) {
		writeStatusCode200();
		addContentType(contentType);
		addContentLength(body.length);
		if (cookieContents != null)
			addSetCookie(cookieContents);
		writeHeaderAndEmptyLine();
		writeBodyInDos(body);
		flushDos();
	}

	public void showSimplePage(int statusCode, String statusMsg, byte[] message) {
		writeStatusCodeAndMessage(statusCode, statusMsg);
		addContentType("text/plain");
		addContentLength(message.length);
		writeHeaderAndEmptyLine();
		writeBodyInDos(message);
		flushDos();
	}

	public void show400WrongRequest() {
		showSimplePage(400, "WRONG_REQUEST",
				"400. There is something wrong in your request!\nPlease retry.".getBytes());
	}

	public void show404NotFound() {
		showSimplePage(404, "NOT_FOUND", "404. Page Not Found!".getBytes());
	}

	public void forwardStaticFile(StaticFile file) {
		show200WithBody(file.getContentType(), file.readFile(), null);
	}
}
