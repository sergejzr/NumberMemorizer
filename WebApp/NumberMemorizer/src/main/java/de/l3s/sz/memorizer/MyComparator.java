package de.l3s.sz.memorizer;

import java.util.Comparator;
import java.util.HashMap;

public class MyComparator implements Comparator <String>{

	private HashMap<String, ? extends Number> scores;

	public MyComparator(HashMap<String,? extends Number> scores) {
		this.scores=scores;
	}

	@Override
	public int compare(String o1, String o2) {
		// TODO Auto-generated method stub
		return -1*Double.compare(scores.get(o1).doubleValue(), scores.get(o2).doubleValue());
	}
	

}
