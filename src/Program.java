import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.xml.sax.SAXException;

public class Program {

	public static void main(String[] args) throws ParserConfigurationException, 
											SAXException, XPathExpressionException, IOException {
		
		File directory = new File(args[0]);
		File stopwordsPath = new File(args[1]);
		
		Collection<File> files = IO.scanDirectory(directory);
		List<String> data = IO.extractData(files);
		
		Tokenizer tokenizer = new Tokenizer();

		for (String entry: data) {
			tokenizer.tokenize(entry, "[a-z0-9]+");
		}
				
		tokenizer.sort();
		IO.writeLines(new File("statistics01.txt"), IO.parseOutput(tokenizer));

		tokenizer.removeStopwords(IO.readFile(stopwordsPath));
		IO.writeLines(new File("statistics02.txt"), IO.parseOutput(tokenizer));
	}
}
