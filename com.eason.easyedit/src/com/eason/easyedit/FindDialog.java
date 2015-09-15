package com.eason.easyedit;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FindDialog extends Dialog {
	private static FindDialog instance;
	
	public static FindDialog getInstance(Shell parent, StyledText searchComponet) {
		if (null == instance) {
			instance = new FindDialog(parent, searchComponet);
		}
		return instance;
	}

	private Text searchText;
	private StyledText searchContext;

	private void findNext(){
		String search = searchText.getText();
		Point p = searchContext.getSelectionRange();
		String rest = searchContext.getTextRange(p.x+p.y, searchContext.getText().length()-(p.x+p.y));
		int x = rest.indexOf(search);
		
		if(x != -1){
			x =  p.x + p.y + x;
			int y = x + search.length();
			searchContext.setSelection(x, y);
		} else if(p.x != 0 && p.y != 0){
			searchContext.setSelection(0, 0);
			findNext();
		}
	}
	
	private void findPrevious(){
		String search = searchText.getText();
		Point p = searchContext.getSelectionRange();
		String rest = searchContext.getTextRange(0, p.x);
		int x = rest.lastIndexOf(search);
		
		if(x != -1){
			int y = x + search.length();
			searchContext.setSelection(x, y);
		} else if(p.x != searchContext.getText().length() && p.y != 0){
			searchContext.setSelection(searchContext.getText().length(), searchContext.getText().length());
			findPrevious();
		}
	}
	
	protected FindDialog(Shell shell, StyledText searchComponet) {  
        super(shell);  
        this.searchContext = searchComponet;
    }  

    @Override  
    protected Control createDialogArea(Composite parent) {  
        Composite comp = new Composite(parent, SWT.None);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 1;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginBottom = 0;
        layout.marginTop = 0;
        layout.marginWidth = 0;
        layout.makeColumnsEqualWidth = true;
        comp.setLayout(layout);  
        Label lbl = new Label(comp, SWT.None);
        lbl.setText("Search Key:");
        searchText = new Text(comp, SWT.BORDER);  
        searchText.setLayoutData(new GridData(GridData.FILL_BOTH));
        return super.createDialogArea(parent);  
    }  

    @Override  
    protected Button createButton(Composite parent, int id, String label,  
            boolean defaultButton) {  
        return null;
    }  

    @Override  
    protected void initializeBounds() {  
        Composite compo = (Composite) getButtonBar();  
        Button prev = super.createButton(compo, IDialogConstants.BACK_ID, "Previous", false);
        prev.addListener(SWT.SELECTED, new Listener() {
			@Override
			public void handleEvent(Event paramEvent) {
				findPrevious();
			}
		});
        Button find = super.createButton(compo, IDialogConstants.NEXT_ID, "Next", true);
        find.addListener(SWT.SELECTED, new Listener() {
			@Override
			public void handleEvent(Event paramEvent) {
				findNext();
			}
		});
        super.createButton(compo, IDialogConstants.NO_ID, "Cancel", false);  
        super.initializeBounds();  
    }  

    @Override  
    protected Point getInitialSize() {  
        return new Point(300, 180);
    }  

    @Override  
    protected Button getButton(int id) {  
        return super.getButton(id);  
    }  
}
