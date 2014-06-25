/**
 * 
 */
package reflexactoring.diagram.action;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.ui.PartInitException;

import reflexactoring.diagram.bean.HeuristicModuleMemberStopMap;
import reflexactoring.diagram.bean.HeuristicModulePartFixMemberMap;
import reflexactoring.diagram.bean.HeuristicModuleUnitFixMemberMap;
import reflexactoring.diagram.bean.HeuristicModuleUnitMap;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleDependencyConfidence;
import reflexactoring.diagram.bean.ModuleDependencyConfidenceTable;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.bean.UnitMemberWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class UserInputMerger {
	
	
	public void mergeHeuristicMappingTable(){
		if(Settings.heuristicModuleUnitFixList != null){
			try {
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
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void mergeHeuristicFixMemberMappingTable(){
		if(Settings.heuristicModuleUnitMemberFixList != null){
			try {
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
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 */
	public void mergeHeuristicFixPartMemberMappingTable() {
		if(Settings.heuristicModuleMemberPartFixList != null){
			try {
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
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void mergeForbiddenModuleMemberTable(){
		if(Settings.heuristicModuleMemberStopMapList != null){
			try {
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
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void mergeConfidenceTable(){
		if(Settings.confidenceTable.size() > 0){
			try {
				ModuleDependencyConfidenceTable table = new ModuleDependencyConfidenceTable();
				
				ArrayList<ModuleWrapper> moduleList = ReflexactoringUtil.getModuleList(Settings.diagramPath);
				
				for(ModuleWrapper moduleWrapper: moduleList){
					double[] confidenceList = new double[moduleList.size()];
					for(int i=0; i<confidenceList.length; i++){
						Double conf = findCorrespondingConfidence(moduleWrapper, moduleList.get(i));
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
				
				Settings.confidenceTable = table;
			} catch (PartInitException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private Double findCorrespondingConfidence(ModuleWrapper sourceModule, ModuleWrapper targetModule){
		for(ModuleDependencyConfidence confidence: Settings.confidenceTable){
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
