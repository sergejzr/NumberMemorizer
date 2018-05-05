package de.l3s.sz.memorizer;

public class Filter {
	int mindf; int minfreq;

	public Filter(int mindf, int minfreq) {
		super();
		this.mindf = mindf;
		this.minfreq = minfreq;
	}
	public int getMindf() {
		return mindf;
	}
	public int getMinfreq() {
		return minfreq;
	}

}
