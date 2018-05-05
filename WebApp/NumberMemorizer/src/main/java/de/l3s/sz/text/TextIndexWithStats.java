package de.l3s.sz.text;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TextIndexWithStats {

	public Integer  getDf(String word) {
		return df.get(word);
	}
	
	public Integer  getTf(String word) {
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

	public TextIndexWithStats(HashSet<Character> consonants) {
		this.consonants = consonants;
	}

	HashMap<String, Integer> tf = new HashMap<>();
	HashMap<String, Integer> df = new HashMap<>();

	HashMap<String, Integer> sequence = new HashMap<>();
	HashMap<String, Integer> isequence = new HashMap<>();

	HashMap<String, HashSet<String>> index = new HashMap<>();

	Integer sequenceScore(String str1, String str2) {
		return getDouble(str1, str2, sequence);

	}

	private Integer getDouble(String str1, String str2, HashMap<String, Integer> sequence) {
		StringBuilder sb = getKey(str1, str2);

		return sequence.get(sb.toString());

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
			}
		}

		for (String word : bagofwords) {
			Integer cnt = df.get(word);
			if (cnt == null)
				cnt = 0;
			df.put(word, cnt + 1);

			StringBuilder collect = new StringBuilder();

			for (Character c : word.toCharArray()) {
				collect.append(c);
				if (!consonants.contains(c)) {
					continue;
				}
			}
			String consonantkey = collect.toString();
			if (consonantkey.length() < 2)
				continue;
			HashSet<String> conti = index.get(consonantkey);
			if (conti == null) {
				conti = new HashSet<>();
			}
			conti.add(consonantkey);
			dict.add(consonantkey);
		}
	}

	private void setDouble(String str1, String str2, HashMap<String, Integer> sequence, int i) {
		StringBuilder sb = getKey(str1, str2);
		sequence.put(sb.toString(), i);

	}

	public TextIndexWithStats() {
		// TODO Auto-generated constructor stub
	}

}
