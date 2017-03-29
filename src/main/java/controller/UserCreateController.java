package controller;

import java.util.Map;

import db.DataBase;
import model.Request;
import model.Response;
import model.User;

public class UserCreateController extends HttpController {

	@Override
	public void get(Request req, Response res) {
		// TODO Auto-generated method stub

	}

	@Override
	public void post(Request req, Response res) {
		Map<String, String> userParam = req.getFormRequestParamsFromBody();

		// map.get("key")는 해당 키 없으면 null 리턴함
		String userId = userParam.get("userId");
		String password = userParam.get("password");
		String name = userParam.get("name");
		String email = userParam.get("email");

		// 필요한 parameter 다 있는지 검증, 없으면 400번 응답
		if (userId != null && password != null && name != null && email != null) {
			User user = new User(userParam.get("userId"), userParam.get("password"), userParam.get("name"),
					userParam.get("email"));
			DataBase.addUser(user);
			DataBase.findAll().forEach(u -> {
				log.debug("database content: {}", u);
			});
			res.redirect(siteRootUrl + "/index.html", null);
			return;
		}

		// 원래는 400 코드 대신 다 채우라고 경고해주는 회원가입 페이지로 리다이렉해줘야 한다
		res.show400WrongRequest();
		return;
	}

	@Override
	public void put(Request req, Response res) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Request req, Response res) {
		// TODO Auto-generated method stub

	}
}
