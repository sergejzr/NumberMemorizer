package de.l3s.sz.text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.DocFlavor.STRING;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ReutersParser extends ParagraphParser {

	@Override
	public List<List<List<String>>> parseDocument(Document doc) {

		ArrayList<List<List<String>>> ret = new ArrayList<>();

		Elements docs = doc.getElementsByTag("REUTERS");

		for (Element e : docs) {

			String text = e.text().toLowerCase();
			ArrayList<List<String>> cleandoc = new ArrayList<>();
			
			ret.add(cleandoc);
			for (String s : text.split("[\\.\\!\\?]")) {
				s = s.replaceAll("\\b((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])", " ");

				// System.out.println(s);
				Pattern pattern = Pattern.compile("[a-zA-Z]+");
				Matcher matcher = pattern.matcher(s);
				ArrayList<String> words = new ArrayList<>();
				while (matcher.find()) {
					if (matcher.group().length() > 2) {
						words.add(matcher.group().toLowerCase());
					}
				}
				if (words.size() > 0) {
					cleandoc.add(words);
				}

			}
		}
		return ret;

	}

}
