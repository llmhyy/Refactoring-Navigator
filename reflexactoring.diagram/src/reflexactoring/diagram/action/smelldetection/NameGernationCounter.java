/**
 * 
 */
package reflexactoring.diagram.action.smelldetection;

/**
 * This class is used to generate a count to avoid duplicatedly generated class name
 * @author linyun
 *
 */
public class NameGernationCounter {
	private static int counter = 0;
	
	public static int retrieveNumber(){
		return counter++;
	}
}
