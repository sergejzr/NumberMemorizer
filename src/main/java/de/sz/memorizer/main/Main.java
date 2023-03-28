package de.sz.memorizer.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.l3s.sz.text.AnnotatedtextCorpora;
import de.sz.crawl.Crawler;
import de.sz.memorizer.Indexer;
import de.sz.memorizer.MEMOMAPPING;
import de.sz.memorizer.Result;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

public class Main {

	private HashMap mappingstrings;
	private HashMap<String, AnnotatedtextCorpora> corporas;
	private Options options;
	private String configuration;

	public static AnnotatedtextCorpora createCorpora(InputStream loader) throws IOException, ClassNotFoundException {
		AnnotatedtextCorpora texts = AnnotatedtextCorpora.read(loader);
		return texts;
	}

	AnnotatedtextCorpora getCorpora(String name) {
		return corporas.get(name);
	}

	public Set<String> getCorporaNames() {
		return corporas.keySet();
	}

	public Indexer getIndexer(String mappingstring, String mappername) {
		Indexer indexer = new Indexer(mappingstring, corporas.get(mappername));
		return indexer;
	}

	private void readOptions(CommandLine cmd) throws ParseException, ClassNotFoundException, IOException {

		String command = "find";

		if (cmd.getOptionValue("find") != null && cmd.getOptionValue("create") != null) {
			throw new ParseException("You can not use \"find\" and \"create\" at the same time!");
		}
		if (cmd.getOptionValue("create") != null) {
			command = "create";
		}
		switch (command) {
		case "find":

			if (cmd.getOptionValue("corpora_file") != null && cmd.getOptionValue("corpora_name") != null) {
				throw new ParseException(
						"You can either use corpora_name, or corpora_file. Nit both at the same time!");
			}

			if (cmd.getOptionValue("corpora_file") != null
					&& !(new File(cmd.getOptionValue("corpora_file"))).exists()) {
				throw new ParseException(
						"Corpora file you specified does not exist! " + cmd.getOptionValue("corpora_file"));
			}

			String corpora_name = cmd.getOptionValue("corpora_name", "en_reuters");

			String mapping = cmd.getOptionValue("mapping");

			switch (corpora_name) {
			case "en_twitter":
			case "en_reuters":
				if (mapping == null) {
					mapping = MEMOMAPPING.ENGLISH1;
				}
				break;
			case "ru_libru":
			case "ru_unislov":
			case "ru_pelevin":
				if (mapping == null) {
					mapping = MEMOMAPPING.RUSSIAN1;
				}
				break;
			}
			String corpora_file = cmd.getOptionValue("corpora_file");
			String inputnumber = cmd.getOptionValue("inputnumber");

			find(inputnumber, corpora_file, corpora_name, mapping);

			break;
		case "create":
			String directory = cmd.getOptionValue("directory");

			if (directory == null) {
				throw new ParseException("Input directory not specified!");
			}
			if (!(new File(directory)).exists()) {
				throw new ParseException("Input directory does not exists! " + directory);
			}
			if ((new File(directory)).list().length == 0) {
				throw new ParseException("Input directory empty! " + directory);
			}

			if (cmd.getOptionValue("consonants") == null || cmd.getOptionValue("consonants").trim().isEmpty()) {
				throw new ParseException("\"consonants\" can not be empty!");
			}
			String output = cmd.getOptionValue("output", "out.ser");

			create(directory, cmd.getOptionValue("consonants"), output);
			break;
		}

		String inputdirstr = cmd.getOptionValue("create");

	}

	private void create(String directory, String consonants, String output) throws IOException {

		AnnotatedtextCorpora corpora = new AnnotatedtextCorpora(consonants);

		for (File f : new File(directory).listFiles()) {
			String res = FileUtils.readFileToString(f, Charset.forName("UTF-8"));
			res = res.replaceAll("[^\\p{Alnum}\\p{javaLowerCase}\\p{javaUpperCase}]", " ").replaceAll("\\s+", "\\s")
					.trim();
			List<String> slist = new ArrayList<>();

			slist.addAll(Arrays.asList(res.split("\\s")));
			corpora.addTextList(slist);

		}

		corpora.save(new File(output));

	}

	private void find(String inputnumber, String corpora_file, String corpora_name, String mapping)
			throws ClassNotFoundException, IOException {

		String modelname = "";

		InputStream loader;

		if (corpora_file != null) {
			loader = new FileInputStream(corpora_file);
		} else {

			switch (corpora_name) {
			case "en_reuters":
				modelname = "reuters.ser";
				break;
			case "en_twitter":
				modelname = "twitter.ser";
				break;
			case "ru_libru":
				modelname = "libru.ser";
				break;
			case "ru_unislov":
				modelname = "unislov.ser";
				break;
			case "ru_pelevin":
				modelname = "pelevin.ser";
				break;

			}
			loader = getClass().getClassLoader()
					.getResourceAsStream("de/l3s/sz/memorizer/models/mapper_standard_" + modelname);
		}

		AnnotatedtextCorpora rtexts = createCorpora(loader);
		loader.close();

		Indexer indexer = new Indexer(mapping, rtexts);

		System.out.println(mapping);

		Iterator<Result> it = indexer.query(inputnumber, 1, 1, true);

		while (it.hasNext()) {
			Result r = it.next();
			System.out.println(r.getScore() + "\t" + r.getSplit() + "\t" + r.getSentence());
		}

	}

	public Main() {

		// create Options object
		options = new Options();
		// add t option
		options.addOption(Option.builder().longOpt("find").hasArg(false).desc(
				"It is the default command and would find woird(s) for a given number. Can not be used together with \"create\"")
				.required(false).build());

		options.addOption(Option.builder().longOpt("create").hasArg(false).desc(
				"Only to be used with \"create\". Creates a corpora from a set of text files. Can not be used together with \"find\"")
				.required(false).build());

		options.addOption(Option.builder().longOpt("directory").hasArg(false).desc(
				"Only to be used with \"create\". A directory with text files to read from. All files in the directory are assumed to be text files in UTF-8. Anything except letters is ignored.")
				.required(false).build());

		options.addOption(Option.builder().longOpt("output").hasArg(false)
				.desc("Only to be used with \"create\". Output file name for the corpora object. Default \"out.ser\"")
				.required(false).build());
		options.addOption(Option.builder().longOpt("consonants").hasArg(false)
				.desc("Only to be used with \"create\". The list of consonants in the alphabet of the coprpora. e.g. bcdfghjklmnpqrstvwxyz ")
				.required(false).build());
		
		options.addOption(Option.builder().longOpt("mapping").hasArg(true).desc(
				"Only to be used with \"find\". Input mapping string. Default for english corpora: \"0:sc,1:dt,2:zn,3:mw:,4:rh,5:lv,6:jb,7:kq,8:fx,9:pgq\", for russian corpora \"0:н,1:т,2:дк,3:зжм,4:чц,5:пл,6:шщб,7:сг,8:вфх,9:р\" ")
				.required(false).build());

		options.addOption(Option.builder().longOpt("corpora_name").hasArg().desc(
				"Only to be used with \"find\". There are built-in corporas: en_reuters, en_twitter, ru_libru, ru_unislov, ru_pelevin. default is en_reuters")
				.required(false).build());

		options.addOption(Option.builder().longOpt("corpora_file").hasArg().desc(
				"Only to be used with \"find\". In case you created your own corpora and exported it into a file, here comes the file path (should be absolute)")
				.required(false).build());

		options.addOption(Option.builder().longOpt("inputnumber").hasArg()
				.desc("Only to be used with \"find\". Your input number to remember").required(false).build());

	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {

		Main m = new Main();
		if (args.length == 1 && args[0].trim().matches("\\d+")) {
			args = new String[] { "--inputnumber", args[0].trim() };
		}

		m.runProgramm(args);
	}

	private void runProgramm(String[] args) throws ClassNotFoundException, IOException {

		this.configuration = String.join(" ", args);
		DefaultParser parser = new DefaultParser();

		if (args.length > 0)
			try {
				readOptions(parser.parse(options, args));
				return;
			} catch (ParseException e) {
				System.err.println(e.getMessage());

			}
		HelpFormatter formatter = new HelpFormatter();

		formatter.printHelp("Simplest: java -jar " + Tool.jarName(this) + " 12345\n" + "or: java -jar "
				+ Tool.jarName(this) + " [OPTIONS]\n" + "or: java -cp " + Tool.jarName(this) + " "
				+ getClass().getCanonicalName().trim() + " [OPTIONS]\n", options);

	}
}

class Tool {
	public static String jarName(Object obj) {

		return obj.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
	}
}