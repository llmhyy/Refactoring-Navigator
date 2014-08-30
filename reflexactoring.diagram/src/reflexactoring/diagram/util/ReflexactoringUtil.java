/**
 * 
 */
package reflexactoring.diagram.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.emf.core.GMFEditingDomainFactory;
import org.eclipse.ui.PartInitException;

import reflexactoring.Activator;
import reflexactoring.Module;
import reflexactoring.ModuleCreation;
import reflexactoring.ModuleDependency;
import reflexactoring.ModuleExtend;
import reflexactoring.ModuleLink;
import reflexactoring.Reflexactoring;
import reflexactoring.diagram.action.UserInputMerger;
import reflexactoring.diagram.action.recommend.suboptimal.Rules;
import reflexactoring.diagram.action.semantic.WordNetDict;
import reflexactoring.diagram.bean.ModuleLinkWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.heuristics.ModuleCreationConfidence;
import reflexactoring.diagram.bean.heuristics.ModuleCreationConfidenceTable;
import reflexactoring.diagram.bean.heuristics.ModuleDependencyConfidence;
import reflexactoring.diagram.bean.heuristics.ModuleDependencyConfidenceTable;
import reflexactoring.diagram.bean.heuristics.ModuleExtendConfidence;
import reflexactoring.diagram.bean.heuristics.ModuleExtendConfidenceTable;
import reflexactoring.diagram.bean.heuristics.ModuleUnitsSimilarity;
import reflexactoring.diagram.bean.heuristics.ModuleUnitsSimilarityTable;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.ReferencingDetail;
import reflexactoring.diagram.bean.programmodel.SimilarityComputable;
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
	
	public static String getMappingIterationNumber() {
		String iterationNum = Activator.getDefault().getPreferenceStore().getString(RecommendSettingPage.MAPPING_ITERATION_NUMBER);
		return (iterationNum == null || iterationNum.length() == 0)? String.valueOf(Settings.mappingIterationNum) : iterationNum;
	}
	
	public static void setMappingIterationNumber(String iterationNumber){
		Activator.getDefault().getPreferenceStore().putValue(RecommendSettingPage.MAPPING_ITERATION_NUMBER, iterationNumber);
	}
	
	public static String getClimbIterationNumber() {
		String iterationNum = Activator.getDefault().getPreferenceStore().getString(RecommendSettingPage.CLIMB_ITERATION_NUMBER);
		return (iterationNum == null || iterationNum.length() == 0)? String.valueOf(Settings.climbIterationNum) : iterationNum;
	}
	
	public static void setClimbIterationNumber(String iterationNumber){
		Activator.getDefault().getPreferenceStore().putValue(RecommendSettingPage.CLIMB_ITERATION_NUMBER, iterationNumber);
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

	public static String getSuggestionNumber() {
		String suggestionNum = Activator.getDefault().getPreferenceStore().getString(RecommendSettingPage.SUGGESTION_NUMBER);
		return (suggestionNum == null || suggestionNum.length() == 0)? String.valueOf(Settings.suggestionNum) : suggestionNum;
	}
	
	public static void setSuggestionNumber(String suggestionNumber){
		Activator.getDefault().getPreferenceStore().putValue(RecommendSettingPage.SUGGESTION_NUMBER, suggestionNumber);
	}

	public static String getRewardRate() {
		String rewardRate = Activator.getDefault().getPreferenceStore().getString(RecommendSettingPage.REWARD_RATE);
		return (rewardRate == null || rewardRate.length() == 0)? String.valueOf(Settings.rewardRate) : rewardRate;
	}
	
	public static void setRewardRate(String rewardRate){
		Activator.getDefault().getPreferenceStore().putValue(RecommendSettingPage.REWARD_RATE, rewardRate);
	}

	public static String getPenaltyRate() {
		String penaltyRate = Activator.getDefault().getPreferenceStore().getString(RecommendSettingPage.PENALTY_RATE);
		return (penaltyRate == null || penaltyRate.length() == 0)? String.valueOf(Settings.penaltyRate) : penaltyRate;
	}
	
	public static void setPenaltyRate(String penaltyRate){
		Activator.getDefault().getPreferenceStore().putValue(RecommendSettingPage.PENALTY_RATE, penaltyRate);
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
	public static ArrayList<ModuleWrapper> getModuleList(String diagramName){
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
				for(ModuleLink moduleLink: reflexactoring.getModuleDenpencies()){
										
					if(!moduleLink.getName().equals(ModuleLinkWrapper.DIVERGENCE)){
						Module origin = moduleLink.getOrigin();
						Module destination = moduleLink.getDestination();
						
						ModuleWrapper originWrapper = findModule(moduleList, origin);
						ModuleWrapper destinationWrapper = findModule(moduleList, destination);
						
						//System.currentTimeMillis();
						
						if(originWrapper != null && destinationWrapper != null){
							if(moduleLink instanceof ModuleDependency){
								originWrapper.addCalleeModule(destinationWrapper, ReferencingDetail.REFER);
								destinationWrapper.addCallerModule(originWrapper, ReferencingDetail.REFER);								
							}else if(moduleLink instanceof ModuleExtend){
								originWrapper.addParentModule(destinationWrapper);
								destinationWrapper.addChildModule(originWrapper);	
							}else if(moduleLink instanceof ModuleCreation){
								originWrapper.addCalleeModule(destinationWrapper, ReferencingDetail.NEW);
								destinationWrapper.addCallerModule(originWrapper, ReferencingDetail.NEW);
							}
						}
					}
					
					
				}
				
				return moduleList;
			}
		}
		
		return null;
	}
	
	public static ModuleWrapper findModule(ArrayList<ModuleWrapper> list, Module module){
		for(ModuleWrapper wrapper: list){
			if(wrapper.getName().equals(module.getName())){
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
		
		if(Settings.dependencyConfidenceTable.size() == 0){
			ModuleDependencyConfidenceTable table = new ModuleDependencyConfidenceTable();
			
			ArrayList<ModuleWrapper> moduleList = getModuleList(Settings.diagramPath);
			
			for(ModuleWrapper moduleWrapper: moduleList){
				double[] confidenceList = new double[moduleList.size()];
				for(int i=0; i<confidenceList.length; i++){
					confidenceList[i] = /*0.5*/1;
				}
				
				ModuleDependencyConfidence confidence = 
						new ModuleDependencyConfidence(moduleWrapper, moduleList, confidenceList);
				table.add(confidence);
			}
			
			Settings.dependencyConfidenceTable = table;
		}
		else{
			UserInputMerger merger = new UserInputMerger();
			merger.mergeDependencyConfidenceTable();
		}
	}
	
	public static String lowercaseFirstCharacterOfString(String str){
		String head = str.toCharArray()[0] + "";
		head = head.toLowerCase();
		String name = head + str.substring(1, str.length());
		return name;
	}
	
	public static void getModuleExtendConfidenceTable(){
		
		if(Settings.extendConfidenceTable.size() == 0){
			ModuleExtendConfidenceTable table = new ModuleExtendConfidenceTable();
			
			ArrayList<ModuleWrapper> moduleList = getModuleList(Settings.diagramPath);
			
			for(ModuleWrapper moduleWrapper: moduleList){
				double[] confidenceList = new double[moduleList.size()];
				for(int i=0; i<confidenceList.length; i++){
					confidenceList[i] = /*0.5*/1;
				}
				
				ModuleExtendConfidence confidence = 
						new ModuleExtendConfidence(moduleWrapper, moduleList, confidenceList);
				table.add(confidence);
			}
			
			Settings.extendConfidenceTable = table;
		}
		else{
			UserInputMerger merger = new UserInputMerger();
			merger.mergeExtendConfidenceTable();
		}
	}
	
	public static void getModuleCreationConfidenceTable(){
		
		if(Settings.creationConfidenceTable.size() == 0){
			ModuleCreationConfidenceTable table = new ModuleCreationConfidenceTable();
			
			ArrayList<ModuleWrapper> moduleList = getModuleList(Settings.diagramPath);
			
			for(ModuleWrapper moduleWrapper: moduleList){
				double[] confidenceList = new double[moduleList.size()];
				for(int i=0; i<confidenceList.length; i++){
					confidenceList[i] = /*0.5*/1;
				}
				
				ModuleCreationConfidence confidence = 
						new ModuleCreationConfidence(moduleWrapper, moduleList, confidenceList);
				table.add(confidence);
			}
			
			Settings.creationConfidenceTable = table;
		}
		else{
			UserInputMerger merger = new UserInputMerger();
			merger.mergeCreationConfidenceTable();
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
		if(Settings.similarityTable.size() != newModuleList.size() 
				|| Settings.dependencyConfidenceTable.size() != newModuleList.size()
				|| Settings.extendConfidenceTable.size() != newModuleList.size()){
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
		
		for(ModuleDependencyConfidence moduleConfidence: Settings.dependencyConfidenceTable){
			ModuleWrapper originalModule = moduleConfidence.getModule();
			ModuleWrapper newModule = findCorrespondingModule(originalModule, newModuleList);
			
			if(newModule == null){
				return true;
			}
			else if(!newModule.getDescription().equals(originalModule.getDescription())){
				return true;
			}
		}

		for(ModuleExtendConfidence moduleConfidence: Settings.extendConfidenceTable){
			ModuleWrapper originalModule = moduleConfidence.getModule();
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
		return Settings.isCompliationUnitChanged || isModuleChaged(getModuleList(Settings.diagramPath))
				|| Settings.similarityTable.size()==0 || Settings.dependencyConfidenceTable.size()==0;
		
		//return true;
	}

	public static ArrayList<ICompilationUnitWrapper> getUnitListFromModule(ModuleWrapper module){
		ArrayList<ICompilationUnitWrapper> unitList = new ArrayList<>();
		
		for(ICompilationUnitWrapper unit: Settings.scope.getScopeCompilationUnitList()){
			if(unit != null && unit.getMappingModule() != null && unit.getMappingModule().equals(module)){
				unitList.add(unit);
			}
		}
		
		return unitList;
	}
	
	public static IProject getSpecificJavaProjectInWorkspace(){
		
		String targetProject = ReflexactoringUtil.getTargetProjectName();
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		
		for(int i=0; i<projects.length; i++){
			if(targetProject.equals(projects[i].getName())){
				return projects[i];
				//return JavaCore.create(projects[i]);
			}
		}
		
		return null;
	}
	
	public static int getModuleIndex(ArrayList<ModuleWrapper> moduleList, ModuleWrapper module){
		for(int i=0; i<moduleList.size(); i++){
			ModuleWrapper m = moduleList.get(i);
			if(m.equals(module)){
				return i;
			}
		}
		
		return -1;
	}
	
	public static boolean checkCorrectMapping(int[] DNA, Rules rules){
		for(int i=0; i<DNA.length; i++){
			Integer index = rules.getMemberModuleFixList().get(i);
			
			if(index != null && index != DNA[i]){
				return false;	
			}
		}
		
		return true;
	}
	
	/**
	 * For string1: a b c d
	 *     string2: a f c d
	 * The result is a c d
	 * @param nodeList1
	 * @param nodeList2
	 * @param comparator
	 * @return
	 */
	public static Object[] generateCommonNodeList(Object[] nodeList1,
			Object[] nodeList2, DefaultComparator comparator) {
		int[][] commonLengthTable = buildLeveshteinTable(nodeList1, nodeList2, comparator);

		int commonLength = commonLengthTable[nodeList1.length][nodeList2.length];
		Object[] commonList = new Object[commonLength];

		for (int k = commonLength - 1, i = nodeList1.length, j = nodeList2.length; (i > 0 && j > 0);) {
			if (comparator.isMatch(nodeList1[i - 1], nodeList2[j - 1])) {
				commonList[k] = nodeList1[i - 1];
				k--;
				i--;
				j--;
			} else {
				if (commonLengthTable[i - 1][j] >= commonLengthTable[i][j - 1])
					i--;
				else
					j--;
			}
		}

		return commonList;
	}
	
	public static int[][] buildLeveshteinTable(Object[] nodeList1,
			Object[] nodeList2, DefaultComparator comparator){
		int[][] commonLengthTable = new int[nodeList1.length + 1][nodeList2.length + 1];
		for (int i = 0; i < nodeList1.length + 1; i++)
			commonLengthTable[i][0] = 0;
		for (int j = 0; j < nodeList2.length + 1; j++)
			commonLengthTable[0][j] = 0;

		for (int i = 1; i < nodeList1.length + 1; i++)
			for (int j = 1; j < nodeList2.length + 1; j++) {
				if (comparator.isMatch(nodeList1[i - 1], nodeList2[j - 1]))
					commonLengthTable[i][j] = commonLengthTable[i - 1][j - 1] + 1;
				else {
					commonLengthTable[i][j] = (commonLengthTable[i - 1][j] >= commonLengthTable[i][j - 1]) ? commonLengthTable[i - 1][j]
							: commonLengthTable[i][j - 1];
				}

			}
		
		return commonLengthTable;
	}
	
	public static double compareStringSimilarity(String str1, String str2){
		
		if(str1 == null || str2 == null){
			return 0;
		}
		
		String[] words1 = ReflexactoringUtil.splitCamelString(str1);
		String[] words2 = ReflexactoringUtil.splitCamelString(str2);
		
		Object[] commonWords = ReflexactoringUtil.generateCommonNodeList(words1, words2, new DefaultComparator());
		double sim = 2d*commonWords.length/(words1.length+words2.length);
		
		return sim;
	}
	
	public static double computeSetSimilarity(ArrayList<? extends SimilarityComputable> set1, 
			ArrayList<? extends SimilarityComputable> set2){
		ArrayList<SimilarityComputable> markedSet = new ArrayList<>();
		
		double sum = 0;
		
		for(SimilarityComputable obj1: set1){
			if(markedSet.contains(obj1))continue;
			
			double bestSim = 0;
			SimilarityComputable bestMatcher = null;
			
			for(SimilarityComputable obj2: set2){
				if(markedSet.contains(obj2))continue;
				
				double sim = obj1.computeSimilarityWith(obj2);
				System.currentTimeMillis();
				if(bestMatcher == null){
					bestSim = sim;
					bestMatcher = obj2;
				}
				else{
					if(sim > bestSim){
						bestSim = sim;
						bestMatcher = obj2;
					}
				}
			}
			
			sum += bestSim;
			
			markedSet.add(obj1);
			markedSet.add(bestMatcher);
		}
		
		return 2*sum/(set1.size()+set2.size());
	}
}
