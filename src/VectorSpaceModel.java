import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class VectorSpaceModel {
	
	private static final int RANK = 1000;
	private int totalDocuments;
	
	private Tokeniser tokeniser;
	private InvertedIndex invertedIndex;
	private List<String> dictionary;
	private Map<Integer, Double[]> vectorSpaceModel;
	
	public VectorSpaceModel(Map<Integer, String> data, String stopwords[]) {
		totalDocuments = data.size();
		initialiseTokeniser(data, stopwords);
		initialiseInvertedIndex(data, stopwords);
		dictionary = tokeniser.topRank(RANK);
		initialiseVectorSpaceModel(data);
	}
	
	private void initialiseTokeniser(Map<Integer, String> data, String stopwords[]) {
		tokeniser = new Tokeniser();
		tokeniser.tokenise(data, "[a-z0-9]+");
		tokeniser.removeStopwords(stopwords);
		tokeniser.sort();
	}
	
	private void initialiseInvertedIndex(Map<Integer, String> data, String stopwords[]) {
		invertedIndex = new InvertedIndex();
		invertedIndex.generate(data, "[a-z0-9]+");
		invertedIndex.removeStopwords(stopwords);
	}
	
	private void initialiseVectorSpaceModel(Map<Integer, String> data) {
		vectorSpaceModel = new LinkedHashMap<Integer, Double[]>();
		Double[] initialVector = new Double[dictionary.size()];
		Arrays.fill(initialVector, 0.0);
		
		for (Entry<Integer, String> entry : data.entrySet()) {
			Double[] vector = new Double[dictionary.size()];
			System.arraycopy(initialVector, 0, vector, 0, dictionary.size());
			vectorSpaceModel.put(entry.getKey(), vector);
		}
	}
	
	public List<String> getDictionary() {
		return dictionary;
	}
	
	public Map<Integer, Double[]> getVectorSpaceModel() {
		return vectorSpaceModel;
	}
	
	public void createVectorSpaceModel() {
		for (int i = 0; i < dictionary.size(); i++) {
			for (Entry<Integer, Integer> entry : invertedIndex.getInvertedIndex().get(dictionary.get(i)).entrySet()) {
				double df = (double) invertedIndex.getInvertedIndex().get(dictionary.get(i)).size();
//				System.out.println("DF = " + df);
				double idf = Math.log10(totalDocuments / df);
//				System.out.println("TF = " + (double) entry.getValue());
//				System.out.println("IDF = " + idf);
//				System.out.println("TF.IDF = " + (entry.getValue() * idf));
				vectorSpaceModel.get(entry.getKey())[i] = ((double) entry.getValue()) * idf;
			}
		}
	}
	
	public void normalise() {
		for (Entry<Integer, Double[]> entry : vectorSpaceModel.entrySet()) {
			double denominator = 0.0;
			for (int i = 0; i < entry.getValue().length - 1; i++) {
					denominator += (entry.getValue()[i] * entry.getValue()[i]);
			}			
			for (int i = 0; i < entry.getValue().length - 1; i++) {
				double normalisedWeight = vectorSpaceModel.get(entry.getKey())[i] / Math.sqrt(denominator);
				vectorSpaceModel.get(entry.getKey())[i] = normalisedWeight;
			}
		}
	}
}
