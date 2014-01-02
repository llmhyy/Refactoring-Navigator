/**
 * 
 */
package reflexactoring.diagram.util;

import java.util.ArrayList;

import reflexactoring.Activator;
import reflexactoring.diagram.action.semantic.WordNetDict;
import reflexactoring.diagram.preferences.ProjectInfoPage;

/**
 * @author linyun
 *
 */
public class ReflexactoringUtil {
	
	public final static int CamelSplitting = 0;
	public final static int DotSplitting = 1;
	
	public static String getTargetProjectName(){
		return Activator.getDefault().getPreferenceStore().getString(ProjectInfoPage.TARGET_PORJECT);
	}
	
	public static void setTargetProjectName(String targetProjectName){
		Activator.getDefault().getPreferenceStore().putValue(ProjectInfoPage.TARGET_PORJECT, targetProjectName);
	}
	
	public static String[] splitString(int splittingStype, String string){
		if(splittingStype == CamelSplitting){
			return splitCamelString(string);
		}
		else if(splittingStype == DotSplitting){
			return string.split("\\.");
		}
		return null;
	}
	
	/**
	 * This method is used to handle the case when underlying separator like "_" is used
	 * in identifier name.
	 * @param s
	 * @return
	 */
	public static String[] mixedSplitting(String s){
		if(s.contains("_")){
			String[] strings = s.split("_");
			
			ArrayList<String> stringList = new ArrayList<>();
			for(String string: strings){
				String[] subList = splitCamelString(string);
				for(String subString: subList){
					stringList.add(subString);	
				}
			}
			
			return stringList.toArray(new String[0]);
		}
		else{
			return splitCamelString(s);
		}
	}
	
	public static String[] splitCamelString(String s) {
		return s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|(?<!^)(?=(\\*)+)");
	}
	
	/**
	 * Assume that the length of both vector should be the same, return cosine value.
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public static double computeCosine(double[] vector1, double[] vector2){
		int length = vector1.length;
		double product = 0.0;
		double len1 = 0.0;
		double len2 = 0.0;
		for(int i=0; i<length; i++){
			product += vector1[i]*vector2[i];
			len1 += vector1[i]*vector1[i];
			len2 += vector2[i]*vector2[i];
		}
		
		if(len1 == 0 || len2==0){
			return 0;
		}
		else{
			return product/(Math.sqrt(len1) * Math.sqrt(len2));
		}
	}
	
	/**
	 * Assume that the length of both vector should be the same, return Euclidean similarity .
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public static double computeEuclideanSimilarity(double[] vector1, double[] vector2){
		int length = vector1.length;
		double product = 0.0;
		for(int i=0; i<length; i++){
			product += Math.pow((vector1[i]-vector2[i]), 2);
		}
		
		return 1 - Math.sqrt(product);
	}
	
	public static String performStemming(String description){
		String[] descList = description.split(" ");
		StringBuffer buffer = new StringBuffer();
		for(String desc: descList){
			buffer.append(WordNetDict.getInstance().getStem(desc)+" ");
		}
		
		String stemString = buffer.toString();
		return stemString.substring(0, stemString.length()-1);
	}
}
