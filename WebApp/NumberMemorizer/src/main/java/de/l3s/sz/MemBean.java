package de.l3s.sz;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import de.l3s.sz.memorizer.Indexer;
import de.l3s.sz.memorizer.MEMOMAPPING;
import de.l3s.sz.memorizer.Result;
import de.l3s.sz.text.AnnotatedtextCorpora;
import de.l3s.sz.text.DocumentParser;
import de.l3s.sz.text.LibRuParser;
import de.l3s.sz.text.ReutersParser;
import de.l3s.sz.text.Twitter1Parser;

@ManagedBean(name = "mem", eager = true)
@ViewScoped
public class MemBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6099751803265683564L;

	static private HashMap<String, AnnotatedtextCorpora> corporas = null;

	HashMap<String, String> mappingstrings = null;
	String mappingstring = MEMOMAPPING.ENGLISH1;

	Indexer indexer = null;

	public Set<String> getIndexers() {
		return corporas.keySet();
	}

	public Set<String> getMappingstrings() {
		return mappingstrings.keySet();
	}

	public String getMappingstring() {
		return mappingstring;
	}

	public void setMappingstring(String mappingstring) {
		this.mappingstring = mappingstring;
	}

	public String getCurrentmapping() {
		if (mappername == null) {
			mappername = "English (Reuters Corpora)";
		}

		if(indexer==null)
		{
			updateModel();
		}
		HashMap<Character, Integer> map = indexer.getMapper();
		HashMap<Integer, List<Character>> imap = new HashMap<>();
		for (int i = 0; i < 10; i++) {
			imap.put(i, new ArrayList<Character>());
		}
		for (Character c : map.keySet()) {
			imap.get(map.get(c)).add(c);
		}
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 10; i++) {
			sb.append(i);
			sb.append(":");
			for (Character c : imap.get(i)) {
				sb.append(c);
			}
			if (i < 9) {
				sb.append(",");
			}
		}
		return sb.toString();

	}

	String mappername;

	public void updateModel() {
		indexer = new Indexer(mappingstring, corporas.get(mappername));
		System.out.println("indexer updated");

		if (!mappingstrings.containsKey(mappingstring)) {
			mappingstrings.put(mappingstring, mappingstring);
		}

	}

	public String getMappername() {
		return mappername;
	}

	public void updateSettings() {
		int z = 0;
		z++;
	}

	public void setMappername(String mappername) {
		this.mappername = mappername;
	}

	Integer mindf = 5;
	Integer minfreq = 5;

	public Integer getMindf() {
		return mindf;
	}

	public void setMindf(Integer mindf) {
		this.mindf = mindf;
	}

	public Integer getMinfreq() {
		return minfreq;
	}

	public void setMinfreq(Integer minfreq) {
		this.minfreq = minfreq;
	}

	List<Result> results = new ArrayList<Result>();


	
	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	String number;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}


	public void map() {

		results.clear();
		int cnt = 0;
		Iterator<Result> it = indexer.query(number, mindf, minfreq, false);
		while (it.hasNext()) {
			results.add(it.next());
			cnt++;
			if (cnt > 50)
				break;
		}

	
	}

	public MemBean() {

		if (corporas == null) {
			try {
				corporas = new HashMap<>();

				InputStream loader = getClass().getClassLoader()
						.getResourceAsStream("de/l3s/sz/memorizer/models/mapper_standard_libru.ser");

				AnnotatedtextCorpora rtexts;

				try {
					rtexts = createCorpora(loader);
					corporas.put("Russian (Lib.ru Corpora)", rtexts);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loader.close();

				loader = getClass().getClassLoader()
						.getResourceAsStream("de/l3s/sz/memorizer/models/mapper_standard_reuters.ser");
				AnnotatedtextCorpora etexts;
				try {
					etexts = createCorpora(loader);
					corporas.put(mappername = "English (Reuters Corpora)", etexts);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				loader.close();

				loader = getClass().getClassLoader()
						.getResourceAsStream("de/l3s/sz/memorizer/models/mapper_standard_twitter.ser"); // File
																										// model
																										// =
																										// new
				AnnotatedtextCorpora ttexts=null;
				try {
					ttexts = createCorpora(loader);
					corporas.put("English (Twitter Corpora)", ttexts);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loader.close();
				
				
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mappingstrings=new HashMap<>();
			mappingstrings.put(MEMOMAPPING.ENGLISH1, MEMOMAPPING.ENGLISH1);
			mappingstrings.put(MEMOMAPPING.ENGLISHD, MEMOMAPPING.ENGLISHD);
			mappingstrings.put(MEMOMAPPING.RUSSIAN1, MEMOMAPPING.RUSSIAN1);
			updateModel();
		}

	}

	public static AnnotatedtextCorpora createCorpora(InputStream loader) throws IOException, ClassNotFoundException {
		AnnotatedtextCorpora texts = AnnotatedtextCorpora.read(loader);

		
		return texts;
	}

}