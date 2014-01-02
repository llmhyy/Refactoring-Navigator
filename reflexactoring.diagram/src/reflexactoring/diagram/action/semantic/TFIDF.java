package reflexactoring.diagram.action.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is taken from Qian Wenyi, a Ph.D. student from our lab. 
 * 
 * @author linyun
 *
 */
public class TFIDF {
	
	private boolean isIDF = true;
	
	public TFIDF(boolean isIDF){
		this.isIDF = isIDF;
	}
	
	public List<String> getWordList(Map<String, Integer> wordIndex, Map<String, Integer> wordInNFiles){
		List<String> ret = new ArrayList<String>();
		for(int i = 0; i < wordIndex.keySet().size(); i++)
			ret.add(null);
		for(String word : wordIndex.keySet())
			ret.set(wordIndex.get(word), word);
		return ret;
	}

	public List<double[]> getTFIDFfromString (List<String> files){
		List<double[]> ret = new ArrayList<>();
		/**
		 * Map a word to its index (or ID)
		 */
		Map<String, Integer> wordIndex = new HashMap<String, Integer>();
		/**
		 * Map a word to its frequency in n files
		 */
		Map<String, Integer> wordInNFiles = new HashMap<String, Integer>();
		
		List<String[]> contents = new ArrayList<String[]>();


		for(int i = 0; i < files.size(); i++){
			Set<String> wordSet = new HashSet<String>();
			String content = files.get(i);
			String[] wordArray = content.split(" ");
			for(String word : wordArray){
				wordSet.add(word);
			}
			contents.add(i, wordArray);
			for(String word : wordSet){
				int n = 0;
				if(wordInNFiles.keySet().contains(word))
					n = wordInNFiles.get(word);
				n++;
				wordInNFiles.put(word, n);
			}
		}
		
		int index = 0;
		for(String word : wordInNFiles.keySet())
			wordIndex.put(word, index++);
		
		int wordAmount = wordInNFiles.keySet().size();
		
		/**
		 * After the preliminary preparation, it begins to do TF and IDF
		 */
		for(int i = 0; i < contents.size(); i++){
			String[] content = contents.get(i);
			double[] vector = new double[wordAmount];
			for(int j = 0; j < wordAmount; j++)
				vector[j] = 0.0;
			for(String word : content){
				int j = wordIndex.get(word);
				vector[j] = vector[j] + 1.0;
			}
			double wordCountInFunction = (double)content.length;
			
			for(int j = 0; j < wordAmount; j++){
				double tf_numerator = vector[j];
				double tf = tf_numerator / wordCountInFunction;
				
				double idf_inlog_denominator = (double)getFileAmountFromIndex(j, wordIndex, wordInNFiles);
				double idf_inlog_numerator = (double)contents.size();
				double idf = Math.log(idf_inlog_numerator / idf_inlog_denominator);
				
				//double tf_idf = tf * idf;
				double tf_idf = this.isIDF ? (tf*idf) : tf;
				
				vector[j] = tf_idf;
			}
			ret.add(i, vector);
		}
		
		return ret;
	}
	
	private int getFileAmountFromIndex(int index, Map<String, Integer> wordIndex, Map<String, Integer> wordInNFiles){
		for(String word : wordIndex.keySet())
			if(wordIndex.get(word) == index)
				return wordInNFiles.get(word);
		return 0;
	}
}
