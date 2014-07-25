/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;

import reflexactoring.diagram.action.smelldetection.AdvanceEvaluatorAdapter;
import reflexactoring.diagram.action.smelldetection.NameGernationCounter;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.ProgramReference;
import reflexactoring.diagram.bean.UnitMemberWrapper;

/**
 * @author linyun
 *
 */
public abstract class PullUpMethodOpportunity extends RefactoringOpportunity{
	protected ArrayList<UnitMemberWrapper> toBePulledMethodList = new ArrayList<>();
	protected ICompilationUnitWrapper targetUnit;
	public ArrayList<UnitMemberWrapper> getToBePulledMethodList() {
		return toBePulledMethodList;
	}
	public void setToBePulledMethodList(
			ArrayList<UnitMemberWrapper> toBePulledMethodList) {
		this.toBePulledMethodList = toBePulledMethodList;
	}
	

	/**
	 * In this method, a new method is created, the following relations are built: containment relation between method and unit,
	 * all the references to to-be-pulled methods now point to the new method in unit.
	 * 
	 * @param newModel
	 * @param superUnit
	 * @return
	 */
	protected MethodWrapper createNewMethod(ProgramModel newModel, ICompilationUnitWrapper superUnit) {
		MethodWrapper methodWrapper = (MethodWrapper)toBePulledMethodList.get(0);
		MethodWrapper newMethod = new MethodWrapper(methodWrapper.getName(), methodWrapper.getParameters(), 
				methodWrapper.isConstructor(), superUnit);
		newModel.getScopeMemberList().add(newMethod);
		superUnit.getMembers().add(newMethod);
		
		for(UnitMemberWrapper member: toBePulledMethodList){
			UnitMemberWrapper newMember = newModel.findMember(member);
			for(ProgramReference reference: newMember.getRefererPointList()){
				reference.setReferee(newMethod);
				newMethod.addProgramReferer(reference);
			}
			
			newMember.setRefererPointList(new ArrayList<ProgramReference>());
		}
		
		return newMethod;
	}
	

	/**
	 * A new interface will be named by ***able, class will be named by ***Parent, and the inheritance relation will be formed as well.
	 * 
	 * @param newModel
	 * @return
	 */
	protected ICompilationUnitWrapper createNewUnit(ProgramModel newModel, boolean isInterface) {
		MethodWrapper methodWrapper = (MethodWrapper)toBePulledMethodList.get(0);
		ICompilationUnitWrapper referringUnit = methodWrapper.getUnitWrapper();
		ICompilationUnitWrapper subClassUnit = newModel.findUnit(referringUnit.getFullQualifiedName());
		
		/**
		 * find a way to name the new unit
		 */
		String simpleName = methodWrapper.getName();
		if(isInterface){
			simpleName += "able" + NameGernationCounter.retrieveNumber();
		}else{
			simpleName += "Parent" + NameGernationCounter.retrieveNumber();
		}
		
		ICompilationUnitWrapper newUnit = new ICompilationUnitWrapper(subClassUnit.getMappingModule(), 
				isInterface, simpleName, subClassUnit.getPackageName());
		
		newModel.getScopeCompilationUnitList().add(newUnit);
		
		for(UnitMemberWrapper member: toBePulledMethodList){
			UnitMemberWrapper newMember = newModel.findMember(member);
			
			ICompilationUnitWrapper unit = newMember.getUnitWrapper();
			if(isInterface){
				unit.addSuperInterface(newUnit);
			}else{
				unit.setSuperClass(newUnit);
			}
			
			unit.addParent(newUnit);
			newUnit.addChild(unit);
		}
		
		return newUnit;
	}
		
	/**
	 * @param newUnit
	 * @return
	 */
	protected ModuleWrapper calculateBestMappingModule(ProgramModel model,
			ICompilationUnitWrapper newUnit) {
		
		ModuleWrapper module = null;
		double fitness = 0;
		
		for(ModuleWrapper m: moduleList){
			newUnit.setMappingModule(m);
			double f = new AdvanceEvaluatorAdapter().computeFitness(model, moduleList);
			if(module == null){
				module = m;
				fitness = f;
			}
			else{
				if(f > fitness){
					module = m;
					fitness = f;
				}
			}
		}
		
		return module;
	}
}
