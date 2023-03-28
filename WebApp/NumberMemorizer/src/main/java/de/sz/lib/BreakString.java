package de.sz.lib;

import java.util.ArrayList;
import java.util.List;

public class BreakString {

	public void breaker(String input, int start, int end, List<String> ans, List<List<String>> all) {
		if (start > end) {
			for (String s : ans) {
				if (s.length() < 2) {
					return;
				}
			}
			ArrayList<String> r = new ArrayList<>();
			r.addAll(ans);
			all.add(r);
		} else {

			ans.add(input.charAt(start) + "");
			breaker(input, start + 1, end, ans, all);

			int listSize = ans.size();
			ans.remove(listSize - 1);
			String lastChar = ans.get(listSize - 2).toString();
			ans.remove(listSize - 2);
			ans.add(lastChar + input.charAt(start) + "");
			breaker(input, start + 1, end, ans, all);
		}
	}

	public List<List<String>> split(String input) {
		List<List<String>> all = new ArrayList<>();
		List<String> ans = new ArrayList<String>();
		ans.add(input.charAt(0) + "");
		breaker(input, 1, input.length() - 1, ans, all);

		return all;
	}

	public static void main(String args[]) {
		String input = "4921345";
		BreakString bs=new BreakString();
		
		List<List<String>> all = bs.split(input);
		
		for(List<String> sentence:all)
		{
			System.out.println(sentence);
		}
		

	}
}