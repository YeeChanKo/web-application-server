package controller;

import model.Request;
import model.Response;

public abstract class HttpController implements Controller {

	@Override
	public void service(Request req, Response res) {
		switch (req.getHttpMethod()) {
		case "GET":
			get(req, res);
			break;
		case "POST":
			post(req, res);
			break;
		case "PUT":
			put(req, res);
			break;
		case "DELETE":
			delete(req, res);
			break;
		default:
			res.show400WrongRequest();
		}
	}

	public abstract void get(Request req, Response res);

	public abstract void post(Request req, Response res);

	public abstract void put(Request req, Response res);

	public abstract void delete(Request req, Response res);
}