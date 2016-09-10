import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InvertedIndex {
	
	public static final int MAX_DOCUMENTS = 10;
	
	private Map<String, Map<Integer, Integer>> invertedIndex;
	
	public InvertedIndex() {
		invertedIndex = new HashMap<String, Map<Integer, Integer>>();
	}
	
	public Map<String, Map<Integer, Integer>> getInvertedIndex() {
		return invertedIndex;
	}
	
	public void generate(int id, String str, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		
		while (matcher.find()) {
			if (invertedIndex.containsKey(matcher.group())) {
				if (invertedIndex.get(matcher.group()).containsKey(id)) {
					invertedIndex.get(matcher.group()).put(id, 
					invertedIndex.get(matcher.group()).get(id) + 1);
				} else {
					invertedIndex.get(matcher.group()).put(id, 1);
				}
			} else {
				Map<Integer, Integer> statistics = new HashMap<Integer, Integer>();
				statistics.put(id, 1);
				invertedIndex.put(matcher.group(), statistics);
			}
		}
	}
	
	public void generateAll(List<String> strList, String regex) {
		for (int i = 0; i < strList.size(); i++) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(strList.get(i));
			
			while (matcher.find()) {
				if (invertedIndex.containsKey(matcher.group())) {
					if (invertedIndex.get(matcher.group()).containsKey(i + 1)) {
						invertedIndex.get(matcher.group()).put(i + 1, 
						invertedIndex.get(matcher.group()).get(i + 1) + 1);
					} else {
						invertedIndex.get(matcher.group()).put(i + 1, 1);
					}
				} else {
					Map<Integer, Integer> statistics = new HashMap<Integer, Integer>();
					statistics.put(i + 1, 1);
					invertedIndex.put(matcher.group(), statistics);
				}
			}
		}
	}
	
	public void removeStopwords(String stopwords[]) {
		for (int i = 0; i < stopwords.length; i++) {
			if (invertedIndex.containsKey(stopwords[i])) {
				invertedIndex.remove(stopwords[i]);
			}
		}	
	}
	
	public Map<Integer, Integer> computeScores(String keywords[]) {
		Map<Integer, Integer> scores = new HashMap<Integer, Integer>();
		
		for (int i = 0; i < keywords.length; i++) {
			if (invertedIndex.containsKey(keywords[i])) {
				for (Entry<Integer, Integer> entry : invertedIndex.get(keywords[i]).entrySet()) {
					if (scores.containsKey(entry.getKey())) {
						scores.put(entry.getKey(), scores.get(entry.getKey()) + entry.getValue());
					} else {
						scores.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}
		
		return sortScores(scores);
	}
	
	private <K, V extends Comparable<? super Integer>> 
	 		Map<Integer, Integer> sortScores(Map<Integer, Integer> scores) {
		return scores.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
				.collect(Collectors.toMap(
						Map.Entry::getKey, 
						Map.Entry::getValue, 
						(x1, x2) -> x1, 
						LinkedHashMap::new
						));
	}
	
	public List<Integer> topDocuments(Map<Integer, Integer> scores) {
		List<Integer> topDocuments = new LinkedList<Integer>();
		
		for (Entry<Integer, Integer> entry : scores.entrySet()) {
			if (topDocuments.size() == MAX_DOCUMENTS) break;
			topDocuments.add(entry.getKey());
		}
		
		return topDocuments;
	}
}
