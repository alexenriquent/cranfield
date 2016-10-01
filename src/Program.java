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
		
		System.out.println(vectorSpaceModel.getDictionary());
				
		for (Entry<Integer, Double[]> entry : vectorSpaceModel.getVectorSpaceModel().entrySet()) {
			System.out.println(Arrays.toString(entry.getValue()));
		}
		
		String keywords[] = {"flow", "study"};
		
		System.out.println(vectorSpaceModel.search(keywords));
		
//		printInstructions();
//
//		while (true) {
//			System.out.print("> ");
//			String query = scanner.nextLine();
//			
//			if (query.equalsIgnoreCase("#quit")) break;
//			
//			String keywords[] = query.toLowerCase().split("\\s+");
//		}
	}
	
	public static void printInstructions() {
		System.out.println("\nEnter a search query after the command prompt >");
		System.out.println("Type #quit to exit.\n");
	}
}
