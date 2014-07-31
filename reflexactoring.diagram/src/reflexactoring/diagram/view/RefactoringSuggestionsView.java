package reflexactoring.diagram.view;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import reflexactoring.diagram.action.smelldetection.bean.RefactoringSequence;

public class RefactoringSuggestionsView extends ViewPart {
	private int i = 0;
	private Composite parent;
	
	public RefactoringSuggestionsView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		parent.setLayout(new GridLayout());
		/*suggestionComposite = new Composite(parent, SWT.NONE);
		
		GridData data = new GridData(GridData.FILL, GridData.FILL, true, true);
		suggestionComposite.setLayoutData(data);*/
		
		Label text = new Label(parent, SWT.NONE);
		text.setText("Hello World" + (i++));;
	}
	
	public void refreshSuggestionsOnUI(ArrayList<RefactoringSequence> suggestions){
		for(Control control: parent.getChildren()){
			control.dispose();
		}
		
		//TODO
		Label text = new Label(parent, SWT.NONE);
		text.setText("Hello World" + (i++));;
		
		parent.layout();
	}

	@Override
	public void setFocus() {

	}

}
