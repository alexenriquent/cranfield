import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Tokeniser {
		
	private Map<String, Integer> tokens;
	
	public Tokeniser() {
		this.tokens = new LinkedHashMap<String, Integer>();
	}
	
	public Map<String, Integer> getTokens() {
		return tokens;
	}

	public void tokenise(Map<Integer, String> data, String regex) {
		for (Entry<Integer, String> entry : data.entrySet()) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(entry.getValue());
			
			while (matcher.find()) {
				if (tokens.containsKey(matcher.group())) {
					tokens.put(matcher.group(), tokens.get(matcher.group()) + 1);
				} else {
					tokens.put(matcher.group(), 1);
				}
			}
		}
	}
	
	public <K, V extends Comparable<? super Integer>> void sort() {
		Map<String, Integer> result = new LinkedHashMap<>();
		Stream<Map.Entry<String, Integer>> stream = tokens.entrySet().stream();
		stream.sorted(Map.Entry.comparingByValue())
			  .forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
		updateTokens(result);
	}
	
	private void updateTokens(Map<String, Integer> map) {
		List<Entry<String, Integer>> entries = new ArrayList<>(map.entrySet());
		tokens.clear();
		
		for (int i = entries.size() - 1; i >= 0; i--) {
			Entry<String, Integer> entry = entries.get(i);
			tokens.put(entry.getKey(), entry.getValue());
		}
	}
	
	public int wordCount() {
		int result = 0;
		
		for (Entry<String, Integer> entry: tokens.entrySet()) {
			result += entry.getValue();
		}
		
		return result;
	}
	
	public int vocabularySize() {
		return tokens.size();
	}
	
	public List<String> topRank(int rank) {
		List<String> result = new ArrayList<String>();
		
		for (Entry<String, Integer> entry : tokens.entrySet()) {
			if (result.size() == rank) break;
			result.add(entry.getKey());
		}
		
		Collections.sort(result);
		return result;
	}
	
	public void removeStopwords(String stopwords[]) {
		for (int i = 0; i < stopwords.length; i++) {
			if (tokens.containsKey(stopwords[i])) {
				tokens.remove(stopwords[i]);
			}
		}	
	}
}