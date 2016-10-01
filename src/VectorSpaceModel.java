import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
				double idf = Math.log10(totalDocuments / df);
				vectorSpaceModel.get(entry.getKey())[i] = ((double) entry.getValue()) * idf;
			}
		}
	}
	
	public void normalise() {
		for (Entry<Integer, Double[]> entry : vectorSpaceModel.entrySet()) {
			double denominator = 0.0;
			for (int i = 0; i < entry.getValue().length; i++) {
					denominator += (entry.getValue()[i] * entry.getValue()[i]);
			}			
			for (int i = 0; i < entry.getValue().length; i++) {
				double normalisedWeight = vectorSpaceModel.get(entry.getKey())[i] / Math.sqrt(denominator);
				vectorSpaceModel.get(entry.getKey())[i] = normalisedWeight;
			}
		}
	}
	
	private Double[] normaliseQuery(Double[] vector) {
		Double[] normalisedVector = new Double[vector.length];
		System.arraycopy(vector, 0, normalisedVector, 0, vector.length);
		
		double denominator = 0.0;
		for (int i = 0; i < vector.length; i++) {
			denominator += (vector[i] * vector[i]);
		}
		
		for (int i = 0; i < vector.length; i++) {
			double normalisedWeight = vector[i] / Math.sqrt(denominator);
			normalisedVector[i] = normalisedWeight;
		}
		
		return normalisedVector;
	}
	
	public Map<Integer, Double> search(String keywords[]) {
		Map<Integer, Double> documents = new LinkedHashMap<>();
		List<Integer> indices = new ArrayList<Integer>();

		Double[] normalisedVector = tranformQuery(keywords, indices);

		if (!indices.isEmpty()) {
			for (Entry<Integer, Double[]> entry : vectorSpaceModel.entrySet()) {
				if (relevant(indices, entry.getValue())) {
					double cosine = 0.0;
					for (Integer index : indices) {
						cosine += normalisedVector[index] * entry.getValue()[index];
					}
					documents.put(entry.getKey(), cosine);
				}
			}
		}
		
		return documents;
	}
	
	private boolean relevant(List<Integer> indices, Double[] vector) {
		for (Integer index : indices) {
			if (vector[index] > 0.0) {
				return true;
			}
		}
		return false;
	}
	
	private Double[] tranformQuery(String keywords[], List<Integer> indices) {
		Map<String, Integer> terms = new HashMap<String, Integer>();
		Double[] vector = new Double[dictionary.size()];
		
		Arrays.fill(vector, 0.0);
		
		for (int i = 0; i < keywords.length; i++) {
			if (terms.containsKey(keywords[i])) {
				terms.put(keywords[i], terms.get(keywords[i]) + 1);
			} else {
				terms.put(keywords[i], 1);
			}
		}
		
		if (!terms.isEmpty()) {
			for (Entry<String, Integer> entry : terms.entrySet()) {
				if (dictionary.contains(entry.getKey())) {
					double df = (double) invertedIndex.getInvertedIndex().get(entry.getKey()).size();
					double idf = Math.log10(totalDocuments / df);
					vector[dictionary.indexOf(entry.getKey())] = ((double) entry.getValue() * idf);
					indices.add(dictionary.indexOf(entry.getKey()));
				}
			}
			return normaliseQuery(vector);
		}	
		return vector;
	}
}
