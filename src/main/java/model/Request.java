package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;
import util.IOUtils;

public class Request {
	private static final Logger log = LoggerFactory.getLogger(Request.class);

	private String httpMethod;
	private String requestPath;
	private Map<String, String> requestParams;
	private String requestProtocol;
	private Map<String, String> requestHeader;
	private String requestBody;

	public Request(InputStream in) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			String requestLine = br.readLine();
			log.debug("request line: {}", requestLine);
			if (requestLine == null) {
				return;
			}
			splitRequestLine(requestLine);

			requestHeader = Maps.newHashMap();
			String temp = "";
			Pair header = null;
			temp = br.readLine();
			while (!temp.equals("")) {
				log.debug("request header: {}", temp);
				header = HttpRequestUtils.parseHeader(temp);
				requestHeader.put(header.getKey(), header.getValue());
				temp = br.readLine();
			}

			String contentLength = requestHeader.get("Content-Length");
			if (contentLength != null && !contentLength.isEmpty())
				requestBody = IOUtils.readData(br, Integer.parseInt(contentLength));
			log.debug("request body: {}", requestBody);

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void splitRequestLine(String requestLine) {
		String[] result = requestLine.split("\\s|\\?");

		httpMethod = result[0];
		requestPath = result[1];

		// if no param
		if (result.length < 4) {
			requestProtocol = result[2];
			return;
		}

		requestParams = HttpRequestUtils.parseQueryString(result[2]);
		requestProtocol = result[3];
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public Map<String, String> getParams() {
		return requestParams;
	}

	public Map<String, String> getRequestHeader() {
		return requestHeader;
	}

	public String getRequestProtocol() {
		return requestProtocol;
	}

	// 바디에 아무 내용 없으면 빈 맵 리턴, 폼에 빈값 있는 건 아예 안들어감
	public Map<String, String> getFormRequestParamsFromBody() {
		return HttpRequestUtils.parseQueryString(requestBody);
	}
}
