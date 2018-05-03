# NumberMemorizer
Converts numbers into unique words/sentences and helps this way to memorize long numbers (Major System or Consonant System).

There are a few recommended approaches ho to memorize large numbers (checkout links [1,2]). These algorithms helps to use so called "Major System" or "Consonant System". The idea - (1) assign consonants to each digit (2) convert a number to consonat sequence and (3) try to add vocals such as a sequence of consonant becomes a sequence of words. 

Simple usage of the given library, first init the mapper (running demo is in de.l3s.sz.main.MemorizerDemo):

    //create an indexer using specific number - letter mapping as follows
    Indexer idx=new Indexer("0:zsc,1:dt,2:n,3:m,4:r,5:l,6:jg,7:kq,8:fv,9:bp");

    //fill indexer with text
    for(String text:texts)
    idx.add(text);

    //save model, if nesseccery for further use
    idx.save(new File("mymodel.ser"));

Than iterate over possible word combinations*

    String num = "84957218383";
    Iterator<Result> it = idx.result(num, true);
		
		while(it.hasNext())
		{
			Result r = it.next();
			System.out.println(r.getScore()+"\t"+r.getSplit()+"\t"+r.getSentence());
		}

* We can not build and evaluate all possible combination of possible words in the lists. In the full mode (idx.result(num, true)) we instead get most promising combinations first and print othe rpossibilities with -- as word placeholders. Example:

    idx.result("81459161", true);
    
    //higly scored results first
    90.0	[8145, 9161]	federal budget
    90.0	[8145, 9161]	federal budget
    1.5	[814, 59, 161]	after libya thought
    0.0	[814, 59, 161]	footwear leap hedged
    0.0	[814, 59, 161]	feature lab digit
    0.0	[814, 59, 161]	feeder help edged
    0.0	[814, 59, 161]	future lap tight
    0.0	[814, 59, 161]	father lip tgt
    
    //rest with placeholders. -- can be replaces as any word in previous column with same splitting [814, 59, 161]
    -1.0	[814, 59, 161]	--  alpha outweighed
    -1.0	[814, 59, 161]	--  elpa  --
    -1.0	[814, 59, 161]	--  lbo  -- 


Some references wth details about large numbers memorisation:
[1] https://www.quickanddirtytips.com/education/math/how-to-memorize-numbers-part-1
[2] https://www.wikihow.com/Memorize-Numbers

