import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvertedIndex {
	
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
	
	public void removeStopwords(String stopwords[]) {
		for (int i = 0; i < stopwords.length; i++) {
			if (invertedIndex.containsKey(stopwords[i])) {
				invertedIndex.remove(stopwords[i]);
			}
		}	
	}
}
