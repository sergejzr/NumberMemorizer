package de.l3s.sz.text;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.DocFlavor.STRING;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SimplerussiantextParser extends DocumentParser {

	@Override
	public List<List<List<String>>> parseDocument(Document doc) {

		ArrayList<List<List<String>>> ret = new ArrayList<>();

		String tweets = doc.text().toLowerCase();
		
		String[] arr = tweets.split("[\\.\\!\\?\\;\\:]");

		for (String text : arr) {

			ArrayList<List<String>> cleandoc = new ArrayList<>();

			ret.add(cleandoc);

		
			String s = text.replaceAll("\\b((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])", " ");

			// System.out.println(s);
			Pattern pattern = Pattern.compile("\\p{L}+");
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
		return ret;

	}

}
