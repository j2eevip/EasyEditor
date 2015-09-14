package com.eason.easyedit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class FileEditor extends EditorPart {
	private CodeLineStyler lineStyler;
	private StyledText text;
	private FindDialog dialog;
	private String contentString;
	private boolean myDirty;

	public FileEditor() {
		super();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		lineStyler = new CodeLineStyler();
		contentString = "";
		setSite(site);
		setInput(input);
	}
	
	@Override
	public void setInput(IEditorInput input) {
		IFile file = ((IFileEditorInput) input).getFile();
		setPartName(file.getName());
		setTitleToolTip(file.getName());
		try {
			InputStream is = file.getContents(false);
			byte[] inputByte = new byte[is.available()];
			is.read(inputByte);
			contentString = new String(inputByte);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.setInput(input);
	}
	
	@Override
	public void dispose() {
		lineStyler.disposeColors();
		setTitleImage(null);
		super.dispose();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			ByteArrayInputStream bi = new ByteArrayInputStream(text.getText().getBytes());
			file.setContents(bi, true, false, monitor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			myDirty = false;
			firePropertyChange(IEditorPart.PROP_DIRTY);
			text.redraw();
		}
	}
	
	@Override
	public boolean isDirty() {
		return myDirty;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		text = new StyledText(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		dialog = new FindDialog(parent.getShell(), text);
		text.setText(contentString);
		Font font = new Font(parent.getDisplay(), "Consolas", 10, SWT.NORMAL);
		text.setFont(font);
		text.addLineStyleListener(lineStyler);
		text.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		GridData spec = new GridData();
		spec.horizontalAlignment = GridData.FILL;
		spec.grabExcessHorizontalSpace = true;
		spec.verticalAlignment = GridData.FILL;
		spec.grabExcessVerticalSpace = true;
		text.setLayoutData(spec);
		
		text.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (((event.stateMask & SWT.CTRL) == SWT.CTRL) && (event.keyCode == 'f')) {
					FileEditor.this.dialog.open();
				}
			}
		});

		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				myDirty = true;
				FileEditor.this.firePropertyChange(IEditorPart.PROP_DIRTY);
				text.redraw();
			}
		});
	}
	
	public boolean isSaveAsAllowed() {
		return false;
	}

	public void doSaveAs() {
	}

	public void setFocus() {
	}
}