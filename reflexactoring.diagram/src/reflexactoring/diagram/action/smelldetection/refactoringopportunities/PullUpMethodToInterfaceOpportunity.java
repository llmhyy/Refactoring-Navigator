/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;
import java.util.Iterator;

import reflexactoring.diagram.action.smelldetection.NameGernationCounter;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
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
		MethodWrapper newMethod = createNewMethod(newModel, newInterfaceUnit);
		
		/**
		 * may calculate which module is proper to hold the newly created interface
		 */
		
		return newModel;
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
			for(ProgramReference reference: member.getRefererPointList()){
				reference.setReferer(newMethod);
				newMethod.addProgramReferer(reference);
			}
			
			member.setRefererPointList(new ArrayList<ProgramReference>());
		}
		
		return newMethod;
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
			ICompilationUnitWrapper unit = member.getUnitWrapper();
			unit.addSuperInterface(newInterfaceUnit);
			
			unit.addParent(newInterfaceUnit);
			newInterfaceUnit.addChild(unit);
		}
		
		return newInterfaceUnit;
	}

	@Override
	public void apply() {
		// TODO Auto-generated method stub
		
	}

}
