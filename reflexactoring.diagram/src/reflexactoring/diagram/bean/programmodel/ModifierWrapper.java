/**
 * 
 */
package reflexactoring.diagram.bean.programmodel;

import org.eclipse.jdt.core.dom.Modifier;

/**
 * @author linyun
 *
 */
public class ModifierWrapper {
	public static final String PUBLIC = "public";
	public static final String PROTECTED = "protected";
	public static final String PRIVATE = "private";
	public static final String DEFAULT = "default";
	
	public static String parseSecurityModifer(int modifierFlag){
		String modifier = null;
		if(Modifier.isPublic(modifierFlag)){
			modifier = ModifierWrapper.PUBLIC;
		}
		else if(Modifier.isProtected(modifierFlag)){
			modifier = ModifierWrapper.PROTECTED;
		}
		else if(Modifier.isPrivate(modifierFlag)){
			modifier = ModifierWrapper.PRIVATE;
		}
		else{
			modifier = ModifierWrapper.DEFAULT;
		}
		
		return modifier;
	}
}
