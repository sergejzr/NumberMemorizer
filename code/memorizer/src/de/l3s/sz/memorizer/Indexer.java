package de.l3s.sz.memorizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.l3s.sz.lib.BreakString;

public class Indexer {

	HashMap<Character, Integer> mapper = new HashMap<>();

	public Indexer() {
		this("0:с,1:т,2:дкн,3:зжм,4:чц,5:пл,6:шщб,7:г,8:вфх,9:р");
	}
	
	

	HashMap<String, Integer> df = new HashMap<>();
	HashMap<String, Integer> freq = new HashMap<>();

	HashMap<String, Integer> cooccurence = new HashMap<>();

	Integer allfreq = 0, alldf = 0;

	HashMap<String, HashSet<String>> hashIndex = new HashMap<>();



	private void print(int start, int end) {

		for (String i : hashIndex.keySet()) {
			System.out.println(i + "\t" + hashIndex.get(i).toString());
		}

	}

	public Indexer(String map) {
		String[] nums = map.split(",");
		for (String num : nums) {
			String pairstr[] = num.split(":");
			for (char c : pairstr[1].toCharArray()) {
				mapper.put(c, Integer.parseInt(pairstr[0].charAt(0) + ""));
			}

		}
	}

	public HashSet<String> getString(String num) {
		return hashIndex.get(num);
	}

	BreakString bs = new BreakString();

	public void addText(String[] document) {

		addText(Arrays.asList(document));
	}

	public List<String> getText(int num) {

		String numstr = "" + num;

		return getText(numstr);
	}

	
	public Iterator<Result> result(String numstr, boolean fullinfo) {
		return new ResultIterator(this, numstr,bs, fullinfo);
	}

	public List<String> getText(String numstr) {
		List<List<String>> possibilities = bs.split(numstr);
		Collections.shuffle(possibilities);

		Collections.sort(possibilities, new Comparator<List<String>>() {

			@Override
			public int compare(List<String> o1, List<String> o2) {
				// TODO Auto-generated method stub
				return o1.size() - o2.size();
			}
		});

		List<String> ret = new ArrayList<>();
		nextpossibility: for (List<String> possibility : possibilities) {

			ArrayList<HashSet<String>> table = new ArrayList<>();
			ArrayList<HashSet<String>> used = new ArrayList<>();

			for (String s : possibility) {
				HashSet<String> stack = new HashSet<>();
				HashSet<String> alternatives = getString(s);
				if (alternatives == null || alternatives.size() == 0)
					continue nextpossibility;
				stack.addAll(alternatives);
				table.add(stack);

				HashSet<String> stack2 = new HashSet<>();
				stack2.addAll(stack);
				used.add(stack2);
			}

			while (!allEmpty(used)) {
				int max = 0;
				int maxsize = Integer.MIN_VALUE;

				for (int i = 0; i < used.size(); i++) {
					if (used.get(i).size() > maxsize) {
						maxsize = used.get(i).size();
						max = i;
					}
				}

				int connectivity = 0;
				HashSet<String> start = used.get(max);
				ArrayList<String> words = new ArrayList<>(start);

				for (String curstr : words) {
					ArrayList<String> prefix = new ArrayList<>();
					ArrayList<String> postfix = new ArrayList<>();

					String prev = curstr;

					for (int i = max + 1; i < used.size(); i++) {
						HashSet<String> conti = used.get(i);
						if (conti.size() == 0) {
							conti = table.get(i);
						}

						int maxscore = Integer.MIN_VALUE;
						String winner = null;
						ArrayList<String> randlist = new ArrayList<>(conti);
						Collections.shuffle(randlist);
						for (String s : randlist) {
							String key = makekey(s, prev);
							Integer score = cooccurence.get(key);
							if (score == null)
								score = 0;

							if (score > maxscore) {
								maxscore = score;
								winner = s;
							}
						}
						connectivity += maxscore;
						used.get(i).remove(winner);
						postfix.add(winner);
						prev = winner;

					}

					prev = curstr;
					for (int i = max - 1; i >= 0; i--) {
						HashSet<String> conti = used.get(i);
						if (conti.size() == 0) {
							conti = table.get(i);
						}

						int maxscore = Integer.MIN_VALUE;
						String winner = null;

						ArrayList<String> randlist = new ArrayList<>(conti);
						Collections.shuffle(randlist);
						for (String s : randlist) {
							String key = makekey(s, prev);
							Integer score = cooccurence.get(key);
							if (score == null)
								score = 0;
							if (score > maxscore) {
								maxscore = score;
								winner = s;
							}
						}
						if (winner == null) {
							int ol = 0;
							ol++;
						}
						connectivity += maxscore;
						used.get(i).remove(winner);
						prefix.add(winner);
						prev = winner;

					}

					StringBuilder sb = new StringBuilder();

					Collections.reverse(prefix);
					for (String s : prefix) {
						sb.append(s);
						sb.append(" ");
					}
					sb.append(curstr);
					sb.append(" ");
					for (String s : postfix) {
						sb.append(s);
						sb.append(" ");
					}

					ret.add(possibility + "(score: " + connectivity + "):\t" + sb.toString().trim());
					if (ret.size() > 20) {
						return ret;
					}

				}

			}
		}

		return ret;

	}

	private boolean allEmpty(ArrayList<HashSet<String>> used) {

		for (HashSet<String> s : used) {
			if (s.size() > 0) {
				return false;
			}
		}
		return true;
	}

	public List<List<List<String>>> getTextO(String numstr) {

		List<List<String>> possibilities = bs.split(numstr);

		List<List<List<String>>> sentences = new ArrayList<>();

		nextpossibility: for (List<String> possibility : possibilities) {
			ArrayList<List<String>> cursentence;

			cursentence = new ArrayList<List<String>>();

			for (String s : possibility) {
				ArrayList<String> words = new ArrayList<String>();

				HashSet<String> res = getString(s);
				if (res == null || res.size() == 0) {
					continue nextpossibility;
				}
				words.addAll(res);
				cursentence.add(words);

			}

			if (cursentence.size() > 0) {

				sentences.add(cursentence);
			}
		}

		return sentences;
	}

	public void getTexts(String numstr, List<List<List<String>>> sentences, List<List<String>> sentence) {
		List<String> altstr = new ArrayList<>();

		for (int i = 2; i < numstr.length(); i++) {
			ArrayList<List<String>> cursentence = new ArrayList<List<String>>();
			String prefix = numstr.substring(0, i);
			HashSet<String> exactnum = getString(prefix);

			if (exactnum == null)
				continue;
			altstr.addAll(exactnum);
			sentence.add(altstr);
			sentences.add(sentence);
			if (i < numstr.length() - 2) {
				getTexts(numstr.substring(i), sentences, cursentence);
			}

		}

	}

	private HashMap<String, Double> rank(List<String> sentences) {

		double factordf = .002, factorfreq = 0.001, factorlength = 100;
		HashMap<String, Double> scores = new HashMap<>();

		for (String sentence : sentences) {
			String[] words = sentence.split(" ");
			int sumdf = 0;
			int sumfreq = 0;
			int sumcoocurrence = 100;
			String oldw = null;
			for (String word : words) {
				if (oldw != null) {
					String key = makekey(word, oldw);
					Integer cnt = cooccurence.get(key);
					if (cnt == null) {
						cnt = 0;
					}
					sumcoocurrence += cnt;

				}
				oldw = word;
				sumdf += df.get(word);
				sumfreq += freq.get(word);
			}

			int length = words.length;

			double score = factordf * (sumdf / (1.0 * length) / alldf)
					+ factorfreq * (sumfreq / (1.0 * length) / allfreq) + factorlength * (1. / (length))
					+ 20 * (sumcoocurrence / (1.0 * length));

			scores.put(sentence, score);
		}

		Collections.sort(sentences, new MyComparator(scores));
		return scores;
	}

	public List<String> collectSentences(List<List<List<String>>> in) {
		List<String> ret = new ArrayList<>();
		for (List<List<String>> sentence : in) {

			ArrayList<String> prefixes = new ArrayList<>();

			prefixes.addAll(sentence.get(0));

			List<String> res = buildrecursive(prefixes, sentence, 1);

			for (String cursentence : res) {

				ret.add(cursentence);

			}
		}

		return ret;
	}

	private List<String> buildrecursive(List<String> prefixes, List<List<String>> sentence, int idx) {

		if (idx + 1 > sentence.size())
			return prefixes;

		List<String> curlist = sentence.get(idx);

		List<String> newprefixes = new ArrayList<>();
		for (String prefix : prefixes) {
			for (String str : curlist) {

				newprefixes.add(prefix + " " + str);
			}
		}

		return buildrecursive(newprefixes, sentence, idx + 1);
	}

	public void read(File f) throws IOException {
		try {
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			hashIndex = (HashMap<String, HashSet<String>>) ois.readObject();
			mapper = (HashMap<Character, Integer>) ois.readObject();
			cooccurence = (HashMap<String, Integer>) ois.readObject();
			df = (HashMap<String, Integer>) ois.readObject();
			alldf = (Integer) ois.readObject();
			freq = (HashMap<String, Integer>) ois.readObject();
			allfreq = (Integer) ois.readObject();
			ois.close();
			fis.close();

			HashSet<String> tmp = new HashSet<>();

			int allcnt = 0;
			int reducecnt = 0;
			for (String s : hashIndex.keySet()) {
				HashSet<String> conti = hashIndex.get(s);

				HashSet<String> copyconti = new HashSet<>(conti);

				for (String sn : copyconti) {
					allcnt++;
					if (df.get(sn) < 4 || freq.get(sn) < 5) {
						conti.remove(sn);
					} else {
						reducecnt++;
					}

				}

			}

			System.out.println("Fullindes: " + allcnt);
			System.out.println("Smallindex: " + reducecnt);

		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
			return;
		}
	}

	public void save(File f) throws IOException {

		FileOutputStream fos = new FileOutputStream(f);
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
		if (s1.compareTo(s2) > 0)
			return s1 + "_" + s2;
		return s2 + "_" + s1;
	}

	public void addText(List<String> document) {

		HashSet<String> bag = new HashSet<>(document);
		alldf += 1;
		String oldw = null;
		for (String w : document) {

			if (oldw != null) {
				String key = makekey(w, oldw);
				Integer cnt = cooccurence.get(key);
				if (cnt == null) {
					cnt = 0;
				}
				cooccurence.put(key, cnt + 1);
			}
			oldw = w;
			StringBuilder sb = new StringBuilder();
			for (char c : w.toCharArray()) {
				Integer i = mapper.get(c);
				if (i != null)
					sb.append(i);
			}
			if (sb.length() == 0)
				continue;
			String num = sb.toString();
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

	public Integer getCoocurrentcScore(String s, String prev) {
		String key = makekey(s, prev);
		return cooccurence.get(key);
	}
}
