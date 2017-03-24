package util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class testTest {
	private static final Logger log = LoggerFactory.getLogger(testTest.class);

	@Test
	public void test() {
		// Response.responseUserList();
		try {
			String s = URLEncoder.encode("jkl@jkl", "UTF-8");
			String d = URLDecoder.decode(s, "UTF-8");
			log.debug("{}", s);
			log.debug("{}", d);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
