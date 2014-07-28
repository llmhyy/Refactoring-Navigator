/**
 * 
 */
package reflexactoring.diagram.action.semantic;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TagElement;

import reflexactoring.diagram.bean.ICompilationUnitWrapper;
import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * This class is used to extract interested key word from a compilation unit.
 * The interesting keyword is split and stemmed.
 * 
 * @author linyun
 * 
 */
public class TokenExtractor {

	private ICompilationUnitWrapper relevantCompilationUnit;
	
	public TokenExtractor(ICompilationUnitWrapper relevantCompilationUnit){
		this.relevantCompilationUnit = relevantCompilationUnit;
	}
	
	public String extractTokens(ASTNode node) {
		final StringBuffer buffer = new StringBuffer();
		
		node.accept(new ASTVisitor() {
			public boolean visit(SimpleName name) {

				String identifier = name.getIdentifier();
				String[] tokens = ReflexactoringUtil.mixedSplitting(identifier);

				for (String token : tokens) {
					if (token.length() > 1) {
						token = token.toLowerCase();
						token = WordNetDict.getInstance().getStem(token);
						buffer.append(token + " ");
					}
				}

				return true;
			}
			
			public boolean visit(Javadoc doc){
				String source;
				try {
					source = relevantCompilationUnit.getCompilationUnit().getSource();
					
					for(Object obj: doc.tags()){
						TagElement element = (TagElement)obj;
						
						String content = source.substring(element.getStartPosition(), element.getStartPosition() + element.getLength());
						content = ReflexactoringUtil.removeDelimit(content);
						content = ReflexactoringUtil.removeStopWord(content);
						//content = ReflexactoringUtil.performStemming(content);
						
						buffer.append(content + " ");
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}

		        return true;
			}
			
			
		});

		String content = buffer.toString();
		if(content.length() > 0){
			content = content.substring(0, content.length() - 1);			
		}

		return content;
	}

}
