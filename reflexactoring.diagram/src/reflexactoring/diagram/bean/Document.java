/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.HashMap;
import java.util.HashSet;

import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * When considering the similarity between a module and a class/method/field,
 * the number of shared words is the key factor. Therefore, class, interface,
 * method and field are all documents in this regard. 
 * 
 * @author linyun
 *
 */
public abstract class Document {
	protected HashMap<String, Integer> termFrequency = new HashMap<>();
	protected String description;
	
	protected abstract String getDocName();
	
	/**
	 * I need to add the weight of the java class name.
	 * @return
	 */
	protected String generateTitle(){
		String title = getDocName();
		String[] titleList = ReflexactoringUtil.mixedSplitting(title);
		
		StringBuffer buffer = new StringBuffer();
		for(String titleKeyword: titleList){
			for(int i=0; i<100; i++){
				//titleKeyword = ReflexactoringUtil.performStemming(titleKeyword);
				buffer.append(titleKeyword + " ");				
			}
		}
		
		return buffer.toString();
	}
	
	/**
	 * Assume that the keywords in content are well formatted, i.e., all the keywords are separated by white space.
	 * @param content
	 */
	public void extractTermFrequency(String content){
		//content = ReflexactoringUtil.performStemmingAndRemovingStopWord(content);
		String[] list = content.split(" ");
		for(String keyword: list){
			Integer freq = termFrequency.get(keyword);
			if(freq == null){
				freq = 1;
			}
			else{
				freq++;
			}
			
			termFrequency.put(keyword, freq);
		}
	}

	public double computeSimilarity(Document doc){
		HashSet<String> union = new HashSet<>();
		union.addAll(termFrequency.keySet());
		union.addAll(doc.getTermFrequency().keySet());
		
		double numerator = 0;
		double denominator = 0;
		for(String key: union){
			Integer thisFreq = this.termFrequency.get(key);
			thisFreq = (thisFreq == null)? 0 : thisFreq;
			
			Integer thatFreq = doc.getTermFrequency().get(key);
			thatFreq = (thatFreq == null)? 0 : thatFreq;
			
			if(thisFreq == 0 && thatFreq != 0){
				denominator += thatFreq;
			}
			else if(thisFreq != 0 && thatFreq == 0){
				denominator += thisFreq;
			}
			else{
				double value = (thisFreq >= thatFreq)? thisFreq : thatFreq;
				numerator += value;
				denominator += value;
			}
		}
		
		double result = numerator/denominator;
		return result;
		
	}
	
	/**
	 * @return the termFrequency
	 */
	public HashMap<String, Integer> getTermFrequency() {
		return termFrequency;
	}
	
	/**
	 * return the well formatted content.
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
