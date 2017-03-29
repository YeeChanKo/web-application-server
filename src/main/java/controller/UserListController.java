package controller;

import java.util.function.Consumer;

import org.jsoup.nodes.Element;

import db.DataBase;
import model.HTML;
import model.Request;
import model.Response;
import model.User;
import util.HttpRequestUtils;

public class UserListController extends HttpController {

	@Override
	public void get(Request req, Response res) {
		String cookie = req.getHeader("Cookie");
		String isLogIn = null;

		// 쿠키 있는지 확인
		if (cookie != null && !cookie.trim().isEmpty()) {
			isLogIn = HttpRequestUtils.parseCookies(cookie).get("logined");
		}

		// logined 값이 true인지 확인
		if (isLogIn != null && isLogIn.equals("true")) {
			HTML html = new HTML(req.getRequestPath());
			Element tbody = html.select("#main tbody");

			// TODO 템플릿 사용하기
			DataBase.findAll().forEach(new Consumer<User>() {
				int count = 3; // 기존 stock 요소로 1,2번이 존재해서 3번부터

				@Override
				public void accept(User user) {
					String element = "<tr><th scope='row'>" + count + "</th><td>" + user.getUserId() + "</td><td>"
							+ user.getName() + "</td><td>" + user.getEmail()
							+ "</td><td><a href='#' class='btn btn-success'role='button'>수정</a></td></tr>";
					html.appendElementAsStringAt(element, tbody);
					count++;
				}
			});

			byte[] body = html.getHTMLAsByte();
			res.show200WithBody("text/html", body, null);
			return;
		}

		// 쿠키 제대로 없으면 로그인하라고 리다이렉트
		res.redirect(siteRootUrl + "/user/login.html", null);
		return;
	}

	@Override
	public void post(Request req, Response res) {
		// TODO Auto-generated method stub

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
