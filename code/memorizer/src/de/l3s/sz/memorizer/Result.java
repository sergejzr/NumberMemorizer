package de.l3s.sz.memorizer;

public class Result {
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


}
