package com.eason.easyedit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.eason.listener.Constant;
import com.eason.listener.UndoManager;

public class FileEditor extends EditorPart {
	private MyStyledText text;
	private String contentString;
	private boolean myDirty;
	public FileEditor() {
		super();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
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
	public void doSave(IProgressMonitor monitor) {
		try {
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			ByteArrayInputStream bi = new ByteArrayInputStream(text.getText()
					.getBytes());
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
	public void createPartControl(final Composite parent) {
		text = new MyStyledText(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setText(contentString);
		Font font = new Font(parent.getDisplay(), "Consolas", 10, SWT.NORMAL);
		text.setFont(font);
		GridData txtGd = new GridData(SWT.FILL, SWT.CENTER, false, false);
		txtGd.heightHint = 200;
		txtGd.widthHint = 400;
		text.setLayoutData(txtGd);

		text.setKeyBinding('A' | SWT.CTRL, ST.SELECT_ALL);
		text.setKeyBinding('Z' | SWT.CTRL, Constant.UNDO);
		text.setKeyBinding('Y' | SWT.CTRL, Constant.REDO);
		text.setKeyBinding('D' | SWT.CTRL, Constant.DELETE);
		text.setKeyBinding('F' | SWT.CTRL, Constant.FIND);

		UndoManager undoManager = new UndoManager(50);
		undoManager.connect(text);
		text.setUndoManager(undoManager);

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