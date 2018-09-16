import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.xml.sax.SAXException;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class Program {
	
	public static void main(String[] args) throws ParserConfigurationException, 
											SAXException, XPathExpressionException, IOException {
		
		File stopwordsPath = new File(args[0]);
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Enter directory: ");
		String corpus = scanner.nextLine();
		System.out.println();
		
		File directory = new File(Paths.get("").toAbsolutePath().toString() + "/" + corpus);
		Collection<File> files = IO.scanDirectory(directory);
		
		Map<Integer, String> data = IO.extractData(files);
				
		VectorSpaceModel vectorSpaceModel = new VectorSpaceModel(data, IO.readFile(stopwordsPath));
		
		vectorSpaceModel.createVectorSpaceModel();
		vectorSpaceModel.normalise();

		while (true) {
			printOptions();
			System.out.print("> ");
			String option = scanner.nextLine();
			
			if (option.equalsIgnoreCase("#quit")) break;
			
			String keywords[];
			Map<Integer, Double> documents;
			List<Integer> topDocuments;
			double start, finish, elapsed;
						
			switch (option) {
				case "1":
					printInstructions();
					System.out.print("> ");
					String query1 = scanner.nextLine();
					
					if (query1.equalsIgnoreCase("#back")) break;
					
					start = System.nanoTime();
					keywords = query1.toLowerCase().split("\\s+");
					documents = vectorSpaceModel.sort(vectorSpaceModel.search(keywords));
					topDocuments = vectorSpaceModel.topDocuments(documents);
					finish = System.nanoTime();
					elapsed = (finish - start) / 10e5;
					printResults(query1, documents, topDocuments, elapsed);
					break;
				case "2":
					printInstructions();
					System.out.print("> ");
					String query2 = scanner.nextLine();
					
					if (query2.equalsIgnoreCase("#back")) break;
					
					start = System.nanoTime();
					keywords = query2.toLowerCase().split("\\s+");
					documents = vectorSpaceModel.sort(vectorSpaceModel.searchInvertedIndex(keywords));
					topDocuments = vectorSpaceModel.topDocuments(documents);
					finish = System.nanoTime();
					elapsed = (finish - start) / 10e5;
					printResults(query2, documents, topDocuments, elapsed);
					break;
				default:
					System.out.println("\nInvalid option.\n");
					break;
			}	
		}
	}
	
	public static void printOptions() {
		System.out.println("(1) Search using the vector space model");
		System.out.println("(2) Search using the vector space model and inverted index\n");
		System.out.println("Type #quit to exit.\n");
	}
	
	public static void printInstructions() {
		System.out.println("\nEnter a search query after the command prompt >");
		System.out.println("Type #back to go back.\n");
	}
	
	public static void printResults(String query, Map<Integer, Double> documents, 
									List<Integer> topDocuments, double elapsed) {
		if (!topDocuments.isEmpty()) {
			if (topDocuments.size() == 1) {
				System.out.println(topDocuments.size() + " from " + documents.size() + " result (" + elapsed + " ms):");
			} else {
				System.out.println(topDocuments.size() + " from " + documents.size() + " results (" + elapsed + " ms):");
			}
			for (int i = 0; i < topDocuments.size(); i++) {
				System.out.println("Rank " + (i + 1) + ": Cranfield " + topDocuments.get(i));
			}
		} else {
			System.out.println(topDocuments.size() + " from " + documents.size() + " result (" + elapsed + " ms):");
			System.out.println("No results containing these search terms were found.");
			System.out.println("The query \"" + query + "\"" + " did not match any documents.");
		}
		System.out.println();
	}
}
