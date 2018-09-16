import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class IO {
	
	public static String[] readFile(File path) throws IOException {
		FileReader file = new FileReader(path.getPath());
		BufferedReader buffer = new BufferedReader(file);
		int lineCount = countLines(path);
		String lines[] = new String[lineCount];
		
		for (int i = 0; i < lineCount; i++) {
			lines[i] = buffer.readLine();
		}
		
		buffer.close();
		return lines;
	}
	
	private static int countLines(File path) throws IOException {
		FileReader file = new FileReader(path.getPath());
		BufferedReader buffer = new BufferedReader(file);
		int lineCount = 0;
		
		while (buffer.readLine() != null) {
			lineCount++;
		}
		
		buffer.close();
		return lineCount;
	}
	
	public static void writeLines(File path, String data) throws IOException {
		FileWriter file = new FileWriter(path.getPath());
		BufferedWriter buffer = new BufferedWriter(file);
		
		buffer.write(data);
		buffer.newLine();	
		buffer.close();
	}
	
	public static Collection<File> scanDirectory(File directory) {
		RegexFileFilter fileFilter = new RegexFileFilter("^(.*?)"); 
		Collection<File> files = FileUtils.listFiles(directory, fileFilter, DirectoryFileFilter.DIRECTORY);
		return files;
	}
	
	public static Map<Integer, String> extractData(Collection<File> files) 
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		Iterator<File> fileIterator = files.iterator();
		Map<Integer, String> data = new LinkedHashMap<Integer, String>();
			
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
			
		while (fileIterator.hasNext()) {
			Document document = builder.parse(fileIterator.next());
			XPath xPath = XPathFactory.newInstance().newXPath();
			Node nodeID = (Node) xPath.evaluate("/DOC/DOCNO", document, XPathConstants.NODE);
			Node node = (Node) xPath.evaluate("/DOC/TEXT", document, XPathConstants.NODE);
			data.put(Integer.parseInt(nodeID.getTextContent()
					.replaceAll("(?m)^[ \t]*\r?\n", "")
					.replaceAll("\n", " ")
					.replaceAll("\\s\\s+", " ")
					.trim()), 
					node.getTextContent()
					.replaceAll("(?m)^[ \t]*\r?\n", "")
					.replaceAll("\n", " ")
					.replaceAll("\\s\\s+", " ")
					.trim().toLowerCase());
		}
		
		return data;
	}
}
