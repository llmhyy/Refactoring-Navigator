/**
 * 
 */
package reflexactoring.diagram.util.test;

import static org.junit.Assert.*;

import org.junit.Test;

import reflexactoring.diagram.util.ReflexactoringUtil;

/**
 * @author linyun
 *
 */
public class UtilTest {

	@Test
	public void test() {
		String str1 = "10.51";
		String str2 = "10.51.1";
		String str3 = "&*aa.32";
		boolean flag1 = ReflexactoringUtil.checkNumber(str1);
		boolean flag2 = ReflexactoringUtil.checkNumber(str2);
		boolean flag3 = ReflexactoringUtil.checkNumber(str3);
		
		assertTrue(flag1);
		assertFalse(flag2);
		assertFalse(flag3);
	}

}
