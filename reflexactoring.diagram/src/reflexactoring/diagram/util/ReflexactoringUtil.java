/**
 * 
 */
package reflexactoring.diagram.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.emf.core.GMFEditingDomainFactory;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.ui.PartInitException;

import reflexactoring.Activator;
import reflexactoring.Module;
import reflexactoring.ModuleDependency;
import reflexactoring.Reflexactoring;
import reflexactoring.diagram.action.semantic.WordNetDict;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleDependencyConfidence;
import reflexactoring.diagram.bean.ModuleDependencyConfidenceTable;
import reflexactoring.diagram.bean.ModuleDependencyWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ModuleUnitsSimilarity;
import reflexactoring.diagram.bean.ModuleUnitsSimilarityTable;
import reflexactoring.diagram.preferences.ProjectInfoPage;
import reflexactoring.diagram.preferences.RecommendSettingPage;

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
	
	public static String getMappingThreshold(){
		return Activator.getDefault().getPreferenceStore().getString(ProjectInfoPage.MAPPING_THRESHOLD);
	}
	
	public static void setMappingThreshold(String mappingThreshold){
		Activator.getDefault().getPreferenceStore().putValue(ProjectInfoPage.MAPPING_THRESHOLD, mappingThreshold);
	}
	
	public static String getStopList(){
		return Activator.getDefault().getPreferenceStore().getString(ProjectInfoPage.STOP_LIST);
	}
	
	public static void setStopList(String stopList){
		Activator.getDefault().getPreferenceStore().putValue(ProjectInfoPage.STOP_LIST, stopList);
	}
	
	public static String getIterationNumber() {
		String iterationNum = Activator.getDefault().getPreferenceStore().getString(RecommendSettingPage.ITERATION_NUMBER);
		return (iterationNum == null || iterationNum.length() == 0)? String.valueOf(Settings.geneticIterationNum) : iterationNum;
	}
	
	public static void setIterationNumber(String iterationNumber){
		Activator.getDefault().getPreferenceStore().putValue(RecommendSettingPage.ITERATION_NUMBER, iterationNumber);
	}
	
	public static String getPopulationSize() {
		String string = Activator.getDefault().getPreferenceStore().getString(RecommendSettingPage.POPULATION_SIZE);
		return (string == null || string.length() == 0)? String.valueOf(Settings.populationSize) : string;
	}
	
	public static void setPopulationSize(String populationSize){
		Activator.getDefault().getPreferenceStore().putValue(RecommendSettingPage.POPULATION_SIZE, populationSize);
	}
	
	public static String getMutationRate() {
		String string = Activator.getDefault().getPreferenceStore().getString(RecommendSettingPage.MUTATION_RATE);
		return (string == null || string.length() == 0)? String.valueOf(Settings.mutationRate) : string;
	}
	
	public static void setMutationRate(String mutationRate){
		Activator.getDefault().getPreferenceStore().putValue(RecommendSettingPage.MUTATION_RATE, mutationRate);
	}
	
	public static String getAlpha() {
		String string = Activator.getDefault().getPreferenceStore().getString(RecommendSettingPage.ALPHA);
		return (string == null || string.length() == 0)? String.valueOf(Settings.alpha) : string;
	}
	
	public static void setAlpha(String alpha){
		Activator.getDefault().getPreferenceStore().putValue(RecommendSettingPage.ALPHA, alpha);
	}
	
	public static String getBeta() {
		String string = Activator.getDefault().getPreferenceStore().getString(RecommendSettingPage.BETA);
		return (string == null || string.length() == 0)? String.valueOf(Settings.beta) : string;
	}
	
	public static void setBeta(String beta){
		Activator.getDefault().getPreferenceStore().putValue(RecommendSettingPage.BETA, beta);
	}
	
	public static String getDictPath(){
		if(Activator.getDefault() == null){
			return "C:\\Program Files (x86)\\WordNet\\2.1\\dict";
		}		
		return Activator.getDefault().getPreferenceStore().getString(ProjectInfoPage.DICT_PATH);
	}
	
	public static void setDictPath(String dictPath){
		Activator.getDefault().getPreferenceStore().putValue(ProjectInfoPage.DICT_PATH, dictPath);
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
	
	/**
	 * This method is used to split identifier by camel splitting.
	 * @param s
	 * @return
	 */
	public static String[] splitCamelString(String s) {
		return s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])|(?<!^)(?=(\\*)+)");
	}

	/**
	 * Filter words from stop list. Assume that the content is formatted with white-space-seperation.
	 * @param s
	 * @return
	 */
	public static String removeStopWord(String content){
		String[] s = content.split(" ");
		String stopString = ReflexactoringUtil.getStopList();
		String[] stopStrings = stopString.split(" ");
		
		ArrayList<String> stopList = new ArrayList<>();
		for(String s2: stopStrings){
			stopList.add(s2);	
		}
		
		ArrayList<String> filteredList = new ArrayList<>();
		for(String s1: s){
			if(!stopList.contains(s1)){
				filteredList.add(s1);
			}
		}
		
		StringBuffer buffer = new StringBuffer();
		for(String word: filteredList){
			buffer.append(word + " ");
		}
		return buffer.toString();
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
	
	/**
	 * This method is for perform stemming, e.g., transfer "students" in its
	 * original tense like "student".
	 * @param description
	 * @return
	 */
	public static String performStemming(String description){
		String[] descList = description.split(" ");
		StringBuffer buffer = new StringBuffer();
		for(String desc: descList){
			buffer.append(WordNetDict.getInstance().getStem(desc)+" ");
		}
		
		String stemString = buffer.toString();
		stemString = stemString.toLowerCase();
		return stemString.substring(0, stemString.length()-1);
	}
	
	public static String performStemmingAndRemovingStopWord(String description){
		String result = performStemming(description);
		result = removeStopWord(result);
		return result;
	}
	
	/**
	 * A description would be like "This module is to do something, for example, it will do A, B, and C." 
	 * Notably, the ".", "," or even ";" actually make no sense to computer program (at least for now). 
	 * Therefore, this method is used to remove those *special character*s. Currently, we could regard ".",
	 * ",", ";", ":", "?", "(", ")", "'", and """ as special character.   
	 * @param descrption
	 * @return
	 */
	public static String removeDelimit(String descrption){
		//descrption = "Represents a mathema*tical opera\rtion.";
		descrption = descrption.replace('\r', ' ');
		descrption = descrption.replace('\n', ' ');
		descrption = descrption.replace('*', ' ');
		Pattern p = Pattern.compile("[.,;:?()\\[\\]{}\'\"\\\\@+-]");
		//Pattern p = Pattern.compile("\\.");
        Matcher m = p.matcher(descrption);   
        String resultString = m.replaceAll(" ");
        p = Pattern.compile(" {2,}");
        m = p.matcher(resultString);
        resultString = m.replaceAll(" ");  
		return resultString;
	}
	
	/**
	 * Given the path a graph editor, this method retrieve all modules which user has drawn on graph.
	 * @param diagramName
	 * @return
	 * @throws PartInitException
	 */
	public static ArrayList<ModuleWrapper> getModuleList(String diagramName) throws PartInitException{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(ReflexactoringUtil.getTargetProjectName());
		
		if(project != null){
			IFile file = project.getFile(diagramName);
			if(file.exists()){
				IPath path = file.getFullPath();
				TransactionalEditingDomain editingDomain = GMFEditingDomainFactory.INSTANCE.createEditingDomain();
				
				URI targetURI = URI.createFileURI(path.toFile().toString());
				ResourceSet resourceSet = editingDomain.getResourceSet();
				Resource diagramResource = resourceSet.getResource(targetURI, true);
				
				Reflexactoring reflexactoring = (Reflexactoring)diagramResource.getContents().get(0);
				
				ArrayList<ModuleWrapper> moduleList = new ArrayList<>();
				for(Module module: reflexactoring.getModules()){
					moduleList.add(new ModuleWrapper(module));
				}
				
				/**
				 * Build calling relation amongst modules.
				 */
				for(ModuleDependency moduleDependency: reflexactoring.getModuleDenpencies()){
					
					if(!moduleDependency.getName().equals(ModuleDependencyWrapper.DIVERGENCE)){
						Module origin = moduleDependency.getOrigin();
						Module destination = moduleDependency.getDestination();
						
						ModuleWrapper originWrapper = findModule(moduleList, origin);
						ModuleWrapper destinationWrapper = findModule(moduleList, destination);
						
						if(originWrapper != null && destinationWrapper != null){
							originWrapper.addCalleeModule(destinationWrapper);
							destinationWrapper.addCallerModule(originWrapper);
						}
					}
					
					
				}
				
				return moduleList;
			}
		}
		
		return null;
	}
	
	private static ModuleWrapper findModule(ArrayList<ModuleWrapper> list, Module module){
		for(ModuleWrapper wrapper: list){
			if(wrapper.getName().equals(module.getName())
					&& wrapper.getDescription().equals(module.getDescription())){
				return wrapper;
			}
		}
		
		return null;
	}
	
	/*public static ArrayList<ICompilationUnitWrapper> recoverCompilationUnitWrapperList(){
		ArrayList<ICompilationUnitWrapper> compilationUnitWrapperList = new ArrayList<>();
		for(ICompilationUnit unit: Settings.scope.getScopeCompilationUnitList()){
			compilationUnitWrapperList.add(new ICompilationUnitWrapper(unit));
		}
		compilationUnitWrapperList = buildStructuralDependency(compilationUnitWrapperList);
		
		return compilationUnitWrapperList;
	}*/
	
	public static void getModuleDependencyConfidenceTable(){
		
		if(isReflexionModelChanged()){
			try {
				ModuleDependencyConfidenceTable table = new ModuleDependencyConfidenceTable();
				
				ArrayList<ModuleWrapper> moduleList = getModuleList(Settings.diagramPath);
				
				for(ModuleWrapper moduleWrapper: moduleList){
					double[] confidenceList = new double[moduleList.size()];
					for(int i=0; i<confidenceList.length; i++){
						confidenceList[i] = 0.5;
					}
					
					ModuleDependencyConfidence confidence = 
							new ModuleDependencyConfidence(moduleWrapper, moduleList, confidenceList);
					table.add(confidence);
				}
				
				Settings.confidenceTable = table;
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static ModuleUnitsSimilarityTable convertRawTableToModuleUnitsSimilarityTable(double[][] similarityTable, ArrayList<ModuleWrapper> modules,
			ArrayList<ICompilationUnitWrapper> units){
		ModuleUnitsSimilarityTable table = new ModuleUnitsSimilarityTable();
		for(int i=0; i<modules.size(); i++){
			ModuleWrapper module = modules.get(i);
			double[] values = similarityTable[i];
			
			ModuleUnitsSimilarity similarity = new ModuleUnitsSimilarity(module, units, values);
			table.add(similarity);
		}
		
		return table;
	}
	
	public static boolean checkNumber(String string){
		try{
			Double.parseDouble(string);
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}
	
	public static boolean isModuleChaged(ArrayList<ModuleWrapper> newModuleList){
		if(Settings.similarityTable.size() != newModuleList.size()){
			return true;
		}
		
		for(ModuleUnitsSimilarity moduleSimilarity: Settings.similarityTable){
			ModuleWrapper originalModule = moduleSimilarity.getModule();
			ModuleWrapper newModule = findCorrespondingModule(originalModule, newModuleList);
			
			if(newModule == null){
				return true;
			}
			else if(!newModule.getDescription().equals(originalModule.getDescription())){
				return true;
			}
		}
		
		return false;
	}
	
	public static ModuleWrapper findCorrespondingModule(ModuleWrapper module, ArrayList<ModuleWrapper> newModuleList){
		for(ModuleWrapper newModule: newModuleList){
			if(module.getName().equals(newModule.getName())){
				return newModule;
			}
		}
		
		return null;
	}
	
	/**
	 * The conditions when a similarity computation is need are:
	 * 1) modules are changed
	 * 2) compilation units are changed
	 * 3) similarity has never been computed yet
	 * @return
	 */
	public static boolean isReflexionModelChanged(){
		try {
			return Settings.isCompliationUnitChanged || isModuleChaged(getModuleList(Settings.diagramPath))
					|| Settings.similarityTable.size()==0;
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	public static ArrayList<ICompilationUnitWrapper> getUnitListFromModule(ModuleWrapper module){
		ArrayList<ICompilationUnitWrapper> unitList = new ArrayList<>();
		
		for(ICompilationUnitWrapper unit: Settings.scope.getScopeCompilationUnitList()){
			if(unit.getMappingModule().equals(module)){
				unitList.add(unit);
			}
		}
		
		return unitList;
	}
	
}
