/**
 * 
 */
package reflexactoring.diagram.util;

import java.util.ArrayList;

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
import org.eclipse.ui.PartInitException;

import reflexactoring.Activator;
import reflexactoring.Module;
import reflexactoring.Reflexactoring;
import reflexactoring.diagram.action.semantic.WordNetDict;
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
				
				return moduleList;
			}
		}
		
		return null;
	}
}
