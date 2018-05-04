package de.l3s.sz.main;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.l3s.sz.memorizer.Indexer;
import de.l3s.sz.memorizer.MEMOMAPPING;
import de.l3s.sz.memorizer.Result;

public class MemorizerDemo {
	
	

	public static void main(String[] args) {

		Indexer idx = new Indexer(MEMOMAPPING.ENGLISH1);

		File modeldir = new File("models");
		File model = new File(modeldir, "mapper_standard_reuters.ser");
		
		if (!model.exists()) {
			try {
				MemorizerDemo.readReuters(new File("/home/zerr/tweetsdl/reuters/"), idx);
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
		idx.filterModel(5, 10);
		
		
		


		String num = "977645445";
		Iterator<Result> it = idx.result(num, true);

		int top = 10;
		while (it.hasNext()) {
			Result r = it.next();
			System.out.println(r.getScore() + "\t" + r.getSplit() + "\t" + r.getSentence());
			if (top-- <= 0)
				break;
		}

	}
	
	;
	
	private static void readReuters(File dir, Indexer idx) {
		for (File f : dir.listFiles()) {
/*
			if (!f.getName().endsWith("sgm"))
				continue;
				*/
			try {
				Document doc = Jsoup.parse(f, "UTF-8");

				String s = doc.text();
				s=s.replaceAll("\\b((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])", "");
				// System.out.println(s);
				Pattern pattern = Pattern.compile("[a-zA-Z]+");
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

	private static void readRussianHtlmTexts(File dir, Indexer idx) {

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

	private static void loadModelIfExist(Indexer idx, File model, File htmlfiledir) {
		
	}

	

}
