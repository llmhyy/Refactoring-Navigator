/**
 * 
 */
package reflexactoring.diagram.action;

import java.util.ArrayList;
import java.util.List;

import reflexactoring.diagram.action.semantic.TFIDF;
import reflexactoring.diagram.bean.HeuristicModuleUnitMap;
import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.bean.ModuleWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;
import reflexactoring.diagram.util.Settings;

/**
 * This class is used to align the high level model with low level model.
 * 
 * @author linyun
 *
 */
public class ModelMapper {
	/**
	 * The result should be that: for each module in moduleList, the module wrapper class
	 * will have a list of mapping compilationUnit; in contrast, for each compilationUnit
	 * in compilationUnit, the compilation unit wrapper will have a corresponding mapped module.
	 * 
	 * @param moduleList
	 * @param compilationUnitList
	 */
	public void generateMappingRelation(ArrayList<ModuleWrapper> moduleList,
			ArrayList<ICompilationUnitWrapper> compilationUnitList) {
		double[][] overallSimilarityTable = initializeOverallSimilarityTable(moduleList, compilationUnitList);
		
		/**
		 * should take care that one compilation unit can be mapped to only one module,
		 * on the contrary, one module can be mapped to many compilation unit.
		 */
		for(int j=0; j<compilationUnitList.size(); j++){
			ICompilationUnitWrapper unit = compilationUnitList.get(j);
			
			/**
			 * see whether user has specified mapping rules for such a compilation unit.
			 */
			HeuristicModuleUnitMap map = Settings.heuristicModuleUnitMapList.findHeuristicMapping(unit);
			if(map != null){
				ModuleWrapper module = map.getModule();
				unit.setMappingModule(module);
				module.getMappingList().add(unit);
				
				continue;
			}
			
			/**
			 * map module and compilation unit by similarity.
			 */
			int index = 0;
			double maxValue = -1.0;
			for(int i=0; i<moduleList.size();i++){
				if(overallSimilarityTable[i][j] >= Double.valueOf(ReflexactoringUtil.getMappingThreshold())
						&& overallSimilarityTable[i][j] >= maxValue){
					index = i;
					maxValue = overallSimilarityTable[i][j];
				}
			}
			
			if(maxValue != -1.0){
				ModuleWrapper module = moduleList.get(index);
				unit.setMappingModule(module);
				module.getMappingList().add(unit);
			}
		}
	}
	
	/**
	 * generate the overall similarity table in which the overall similarity bewteen
	 * each module and each compilation unit is computed. 
	 * @param moduleList
	 * @param compilationUnitList
	 * @return
	 */
	private double[][] initializeOverallSimilarityTable(ArrayList<ModuleWrapper> moduleList,
			ArrayList<ICompilationUnitWrapper> compilationUnitList){
		double[][] semanticSimilarityTable = generateSemanticSimilarityTable(moduleList, compilationUnitList);
		double[][] structuralSimilarityTable = generateStructuralSimilarityTable(semanticSimilarityTable, moduleList, compilationUnitList);
		
		/**
		 * after the above calculation, the dimensions of both semantic similarity table and
		 * structural similarity table should be the same.
		 */
		int m = moduleList.size();
		int n = compilationUnitList.size();
		double[][] overallSimilarity = new double[m][n];
		for(int i=0; i<m; i++){
			for(int j=0; j<n; j++){
				overallSimilarity[i][j] = (semanticSimilarityTable[i][j] + structuralSimilarityTable[i][j])/2;
			}
		}
		
		return overallSimilarity;
	}
	
	/**
	 * calculate the semantic (lexical) similarities between modules and compilation units.
	 * @param moduleList
	 * @param compilationUnitList
	 * @return
	 */
	private double[][] generateSemanticSimilarityTable(ArrayList<ModuleWrapper> moduleList,
			ArrayList<ICompilationUnitWrapper> compilationUnitList){
		double[][] similarityTable = new double[moduleList.size()][compilationUnitList.size()];
		
		for(int i=0; i<moduleList.size(); i++){
			ModuleWrapper module = moduleList.get(i);
			/**
			 * The first vector represents module description while the following vectors represent
			 * compilation unit description.
			 */
			List<double[]> vectorList = generateTFIDFTable(module, compilationUnitList);
			double[] moduleVector = vectorList.get(0);
			for(int k=1; k<vectorList.size(); k++){
				double[] compilationUnitVector = vectorList.get(k);
				double similarity = ReflexactoringUtil.computeEuclideanSimilarity(moduleVector, compilationUnitVector);
				similarityTable[i][k-1] = similarity;
			}
		}
		
		return similarityTable;
	}
	
	/**
	 * The first vector represents module description while the following vectors represent
	 * compilation unit description.
	 * @param module
	 * @param compilationUnitList
	 * @return
	 */
	private List<double[]> generateTFIDFTable(ModuleWrapper module,
			ArrayList<ICompilationUnitWrapper> compilationUnitList){
		TFIDF tfidf = new TFIDF(false);
		
		ArrayList<String> documentList = new ArrayList<>();
		String content = module.getDescription();
		content = (content == null)? "" : content;
		documentList.add(ReflexactoringUtil.performStemming(content.toLowerCase()));
		
		for(ICompilationUnitWrapper compiltionUnit: compilationUnitList){
			documentList.add(compiltionUnit.getDescription());
		}
		
		return tfidf.getTFIDFfromString(documentList);
	}
	
	/**
	 * transmit the semantic similarity with page ranking algorithm.
	 * @param semanticSimilarityTable
	 * @param moduleList
	 * @param compilationUnitList
	 * @return
	 */
	private double[][] generateStructuralSimilarityTable(double[][] semanticSimilarityTable, 
			ArrayList<ModuleWrapper> moduleList, ArrayList<ICompilationUnitWrapper> compilationUnitList){
		double[][] similarityTable = new double[moduleList.size()][compilationUnitList.size()];
		
		return similarityTable;
	}
}
