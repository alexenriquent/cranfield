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
		
		InvertedIndex invertedIndex = new InvertedIndex();
		
		for (int i = 0; i < data.size(); i++) {
			invertedIndex.generate(i + 1, data.get(i), "[a-z0-9]+");
		}
		
		invertedIndex.removeStopwords(IO.readFile(stopwordsPath));
		
		System.out.println(invertedIndex.getInvertedIndex());
	}
}