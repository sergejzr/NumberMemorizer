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
import de.l3s.sz.text.AnnotatedtextCorpora;

public class Indexer {

	private HashMap<Character, Integer> mapper = new HashMap<>();

	

	public String getCurrentmapping() {
		return currentmapping;
	}

	
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
	private AnnotatedtextCorpora stats;

	/**
	 * Create Indexer with given personalized mapping. Standard mappings are in
	 * the class MEMOMAPPING.
	 * 
	 * @param map
	 */
	public Indexer(String map, AnnotatedtextCorpora stats) {
		currentmapping = map;
		this.stats=stats;
		
		
		String[] nums = map.split(",");
		for (String num : nums) {
			String pairstr[] = num.split(":");
			for (char c : pairstr[1].toCharArray()) {
				mapper.put(c, Integer.parseInt(pairstr[0].charAt(0) + ""));
			}

		}
		
		
		hashIndex.clear();
		for(String key:stats.index.keySet())
		{
			String num = convert(key);
			HashSet<String> conti = hashIndex.get(num);
			
			if(conti==null)
			{
				hashIndex.put(num, conti=new HashSet<>());
			}
			conti.addAll(stats.index.get(key));
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

	
	public Iterator<Result> query(String number, int mindf, int minfreq, boolean fullinfo)
	{
		
		return new FlexResultIterator(this, number, new BreakString(),  mindf,  minfreq,fullinfo,stats);
		
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
	public Integer getCoocurrentsScore(String str1, String str2) {
		
		return stats.sequenceScore(str1,str2);
	}

	
}
