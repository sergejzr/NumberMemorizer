package de.l3s.sz.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.l3s.sz.memorizer.Indexer;
import de.l3s.sz.memorizer.Result;

public class MemorizerDemo {
	
	public static void readRussianHtlmTexts(File dir, Indexer idx)
	{


		

		for (File f : dir.listFiles()) {

			try {
				Document doc = Jsoup.parse(f, "KOI8-R");

				String s = doc.text();
				// System.out.println(s);
				Pattern pattern = Pattern.compile("\\p{L}+");
				Matcher matcher = pattern.matcher(s);
				ArrayList<String> text = new ArrayList<>();
				while (matcher.find()) {
					if (matcher.group().length() > 2) {
						text.add(matcher.group().toLowerCase());
					}
				}

				idx.addText(text);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	
	}
	public static void main(String[] args) {


		Indexer idx = new Indexer();

		File model = new File("mapper.ser");

		if (!model.exists()) {
			try {
				MemorizerDemo.readRussianHtlmTexts(new File("/home/zerr/russtext/"), idx);
				idx.save(model);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		} else {
			try {
				idx.read(model);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		// idx.print(0, 5);

		String num = "84957218383";
		Iterator<Result> it = idx.result(num, true);
		
		while(it.hasNext())
		{
			Result r = it.next();
			System.out.println(r.getScore()+"\t"+r.getSplit()+"\t"+r.getSentence());
		}
	
	
	}

}
