package de.sz.memorizer;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;

public class MyComparator implements Comparator <String>, Serializable{

	private HashMap<String, ? extends Number> scores;

	public MyComparator(HashMap<String,? extends Number> scores) {
		this.scores=scores;
	}

	@Override
	public int compare(String o1, String o2) {
		// TODO Auto-generated method stub
		Number score1=scores.get(o1);
		if(score1==null){score1=0.;}
		Number score2=scores.get(o2);
		if(score2==null){score2=0.;}
		
		return -1*Double.compare(score1.doubleValue(), score2.doubleValue());
	}
	

}
