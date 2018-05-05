package de.l3s.sz.text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;

public class LibRuParser extends ParagraphParser{

	@Override
	public List<List<List<String>>> parseDocument(Document jsdoc) {
	
		String text=jsdoc.text().toLowerCase();
		List<List<List<String>>> ret=new ArrayList<>();
		List<List<String>> doc=new ArrayList<>();
		ret.add(doc);
		for(String s:text.split("[\\.\\!\\?]"))
		{
			
			// System.out.println(s);
			Pattern pattern = Pattern.compile("\\p{L}+");
			Matcher matcher = pattern.matcher(s);
			ArrayList<String> words = new ArrayList<>();
			while (matcher.find()) {
				if (matcher.group().length() > 2) {
					words.add(matcher.group().toLowerCase());
				}
			}
			if(words.size()>0)
			{
				doc.add(words);
			}

		}
		return ret;

	}

}
