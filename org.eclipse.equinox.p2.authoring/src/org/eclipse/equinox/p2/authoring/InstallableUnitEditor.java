/*******************************************************************************
 * Copyright (c) 2008
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed below, as Initial Contributors under such license.
 * The text of such license is available at 
 * http://www.eclipse.org/legal/epl-v10.html.
 * 
 * Contributors:
 * 		Henrik Lindberg
 *******************************************************************************/


package org.eclipse.equinox.p2.authoring;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.buckminster.core.CorePlugin;
import org.eclipse.buckminster.distro.ui.editor.distro.FeedsPage2;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.p2.authoring.forms.RichFormEditor;
import org.eclipse.equinox.p2.authoring.internal.InstallableUnitBuilder;
import org.eclipse.equinox.p2.authoring.internal.InstallableUnitEditorInput;
import org.eclipse.equinox.p2.authoring.internal.P2MetadataReader;
import org.eclipse.equinox.p2.authoring.internal.SaveIURunnable;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.ILocationProvider;

/**
 * A multi page Eclipse form based editor for p2 Installable Unit.
 * 
 * @author Henrik Lindberg
 * 
 */
public class InstallableUnitEditor extends RichFormEditor
{
	private InstallableUnitBuilder m_iu;
	private boolean m_readOnly = false;
	public InstallableUnitEditor()
	{
		setTmpPrefix("p2iu-");
		setTmpSuffix("iu");
	}
	@Override
	protected void addPages()
	{
		createActions();
		try
		{
			addPage(new OverviewPage(this));
			addPage(new RequiredCapabilitiesPage(this));
			addPage(new FeedsPage2(this));
//			addPage(new ThirdPage(this));
//			addPage(new MasterDetailsPage(this));
//			addPage(new PageWithSubPages(this));
		}
		catch(PartInitException e)
		{
			// TODO: Proper logging
			e.printStackTrace();
		}
	}
	public void doExternalSaveAs()
	{
		if(!commitChanges())
			return;
		FileDialog dlg = new FileDialog(getSite().getShell(), SWT.SAVE);
		dlg.setFilterExtensions(new String[] { "*.iu" });
		final String location = dlg.open();
		if(location == null)
			return;
		saveToPath(new Path(location));
	}

	public InstallableUnitBuilder getInstallableUnit()
	{
		return m_iu;
	}
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{ 
		// super version sets the site, sets the input (without notify), and then installs a selection provider.
		// 
		super.init(site, input);
		if(!(input instanceof ILocationProvider || input instanceof IPathEditorInput || //
				input instanceof IURIEditorInput || input instanceof InstallableUnitEditorInput))
			throw new PartInitException("Invalid Input");
		// setSite(site);

		if(input instanceof IURIEditorInput)
		{
			try
			{
				input = getExternalFileEditorInput((IURIEditorInput)input);
			}
			catch(Exception e)
			{
				throw new PartInitException("Unable to open editor", e);
			}
		}

		InputStream stream = null;
		try
		{
			if(input instanceof InstallableUnitEditorInput)
			{
				m_readOnly = true;
				// make mutable copy
				m_iu = new InstallableUnitBuilder((((InstallableUnitEditorInput)input).getInstallableUnit()));
			}
			else
			{
				IPath path = (input instanceof ILocationProvider)
						? ((ILocationProvider)input).getPath(input)
						: ((IPathEditorInput)input).getPath();
	
				// Always allow edit of a file TODO: should perhaps check for extension = ".iu"
				m_readOnly = false;
						
				File file = path.toFile();
				if(file.length() != 0)
				{
					stream = new FileInputStream(file);
					// note url passed is only for information - creates mutable copy for editing
					m_iu = new InstallableUnitBuilder(P2MetadataReader.readInstallableUnit(file.toURL(), stream, 
							site.getActionBars().getStatusLineManager().getProgressMonitor()));
				}
			}
			
			setInputWithNotify(input);
			setPartName(input.getName() + (m_readOnly ? " (read only)" : ""));
		}
		catch(Exception e)
		{
			// TODO: Uses Buckminster exception wrapper
			throw new PartInitException(BuckminsterException.wrap(e).getMessage());
		}
		finally
		{
			try
			{
				stream.close();
			}
			catch(IOException e)
			{
			}
		}
	}
	
	public boolean isReadOnly()
	{
		return m_readOnly;
	}
	
	@Override
	public final void saveToPath(IPath path)
	{
		try
		{
			SaveIURunnable sr = new SaveIURunnable(m_iu.createInstallableUnit(), path);
			getSite().getWorkbenchWindow().run(true, true, sr);
			setInputWithNotify(sr.getSavedInput());

			setPartName(path.lastSegment());
			firePropertyChange(IWorkbenchPart.PROP_TITLE);
		}
		catch(InvocationTargetException e)
		{
			CoreException t = BuckminsterException.wrap(e);
			String msg = "Unable to save file " + path;
			CorePlugin.getLogger().error(t, msg);
			ErrorDialog.openError(getSite().getShell(), null, msg, t.getStatus());
		}
		catch(InterruptedException e)
		{
		}
	}	
}