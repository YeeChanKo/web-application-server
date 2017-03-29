package util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Request;
import model.Response;
import model.StaticFile;

public class WebServerTest {
	private static final Logger log = LoggerFactory.getLogger(WebServerTest.class);

	private String testDirectory = "./src/test/resources/";

	@Test
	public void request_GET() throws Exception {
		InputStream in = new FileInputStream(new File(testDirectory + "HTTP_GET.txt"));
		Request request = new Request(in);

		assertEquals("GET", request.getHttpMethod());
		assertEquals("/user/create", request.getRequestPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("javajigi", request.getParamByKey("userId"));
	}

	@Test
	public void request_POST() throws Exception {
		InputStream in = new FileInputStream(new File(testDirectory + "HTTP_POST.txt"));
		Request request = new Request(in);

		assertEquals("POST", request.getHttpMethod());
		assertEquals("/user/create", request.getRequestPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("javajigi", request.getFormRequestParamsFromBody().get("userId"));
	}

	@Test
	public void responseForward() throws Exception {
		// Http_Forward.txt 결과는 응답 body에 index.html이 포함되어 있어야 한다.
		Response response = new Response(createOutputStream("Http_Forward.txt"));
		response.forwardStaticFile(new StaticFile("/index.html"));
	}

	@Test
	public void responseRedirect() throws Exception {
		// Http_Redirect.txt 결과는 응답 headere에 Location 정보가 /index.html로 포함되어 있어야
		// 한다.
		Response response = new Response(createOutputStream("Http_Redirect.txt"));
		response.redirect(("/index.html"), null);
	}

	@Test
	public void responseCookies() throws Exception {
		// Http_Cookie.txt 결과는 응답 header에 Set-Cookie 값으로 logined=true 값이 포함되어
		// 있어야 한다.
		Response response = new Response(createOutputStream("Http_Cookie.txt"));
		response.addHeader("Set-Cookie", "logined=true");
		response.redirect("/index.html", null);
	}

	private OutputStream createOutputStream(String filename) throws FileNotFoundException {
		return new FileOutputStream(new File(testDirectory + filename));
	}
}
