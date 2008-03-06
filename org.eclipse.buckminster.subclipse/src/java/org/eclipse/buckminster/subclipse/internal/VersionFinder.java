/*******************************************************************************
 * Copyright (c) 2004, 2006
 * Thomas Hallgren, Kenneth Olwing, Mitch Sonies
 * Pontus Rydin, Nils Unden, Peer Torngren
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed above, as Initial Contributors under such license.
 * The text of such license is available at www.eclipse.org.
 *******************************************************************************/
package org.eclipse.buckminster.subclipse.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.buckminster.core.ctype.IComponentType;
import org.eclipse.buckminster.core.resolver.NodeQuery;
import org.eclipse.buckminster.core.rmap.model.Provider;
import org.eclipse.buckminster.core.version.AbstractSCCSVersionFinder;
import org.eclipse.buckminster.core.version.VersionMatch;
import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.tigris.subversion.svnclientadapter.ISVNDirEntry;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;

public class VersionFinder extends AbstractSCCSVersionFinder
{
	private final SvnSession m_session;

	public VersionFinder(Provider provider, IComponentType ctype, NodeQuery query) throws CoreException
	{
		super(provider, ctype, query);
		m_session = new SvnSession(provider.getURI(query.getProperties()), null, query.getRevision(), query.getTimestamp(), query.getContext());
	}

	@Override
	protected boolean checkComponentExistence(VersionMatch versionMatch, IProgressMonitor monitor) throws CoreException
	{
		NodeQuery query = getQuery();
		String uri = getProvider().getURI(query.getProperties());
		SvnSession checkerSession = new SvnSession(uri, versionMatch.getBranchOrTag(), versionMatch.getRevision(), versionMatch.getTimestamp(), query.getContext());
		try
		{
			// We list the folder rather then just obtaining the entry since the listing
			// is cached. It is very likely that we save a call later.
			//
			return checkerSession.listFolder(checkerSession.getSVNUrl(null), monitor).length > 0;
		}
		finally
		{
			checkerSession.close();
		}
	}

	@Override
	protected List<RevisionEntry> getBranchesOrTags(boolean branches, IProgressMonitor monitor) throws CoreException
	{
		monitor.beginTask(null, 200);
		try
		{
			if(!m_session.hasTrunkStructure())
				return Collections.emptyList();
				
			SVNUrl url = m_session.getSVNRootUrl(branches);
			SVNRevision.Number repoRev = m_session.getRepositoryRevision(MonitorUtils.subMonitor(monitor, 50));
			if(repoRev == null)
				return Collections.emptyList();
	
			ISVNDirEntry[] list = m_session.listFolder(url, MonitorUtils.subMonitor(monitor, 150));
			if(list.length == 0)
				return Collections.emptyList();
	
			ArrayList<RevisionEntry> entries = new ArrayList<RevisionEntry>(list.length);
			for(ISVNDirEntry e : list)
				entries.add(new RevisionEntry(e.getPath(), null, repoRev.getNumber()));
			return entries;
		}
		finally
		{
			monitor.done();
		}
	}

	@Override
	protected RevisionEntry getTrunk(IProgressMonitor monitor) throws CoreException
	{
		SVNRevision.Number repoRev = m_session.getRepositoryRevision(monitor);
		return repoRev == null ? null : new RevisionEntry(null, null, repoRev.getNumber());
	}
}
