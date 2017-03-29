package controller;

import java.util.Map;

import db.DataBase;
import model.Request;
import model.Response;
import model.User;

public class UserLoginController extends HttpController {

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

		User user = null;
		if (userId != null)
			user = DataBase.findUserById(userId);

		// 로그인 성공
		// 악의적인 리퀘스트 가능성 대비한 서버 방어코드
		if (user != null && password != null && user.getPassword().equals(password)) {
			res.redirect(siteRootUrl + "/index.html", "logined=true");
			return;
		}

		// 로그인 실패
		res.redirect(siteRootUrl + "/user/login_failed.html", "logined=false");
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
