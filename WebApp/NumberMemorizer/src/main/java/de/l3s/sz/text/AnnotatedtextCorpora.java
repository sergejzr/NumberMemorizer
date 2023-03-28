package de.l3s.sz.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class AnnotatedtextCorpora implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Integer getDf(String word) {
		return df.get(word);
	}

	public Integer getTf(String word) {
		return tf.get(word);
	}

	int cntdocs = 0;
	int cntterms = 0;
	Integer maxdf = null, maxfreq = null;

	HashSet<String> dict = new HashSet<>();

	public Integer getMaxdf() {

		if (maxdf == null) {
			Integer ret = Integer.MIN_VALUE;
			for (Integer curdf : df.values()) {
				if (curdf > ret) {
					ret = curdf;
				}
			}

		}
		return maxdf;
	}

	public Integer getMaxTf() {

		if (maxdf == null) {
			Integer ret = Integer.MIN_VALUE;
			for (Integer curdf : tf.values()) {
				if (curdf > ret) {
					ret = curdf;
				}
			}

		}
		return maxdf;
	}

	private HashSet<Character> consonants;

	public AnnotatedtextCorpora(HashSet<Character> consonants) {
		this.consonants = new HashSet<>(consonants);
	}

	public AnnotatedtextCorpora(String consonants) {
		this.consonants = new HashSet<>();

		for (Character c : consonants.toCharArray())
			this.consonants.add(c);
	}

	public HashMap<String, Integer> tf = new HashMap<>();
	public HashMap<String, Integer> df = new HashMap<>();

	HashMap<String, HashMap<String, Integer>> sequence = new HashMap<>();
	HashMap<String, HashMap<String, Integer>> isequence = new HashMap<>();

	public HashMap<String, HashSet<String>> index = new HashMap<>();

public	Integer sequenceScore(String str1, String str2) {
		return getDouble(str1, str2, sequence);

	}

	private Integer getDouble(String str1, String str2, HashMap<String, HashMap<String, Integer>> sequence) {

		HashMap<String, Integer> conti = sequence.get(str1);
		if (conti == null)
			return 0;

		Integer cnt = conti.get(str2);

		if (cnt == null)
			return 0;
		return cnt;

	}

	private StringBuilder getKey(String str1, String str2) {
		StringBuilder sb = new StringBuilder();
		sb.append(str1);
		sb.append(" ");
		sb.append(str2);
		return sb;
	}

	public void cleanStats() {
		HashSet<String> tmp = new HashSet<>(df.keySet());

		for (String s : tmp) {
			if (!dict.contains(s)) {
				tf.remove(s);
				df.remove(s);
			}
		}
	}

	/**
	 * Expect a list of paragraphs,
	 * 
	 * @param paragraphs
	 */
	public void addTextList(List<String> paragraphs) {
		cntdocs++;
		HashSet<String> bagofwords = new HashSet<>();

			for (String word : paragraphs) {
				Integer cnt = tf.get(word);
				if (cnt == null) {
					cnt = 0;
				}
				tf.put(word, cnt + 1);
				bagofwords.add(word);
				setDouble(word, word, sequence, 5);
			}
		

		for (String word : bagofwords) {
			Integer cnt = df.get(word);
			if (cnt == null)
				cnt = 0;
			df.put(word, 5);

			StringBuilder collect = new StringBuilder();

			int maxcntseqconsonants = 0;
			int cntseqconsonants = 0;

			int maxcntseqvocals = 0;
			int cntseqvocalss = 0;

			for (Character c : word.toCharArray()) {

				if (!consonants.contains(c)) {
					if (cntseqconsonants > maxcntseqconsonants) {
						maxcntseqconsonants = 0;
					}
					cntseqconsonants = 0;
					cntseqvocalss++;
					continue;
				} else {
					if (cntseqvocalss > maxcntseqvocals) {
						maxcntseqvocals = cntseqvocalss;
					}
					cntseqvocalss = 0;
					cntseqconsonants++;
				}

				collect.append(c);
			}

			if (maxcntseqconsonants > 4 || maxcntseqvocals > 4) {
				continue;
			}
			String consonantkey = collect.toString();
			if (consonantkey.length() < 2)
				continue;
			HashSet<String> conti = index.get(consonantkey);
			if (conti == null) {
				index.put(consonantkey, conti = new HashSet<>());
			}
			conti.add(word);
			dict.add(word);
		}
	}
	
	
	/**
	 * Expect a list of paragraphs,
	 * 
	 * @param paragraphs
	 */
	public void addTextDocument(List<List<String>> paragraphs) {
		cntdocs++;
		HashSet<String> bagofwords = new HashSet<>();
		for (List<String> paragraph : paragraphs) {

			String prev = null;

			for (String word : paragraph) {
				Integer cnt = tf.get(word);
				if (cnt == null) {
					cnt = 0;
				}
				tf.put(word, cnt + 1);
				bagofwords.add(word);

				if (prev != null) {

					Integer score = getDouble(prev, word, sequence);
					if (score == null)
						score = 0;

					Integer iscore = getDouble(word, prev, isequence);
					if (iscore == null)
						iscore = 0;

					setDouble(prev, word, sequence, score + 1);
					setDouble(word, prev, isequence, iscore + 1);

				}
				prev = word;
			}
		}

		for (String word : bagofwords) {
			Integer cnt = df.get(word);
			if (cnt == null)
				cnt = 0;
			df.put(word, cnt + 1);

			StringBuilder collect = new StringBuilder();

			int maxcntseqconsonants = 0;
			int cntseqconsonants = 0;

			int maxcntseqvocals = 0;
			int cntseqvocalss = 0;

			for (Character c : word.toCharArray()) {

				if (!consonants.contains(c)) {
					if (cntseqconsonants > maxcntseqconsonants) {
						maxcntseqconsonants = 0;
					}
					cntseqconsonants = 0;
					cntseqvocalss++;
					continue;
				} else {
					if (cntseqvocalss > maxcntseqvocals) {
						maxcntseqvocals = cntseqvocalss;
					}
					cntseqvocalss = 0;
					cntseqconsonants++;
				}

				collect.append(c);
			}

			if (maxcntseqconsonants > 4 || maxcntseqvocals > 4) {
				continue;
			}
			String consonantkey = collect.toString();
			if (consonantkey.length() < 2)
				continue;
			HashSet<String> conti = index.get(consonantkey);
			if (conti == null) {
				index.put(consonantkey, conti = new HashSet<>());
			}
			conti.add(word);
			dict.add(word);
		}
	}

	private void setDouble(String str1, String str2, HashMap<String, HashMap<String, Integer>> sequence, int i) {

		HashMap<String, Integer> conti = sequence.get(str1);

		if (conti == null) {
			sequence.put(str1, conti = new HashMap<>());
		}
		Integer cnt = conti.get(str2);

		if (cnt == null) {
			cnt = 0;
		}

		conti.put(str2, cnt + i);

	}

	public void print(int start, int end, int mindf, int mintf) {

		int cnt = 0;
		int cntall = 0;
		int cntskipped = 0;
		HashSet<String> skippedwords = new HashSet<>();
		for (String i : index.keySet()) {
			if (cnt < start)
				continue;
			if (cnt >= end)
				break;

			HashSet<String> terms = index.get(i);

			StringBuilder sb = new StringBuilder();
			for (String s : terms) {
				Integer cdf = df.get(s);
				Integer ctf = tf.get(s);

				if (cdf >= mindf && ctf >= mintf) {
					if (sb.length() > 0) {
						sb.append(", ");
					}
					sb.append(s + "(" + df.get(s) + "," + tf.get(s) + ") ");

				} else {
					cntskipped++;
					skippedwords.add(s + "(" + df.get(s) + "," + tf.get(s) + ") ");
				}

				cntall++;
			}
			if (sb.length() > 0) {
				System.out.print(cnt + ": " + i + "\t[" + sb.toString());

				System.out.println("]");
				cnt++;
			}

		}
		System.out.println(cntskipped + " from " + cntall + " skipped. \n" + skippedwords);
	}

	/**
	 * Save text corpora model to a file
	 * 
	 * @param modelFile
	 * @throws IOException
	 */
	public void save(File modelFile) throws IOException {

		if (index.size() == 0) {
			throw new IOException("no data added to model");
		}
		FileOutputStream fos = new FileOutputStream(modelFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		System.out.printf("Serialized HashMap data is saved in hashmap.ser");

	}

	public static AnnotatedtextCorpora read(InputStream loader) throws IOException, ClassNotFoundException {

		ObjectInputStream ois = new ObjectInputStream(loader);
		AnnotatedtextCorpora ret = (AnnotatedtextCorpora) ois.readObject();

		ois.close();
		return ret;

	}
	
	/**
	 * Read text corpora model from a file
	 * 
	 * @param modelFile
	 * @throws IOException
	 */
	public AnnotatedtextCorpora read(File modelFile) throws IOException {
		try {
			FileInputStream fis = new FileInputStream(modelFile);

			AnnotatedtextCorpora ret = read(fis);

			fis.close();
			return ret;

		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			return null;
		}
	}
	

}
