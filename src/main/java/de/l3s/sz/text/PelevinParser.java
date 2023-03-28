package de.l3s.sz.text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PelevinParser extends DocumentParser {

	@Override
	public List<List<List<String>>> parseDocument(Document hdoc) {
		List<List<List<String>>> ret=new ArrayList<>();
		Elements elements = hdoc.getElementsByTag("table");
		
		
	List<List<String>> doc=new ArrayList<>();
	
String text = null;

for(Element tab:elements){
text = tab.text();

if(text.startsWith("Виктор Пелевин")){break;}
}
	ret.add(doc);
	
	if(text==null){return ret;}
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
