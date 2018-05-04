package de.l3s.sz.memorizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import de.l3s.sz.lib.BreakString;

public class ResultIterator implements Iterator<Result> {

	Indexer indexer;
	String numstr;

	private BreakString bs;
	private boolean fullinfo;
	private MyComparator dfcomparator;
	private MyComparator freqcomparator;

	public ResultIterator(Indexer indexer, String numstr, BreakString bs, boolean fullinfo) {
		super();
		this.indexer = indexer;
		this.numstr = numstr;
		this.bs = bs;
		this.fullinfo = fullinfo;

		dfcomparator = new MyComparator(indexer.df);
		freqcomparator = new MyComparator(indexer.freq);

		init();
	}

	public ResultIterator(Indexer indexer, String numstr, BreakString bs) {
		this(indexer, numstr, bs, true);
	}

	int posindex = 0;
	List<List<String>> possibilities;

	List<Result> curbatch;
	int batchindex = 0;

	private void init() {
		possibilities = bs.split(numstr);
		Collections.shuffle(possibilities);

		Collections.sort(possibilities, new Comparator<List<String>>() {

			@Override
			public int compare(List<String> o1, List<String> o2) {
				// TODO Auto-generated method stub
				return o1.size() - o2.size();
			}
		});
		posindex = 0;
		loadbatch();
	}

	private boolean allEmpty(ArrayList<HashSet<String>> used) {

		for (HashSet<String> s : used) {
			if (s.size() > 0) {
				return false;
			}
		}
		return true;
	}

	private void loadbatch() {

		List<Result> ret = new ArrayList<>();

		nextpossibility: for (; posindex < possibilities.size(); posindex++) {
			List<String> possibility = possibilities.get(posindex);

			ArrayList<HashSet<String>> table = new ArrayList<>();
			ArrayList<HashSet<String>> used = new ArrayList<>();

			for (String s : possibility) {
				HashSet<String> stack = new HashSet<>();
				HashSet<String> alternatives = indexer.getStringFromNumber(s);
				if (alternatives == null || alternatives.size() == 0)
					continue nextpossibility;
				stack.addAll(alternatives);
				table.add(stack);

				HashSet<String> stack2 = new HashSet<>();
				stack2.addAll(stack);
				used.add(stack2);
			}

			int max = 0;

			int connectivity = 0;
			double cscore = 0.0;
			HashSet<String> start = used.get(max);
			ArrayList<String> words = new ArrayList<>(start);

			Collections.sort(words, freqcomparator);
			Collections.sort(words, dfcomparator);
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

					Collections.sort(randlist, freqcomparator);
					Collections.sort(randlist, dfcomparator);

					for (String s : randlist) {

						Integer score = indexer.getCoocurrentcScore(prev, s);
						if (score == null)
							score = 0;

						if (score > maxscore) {
							maxscore = score;
							winner = s;
						}
					}

					connectivity += maxscore;
					if (connectivity > 0) {
						cscore += 1. / connectivity;
					}
					used.get(i).remove(winner);
					postfix.add(winner);
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

				double score = 0;
				if (cscore != 0) {
					score = Math.round((used.size() / cscore) * 10000) / 10000.;
				}
				ret.add(new Result(score, possibility.toString(), sb.toString().trim()));

			}

			if (fullinfo) {
				used.get(0).clear();
				int maxsize = Integer.MIN_VALUE;
				for (int i = 1; i < used.size(); i++) {
					if (used.get(i).size() > maxsize) {
						maxsize = used.get(i).size();
						max = i;
					}
				}

				ArrayList<ArrayList<String>> restwords = new ArrayList<>();

				for (int i = 0; i < used.size(); i++) {
					restwords.add(new ArrayList<String>());
				}
				for (int i = 0; i < used.size(); i++) {

					int cnt = 0;
					for (String s : used.get(i)) {

						ArrayList<String> line = restwords.get(i);
						line.add(s);
						cnt++;
					}

					while (cnt < maxsize) {
						restwords.get(i).add(" -- ");
						cnt++;
					}

				}

				for (ArrayList<String> l : restwords) {
					Collections.sort(l, freqcomparator);
					Collections.sort(l, dfcomparator);
				}

				for (int i = 0; i < maxsize; i++) {
					StringBuilder sb = new StringBuilder();
					for (int y = 0; y < restwords.size(); y++) {

						String s = restwords.get(y).get(i);
						sb.append(s);
						sb.append(" ");

					}
					ret.add(new Result(-1, possibility.toString(), sb.toString().trim()));
				}
			}

			break;
		}

		Collections.sort(ret, new Comparator<Result>() {

			@Override
			public int compare(Result o1, Result o2) {
				// TODO Auto-generated method stub
				return Double.compare(o2.score, o1.score);
			}
		});
		curbatch = ret;
		batchindex = 0;
	}

	private void loadbatchF() {

		List<Result> ret = new ArrayList<>();

		nextpossibility: for (; posindex < possibilities.size(); posindex++) {
			List<String> possibility = possibilities.get(posindex);

			ArrayList<HashSet<String>> table = new ArrayList<>();
			ArrayList<HashSet<String>> used = new ArrayList<>();

			for (String s : possibility) {
				HashSet<String> stack = new HashSet<>();
				HashSet<String> alternatives = indexer.getStringFromNumber(s);
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

							Integer score = indexer.getCoocurrentcScore(prev, s);
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
							Integer score = indexer.getCoocurrentcScore(s, prev);

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

					ret.add(new Result(connectivity, possibility.toString(), sb.toString().trim()));

				}

			}
			break;
		}

		curbatch = ret;
		batchindex = 0;
	}

	boolean asked = false;
	Result r = null;

	@Override
	public boolean hasNext() {

		if (!asked) {
			r = loadNext();
			asked = true;
		}
		if (r == null)
			return false;
		return true;
	}

	private Result loadNext() {

		Result r = null;
		if (batchindex < curbatch.size()) {

			r = curbatch.get(batchindex);
		} else {
			if (posindex < possibilities.size()) {
				loadbatch();
			}
			posindex++;
			if (posindex > possibilities.size()) {
				return null;
			}
			return loadNext();
		}
		batchindex++;
		return r;

	}

	@Override
	public Result next() {

		if (asked) {

			asked = false;
			return r;
		}

		r = loadNext();
		return r;

	}

}
