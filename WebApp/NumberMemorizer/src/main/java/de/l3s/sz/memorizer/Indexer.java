package de.l3s.sz.memorizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.l3s.sz.lib.BreakString;

public class Indexer {

	private HashMap<Character, Integer> mapper = new HashMap<>();

	/**
	 * Create Indexer with English mapping by deafult
	 */
	public Indexer() {
		this(MEMOMAPPING.ENGLISH1);
	}

	public String getCurrentmapping() {
		return currentmapping;
	}

	HashMap<String, Integer> df = new HashMap<>();
	HashMap<String, Integer> freq = new HashMap<>();

	private HashMap<String, Integer> cooccurence = new HashMap<>();

	private Integer allfreq = 0, alldf = 0;

	private HashMap<String, HashSet<String>> hashIndex = new HashMap<>();

	/**
	 * Prints complete index word to numbers
	 * 
	 * @param start
	 * @param end
	 */
	public void print(int start, int end) {

		for (String i : hashIndex.keySet()) {
			System.out.println(i + "\t" + hashIndex.get(i).toString());
		}

	}

	public HashMap<Character, Integer> getMapper() {
		return mapper;
	}

	String currentmapping = null;

	/**
	 * Create Indexer with given personalized mapping. Standard mappings are in
	 * the class MEMOMAPPING.
	 * 
	 * @param map
	 */
	public Indexer(String map) {
		currentmapping = map;
		String[] nums = map.split(",");
		for (String num : nums) {
			String pairstr[] = num.split(":");
			for (char c : pairstr[1].toCharArray()) {
				mapper.put(c, Integer.parseInt(pairstr[0].charAt(0) + ""));
			}

		}
	}

	/**
	 * Returns a list of words corresponding to given number
	 * 
	 * @param num
	 * @return
	 */
	public HashSet<String> getStringFromNumber(String num) {
		return hashIndex.get(num);
	}

	// private BreakString bs = new BreakString();

	/**
	 * Returns result sentence for the given number.
	 * 
	 * @param number
	 *            - the number to be converted to sentence
	 * @param minfreq 
	 * @param mindf 
	 * @param fullinfo
	 *            - if false, only best matches are returned.
	 * @return iterator with results of this conversion
	 */
	public Iterator<Result> result(String number, int mindf, int minfreq, boolean fullinfo) {
		return new ResultIterator(this, number, new BreakString(),  mindf,  minfreq,fullinfo);
	}



	/**
	 * Read text corpora model from a file
	 * 
	 * @param modelFile
	 * @throws IOException
	 */
	public void read(File modelFile) throws IOException {
		try {
			FileInputStream fis = new FileInputStream(modelFile);

			read(fis);

			fis.close();

		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			return;
		}
	}

	/**
	 * Save text corpora model to a file
	 * 
	 * @param modelFile
	 * @throws IOException
	 */
	public void save(File modelFile) throws IOException {

		if (hashIndex.size() == 0) {
			throw new IOException("no data added to model");
		}
		FileOutputStream fos = new FileOutputStream(modelFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(hashIndex);
		oos.writeObject(mapper);
		oos.writeObject(cooccurence);
		oos.writeObject(df);
		oos.writeObject(alldf);
		oos.writeObject(freq);
		oos.writeObject(allfreq);
		oos.close();
		fos.close();
		System.out.printf("Serialized HashMap data is saved in hashmap.ser");

	}

	private String makekey(String s1, String s2) {
		// if (s1.compareTo(s2) > 0)
		return s1 + "_" + s2;
		// return s2 + "_" + s1;
	}
Hashtable<String, HashSet<String>> universalIndex=new Hashtable<>();
	public void addText(List<String> document) {

		HashSet<String> bag = new HashSet<>(document);
		alldf += 1;
		String oldw = null;
		for (String w : document) {

			if (oldw != null) {
				String key = makekey(oldw, w);
				Integer cnt = cooccurence.get(key);
				if (cnt == null) {
					cnt = 0;
				}
				cooccurence.put(key, cnt + 1);
			}
			oldw = w;
			String num = convert(w);
			if (num.length() == 0)
				continue;

			HashSet<String> conti = hashIndex.get(num);
			if (conti == null) {
				hashIndex.put(num, conti = new HashSet<String>());
			}
			conti.add(w);
			
			
			
		
			

			Integer cnt = freq.get(w);
			if (cnt == null) {
				cnt = 1;
			}
			freq.put(w, cnt + 1);
			allfreq += 1;
			bag.add(w);

		}

		for (String word : bag) {
			Integer cnt = df.get(word);
			if (cnt == null) {
				cnt = 0;
			}
			df.put(word, cnt + 1);

		}

	}

	/**
	 * Converts a text into a number as specified by given mapping
	 * 
	 * @param w
	 * @return
	 */
	public String convert(String w) {
		StringBuilder sb = new StringBuilder();
		for (char c : w.toCharArray()) {
			Integer i = mapper.get(c);
			if (i != null)
				sb.append(i);
		}
		return sb.toString();
	}

	/**
	 * Returns number of co-occurences of this two strings in the corpora used
	 * in the model
	 * 
	 * @param string1
	 * @param string2
	 * @return
	 */
	public Integer getCoocurrentsScore(String string1, String string2) {
		String key = makekey(string1, string2);
		return cooccurence.get(key);
	}

	public void read(InputStream loader) throws IOException, ClassNotFoundException {

		ObjectInputStream ois = new ObjectInputStream(loader);
		hashIndex = (HashMap<String, HashSet<String>>) ois.readObject();
		mapper = (HashMap<Character, Integer>) ois.readObject();
		cooccurence = (HashMap<String, Integer>) ois.readObject();
		df = (HashMap<String, Integer>) ois.readObject();
		alldf = (Integer) ois.readObject();
		freq = (HashMap<String, Integer>) ois.readObject();
		allfreq = (Integer) ois.readObject();
		ois.close();

	}
}
