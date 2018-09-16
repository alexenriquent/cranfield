import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class VectorSpaceModel {
	
	private static final int RANK = 1000;
	private static final int MAX_DOCUMENTS = 10;
	private int totalDocuments;
	
	private Tokeniser tokeniser;
	private InvertedIndex invertedIndex;
	private List<String> dictionary;
	private Map<Integer, Double[]> vectorSpaceModel;
	private Map<Integer, Integer> frequencies;
	
	public VectorSpaceModel(Map<Integer, String> data, String stopwords[]) {
		totalDocuments = data.size();
		initialiseTokeniser(data, stopwords);
		initialiseInvertedIndex(data, stopwords);
		dictionary = tokeniser.topRank(RANK);
		initialiseVectorSpaceModel(data);
		initialiseFrequencies();	
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
	
	private void initialiseFrequencies() {
		frequencies = new HashMap<Integer, Integer>();
		
		for (Entry<String, Map<Integer, Integer>> term : invertedIndex.getInvertedIndex().entrySet()) {
			if (dictionary.contains(term.getKey())) {
				for (Entry<Integer, Integer> document : term.getValue().entrySet()) {
					if (frequencies.containsKey(document.getKey())) {
						frequencies.put(document.getKey(), frequencies.get(document.getKey()) + document.getValue());
					} else {
						frequencies.put(document.getKey(), document.getValue());
					}
				}
			}
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
			double df = (double) invertedIndex.getInvertedIndex().get(dictionary.get(i)).size();
			double idf = Math.log10(totalDocuments / df);
			for (Entry<Integer, Integer> entry : invertedIndex.getInvertedIndex().get(dictionary.get(i)).entrySet()) {
				double tf = (double) entry.getValue() / (double) frequencies.get(entry.getKey());
				vectorSpaceModel.get(entry.getKey())[i] = tf * idf;
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
	
	private Double[] tranformQuery(String keywords[], List<Integer> indices) {
		Map<String, Integer> terms = new HashMap<String, Integer>();
		Double[] vector = new Double[dictionary.size()];
		double totalWords = 0.0;
		
		Arrays.fill(vector, 0.0);
		
		for (int i = 0; i < keywords.length; i++) {
			if (terms.containsKey(keywords[i])) {
				terms.put(keywords[i], terms.get(keywords[i]) + 1);
				totalWords++;
			} else {
				terms.put(keywords[i], 1);
				totalWords++;
			}
		}
		
		if (!terms.isEmpty()) {
			for (Entry<String, Integer> entry : terms.entrySet()) {
				double df = (double) invertedIndex.getInvertedIndex().get(entry.getKey()).size();
				double idf = Math.log10(totalDocuments / df);
				if (dictionary.contains(entry.getKey())) {
					double tf = ((double) entry.getValue()) / totalWords;
					vector[dictionary.indexOf(entry.getKey())] = tf * idf;
					indices.add(dictionary.indexOf(entry.getKey()));
				}
			}
			return normaliseQuery(vector);
		}	
		return vector;
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
				double cosine = 0.0;
				for (int i = 0; i < entry.getValue().length; i++) {
					cosine += normalisedVector[i] * entry.getValue()[i];
				}
				if (cosine > 0.0) {
					documents.put(entry.getKey(), cosine);
				}
			}
		}
				
		return documents;
	}
	
	public Map<Integer, Double> searchInvertedIndex(String keywords[]) {
		Map<Integer, Double> documents = new LinkedHashMap<>();
		List<Integer> indices = new ArrayList<Integer>();
		Double[] normalisedVector = tranformQuery(keywords, indices);
		
		if (!indices.isEmpty()) {
			for (int i = 0; i < keywords.length; i++) {
				if (invertedIndex.getInvertedIndex().containsKey(keywords[i])) {
					for (Entry<Integer, Integer> entry : invertedIndex.getInvertedIndex().get(keywords[i]).entrySet()) {
						if (!documents.containsKey(entry.getKey())) {
							double cosine = 0.0;
							for (Integer index : indices) {
								cosine += normalisedVector[index] * vectorSpaceModel.get(entry.getKey())[index];
							}
							if (cosine > 0.0) {
								documents.put(entry.getKey(), cosine);
							}
						}
					}
				}
			}
		}
		
		return documents;
	}

	public <K, V extends Comparable<? super Double>> Map<Integer, Double> sort(Map<Integer, Double> documents) {
		return documents.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
				.collect(Collectors.toMap(
						Map.Entry::getKey, 
						Map.Entry::getValue, 
						(x1, x2) -> x1, 
						LinkedHashMap::new
						));
	}
	
	public List<Integer> topDocuments(Map<Integer, Double> documents) {
		List<Integer> topDocuments = new LinkedList<Integer>();
		
		for (Entry<Integer, Double> entry : documents.entrySet()) {
			if (topDocuments.size() == MAX_DOCUMENTS) break;
			topDocuments.add(entry.getKey());
		}
		
		return topDocuments;
	}
}
