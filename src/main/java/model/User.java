package model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User {
	private static final Logger log = LoggerFactory.getLogger(User.class);

	private String userId;
	private String password;
	private String name;
	private String email;

	public User(String userId, String password, String name, String email) {
		try {
			this.userId = decodeToUTF8(userId);
			this.password = decodeToUTF8(password);
			this.name = decodeToUTF8(name);
			this.email = decodeToUTF8(email);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
		}
	}

	public String getUserId() {
		return userId;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
	}

	public String decodeToUTF8(String str) throws UnsupportedEncodingException {
		return URLDecoder.decode(str, "UTF-8");
	}
}
