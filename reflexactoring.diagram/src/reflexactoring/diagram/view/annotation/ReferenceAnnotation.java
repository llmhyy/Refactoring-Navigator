/**
 * 
 */
package reflexactoring.diagram.view.annotation;

import org.eclipse.jface.text.source.Annotation;

/**
 * @author linyun
 *
 */
public class ReferenceAnnotation extends Annotation {
	public static String ANNOTATION_TYPE = "reflexactoring.diagram.specification.hint";
	
	public ReferenceAnnotation(boolean isPersistent, String text){
		super(ReferenceAnnotation.ANNOTATION_TYPE, isPersistent, text);
	}
}
