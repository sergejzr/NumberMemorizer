package de.sz.crawl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.l3s.sz.text.AnnotatedtextCorpora;

/**
 * Example crawler to get words from a website with a dictionary
 * @author szerr
 *
 */
public class Crawler {
	public static HashSet<String> fileToHashSet(File filename) {
		HashSet<String> lines = new HashSet<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = reader.readLine();
			while (line != null) {
				lines.add(line);
				line = reader.readLine();
			}
			reader.close();
			System.out.println("Successfully read file.");
		} catch (IOException e) {
			System.err.println("Error reading file: " + e.getMessage());
		}
		return lines;
	}

	/**
	 * Parses webpages and stores the corpora into a serialized file.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String[] starturls = new String[] { "http://nskhuman.ru/unislov/suschestv.php","http://nskhuman.ru/unislov/glagol.php",
					"http://nskhuman.ru/unislov/morfolog.php?nchast=3",
					"http://nskhuman.ru/unislov/morfolog.php?nchast=4",
					"http://nskhuman.ru/unislov/morfolog.php?nchast=5",
					"http://nskhuman.ru/unislov/morfolog.php?nchast=6",
					"http://nskhuman.ru/unislov/morfolog.php?nchast=7",
					"http://nskhuman.ru/unislov/morfolog.php?nchast=8",
					"http://nskhuman.ru/unislov/morfolog.php?nchast=9",
					"http://nskhuman.ru/unislov/morfolog.php?nchast=10",
					"http://nskhuman.ru/unislov/morfolog.php?nchast=11",
					"http://nskhuman.ru/unislov/morfolog.php?multi=1" };
			String consonants = "бвгджзйклмнпрстфхцчшщ";
			AnnotatedtextCorpora corpora = new AnnotatedtextCorpora(consonants);

			for (String starturl : starturls) {
				Crawler c = new Crawler();
				HashSet<String> li = c.crawl(starturl,new File("example_corpora"));
				List<String> slist = new ArrayList<>();
				slist.addAll(li);
				corpora.addTextList(slist);

			}
			corpora.save(new File("src/main/resources/de/l3s/sz/memorizer/models/mapper_standard_slovar.ser"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeStringsToFile(HashSet<String> strings, File filename) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			for (String string : strings) {
				writer.write(string);
				writer.newLine();
			}
			writer.close();
			System.out.println("Successfully wrote strings to file.");
		} catch (IOException e) {
			System.err.println("Error writing strings to file: " + e.getMessage());
		}
	}

	HashSet<String> visitedurlls = new HashSet<>();

	public Crawler() throws MalformedURLException, IOException {
	}

	private HashSet<String> crawl(String starturl, File corporaFolder) throws MalformedURLException, IOException {

		HashSet<String> urls = new HashSet<>();
		HashSet<String> words = new HashSet<>();

		Document miaindoc = Jsoup.parse(new URL(starturl), 2000);
		Elements elsmain = miaindoc.getElementsByAttributeValueMatching("href", "\\.*nlet1=\\d+");
		Iterator<Element> itmain = elsmain.iterator();

		File outfile = new File(corporaFolder,starturl.hashCode() + ".txt");

		if(outfile.exists()) 
		{
			return fileToHashSet(outfile);
		}
		if (outfile.exists() || visitedurlls.contains(starturl)) {
			return new HashSet<>();
		}
		visitedurlls.add(starturl);

		while (itmain.hasNext()) {
			Element mainel = itmain.next();

			String mainurlstr = mainel.attr("href");
			if (!mainurlstr.startsWith("http")) {
				mainurlstr = "http://nskhuman.ru/unislov/" + mainurlstr;
			}

			if (visitedurlls.contains(mainurlstr)) {
				continue;
			}
			visitedurlls.add(mainurlstr);
			Document doc = Jsoup.parse(new URL(mainurlstr), 2000);
			// String out = new Scanner(new URL(urlstr+"?nlet1="+i).openStream(),
			// "UTF-8").useDelimiter("\\A").next();

			Elements els = doc.getElementsByAttributeValueMatching("href", "\\.*nlet2\\.*");
			Iterator<Element> it = els.iterator();

			while (it.hasNext()) {
				Element el = it.next();
				String nurl = "http://nskhuman.ru/unislov/" + el.attr("href");
				if (visitedurlls.contains(nurl)) {
					continue;
				}
				visitedurlls.add(nurl);
				urls.add(nurl);
			}

			words.addAll(getWords(doc));

			for (String s : urls) {
				Document docx = Jsoup.parse(new URL(s), 2000);
				words.addAll(getWords(docx));
			}

		}
		writeStringsToFile(words, outfile);

		return words;

	}

	private HashSet<String> getWords(Document doc) {
		HashSet<String> ret = new HashSet<>();

		Elements els = doc.getElementsByAttributeValueMatching("href", "\\.*nslovo\\.*");
		Iterator<Element> it = els.iterator();

		while (it.hasNext()) {
			Element el = it.next();
			String cleanText = removeHtmlTags(el.html()).replaceAll("[^\\p{Alnum}\\p{javaLowerCase}\\p{javaUpperCase}]",
					" ");
			ret.add(cleanText.trim());
		}

		return ret;
	}

	private String removeHtmlTags(String html) {
		Pattern pattern = Pattern.compile("<[^>]*>");
		Matcher matcher = pattern.matcher(html);
		return matcher.replaceAll("");
	}
}
