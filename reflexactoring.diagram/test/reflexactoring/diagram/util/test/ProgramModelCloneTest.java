/**
 * 
 */
package reflexactoring.diagram.util.test;

import org.junit.Test;

import reflexactoring.diagram.bean.ProgramModel;
import reflexactoring.diagram.util.Settings;

/**
 * @author linyun
 *
 */
public class ProgramModelCloneTest {

	@Test
	public void test() {
		ProgramModel model = Settings.scope.clone();
		System.currentTimeMillis();
	}

}
