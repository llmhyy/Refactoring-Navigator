/**
 * 
 */
package reflexactoring.diagram.util;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author linyun
 *
 */
public class JavaCodeUtil {
	public static void goToLine(IEditorPart editorPart, int startLine, int endLine) {

		if (!(editorPart instanceof ITextEditor)) {
			return;
		}

		ITextEditor editor = (ITextEditor) editorPart;
		IDocument document = editor.getDocumentProvider().getDocument(
				editor.getEditorInput());
		if (document != null) {
			
			IRegion startLineInfo = null;
			IRegion endLineInfo = null;
			try {
				// line count internally starts with 0, and not with 1 like in
				// GUI
				startLineInfo = document.getLineInformation(startLine - 1);
				endLineInfo = document.getLineInformation(endLine - 1);
			} catch (BadLocationException e) {
				// ignored because line number may not really exist in document,
				// we guess this...
				e.printStackTrace();
			}
			if (startLineInfo != null && endLineInfo != null) {
				editor.selectAndReveal(startLineInfo.getOffset(),
						(endLineInfo.getOffset() - startLineInfo.getOffset() + endLineInfo.getLength()));
			}
		}
	}
}
