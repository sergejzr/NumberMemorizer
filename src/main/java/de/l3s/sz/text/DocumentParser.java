package de.l3s.sz.text;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public abstract class DocumentParser {

	public DocumentParser() {
		// TODO Auto-generated constructor stub
	}

	public List<List<List<String>>> readHtmlDocument(File f, String charset) {
		try {
			Document doc = Jsoup.parse(f, charset);

			
			return parseDocument(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public abstract List<List<List<String>>> parseDocument(Document doc);

}
