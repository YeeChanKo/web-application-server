package model;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTML {
	private static final Logger log = LoggerFactory.getLogger(HTML.class);

	private StaticFile file;
	private Document doc;

	public HTML(String path) {
		try {
			file = new StaticFile(path);
			doc = Jsoup.parse(file.getFile(), "UTF-8", "");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public Element select(String selector) {
		return doc.select(selector).first();
	}

	public void appendElementAsStringAt(String element, Element parent) {
		// child(0) - <#root> 벗기기
		Element ele = Jsoup.parse(element, "", Parser.xmlParser()).child(0);
		parent.appendChild(ele);
	}

	public byte[] getHTMLAsByte() {
		return doc.toString().getBytes();
	}
}
