/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.policy;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eposoft.jccd.data.JCCDFile;
import org.eposoft.jccd.data.SimilarityGroup;
import org.eposoft.jccd.data.SimilarityGroupManager;
import org.eposoft.jccd.detectors.APipeline;
import org.eposoft.jccd.detectors.ASTDetector;
import org.eposoft.jccd.preprocessors.java.GeneralizeArrayInitializers;
import org.eposoft.jccd.preprocessors.java.GeneralizeClassDeclarationNames;
import org.eposoft.jccd.preprocessors.java.GeneralizeMethodArgumentTypes;
import org.eposoft.jccd.preprocessors.java.GeneralizeMethodCallNames;
import org.eposoft.jccd.preprocessors.java.GeneralizeMethodReturnTypes;
import org.eposoft.jccd.preprocessors.java.GeneralizeVariableDeclarationTypes;
import org.eposoft.jccd.preprocessors.java.GeneralizeVariableNames;


import reflexactoring.diagram.action.smelldetection.refactoringopportunities.RefactoringOpportunity;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;

/**
 * @author linyun
 *
 */
public class PullUpMethodPolicy extends RefactoringPolicy{
	@Override
	public ArrayList<? extends RefactoringOpportunity> detectOpportunities(
			ArrayList<ICompilationUnitWrapper> list) {
		
		return null;
	}
}
