/**
 * 
 */
package reflexactoring.diagram.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.lang.model.type.ReferenceType;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eposoft.jccd.data.ASourceUnit;
import org.eposoft.jccd.data.JCCDFile;
import org.eposoft.jccd.data.SimilarityGroup;
import org.eposoft.jccd.data.SimilarityGroupManager;
import org.eposoft.jccd.data.SourceUnitPosition;
import org.eposoft.jccd.data.ast.ANode;
import org.eposoft.jccd.data.ast.NodeTypes;
import org.eposoft.jccd.detectors.APipeline;
import org.eposoft.jccd.detectors.ASTDetector;
import org.eposoft.jccd.preprocessors.java.GeneralizeArrayInitializers;
import org.eposoft.jccd.preprocessors.java.GeneralizeClassDeclarationNames;
import org.eposoft.jccd.preprocessors.java.GeneralizeMethodArgumentTypes;
import org.eposoft.jccd.preprocessors.java.GeneralizeMethodCallNames;
import org.eposoft.jccd.preprocessors.java.GeneralizeMethodReturnTypes;
import org.eposoft.jccd.preprocessors.java.GeneralizeVariableDeclarationTypes;
import org.eposoft.jccd.preprocessors.java.GeneralizeVariableNames;
import org.eposoft.jccd.preprocessors.java.RemoveAnnotations;
import org.eposoft.jccd.preprocessors.java.RemoveAssertions;
import org.eposoft.jccd.preprocessors.java.RemoveEmptyBlocks;
import org.eposoft.jccd.preprocessors.java.RemoveSemicolons;

import reflexactoring.Type;
import reflexactoring.diagram.action.smelldetection.bean.CloneInstance;
import reflexactoring.diagram.action.smelldetection.bean.CloneSet;

/**
 * @author linyun
 *
 */
public class ProgramModel{
	private ArrayList<ICompilationUnitWrapper> scopeCompilationUnitList = new ArrayList<>();
	private UnitMemberWrapperList scopeMemberList = new UnitMemberWrapperList();
	private ArrayList<ProgramReference> referenceList = new ArrayList<>();
	private ArrayList<CloneSet> cloneSets;
	
	/**
	 * calculate the CBO (coupling between objects)
	 * @return
	 */
	public double computeNormalizedCBOMetrics(){
		double links = 0;
		for(ICompilationUnitWrapper callerUnit: this.scopeCompilationUnitList){
			links += callerUnit.getCalleeCompilationUnitList().size();
		}
		
		double maximumValue = this.scopeCompilationUnitList.size() * (this.scopeCompilationUnitList.size()-1); 
		
		return links/maximumValue;
	}
	
	/**
	 * calculate LCOM (lack of cohesion of methods)
	 * @return
	 */
	public double computeNormalizedLCOMMetrics(){
		double sum = 0;
		
		for(ICompilationUnitWrapper unit: this.scopeCompilationUnitList){
			double methodNum = 0;
			double fieldNum = 0;
			double distinctAccessTime = 0;
			
			for(UnitMemberWrapper member: unit.getMembers()){
				if(member instanceof MethodWrapper){
					methodNum++;
					
					ArrayList<UnitMemberWrapper> visitedMemberList = new ArrayList<>();
					double accessTime = 0;
					for(ProgramReference reference: member.getRefereePointList()){
						LowLevelGraphNode calleeNode = reference.getReferee();
						if(calleeNode instanceof UnitMemberWrapper){
							UnitMemberWrapper calleeMember = (UnitMemberWrapper)calleeNode;
							if(calleeMember instanceof FieldWrapper && 
									calleeMember.getUnitWrapper().equals(member.getUnitWrapper())){
								if(!visitedMemberList.contains(calleeMember)){
									accessTime++;
									visitedMemberList.add(calleeMember);
								}
							}
							
						}
					}
					
					distinctAccessTime += accessTime;
				}
				else if(member instanceof FieldWrapper){
					fieldNum++;
				}
			}
			
			double LCOM = 0; 
			if(distinctAccessTime >= 2){
				LCOM = (methodNum*fieldNum-distinctAccessTime)/(methodNum*fieldNum-fieldNum);
			}
			
			double normalizedLCOM = LCOM/2;
			sum += normalizedLCOM;
		}
		
		
		return sum/this.scopeCompilationUnitList.size();
	}
	
	public ProgramModel clone(){
		long t1 = System.currentTimeMillis();
		
		ProgramModel clonedModel = new ProgramModel();
		
		ArrayList<ICompilationUnitWrapper> unitList = cloneUnits();
		
		long t2 = System.currentTimeMillis();
		//System.out.println("Unit Cloned: " + (t2-t1));
		
		clonedModel.setScopeCompilationUnitList(unitList);
		cloneUnitRelations(clonedModel, this);
		
		long t3 = System.currentTimeMillis();
		//System.out.println("Unit Relation Cloned: " + (t3-t2));
		
		UnitMemberWrapperList memberList = cloneMembers(clonedModel, this);
		
		long t4 = System.currentTimeMillis();
		//System.out.println("Member Cloned: " + (t4-t3));
		
		clonedModel.setScopeMemberList(memberList);
		cloneMemberRelations(clonedModel, this);
		
		long t5 = System.currentTimeMillis();
		//System.out.println("Member Relation Cloned: " + (t5-t4));
		
		ArrayList<ProgramReference> prList = cloneReference(clonedModel, this);
		clonedModel.setReferenceList(prList);
		
		long t6 = System.currentTimeMillis();
		//System.out.println("Reference Cloned: " + (t5-t4));
		
		ArrayList<CloneSet> cloneSets = cloneCloneSets(clonedModel, this);
		clonedModel.setCloneSets(cloneSets);
		
		long t7 = System.currentTimeMillis();
		//System.out.println("Clone Sets Cloned: " + (t5-t4));
		
		//System.out.println("Total Cloned: " + (t2-t1));
		
		return clonedModel;
	}
	
	/**
	 * This method must be invoked after other clone methods.
	 * 
	 * @param newModel
	 * @param programModel
	 * @return
	 */
	private ArrayList<CloneSet> cloneCloneSets(ProgramModel newModel, ProgramModel oldModel) {
		ArrayList<CloneSet> cloneSets = new ArrayList<>();
		if(oldModel.getCloneSets() == null){
			return cloneSets;
		}
		
		for(CloneSet oldSet: oldModel.getCloneSets()){
			CloneSet newSet = new CloneSet(oldSet.getId());
			ArrayList<CloneInstance> newInstanceList = new ArrayList<>();
			
			for(CloneInstance oldInstance: oldSet.getInstances()){
				CloneInstance newInstance = new CloneInstance(newSet, oldInstance.getFileName(), 
						oldInstance.getStartLineNumber(), oldInstance.getEndLineNumber());
				
				int memberIndex = oldModel.getUnitMemberIndex(oldInstance.getMember());
				if(memberIndex == -1){
					System.out.println(oldInstance.getMember());
				}
				UnitMemberWrapper newMember = newModel.getScopeMemberList().get(memberIndex);
				newInstance.setMember(newMember);
				
				ArrayList<ProgramReference> newCoveringReferenceList = new ArrayList<>();
				for(ProgramReference oldReference: oldInstance.getCoveringReferenceList()){
					int referenceIndex = oldModel.getProgramReferenceIndexByAddress(oldReference);
					ProgramReference newReference = newModel.getReferenceList().get(referenceIndex);
					
					newCoveringReferenceList.add(newReference);
				}
				newInstance.setCoveringReferenceList(newCoveringReferenceList);
				
				newInstanceList.add(newInstance);
			}
			
			newSet.setInstances(newInstanceList);
			cloneSets.add(newSet);
		}
		
		
		return cloneSets;
	}

	/**
	 * This method is invoked with the following precondition:
	 * All this relevant reference must be changed!
	 * 
	 * @param oldToBeDeletedMember
	 */
	public void removeMember(UnitMemberWrapper oldToBeDeletedMember){
		Iterator<UnitMemberWrapper> memberIter = this.scopeMemberList.iterator();
		while(memberIter.hasNext()){
			UnitMemberWrapper member = memberIter.next();
			if(member.equals(oldToBeDeletedMember)){
				memberIter.remove();
				member.getUnitWrapper().getMembers().remove(member);
				return;
			}
		}
		
	}
	
	/**
	 * In this step, only clone mapping module and java icompilation unit.
	 * @return
	 */
	private ArrayList<ICompilationUnitWrapper> cloneUnits() {
		ArrayList<ICompilationUnitWrapper> clonedUnits = new ArrayList<>();
		for(ICompilationUnitWrapper unit: scopeCompilationUnitList){
			ICompilationUnitWrapper clonedUnit = new ICompilationUnitWrapper(unit.getMappingModule(), unit.isInterface(), 
					unit.getSimpleName(), unit.getPackageName(), unit.getTermFrequency(), unit.getDescription());
			if(unit.getCompilationUnit() != null){
				/**
				 * The following four statements is time-consuming
				 */
				//clonedUnit = new ICompilationUnitWrapper(unit.getCompilationUnit());
				//clonedUnit.setMappingModule(unit.getMappingModule());
				//clonedUnit.setInterface(unit.isInterface());
				//clonedUnit.setJavaUnit(unit.getJavaUnit());
				clonedUnit.setCompilationUnit(unit.getCompilationUnit());
				clonedUnit.setJavaUnit(unit.getJavaUnit());
			}
			
			clonedUnits.add(clonedUnit);
		}
		
		return clonedUnits;
	}
	
	private void cloneUnitRelations(ProgramModel clonedModel, ProgramModel oldModel){
		ArrayList<ICompilationUnitWrapper> clonedUnits = clonedModel.getScopeCompilationUnitList();
		ArrayList<ICompilationUnitWrapper> oldUnits = oldModel.getScopeCompilationUnitList();
		for(int i=0; i<oldUnits.size(); i++){
			ICompilationUnitWrapper oldUnit = oldUnits.get(i);
			ICompilationUnitWrapper clonedUnit = clonedUnits.get(i);
			/**
			 * clone super class relation
			 */
			ICompilationUnitWrapper superClass = oldUnit.getSuperClass();
			if(null != superClass){
				int index = oldModel.getICompilationUnitIndex(superClass);
				ICompilationUnitWrapper clonedSuperclass = clonedModel.getScopeCompilationUnitList().get(index);
				clonedUnit.setSuperClass(clonedSuperclass);
			}
			
			/**
			 * clone interface relation
			 */
			ArrayList<ICompilationUnitWrapper> interfaceList = oldUnit.getSuperInterfaceList();
			for(ICompilationUnitWrapper interf: interfaceList){
				int index = oldModel.getICompilationUnitIndex(interf);
				ICompilationUnitWrapper clonedInterface = clonedModel.getScopeCompilationUnitList().get(index);
				clonedUnit.addSuperInterface(clonedInterface);
			}
			
			/**
			 * clone parent list
			 */
			ArrayList<ICompilationUnitWrapper> parentList = (ArrayList<ICompilationUnitWrapper>) oldUnit.getParentList();
			for(ICompilationUnitWrapper parent: parentList){
				int index = oldModel.getICompilationUnitIndex(parent);
				ICompilationUnitWrapper clonedParent = clonedModel.getScopeCompilationUnitList().get(index);
				clonedUnit.addParent(clonedParent);
			}
			
			/**
			 * clone child list
			 */
			ArrayList<ICompilationUnitWrapper> childList = (ArrayList<ICompilationUnitWrapper>) oldUnit.getChildList();
			for(ICompilationUnitWrapper child: childList){
				int index = oldModel.getICompilationUnitIndex(child);
				ICompilationUnitWrapper clonedChild = clonedModel.getScopeCompilationUnitList().get(index);
				clonedUnit.addChild(clonedChild);
			}
			/**
			 * clone caller list
			 */
			HashMap<ICompilationUnitWrapper, ReferencingDetail> oldCallerList = oldUnit.getCallerCompilationUnitList();
			for(ICompilationUnitWrapper oldCaller: oldCallerList.keySet()){
				int index = oldModel.getICompilationUnitIndex(oldCaller);
				ReferencingDetail detail = oldCallerList.get(oldCaller).clone();
				ICompilationUnitWrapper clonedCaller = clonedModel.getScopeCompilationUnitList().get(index);
				//clonedUnit.addCaller(clonedCaller);
				clonedUnit.getCallerCompilationUnitList().put(clonedCaller, detail);
			}
			/**
			 * clone callee list
			 */
			HashMap<ICompilationUnitWrapper, ReferencingDetail> oldCalleeList = oldUnit.getCalleeCompilationUnitList();
			for(ICompilationUnitWrapper oldCallee: oldCalleeList.keySet()){
				int index = oldModel.getICompilationUnitIndex(oldCallee);
				ReferencingDetail detail = oldCalleeList.get(oldCallee).clone();
				ICompilationUnitWrapper clonedCallee = clonedModel.getScopeCompilationUnitList().get(index);
				//clonedUnit.addCallee(clonedCallee);
				clonedUnit.getCalleeCompilationUnitList().put(clonedCallee, detail);
			}
		}
	}
	
	/**
	 * In this step, clone the member list, including its field/method and its corresponding belonging ICompilationUnitWrapper
	 */
	private UnitMemberWrapperList cloneMembers(ProgramModel clonedModel, ProgramModel oldModel){
		UnitMemberWrapperList clonedMembers = new UnitMemberWrapperList();
		for(UnitMemberWrapper oldMember: oldModel.getScopeMemberList()){			
			/**
			 * As the constructor method of Member need its ICompilationUnitWrapper, 
			 * we have to get the corresponding ICompilationUnitWrapper at the mean time.
			 */
			ICompilationUnitWrapper memberUnit = oldMember.getUnitWrapper();
			int index = oldModel.getICompilationUnitIndex(memberUnit);
			ICompilationUnitWrapper clonedMemberUnit = clonedModel.getScopeCompilationUnitList().get(index);						

			UnitMemberWrapper clonedMember = null;	
			if(oldMember instanceof FieldWrapper){
				clonedMember = new FieldWrapper(oldMember.getName(), ((FieldWrapper) oldMember).getType(), 
						clonedMemberUnit, oldMember.getTermFrequency(), oldMember.getDescription(), ((FieldWrapper) oldMember).getField());
			}else if(oldMember instanceof MethodWrapper){
				MethodWrapper methodWrapper = (MethodWrapper)oldMember;
				clonedMember = new MethodWrapper(methodWrapper.getName(), methodWrapper.getReturnType(), methodWrapper.getParameters(), 
						methodWrapper.isConstructor(), clonedMemberUnit, methodWrapper.getTermFrequency(), methodWrapper.getDescription(),
						methodWrapper.getMethod());
			}	
			/*if(member.getJavaElement() != null){
				if(member instanceof FieldWrapper){
					clonedMember = new FieldWrapper(((FieldWrapper) member).getField(), clonedMemberUnit);
				}else if(member instanceof MethodWrapper){
					clonedMember = new MethodWrapper(((MethodWrapper) member).getMethod(), clonedMemberUnit);
				}				
			}
			else{
				
			}*/
			clonedMembers.add(clonedMember);
		}
		return clonedMembers;		
	}
	
	private void cloneMemberRelations(ProgramModel clonedModel, ProgramModel oldModel){
		ArrayList<ICompilationUnitWrapper> clonedUnits = clonedModel.getScopeCompilationUnitList();
		ArrayList<ICompilationUnitWrapper> oldUnits = oldModel.getScopeCompilationUnitList();
		for(int i=0; i<oldUnits.size(); i++){
			ICompilationUnitWrapper oldUnit = oldUnits.get(i);
			ICompilationUnitWrapper clonedUnit = clonedUnits.get(i);
			
			/**
			 * clone member relation
			 */
			ArrayList<UnitMemberWrapper> oldMemberList = oldUnit.getMembers();
			for(UnitMemberWrapper oldMember: oldMemberList){
				int index = oldModel.getUnitMemberIndex(oldMember);
				if(index == -1){
					System.out.print(oldMember);
				}
				UnitMemberWrapper clonedMember = clonedModel.getScopeMemberList().get(index);
				clonedUnit.addMember(clonedMember);
			}
		}
		
	}

	/**
	 * In this step, clone the reference list, including its type, ASTnode and its corresponding referer/referee member
	 */
	private ArrayList<ProgramReference> cloneReference(ProgramModel clonedModel, ProgramModel model){
		ArrayList<ProgramReference> clonedReferences = new ArrayList<ProgramReference>();
		for(ProgramReference reference: model.getReferenceList()){
			/**
			 * As the constructor method of ProgramReference need its referer and referee member, 
			 * we have to get the corresponding UnitMemberWrappers at the mean time.
			 */
			UnitMemberWrapper referer = reference.getReferer();
			int refererIndex = model.getUnitMemberIndex(referer);
			if(refererIndex == -1){
				System.out.println(reference);
			}
			UnitMemberWrapper clonedReferer = clonedModel.scopeMemberList.get(refererIndex);			
			
			LowLevelGraphNode refereeNode = reference.getReferee();
			LowLevelGraphNode clonedReferee = null;
			if(refereeNode instanceof UnitMemberWrapper){
				UnitMemberWrapper referee = (UnitMemberWrapper)refereeNode;
				int refereeIndex = model.getUnitMemberIndex(referee);
				clonedReferee = clonedModel.scopeMemberList.get(refereeIndex);
			}
			else if(refereeNode instanceof ICompilationUnitWrapper){
				ICompilationUnitWrapper referee = (ICompilationUnitWrapper)refereeNode;
				int refereeIndex = model.getICompilationUnitIndex(referee);
				clonedReferee = clonedModel.scopeCompilationUnitList.get(refereeIndex);
			}
			
			
			ProgramReference clonedReference = new ProgramReference(clonedReferer, clonedReferee, 
					reference.getASTNode(), reference.getReferenceType());
			clonedReference.setReferenceType(reference.getReferenceType());
			
			clonedReferences.add(clonedReference);
			clonedReferer.addProgramReferee(clonedReference);
			clonedReferee.addProgramReferer(clonedReference);
			
		}
		return clonedReferences;
	}
	
	/*private void cloneReferenceRelations(ProgramModel clonedModel, ProgramModel model){
		UnitMemberWrapperList clonedMembers = clonedModel.scopeMemberList;
		UnitMemberWrapperList members = model.scopeMemberList;
		for(int i=0; i<members.size(); i++){
			UnitMemberWrapper member = members.get(i);
			UnitMemberWrapper clonedMember = clonedMembers.get(i);
			
			*//**
			 * clone reference relation of referer
			 *//*
			ArrayList<ProgramReference> refererList = member.refererPointList;
			for(ProgramReference refererPoint: refererList){
				int index = model.getProgramReferenceIndex(refererPoint);
				ProgramReference clonedRefererPoint = clonedModel.referenceList.get(index);
				clonedMember.addProgramReferer(clonedRefererPoint);
			}

			*//**
			 * clone reference relation of referee
			 *//*
			ArrayList<ProgramReference> refereeList = member.refereePointList;
			for(ProgramReference refereePoint: refereeList){
				int index = model.getProgramReferenceIndex(refereePoint);
				ProgramReference clonedRefereePoint = clonedModel.referenceList.get(index);
				clonedMember.addProgramReferee(clonedRefereePoint);
			}
		}		
	}*/	
	
	/**
	 * Compare address of two references, not only the content.
	 * @param reference
	 * @return
	 */
	public ProgramReference findReference(ProgramReference reference){
		for(ProgramReference ref: referenceList){
			if(ref.equals(reference)){
				return ref;
			}
		}
		
		return null;
	}
	
	public CloneInstance findCloneInstance(CloneInstance instance){
		for(CloneSet set: this.cloneSets){
			for(CloneInstance ins: set.getInstances()){
				if(ins.equals(instance)){
					return ins;
				}
			}
		}
		
		return null;
	}
	
	public CloneSet findCloneSet(String id){
		for(CloneSet set: this.cloneSets){
			if(set.getId().equals(id)){
				return set;
			}
		}
		
		return null;
	}
	
	public CloneSet findCloneSet(CloneInstance instance){
		CloneInstance ins = findCloneInstance(instance);
		if(ins != null){
			return ins.getSet();
		}
		
		return null;
	}
	
	public ArrayList<CloneSet> findCloneSet(ArrayList<UnitMemberWrapper> refactoringPalce){
		ArrayList<CloneSet> setList = new ArrayList<>();
		for(CloneSet set: this.cloneSets){
			for(CloneInstance ins: set.getInstances()){
				for(UnitMemberWrapper member: refactoringPalce){
					if(ins.getMember().equals(member) && !setList.contains(set)){
						setList.add(set);
					}					
				}
			}
		}
		
		return setList;
	}
	
	public void updateUnitCallingRelationByMemberRelations(){
		/**
		 * clear original call relations between compilation unit.
		 */
		for(ICompilationUnitWrapper unitWrapper: this.scopeCompilationUnitList){
			unitWrapper.setCalleeCompilationUnitList(new HashMap<ICompilationUnitWrapper, ReferencingDetail>());
			unitWrapper.setCallerCompilationUnitList(new HashMap<ICompilationUnitWrapper, ReferencingDetail>());
		}
		
		for(ProgramReference reference: this.getReferenceList()){
			UnitMemberWrapper refererMember = reference.getReferer();
			ICompilationUnitWrapper refererUnit = refererMember.getUnitWrapper();
			
			ICompilationUnitWrapper refereeUnit = null;
			LowLevelGraphNode refereeNode = reference.getReferee();
			
			int referencingType = ReferencingDetail.REFER;
			if(refereeNode instanceof UnitMemberWrapper){
				UnitMemberWrapper refereeMember = (UnitMemberWrapper)refereeNode;
				refereeUnit = refereeMember.getUnitWrapper();			
				
				if(refereeMember instanceof MethodWrapper){
					referencingType = ((MethodWrapper)refereeMember).isConstructor()? 
							ReferencingDetail.NEW : ReferencingDetail.REFER;
				}
			}
			else if(refereeNode instanceof ICompilationUnitWrapper){
				refereeUnit = (ICompilationUnitWrapper)refereeNode;
			}
			
			/**
			 * In current philosophy, if some methods of a super class are invoked by some methods of its subclasses,
			 * I will not build a dependency relation between the super class and the subclass, because the inheritance
			 * relation has already indicated the dependency relation. 
			 */
			if(!refererUnit.equals(refereeUnit) && !refererUnit.getAllAncestors().contains(refereeUnit)){
				refererUnit.addCallee(refereeUnit, referencingType);
				refereeUnit.addCaller(refererUnit, referencingType);
				
				refererUnit.putReferringDetail(refereeUnit, reference.getASTNode());
			}
		}
	}
	
	/**
	 * @return the scopeCompilationUnitList
	 */
	public ArrayList<ICompilationUnitWrapper> getScopeCompilationUnitList() {
		return scopeCompilationUnitList;
	}
	
	public ArrayList<ICompilationUnit> getScopeRawCompilationUnitList(){
		ArrayList<ICompilationUnit> units = new ArrayList<>();
		for(ICompilationUnitWrapper wrapper: this.scopeCompilationUnitList){
			units.add(wrapper.getCompilationUnit());
		}
		
		return units;
	}

	/**
	 * @param scopeCompilationUnitList the scopeCompilationUnitList to set
	 */
	public void setScopeCompilationUnitList(ArrayList<ICompilationUnitWrapper> scopeCompilationUnitList) {
		this.scopeCompilationUnitList = scopeCompilationUnitList;
	}
	
	public ICompilationUnitWrapper findUnit(String fullQualifiedName){
		for(ICompilationUnitWrapper unit: this.scopeCompilationUnitList){
			//ICompilationUnitWrapper wrapper = new ICompilationUnitWrapper(unit, false);
			if(unit.getFullQualifiedName().equals(fullQualifiedName)){
				return unit;
			}
		}
		
		return null;
	}
	
	public int getICompilationUnitIndex(ICompilationUnitWrapper u){
		for(int i=0; i<this.scopeCompilationUnitList.size(); i++){
			ICompilationUnitWrapper unit = this.scopeCompilationUnitList.get(i);
			if(unit.getFullQualifiedName().equals(u.getFullQualifiedName())){
				return i;
			}
		}
		
		return -1;

	}
	
	public int getUnitMemberIndex(UnitMemberWrapper u){
		for(int i=0; i<this.scopeMemberList.size(); i++){
			UnitMemberWrapper member = this.scopeMemberList.get(i);
			if(member.equals(u)){
				return i;
			}
		}
		
		return -1;

	}
	
	/**
	 * compare the address of program reference, this require the input r should be the element inside reference list.
	 * @param r
	 * @return
	 */
	public int getProgramReferenceIndexByAddress(ProgramReference r){
		for(int i=0; i<this.referenceList.size(); i++){
			ProgramReference reference = this.referenceList.get(i);
			if(reference == r){
				return i;
			}
		}
		
		return -1;
	}
	
	public ICompilationUnitWrapper findUnit(Type type){
		String packageName = type.getPackageName();
		String typeName = type.getName();
		
		String identifier = packageName+"."+typeName;
		
		return findUnit(identifier);
	}
	
	public void removeUnit(ICompilationUnitWrapper toBeRemovedUnit){
		this.scopeCompilationUnitList.remove(toBeRemovedUnit);
		for(ICompilationUnitWrapper unit: this.scopeCompilationUnitList){
			unit.getCalleeCompilationUnitList().remove(toBeRemovedUnit);
			unit.getCallerCompilationUnitList().remove(toBeRemovedUnit);
		}
		
		/**
		 * remove corresponding method and dependency relation.
		 */
		ArrayList<UnitMemberWrapper> toBeRemovedOnes = new ArrayList<>();
		for(UnitMemberWrapper member: this.scopeMemberList){
			if(member.getUnitWrapper().equals(toBeRemovedUnit)){
				toBeRemovedOnes.add(member);
			}
		}
		
		for(UnitMemberWrapper member: toBeRemovedOnes){
			this.scopeMemberList.remove(member);
		}
		
		for(UnitMemberWrapper member: this.scopeMemberList){
			for(UnitMemberWrapper toBeRemovedMember: toBeRemovedOnes){
				member.getCalleeList(ReferencingDetail.ALL).remove(toBeRemovedMember);
				member.getCallerList(ReferencingDetail.ALL).remove(toBeRemovedMember);
			}
		}
	}
	
	public UnitMemberWrapper findMember(UnitMemberWrapper member){
		return getScopeMemberList().findMember(member);
	}
	
	public ArrayList<ProgramReference> findProgramReferenceByCallerMember(UnitMemberWrapper callerMember){
		ArrayList<ProgramReference> references = new ArrayList<>();
		for(ProgramReference reference: this.referenceList){
			if(reference.getReferer().equals(callerMember)){
				references.add(reference);
			}
		}
		
		return references;
	}

	/**
	 * @return the scopeMemberList
	 */
	public UnitMemberWrapperList getScopeMemberList() {
		return scopeMemberList;
	}

	/**
	 * @param scopeMemberList the scopeMemberList to set
	 */
	public void setScopeMemberList(UnitMemberWrapperList scopeMemberList) {
		this.scopeMemberList = scopeMemberList;
	}

	/**
	 * @return the referenceList
	 */
	public ArrayList<ProgramReference> getReferenceList() {
		return referenceList;
	}

	/**
	 * @param referenceList the referenceList to set
	 */
	public void setReferenceList(ArrayList<ProgramReference> referenceList) {
		this.referenceList = referenceList;
	}

	/**
	 * @return the cloneSets
	 */
	public ArrayList<CloneSet> getCloneSets() {
		return cloneSets;
	}

	/**
	 * @param cloneSets the cloneSets to set
	 */
	public void setCloneSets(ArrayList<CloneSet> cloneSets) {
		this.cloneSets = cloneSets;
	}

	/**
	 * Given a unit, find other unit in this model
	 * @param unit
	 * @return
	 */
	public ArrayList<ICompilationUnitWrapper> findOtherUnits(
			ICompilationUnitWrapper unit) {
		ArrayList<ICompilationUnitWrapper> otherUnits = new ArrayList<>();
		for(ICompilationUnitWrapper otherUnit: this.scopeCompilationUnitList){
			if(!unit.equals(otherUnit)){
				otherUnits.add(otherUnit);
			}
		}
		return otherUnits;
	}
	
	public void detectClone(ProgramModel model){
		
		if(this.cloneSets != null){
			return;
		}
		
		ArrayList<JCCDFile> fileList = new ArrayList<JCCDFile>();
		HashMap<String, ICompilationUnitWrapper> map = new HashMap<>();
		
		for(ICompilationUnitWrapper unit: model.getScopeCompilationUnitList()){
			IResource resource = unit.getCompilationUnit().getResource();			
			fileList.add(new JCCDFile(resource.getRawLocation().toFile()));
			
			String path = resource.getRawLocation().toFile().getAbsolutePath();
			map.put(path, unit);
		}
		
		APipeline<?> detector = new ASTDetector();
		JCCDFile[] files = fileList.toArray(new JCCDFile[0]);
		detector.setSourceFiles(files);
		
		detector.addOperator(new RemoveAnnotations());
		//detector.addOperator(new RemoveSimpleMethods());
		detector.addOperator(new RemoveSemicolons());
		detector.addOperator(new RemoveAssertions());
		detector.addOperator(new RemoveEmptyBlocks());		
		detector.addOperator(new GeneralizeArrayInitializers());
		detector.addOperator(new GeneralizeClassDeclarationNames());
		detector.addOperator(new GeneralizeMethodArgumentTypes());
		detector.addOperator(new GeneralizeMethodReturnTypes());
		detector.addOperator(new GeneralizeVariableDeclarationTypes());
		detector.addOperator(new GeneralizeMethodCallNames());
		detector.addOperator(new GeneralizeVariableDeclarationTypes());
		detector.addOperator(new GeneralizeVariableNames());
		
		SimilarityGroupManager manager = detector.process();
		SimilarityGroup[] simGroups = manager.getSimilarityGroups();
		
		ArrayList<CloneSet> cloneSets = convertToCloneSets(simGroups, map);
		
		this.cloneSets = cloneSets;
	}

	/**
	 * In this method, clone sets will be constructed, in which every clone instance will contain not only file name, line numbers,
	 * but its related unit member and program reference as well.
	 * 
	 * @param simGroups
	 * @return
	 */
	private ArrayList<CloneSet> convertToCloneSets(SimilarityGroup[] simGroups, HashMap<String, ICompilationUnitWrapper> map) {
		
		ArrayList<CloneSet> sets = new ArrayList<>();
		
		for (int i = 0; i < simGroups.length; i++) {
			final ASourceUnit[] nodes = simGroups[i].getNodes();
			
			CloneSet set = new CloneSet(String.valueOf(simGroups[i].getGroupId()));
			
			for (int j = 0; j < nodes.length; j++) {
				
				final SourceUnitPosition minPos = APipeline.getFirstNodePosition((ANode) nodes[j]);
				final SourceUnitPosition maxPos = APipeline.getLastNodePosition((ANode) nodes[j]);

				ANode fileNode = (ANode) nodes[j];
				while (fileNode.getType() != NodeTypes.FILE.getType()) {
					fileNode = fileNode.getParent();
				}
				
				CloneInstance cloneInstance = new CloneInstance(set, fileNode.getText(), 
						minPos.getLine(), maxPos.getLine());
				
				//set.getInstances().add(cloneInstance);
				
				UnitMemberWrapper member = findResidingUnitMember(cloneInstance, map);
				if(member != null){
					set.getInstances().add(cloneInstance);					
				}
			}
			
			if(set.getInstances().size() >= 2){
				sets.add(set);				
			}
		}
		
		return sets;
	}

	/**
	 * @param cloneInstance
	 * @param map
	 * @return
	 */
	private UnitMemberWrapper findResidingUnitMember(
			CloneInstance cloneInstance,
			HashMap<String, ICompilationUnitWrapper> map) {
		ICompilationUnitWrapper unitWrapper = map.get(cloneInstance.getFileName());
		CompilationUnit unit = unitWrapper.getJavaUnit();
		for(UnitMemberWrapper member: unitWrapper.getMembers()){
			ASTNode declaringNode = member.getJavaElement();
			int startLine = unit.getLineNumber(declaringNode.getStartPosition());
			int endLine = unit.getLineNumber(declaringNode.getStartPosition()+declaringNode.getLength());
			
			/**
			 * Judge whether the cloned code fragment is located in the right scope.
			 */
			if(cloneInstance.getStartLineNumber() >= startLine && cloneInstance.getEndLineNumber() <= endLine){
				cloneInstance.setMember(member);
				
				/**
				 * Checking the program reference covered by this cloned code fragment.
				 */
				for(ProgramReference reference: member.getRefereePointList()){
					int lineNumber = unit.getLineNumber(reference.getASTNode().getStartPosition());
					if(lineNumber >= cloneInstance.getStartLineNumber() && lineNumber <= cloneInstance.getEndLineNumber()){
						cloneInstance.getCoveringReferenceList().add(reference);
					}
				}
				
				return member;
			}
		}
		
		
		return null;
	}
}
