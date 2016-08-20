import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Program {

	public static void main(String[] args) throws ParserConfigurationException, IOException,
												  XPathExpressionException, SAXException {
		
		File directory = new File(Paths.get("").toAbsolutePath().toString() + "/resource/cranfield");
		RegexFileFilter fileFilter = new RegexFileFilter("^(.*?)"); 
		Collection<File> files = FileUtils.listFiles(directory, fileFilter, DirectoryFileFilter.DIRECTORY);	
		Iterator<File> fileIterator = files.iterator();
		List<String> data = new LinkedList<String>();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		while (fileIterator.hasNext()) {
			Document document = builder.parse(fileIterator.next());
			XPath xPath = XPathFactory.newInstance().newXPath();
			Node node = (Node) xPath.evaluate("/DOC/TEXT", document, XPathConstants.NODE);
			data.add(node.getTextContent().replaceAll("(?m)^[ \t]*\r?\n", "").replaceAll("\n", ""));
		}
		
		try {
			writeLines(Paths.get("").toAbsolutePath().toString() + "/resource/data.txt", data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeLines(String path, List<String> data) throws IOException {
		FileWriter file = new FileWriter(path);
		BufferedWriter buffer = new BufferedWriter(file);
		Iterator<String> iterator = data.iterator();
		
		while (iterator.hasNext()) {
			buffer.write(iterator.next());
			buffer.newLine();
		}
		
		buffer.close();
	}
}
