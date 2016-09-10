import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.xml.sax.SAXException;

public class Program {

	public static void main(String[] args) throws ParserConfigurationException, 
											SAXException, XPathExpressionException, IOException {
		
		File stopwordsPath = new File(args[0]);
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Enter directory: ");
		String corpus = scanner.nextLine();
		
		File directory = new File(Paths.get("").toAbsolutePath().toString() + "/" + corpus);
		Collection<File> files = IO.scanDirectory(directory);
		List<String> data = IO.extractData(files);
		
		InvertedIndex invertedIndex = new InvertedIndex();
		
		for (int i = 0; i < data.size(); i++) {
			invertedIndex.generate(i + 1, data.get(i), "[a-z0-9]+");
		}
		
		invertedIndex.removeStopwords(IO.readFile(stopwordsPath));
		
		System.out.println("\nEnter a search query after the command prompt >");
		System.out.println("Type #quit to exit.\n");
				
		do {
			System.out.print("> ");
			String query = scanner.nextLine();
			
			if (query.equalsIgnoreCase("#quit")) break;
			
			String keywords[] = query.toLowerCase().split("\\s+");
			Map<Integer, Integer> scores = invertedIndex.computeScores(keywords);
			List<Integer> topDocuments = invertedIndex.topDocuments(scores);
			
			if (!topDocuments.isEmpty()) {
				if (topDocuments.size() > 1) {
					System.out.println(topDocuments.size() + " from " + scores.size() + " results: ");
				} else {
					System.out.println(topDocuments.size() + " from " + scores.size() + " result: ");
				}
				for (Integer id : topDocuments) {
					System.out.println(id);
				}
			} else {
				System.out.println("No results containing these search terms were found.");
				System.out.println("The query \"" + query + "\"" + " did not match any documents.");
			}
			System.out.print("\n");
		} while (true);		
	}
}