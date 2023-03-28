package de.sz.memorizer;

import java.io.Serializable;

public class Result implements Serializable{
	/**
	 * 
	 */
	public Result() {
		// TODO Auto-generated constructor stub
	}
	private static final long serialVersionUID = -1424891535500793997L;
	double score;
	String split;
	String sentence;
	public double getScore() {
		return score;
	}
	public String getSplit() {
		return split;
	}
	public String getSentence() {
		return sentence;
	}
	public Result(double score, String split, String sentence) {
		super();
		this.score = score;
		this.split = split;
		this.sentence = sentence;
	}
public void setScore(double score) {
	this.score = score;
}
public void setSentence(String sentence) {
	this.sentence = sentence;
}
public void setSplit(String split) {
	this.split = split;
}
@Override
public int hashCode() {
	// TODO Auto-generated method stub
	return super.hashCode();
}
@Override
public boolean equals(Object obj) {
	// TODO Auto-generated method stub
	return super.equals(obj);
}
@Override
public String toString() {
	// TODO Auto-generated method stub
	return split+"("+score+"): "+sentence ;
}
}
