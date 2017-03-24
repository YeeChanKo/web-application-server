package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.HttpRequestUtils.Pair;
import util.IOUtils;

public class Request {
	private static final Logger log = LoggerFactory.getLogger(Request.class);

	private String httpMethod;
	private String requestPath;
	private Map<String, String> params;
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

			requestHeader = new HashMap<String, String>();
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

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public Request(String httpMethod, String requestPath, Map<String, String> params, String requestProtocol,
			Map<String, String> requestHeader, String requestBody) {
		super();
		this.httpMethod = httpMethod;
		this.requestPath = requestPath;
		this.params = params;
		this.requestProtocol = requestProtocol;
		this.requestHeader = requestHeader;
		this.requestBody = requestBody;
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

		params = HttpRequestUtils.parseQueryString(result[2]);
		requestProtocol = result[3];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((httpMethod == null) ? 0 : httpMethod.hashCode());
		result = prime * result + ((params == null) ? 0 : params.hashCode());
		result = prime * result + ((requestBody == null) ? 0 : requestBody.hashCode());
		result = prime * result + ((requestHeader == null) ? 0 : requestHeader.hashCode());
		result = prime * result + ((requestPath == null) ? 0 : requestPath.hashCode());
		result = prime * result + ((requestProtocol == null) ? 0 : requestProtocol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Request other = (Request) obj;
		if (httpMethod == null) {
			if (other.httpMethod != null)
				return false;
		} else if (!httpMethod.equals(other.httpMethod))
			return false;
		if (params == null) {
			if (other.params != null)
				return false;
		} else if (!params.equals(other.params))
			return false;
		if (requestBody == null) {
			if (other.requestBody != null)
				return false;
		} else if (!requestBody.equals(other.requestBody))
			return false;
		if (requestHeader == null) {
			if (other.requestHeader != null)
				return false;
		} else if (!requestHeader.equals(other.requestHeader))
			return false;
		if (requestPath == null) {
			if (other.requestPath != null)
				return false;
		} else if (!requestPath.equals(other.requestPath))
			return false;
		if (requestProtocol == null) {
			if (other.requestProtocol != null)
				return false;
		} else if (!requestProtocol.equals(other.requestProtocol))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Request [httpMethod=" + httpMethod + ", requestPath=" + requestPath + ", params=" + params
				+ ", requestProtocol=" + requestProtocol + ", requestHeader=" + requestHeader + ", requestBody="
				+ requestBody + "]";
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public String getRequestBody() {
		return requestBody;
	}
}

