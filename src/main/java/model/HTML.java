package model;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

public class HTML {

	private File file;
	private Document doc;

	public HTML(String path) throws IOException {
		file = new File(new File("").getAbsolutePath() + path);
		doc = Jsoup.parse(file, "UTF-8", "");
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
