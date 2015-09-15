package com.eason.listener;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;

public class UndoManager {
	private final IOperationHistory opHistory;
	private IUndoContext undoContext = null;
	private StyledText styledText = null;

	private int undoLevel = 0;

	public UndoManager(int undoLevel) {
		opHistory = OperationHistoryFactory.getOperationHistory();
		setMaxUndoLevel(undoLevel);
	}

	public void setMaxUndoLevel(int undoLevel) {
		this.undoLevel = Math.max(0, undoLevel);
		if (isConnected())
			opHistory.setLimit(undoContext, this.undoLevel);
	}

	public boolean isConnected() {
		return styledText != null;
	}

	public void connect(StyledText styledText) {
		if (!isConnected() && styledText != null) {
			this.styledText = styledText;
			if (undoContext == null)
				undoContext = new ObjectUndoContext(this);
			opHistory.setLimit(undoContext, undoLevel);
			opHistory.dispose(undoContext, true, true, false);
			addListeners();
		}
	}

	public void disconnect() {
		if (isConnected()) {
			removeListeners();
			styledText = null;
			opHistory.dispose(undoContext, true, true, true);
			undoContext = null;
		}
	}

	private ExtendedModifyListener extendedModifyListener = null;
	
	private CodeLineStylerListener codeLineStylerListener = null;
	
	private boolean isUndoing = false;

	private void addListeners() {
		if (styledText != null) {
			extendedModifyListener = new ExtendedModifyListener() {
				public void modifyText(ExtendedModifyEvent event) {
					if (isUndoing)
						return;
					String newText = styledText.getText().substring(
							event.start, event.start + event.length);
					UndoableOperation operation = new UndoableOperation(
							undoContext);
					operation.set(event.start, newText, event.replacedText);
					opHistory.add(operation);
				}
			};
			styledText.addExtendedModifyListener(extendedModifyListener);

			codeLineStylerListener = new CodeLineStylerListener();
			styledText.addLineStyleListener(codeLineStylerListener);
		}
	}

	private void removeListeners() {
		if (styledText != null) {
			if (extendedModifyListener != null) {
				styledText.removeExtendedModifyListener(extendedModifyListener);
				extendedModifyListener = null;
			}
			if (codeLineStylerListener != null) {
				codeLineStylerListener.disposeColors();
				styledText.removeLineStyleListener(codeLineStylerListener);
				codeLineStylerListener = null;
			}
		}
	}

	public void redo() {
		if (isConnected()) {
			try {
				opHistory.redo(undoContext, null, null);
			} catch (ExecutionException ex) {
			}
		}
	}

	public void undo() {
		if (isConnected()) {
			try {
				opHistory.undo(undoContext, null, null);
			} catch (ExecutionException ex) {
			}
		}
	}

	/*
	 * Undo操作用于记录StyledText的文本被改变时的相关数据。
	 * 比如文本框中本来的文本为111222333，如果此时选中222替换为444（用复制粘帖的方法），
	 * 则Undo操作中记录的相关数据为： startIndex = 3; newText = 444; replacedText = 222;
	 */
	private class UndoableOperation extends AbstractOperation {
		protected int startIndex = -1;

		protected String newText = null;

		protected String replacedText = null;

		public UndoableOperation(IUndoContext context) {
			super("Undo-Redo");
			addContext(context);
		}

		public void set(int startIndex, String newText, String replacedText) {
			this.startIndex = startIndex;
			this.newText = newText;
			this.replacedText = replacedText;
		}
		
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			isUndoing = true;
			styledText.replaceTextRange(startIndex, newText.length(),
					replacedText);
			isUndoing = false;

			return Status.OK_STATUS;
		}
		
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			isUndoing = true;
			styledText.replaceTextRange(startIndex, replacedText.length(),
					newText);
			isUndoing = false;

			return Status.OK_STATUS;
		}
		
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return Status.OK_STATUS;
		}
	}
}