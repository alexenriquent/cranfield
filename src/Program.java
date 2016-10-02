import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

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
		
		Map<Integer, String> data = IO.extractData(files);
				
		VectorSpaceModel vectorSpaceModel = new VectorSpaceModel(data, IO.readFile(stopwordsPath));
		
		vectorSpaceModel.createVectorSpaceModel();
		vectorSpaceModel.normalise();
		
//		System.out.println(vectorSpaceModel.getDictionary());
				
//		for (Entry<Integer, Double[]> entry : vectorSpaceModel.getVectorSpaceModel().entrySet()) {
//			System.out.println(Arrays.toString(entry.getValue()));
//		}
		
//		String keyword[] = {"pressure", "flow", "theory"};
//		
//		double s1 = System.nanoTime();
//		Map<Integer, Double> document1 = vectorSpaceModel.sort(vectorSpaceModel.search(keyword));
//		List<Integer> topDocuments1 = vectorSpaceModel.topDocuments(document1);
//		double e1 = System.nanoTime();
//		System.out.println(topDocuments1);
//
//		double s2 = System.nanoTime();
//		Map<Integer, Double> document2 = vectorSpaceModel.sort(vectorSpaceModel.searchInvertedIndex(keyword));
//		List<Integer> topDocuments2 = vectorSpaceModel.topDocuments(document2);
//		double e2 = System.nanoTime();
//		System.out.println(topDocuments2);
//		
//		System.out.println((e1 - s1) / 10e5);
//		System.out.println((e2 - s2) / 10e5);
//		System.out.println();

		while (true) {
			System.out.println("\n(1) Search using the vector space model");
			System.out.println("(2) Search using the vector space model and inverted index\n");
			System.out.println("Type #quit to exit.\n");
			System.out.print("> ");
			String option = scanner.nextLine();
			
			if (option.equalsIgnoreCase("#quit")) break;
			
			String keywords[];
			Map<Integer, Double> documents;
			List<Integer> topDocuments;
			
			switch (option) {
				case "1":
					printInstructions();
					System.out.print("> ");
					String query1 = scanner.nextLine();
					
					if (query1.equalsIgnoreCase("#back")) break;
					
					keywords = query1.toLowerCase().split("\\s+");
					documents = vectorSpaceModel.sort(vectorSpaceModel.search(keywords));
					topDocuments = vectorSpaceModel.topDocuments(documents);
					printResults(query1, documents, topDocuments);
					break;
				case "2":
					printInstructions();
					System.out.print("> ");
					String query2 = scanner.nextLine();
					
					if (query2.equalsIgnoreCase("#back")) break;
					
					keywords = query2.toLowerCase().split("\\s+");
					documents = vectorSpaceModel.sort(vectorSpaceModel.search(keywords));
					topDocuments = vectorSpaceModel.topDocuments(documents);
					printResults(query2, documents, topDocuments);
					break;
				default:
					System.out.println("\nInvalid option.");
					break;
			}			
		}
	}
	
	public static void printInstructions() {
		System.out.println("\nEnter a search query after the command prompt >");
		System.out.println("Type #back to go back.\n");
	}
	
	public static void printResults(String query, Map<Integer, Double> documents, 
									List<Integer> topDocuments) {
		if (!topDocuments.isEmpty()) {
			if (topDocuments.size() == 1) {
				System.out.println(topDocuments.size() + " from " + documents.size() 
												+ " result: ");
			} else {
				System.out.println(topDocuments.size() + " from " + documents.size() 
												+ " results: ");
			}
			for (Integer id : topDocuments) {
				System.out.println(id);
			}
		} else {
			System.out.println(topDocuments.size() + " from " + documents.size() 
												+ " result");
			System.out.println("No results containing these search terms were found.");
			System.out.println("The query \"" + query + "\"" + " did not match any documents.");
		}
		System.out.println();
	}
}
