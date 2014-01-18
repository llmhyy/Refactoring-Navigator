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
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.ui.PartInitException;

import reflexactoring.Activator;
import reflexactoring.Module;
import reflexactoring.ModuleDependency;
import reflexactoring.Reflexactoring;
import reflexactoring.diagram.action.semantic.WordNetDict;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleDependencyWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
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
			
			return filterStopList(stringList.toArray(new String[0]));
		}
		else{
			return filterStopList(splitCamelString(s));
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
	 * Filter words from stop list.
	 * @param s
	 * @return
	 */
	public static String[] filterStopList(String[] s){
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
		
		return filteredList.toArray(new String[0]);
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
		return stemString.substring(0, stemString.length()-1);
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
		Pattern p = Pattern.compile("[.,;:?()\\[\\]{}\'\"\\\\]");		  
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
	
	/**
	 * Identifying the reference relations in classes/interfaces in scope.
	 * @param compilationUnitList
	 * @return
	 */
	public static ArrayList<ICompilationUnitWrapper> buildStructuralDependency(final ArrayList<ICompilationUnitWrapper> compilationUnitList){
		for(final ICompilationUnitWrapper refererCompilationUnit: compilationUnitList){
			final CompilationUnit compilationUnit = refererCompilationUnit.getJavaUnit();
			compilationUnit.accept(new ASTVisitor() {
				/**
				 * Currently, I just simplify the problem by considering only SimpleType. It could be extended to
				 * other subclasses of Type.
				 */
				public boolean visit(SimpleType type){
					String typeName = type.getName().getFullyQualifiedName();
					for(ICompilationUnitWrapper refereeCompilationUnit: compilationUnitList){
						String tobeComparedName = refereeCompilationUnit.getCompilationUnit().getElementName();
						tobeComparedName = tobeComparedName.substring(0, tobeComparedName.indexOf(".java"));
						if(typeName.equals(tobeComparedName) && !refererCompilationUnit.equals(refereeCompilationUnit)){
							refererCompilationUnit.addCalleeCompilationUnit(refereeCompilationUnit);
							refereeCompilationUnit.addCallerCompilationUnit(refererCompilationUnit);
						}
					}
					
					
					return true;
				}
			});
		}
		
		return compilationUnitList;
	}
}
