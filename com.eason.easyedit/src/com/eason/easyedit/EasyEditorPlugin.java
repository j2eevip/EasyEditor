package com.eason.easyedit;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class EasyEditorPlugin extends AbstractUIPlugin {
	public void start(BundleContext context) throws Exception {
		System.out.println("start..");
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("stop.");
		super.stop(context);
	}
}
