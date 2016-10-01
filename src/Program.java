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
		
		double s = System.nanoTime();
		
		VectorSpaceModel vectorSpaceModel = new VectorSpaceModel(data, IO.readFile(stopwordsPath));
		
		vectorSpaceModel.createVectorSpaceModel();
		vectorSpaceModel.normalise();
		
		System.out.println(vectorSpaceModel.getDictionary());
				
		for (Entry<Integer, Double[]> entry : vectorSpaceModel.getVectorSpaceModel().entrySet()) {
			System.out.println(Arrays.toString(entry.getValue()));
		}

		double f = System.nanoTime();
		double e = (f - s) / 10e5;
				
		System.out.println(e);
	}
}
