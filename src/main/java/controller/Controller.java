package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.Request;
import model.Response;

public interface Controller {
	public static final Logger log = LoggerFactory.getLogger(Controller.class);
	public static final String siteRootUrl_production = "http://1.255.56.137:7070";
	public static final String siteRootUrl_development = "http://localhost:8080";
	public static final String siteRootUrl = siteRootUrl_development;

	public abstract void service(Request req, Response res);
}
