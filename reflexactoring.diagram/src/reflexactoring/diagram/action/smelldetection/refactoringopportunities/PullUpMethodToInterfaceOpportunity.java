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
public class PullUpMethodToInterfaceOpportunity extends PullUpMethodOpportunity {
	
	@Override
	public ProgramModel simulate(ProgramModel model) {
		ProgramModel newModel = model.clone();
		/**
		 * create a new interface
		 */
		ICompilationUnitWrapper newInterfaceUnit = createNewInterface(newModel);
		
		/**
		 * create a new method in the newly created interface and change reference
		 */
		createNewMethod(newModel, newInterfaceUnit);
		
		newModel.updateUnitCallingRelationByMemberRelations();
		
		/**
		 * may calculate which module is proper to hold the newly created interface
		 */
		ModuleWrapper bestMappingModule = calculateBestMappingModule(newModel, newInterfaceUnit);
		newInterfaceUnit.setMappingModule(bestMappingModule);
		
		return newModel;
	}

	
	/**
	 * @param newInterfaceUnit
	 * @return
	 */
	private ModuleWrapper calculateBestMappingModule(ProgramModel model,
			ICompilationUnitWrapper newInterfaceUnit) {
		
		ModuleWrapper module = null;
		double fitness = 0;
		
		for(ModuleWrapper m: moduleList){
			newInterfaceUnit.setMappingModule(m);
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


	/**
	 * A new interface will be named by ***able, and the inheritance relation will be formed as well.
	 * 
	 * @param newModel
	 * @return
	 */
	private ICompilationUnitWrapper createNewInterface(ProgramModel newModel) {
		MethodWrapper methodWrapper = (MethodWrapper)toBePulledMethodList.get(0);
		ICompilationUnitWrapper referringUnit = methodWrapper.getUnitWrapper();
		ICompilationUnitWrapper subClassUnit = newModel.findUnit(referringUnit.getFullQualifiedName());
		
		/**
		 * find a way to name the new interface
		 */
		String simpleName = methodWrapper.getName() + "able" + NameGernationCounter.retrieveNumber();
		
		ICompilationUnitWrapper newInterfaceUnit = new ICompilationUnitWrapper(subClassUnit.getMappingModule(), 
				true, simpleName, subClassUnit.getPackageName());
		
		newModel.getScopeCompilationUnitList().add(newInterfaceUnit);
		
		for(UnitMemberWrapper member: toBePulledMethodList){
			UnitMemberWrapper newMember = newModel.findMember(member);
			
			ICompilationUnitWrapper unit = newMember.getUnitWrapper();
			unit.addSuperInterface(newInterfaceUnit);
			
			unit.addParent(newInterfaceUnit);
			newInterfaceUnit.addChild(unit);
		}
		
		return newInterfaceUnit;
	}


	/**
	 * In this method, a new method is created, the following relations are built: containment relation between method and unit,
	 * all the references to to-be-pulled methods now point to the new method in unit.
	 * 
	 * @param newModel
	 * @param newInterfaceUnit
	 * @return
	 */
	private MethodWrapper createNewMethod(ProgramModel newModel, ICompilationUnitWrapper newInterfaceUnit) {
		MethodWrapper methodWrapper = (MethodWrapper)toBePulledMethodList.get(0);
		MethodWrapper newMethod = new MethodWrapper(methodWrapper.getName(), methodWrapper.getParameters(), 
				methodWrapper.isConstructor(), newInterfaceUnit);
		newModel.getScopeMemberList().add(newMethod);
		newInterfaceUnit.getMembers().add(newMethod);
		
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


	@Override
	public void apply() {
		// TODO Auto-generated method stub
		
	}

}
