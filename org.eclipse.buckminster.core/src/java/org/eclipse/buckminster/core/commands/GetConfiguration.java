/*******************************************************************************
 * Copyright (c) 2004, 2006
 * Thomas Hallgren, Kenneth Olwing, Mitch Sonies
 * Pontus Rydin, Nils Unden, Peer Torngren
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed above, as Initial Contributors under such license.
 * The text of such license is available at www.eclipse.org.
 *******************************************************************************/

package org.eclipse.buckminster.core.commands;

import java.net.URL;

import org.eclipse.buckminster.cmdline.UsageException;
import org.eclipse.buckminster.core.Messages;
import org.eclipse.buckminster.core.materializer.MaterializationContext;
import org.eclipse.buckminster.core.materializer.MaterializationJob;
import org.eclipse.buckminster.core.metadata.model.BillOfMaterials;
import org.eclipse.buckminster.core.mspec.builder.MaterializationSpecBuilder;
import org.eclipse.buckminster.core.query.model.ComponentQuery;
import org.eclipse.buckminster.core.resolver.MainResolver;
import org.eclipse.buckminster.core.resolver.ResolutionContext;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.buckminster.runtime.URLUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.osgi.util.NLS;

public class GetConfiguration extends WorkspaceCommand {
	private URL url;

	private IConnectContext connectContext;

	@Override
	protected void handleUnparsed(String[] unparsed) throws Exception {
		int len = unparsed.length;
		if (len > 1)
			throw new UsageException(Messages.Too_many_arguments);
		if (len == 1)
			url = URLUtils.normalizeToURL(unparsed[0]);
	}

	@Override
	protected int internalRun(IProgressMonitor monitor) throws Exception {
		System.out.println(NLS.bind(Messages.Using_workspace_at_0, Platform.getInstanceLocation().getURL().toString()));
		monitor.beginTask(null, 3);
		try {
			ComponentQuery query = ComponentQuery.fromURL(url, connectContext, true);
			MonitorUtils.worked(monitor, 1);
			ResolutionContext context = new ResolutionContext(query);
			MainResolver resolver = new MainResolver(context);
			BillOfMaterials bom = resolver.resolve(query.getExpandedRootRequest(context), MonitorUtils.subMonitor(monitor, 1));
			IStatus status = context.getStatus();
			switch (status.getSeverity()) {
				case IStatus.ERROR:
					throw new CoreException(status);
				case IStatus.WARNING:
					System.err.print(NLS.bind(Messages.GetConfiguration_Warning, status.getMessage()));
					break;
				case IStatus.INFO:
					System.out.print(status.getMessage());
			}

			MaterializationSpecBuilder mspecBuilder = new MaterializationSpecBuilder();
			mspecBuilder.setName(bom.getViewName());
			bom.addMaterializationNodes(mspecBuilder);
			MaterializationContext matCtx = new MaterializationContext(bom, mspecBuilder.createMaterializationSpec(), context);
			MaterializationJob.run(matCtx);
			if (matCtx.getStatus().getSeverity() == IStatus.ERROR)
				return 1;
			MonitorUtils.worked(monitor, 1);
			System.out.println(Messages.Query_complete);
		} catch (Throwable t) {
			CoreException be = BuckminsterException.wrap(t);
			if (be.getCause() instanceof javax.net.ssl.SSLHandshakeException)
				System.err.println(Messages.SSL_handshake_exception);
			throw be;
		} finally {
			monitor.done();
		}
		return 0;
	}
}
