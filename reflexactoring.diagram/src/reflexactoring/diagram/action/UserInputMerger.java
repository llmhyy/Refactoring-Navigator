/**
 * 
 */
package reflexactoring.diagram.action;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.ui.PartInitException;

import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.heuristics.HeuristicModuleMemberStopMap;
import reflexactoring.diagram.bean.heuristics.HeuristicModulePartFixMemberMap;
import reflexactoring.diagram.bean.heuristics.HeuristicModuleUnitFixMemberMap;
import reflexactoring.diagram.bean.heuristics.HeuristicModuleUnitMap;
import reflexactoring.diagram.bean.heuristics.ModuleCreationConfidence;
import reflexactoring.diagram.bean.heuristics.ModuleCreationConfidenceTable;
import reflexactoring.diagram.bean.heuristics.ModuleDependencyConfidence;
import reflexactoring.diagram.bean.heuristics.ModuleDependencyConfidenceTable;
import reflexactoring.diagram.bean.heuristics.ModuleExtendConfidence;
import reflexactoring.diagram.bean.heuristics.ModuleExtendConfidenceTable;
import reflexactoring.diagram.bean.programmodel.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.programmodel.UnitMemberWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class UserInputMerger {
	
	
	public void mergeHeuristicMappingTable(){
		if(Settings.heuristicModuleUnitFixList != null){
			ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
			
			Iterator<HeuristicModuleUnitMap> iterator = Settings.heuristicModuleUnitFixList.iterator();
			while(iterator.hasNext()){
				HeuristicModuleUnitMap map = iterator.next();
				
				ModuleWrapper moduleWrapper = map.getModule();
				ICompilationUnitWrapper unit = map.getUnit();
				
				ModuleWrapper correspondingModule = ReflexactoringUtil.findModule(moduleList, moduleWrapper.getModule());
				ICompilationUnitWrapper correspondingUnit = Settings.scope.findUnit(unit.getFullQualifiedName());
			
				if(correspondingModule == null || correspondingUnit == null){
					iterator.remove();
				}
				else{
					map.setModule(correspondingModule);
					map.setUnit(correspondingUnit);
				}
			}
		}
	}
	
	public void mergeHeuristicFixMemberMappingTable(){
		if(Settings.heuristicModuleUnitMemberFixList != null){
			ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
			
			Iterator<HeuristicModuleUnitFixMemberMap> iterator = Settings.heuristicModuleUnitMemberFixList.iterator();
			while(iterator.hasNext()){
				HeuristicModuleUnitFixMemberMap map = iterator.next();
				
				ModuleWrapper moduleWrapper = map.getModule();
				ICompilationUnitWrapper unit = map.getUnit();
				
				ModuleWrapper correspondingModule = ReflexactoringUtil.findModule(moduleList, moduleWrapper.getModule());
				ICompilationUnitWrapper correspondingUnit = Settings.scope.findUnit(unit.getFullQualifiedName());
			
				if(correspondingModule == null || correspondingUnit == null){
					iterator.remove();
				}
				else{
					map.setModule(correspondingModule);
					map.setUnit(correspondingUnit);
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public void mergeHeuristicFixPartMemberMappingTable() {
		if(Settings.heuristicModuleMemberPartFixList != null){
			ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
			
			Iterator<HeuristicModulePartFixMemberMap> iterator = Settings.heuristicModuleMemberPartFixList.iterator();
			while(iterator.hasNext()){
				HeuristicModulePartFixMemberMap map = iterator.next();
				
				ModuleWrapper moduleWrapper = map.getModule();
				UnitMemberWrapper memberWrapper = map.getMember();
				
				ModuleWrapper correspondingModule = ReflexactoringUtil.findModule(moduleList, moduleWrapper.getModule());
				UnitMemberWrapper correspondingMember = Settings.scope.findMember(memberWrapper);
			
				if(correspondingModule == null || correspondingMember == null){
					iterator.remove();
				}
				else{
					map.setModule(correspondingModule);
					map.setMember(correspondingMember);
				}
			}
		}
		
	}

	public void mergeForbiddenModuleMemberTable(){
		if(Settings.heuristicModuleMemberStopMapList != null){
			ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
			
			Iterator<HeuristicModuleMemberStopMap> iterator = Settings.heuristicModuleMemberStopMapList.iterator();
			while(iterator.hasNext()){
				HeuristicModuleMemberStopMap map = iterator.next();
				
				ModuleWrapper moduleWrapper = map.getModule();
				UnitMemberWrapper memberWrapper = map.getMember();
				
				ModuleWrapper correspondingModule = ReflexactoringUtil.findModule(moduleList, moduleWrapper.getModule());
				UnitMemberWrapper correspondingMember = Settings.scope.findMember(memberWrapper);
			
				if(correspondingModule == null || correspondingMember == null){
					iterator.remove();
				}
				else{
					map.setModule(correspondingModule);
					map.setMember(correspondingMember);
				}
			}
		}
	}
	
	public void mergeDependencyConfidenceTable(){
		if(Settings.dependencyConfidenceTable.size() > 0){
			ModuleDependencyConfidenceTable table = new ModuleDependencyConfidenceTable();
			
			ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
			
			for(ModuleWrapper moduleWrapper: moduleList){
				double[] confidenceList = new double[moduleList.size()];
				for(int i=0; i<confidenceList.length; i++){
					Double conf = findCorrespondingDependencyConfidence(moduleWrapper, moduleList.get(i));
					if(conf == null){
						confidenceList[i] = 0.5;							
					}
					else{
						confidenceList[i] = conf;
					}
				}
				
				ModuleDependencyConfidence confidence = 
						new ModuleDependencyConfidence(moduleWrapper, moduleList, confidenceList);
				table.add(confidence);
			}
			
			Settings.dependencyConfidenceTable = table;
			
		}
	}
	
	public void mergeExtendConfidenceTable(){
		if(Settings.extendConfidenceTable.size() > 0){
			ModuleExtendConfidenceTable table = new ModuleExtendConfidenceTable();
			
			ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
			
			for(ModuleWrapper moduleWrapper: moduleList){
				double[] confidenceList = new double[moduleList.size()];
				for(int i=0; i<confidenceList.length; i++){
					Double conf = findCorrespondingExtendConfidence(moduleWrapper, moduleList.get(i));
					if(conf == null){
						confidenceList[i] = 0.5;							
					}
					else{
						confidenceList[i] = conf;
					}
				}
				
				ModuleExtendConfidence confidence = 
						new ModuleExtendConfidence(moduleWrapper, moduleList, confidenceList);
				table.add(confidence);
			}
			
			Settings.extendConfidenceTable = table;
			
		}
	}
	
	public void mergeCreationConfidenceTable(){
		if(Settings.creationConfidenceTable.size() > 0){
			ModuleCreationConfidenceTable table = new ModuleCreationConfidenceTable();
			
			ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
			
			for(ModuleWrapper moduleWrapper: moduleList){
				double[] confidenceList = new double[moduleList.size()];
				for(int i=0; i<confidenceList.length; i++){
					Double conf = findCorrespondingCreationConfidence(moduleWrapper, moduleList.get(i));
					if(conf == null){
						confidenceList[i] = 0.5;							
					}
					else{
						confidenceList[i] = conf;
					}
				}
				
				ModuleCreationConfidence confidence = 
						new ModuleCreationConfidence(moduleWrapper, moduleList, confidenceList);
				table.add(confidence);
			}
			
			Settings.creationConfidenceTable = table;
			
		}
	}
	
	private Double findCorrespondingDependencyConfidence(ModuleWrapper sourceModule, ModuleWrapper targetModule){
		for(ModuleDependencyConfidence confidence: Settings.dependencyConfidenceTable){
			if(confidence.getModule().getName().equals(sourceModule.getName())){
				
				for(int i=0; i<confidence.getModuleList().size(); i++){
					ModuleWrapper module = confidence.getModuleList().get(i);
					if(module.getName().equals(targetModule.getName())){
						return confidence.getConfidenceList()[i];
					}
				}
			}
		}
		
		return null;
	}
	
	private Double findCorrespondingExtendConfidence(ModuleWrapper sourceModule, ModuleWrapper targetModule){
		for(ModuleExtendConfidence confidence: Settings.extendConfidenceTable){
			if(confidence.getModule().getName().equals(sourceModule.getName())){
				
				for(int i=0; i<confidence.getModuleList().size(); i++){
					ModuleWrapper module = confidence.getModuleList().get(i);
					if(module.getName().equals(targetModule.getName())){
						return confidence.getConfidenceList()[i];
					}
				}
			}
		}
		
		return null;
	}
	
	private Double findCorrespondingCreationConfidence(ModuleWrapper sourceModule, ModuleWrapper targetModule){
		for(ModuleCreationConfidence confidence: Settings.creationConfidenceTable){
			if(confidence.getModule().getName().equals(sourceModule.getName())){
				
				for(int i=0; i<confidence.getModuleList().size(); i++){
					ModuleWrapper module = confidence.getModuleList().get(i);
					if(module.getName().equals(targetModule.getName())){
						return confidence.getConfidenceList()[i];
					}
				}
			}
		}
		
		return null;
	}
}
