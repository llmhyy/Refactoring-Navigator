/**
 * 
 */
package reflexactoring.diagram.action.semantic;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SimpleName;

import reflexactoring.diagram.util.ReflexactoringUtil;


/**
 * This class is used to extract interested key word from a compilation unit. The interesting
 * keyword is split and stemmed.
 * 
 * @author linyun
 *
 */
public class TokenExtractor {
	
	public String extractTokens(ICompilationUnit compilationUnit){
		
		@SuppressWarnings("deprecation")
		CompilationUnit unit = AST.parseCompilationUnit(compilationUnit, false);
		final StringBuffer buffer = new StringBuffer();
		
		unit.accept(new ASTVisitor() {
			public boolean visit(SimpleName name){
				
				String identifier = name.getIdentifier();
				String[] tokens = ReflexactoringUtil.mixedSplitting(identifier);
				
				for(String token: tokens){
					if(token.length() > 1){
						token = token.toLowerCase();
						token = WordNetDict.getInstance().getStem(token);
						buffer.append(token + " ");											
					}
				}
				
				return false;
			}
		});
		
		String content = buffer.toString();
		content = content.substring(0, content.length()-1);
		
		return content;
	}
	
	
}
