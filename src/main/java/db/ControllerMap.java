package db;

import java.util.Map;

import com.google.common.collect.Maps;

import controller.Controller;
import controller.UserCreateController;
import controller.UserListController;
import controller.UserLoginController;

public class ControllerMap {

	private static Map<String, Controller> controllerMap;

	static {
		// controllerMap 초기화
		controllerMap = Maps.newHashMap();
		controllerMap.put("/user/create", new UserCreateController());
		controllerMap.put("/user/login", new UserLoginController());
		controllerMap.put("/user/list.html", new UserListController());
	}

	private ControllerMap() {
	}

	public static Controller getController(String key) {
		return controllerMap.get(key);
	}
}
