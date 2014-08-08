/**
 * 
 */
package reflexactoring.diagram.action.smelldetection.refactoringopportunities;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;

import reflexactoring.diagram.action.smelldetection.NameGernationCounter;
import reflexactoring.diagram.bean.FieldWrapper;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.MethodWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.bean.ProgramReference;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public abstract class PullUpMemberOpportunity extends RefactoringOpportunity{
	protected ArrayList<UnitMemberWrapper> toBePulledMemberList = new ArrayList<>();
	protected ICompilationUnitWrapper targetUnit;
	
	public PullUpMemberOpportunity(ArrayList<UnitMemberWrapper> toBePulledMemberList, ArrayList<ModuleWrapper> moduleList){
		this.moduleList = moduleList;
		this.toBePulledMemberList = toBePulledMemberList;
	}
	
	
	public ArrayList<UnitMemberWrapper> getToBePulledMemberList() {
		return toBePulledMemberList;
	}
	public void setToBePulledMemberList(
			ArrayList<UnitMemberWrapper> toBePulledMemberList) {
		this.toBePulledMemberList = toBePulledMemberList;
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("Pull up ");
		String elementType = (toBePulledMemberList.get(0) instanceof MethodWrapper)?"method":"field";
		buffer.append(elementType + " ");
		for(UnitMemberWrapper member: toBePulledMemberList){
			buffer.append(member.toString()+",");
		}
		return buffer.toString();
	}
	
	@Override
	public ArrayList<ASTNode> getHints() {
		ArrayList<ASTNode> hints = new ArrayList<>();
		for(UnitMemberWrapper member: toBePulledMemberList){
			for(ProgramReference reference: member.getRefererPointList()){
				ASTNode node = reference.getASTNode();
				hints.add(node);
			}
		}
		return hints;
	}

	/**
	 * In this method, a new member is created, the following relations are built: containment relation between member and unit,
	 * all the references to to-be-pulled members now point to the new member in unit.
	 * 
	 * @param newModel
	 * @param superUnit
	 * @return
	 */
	protected UnitMemberWrapper createNewMemberInSuperClass(ProgramModel newModel, ICompilationUnitWrapper superUnit) {
		UnitMemberWrapper oldMember = toBePulledMemberList.get(0);
		
		UnitMemberWrapper newMember = null;
		if(oldMember instanceof MethodWrapper){
			MethodWrapper methodWrapper = (MethodWrapper)oldMember;
			newMember = new MethodWrapper(methodWrapper.getName(), methodWrapper.getReturnType(), methodWrapper.getParameters(), 
					methodWrapper.isConstructor(), superUnit, null, methodWrapper.getDescription(), null);			
		}
		else if(oldMember instanceof FieldWrapper){
			FieldWrapper fieldWrapper = (FieldWrapper)oldMember;
			newMember = new FieldWrapper(fieldWrapper.getName(), fieldWrapper.getType(), superUnit,
					null, fieldWrapper.getDescription(), null);
		}
		
		if(newMember == null)return null;
		
		newModel.getScopeMemberList().add(newMember);
		superUnit.getMembers().add(newMember);
		
		for(UnitMemberWrapper oldMem: toBePulledMemberList){
			UnitMemberWrapper newToBePulledMember = newModel.findMember(oldMem);
			handleReferersOfToBePulledMember(newToBePulledMember, newMember);
			
			if(!superUnit.isInterface()){
				handleRefereesOfToBePulledMember(newToBePulledMember, newMember);
			}
			
		}
		
		return newMember;
	}
	
	/**
	 * On creating a new member, new_mem in super class (or interface) to be overriden by the to-be-pulled member, tbp_mem, this method will do
	 * the following things:
	 * 
	 * 1) all the referers of tbp_mem (but are not located in the class declaring tbp_m) will now refer to new_mem.
	 * 2) the refer pointer list in tbp_mem is set empty.
	 * @param newToBePulledMember
	 * @param newMember
	 */
	private void handleReferersOfToBePulledMember(UnitMemberWrapper newToBePulledMember, UnitMemberWrapper newMember){
		for(ProgramReference reference: newToBePulledMember.getRefererPointList()){
			reference.setReferee(newMember);
			newMember.addProgramReferer(reference);		
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
	 * @param newMember
	 */
	private void handleRefereesOfToBePulledMember(UnitMemberWrapper newToBePulledMember, UnitMemberWrapper newMember){
		for(ProgramReference reference: newToBePulledMember.getRefereePointList()){
			reference.setReferer(newMember);
			newMember.addProgramReferee(reference);
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
		UnitMemberWrapper memberWrapper = toBePulledMemberList.get(0);
		ICompilationUnitWrapper referringUnit = memberWrapper.getUnitWrapper();
		ICompilationUnitWrapper subClassUnit = newModel.findUnit(referringUnit.getFullQualifiedName());
		
		/**
		 * find a way to name the new unit
		 */
		String simpleName = memberWrapper.getName();
		if(isInterface){
			simpleName += "able" + NameGernationCounter.retrieveNumber();
		}else{
			simpleName += "Parent" + NameGernationCounter.retrieveNumber();
		}
		
		ICompilationUnitWrapper newUnit = new ICompilationUnitWrapper(subClassUnit.getMappingModule(), 
				isInterface, simpleName, subClassUnit.getPackageName(), null, "abstract");
		
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
	
	@Override
	public double computeSimilarityWith(RefactoringOpportunity opp){
		if(opp instanceof PullUpMemberOpportunity){
			PullUpMemberOpportunity thatOpp = (PullUpMemberOpportunity)opp;
			
			double memberSim = ReflexactoringUtil.computeSetSimilarity(toBePulledMemberList, thatOpp.getToBePulledMemberList());
			double unitSim = ReflexactoringUtil.computeSetSimilarity(getUnitsOfToBePulledMembers(), thatOpp.getUnitsOfToBePulledMembers());
			
			return (memberSim + unitSim)/2;
		}
		
		return 0;
	}
	
	public ArrayList<ICompilationUnitWrapper> getUnitsOfToBePulledMembers(){
		ArrayList<ICompilationUnitWrapper> units = new ArrayList<>();
		for(UnitMemberWrapper member: toBePulledMemberList){
			units.add(member.getUnitWrapper());
		}
		return units;
	} 
	
	protected boolean isHavingSameMemberList(ArrayList<UnitMemberWrapper> memberList){
		if(memberList.size() == toBePulledMemberList.size()){
			for(UnitMemberWrapper thatMember: memberList){
				if(!canFindAnEqualMemberInList(toBePulledMemberList, thatMember)){
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	private boolean canFindAnEqualMemberInList(ArrayList<UnitMemberWrapper> list, UnitMemberWrapper member){
		for(UnitMemberWrapper memberInList: list){
			if(memberInList.equals(member)){
				return true;
			}
		}
		
		return false;
	}
}
