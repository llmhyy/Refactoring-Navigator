/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;
import java.util.Iterator;

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
public abstract class PullUpMemberOpportunity extends RefactoringOpportunity{
	protected ArrayList<UnitMemberWrapper> toBePulledMemberList = new ArrayList<>();
	protected ICompilationUnitWrapper targetUnit;
	public ArrayList<UnitMemberWrapper> getToBePulledMethodList() {
		return toBePulledMemberList;
	}
	public void setToBePulledMethodList(
			ArrayList<UnitMemberWrapper> toBePulledMethodList) {
		this.toBePulledMemberList = toBePulledMethodList;
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
		MethodWrapper methodWrapper = (MethodWrapper)toBePulledMemberList.get(0);
		MethodWrapper newMethod = new MethodWrapper(methodWrapper.getName(), methodWrapper.getParameters(), 
				methodWrapper.isConstructor(), superUnit);
		newModel.getScopeMemberList().add(newMethod);
		superUnit.getMembers().add(newMethod);
		
		for(UnitMemberWrapper member: toBePulledMemberList){
			UnitMemberWrapper newToBePulledMember = newModel.findMember(member);
			handleReferersOfToBePulledMember(newToBePulledMember, newMethod);
			
			if(!superUnit.isInterface()){
				handleRefereesOfToBePulledMember(newToBePulledMember, newMethod);
			}
			
		}
		
		return newMethod;
	}
	
	/**
	 * On creating a new member, new_mem in super class (or interface) to be overriden by the to-be-pulled member, tbp_mem, this method will do
	 * the following things:
	 * 
	 * 1) all the referers of tbp_mem (but are not located in the class declaring tbp_m) will now refer to new_mem.
	 * 2) the refer pointer list in tbp_mem is set empty.
	 * @param newToBePulledMember
	 * @param newMethod
	 */
	private void handleReferersOfToBePulledMember(UnitMemberWrapper newToBePulledMember, MethodWrapper newMethod){
		for(ProgramReference reference: newToBePulledMember.getRefererPointList()){
			reference.setReferee(newMethod);
			newMethod.addProgramReferer(reference);		
		}
		
		newToBePulledMember.setRefererPointList(new ArrayList<ProgramReference>());
	}
	
	/**
	 * On creating a new member, new_mem in super class to be overriden by the to-be-pulled member, tbp_mem, this method will do
	 * the following things:
	 * 
	 * 1) all the referees of tbp_mem (but are not located in the class declaring tbp_m) will now be refered by new_mem.
	 * 2) the referee pointer list in tbp_mem is set empty.
	 * @param newToBePulledMember
	 * @param newMethod
	 */
	private void handleRefereesOfToBePulledMember(UnitMemberWrapper newToBePulledMember, MethodWrapper newMethod){
		for(ProgramReference reference: newToBePulledMember.getRefereePointList()){
			reference.setReferer(newMethod);
			newMethod.addProgramReferee(reference);
		}
		
		newToBePulledMember.setRefereePointList(new ArrayList<ProgramReference>());
	}

	/**
	 * A new interface will be named by ***able, class will be named by ***Parent, and the inheritance relation will be formed as well.
	 * 
	 * @param newModel
	 * @return
	 */
	protected ICompilationUnitWrapper createNewUnit(ProgramModel newModel, boolean isInterface) {
		MethodWrapper methodWrapper = (MethodWrapper)toBePulledMemberList.get(0);
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
		
		for(UnitMemberWrapper member: toBePulledMemberList){
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
