package com.eason.easyedit;

import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

import com.eason.listener.Constant;
import com.eason.listener.UndoManager;

public class MyStyledText extends StyledText {
	public MyStyledText(Composite parent, int style) {
		super(parent, style);
	}

	public void invokeAction(int action) {
		if (action == Constant.DELETE)
			action = ST.DELETE_NEXT;

		super.invokeAction(action);

		switch (action) {
		case Constant.UNDO:
			undo();
			break;
		case Constant.REDO:
			redo();
			break;
		case Constant.FIND:
			find();
			break;
		}
	}

	private void undo() {
		if (undoManager != null)
			undoManager.undo();
	}

	private void redo() {
		if (undoManager != null)
			undoManager.redo();
	}

	private void find() {
		FindDialog.getInstance(this.getShell(), this).open();
	}

	private UndoManager undoManager = null;

	public UndoManager getUndoManager() {
		return undoManager;
	}

	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

	public void dispose() {
		if (undoManager != null)
			undoManager.disconnect();
		super.dispose();
	}

	/*public static class ActionCode {
		public static final int UNDO = Integer.MAX_VALUE;
		public static final int REDO = UNDO - 1;
		public static final int CLEAR = UNDO - 2;
		public static final int DELETE = UNDO - 3;
	}

	public static void main(String[] args) {
		final Display display = Display.getDefault();
		final Shell shell = new Shell();
		shell.setLayout(new GridLayout());
		shell.setSize(420, 250);
		shell.setText("SWT Application");

		MyStyledText styledText = new MyStyledText(shell, SWT.BORDER);
		GridData gd_styledText = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		gd_styledText.heightHint = 200;
		gd_styledText.widthHint = 400;
		styledText.setLayoutData(gd_styledText);

		// Ctrl+C, Ctrl+X, Ctrl+V 都是StyledText的默认行为。

		// styledText.setKeyBinding('C' | SWT.CTRL, ST.COPY);
		// styledText.setKeyBinding('V' | SWT.CTRL, ST.PASTE);
		// styledText.setKeyBinding('X' | SWT.CTRL, ST.CUT);

		styledText.setKeyBinding('A' | SWT.CTRL, ST.SELECT_ALL);
		styledText.setKeyBinding('Z' | SWT.CTRL, ActionCode.UNDO);
		styledText.setKeyBinding('Y' | SWT.CTRL, ActionCode.REDO);
		styledText.setKeyBinding('F' | SWT.CTRL, ActionCode.CLEAR);
		styledText.setKeyBinding('D' | SWT.CTRL, ActionCode.DELETE);

		UndoManager undoManager = new UndoManager(50);
		undoManager.connect(styledText);

		styledText.setUndoManager(undoManager);

		shell.open();

		shell.layout();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}*/
}