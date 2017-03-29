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

			// 헤더 첫번째 줄 requestline 읽어들인다
			// httpMethod, requestPath, requestParams 설정
			String requestLine = br.readLine();
			log.debug("request line: {}", requestLine);
			if (requestLine == null) {
				return;
			}
			splitRequestLine(requestLine);

			// 나머지 헤더들 맵 형식으로 읽어들인다
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

			// 바디가 있는 경우 바디도 읽어들인다
			String contentLength = requestHeader.get("Content-Length");
			if (contentLength != null && !contentLength.isEmpty())
				requestBody = IOUtils.readData(br, Integer.parseInt(contentLength));
			// log.debug("request body: {}", requestBody);

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void splitRequestLine(String requestLine) {
		String[] result = requestLine.split("\\s|\\?");

		httpMethod = result[0];
		requestPath = result[1];

		// ?로 구분되는 get으로 넘겨주는 파라미터 없을 경우
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

	public String getParamByKey(String key) {
		return requestParams.get(key);
	}

	public String getHeader(String key) {
		return requestHeader.get(key);
	}

	public String getRequestProtocol() {
		return requestProtocol;
	}

	// post form request 에서 바디로 들어오는 내용 반환
	// 바디에 아무 내용 없으면 빈 맵 리턴
	// value가 빈 값인 경우엔 key도 아예 안들어간다
	public Map<String, String> getFormRequestParamsFromBody() {
		return HttpRequestUtils.parseQueryString(requestBody);
	}
}
