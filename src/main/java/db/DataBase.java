package db;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

import model.User;

public class DataBase {
	private static Map<String, User> users = Maps.newHashMap();
	static {
		addUser(new User("asdf", "asdf", "asdf", "asdf"));
		addUser(new User("zxcv", "zxcv", "zxcv", "zxcv"));
	}

	public static void addUser(User user) {
		users.put(user.getUserId(), user);
	}

	public static User findUserById(String userId) {
		return users.get(userId);
	}

	public static Collection<User> findAll() {
		return users.values();
	}
}
