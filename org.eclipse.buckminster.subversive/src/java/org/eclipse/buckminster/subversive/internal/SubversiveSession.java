/*******************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ******************************************************************************/

package org.eclipse.buckminster.subversive.internal;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.eclipse.buckminster.core.RMContext;
import org.eclipse.buckminster.core.version.VersionSelector;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.subversion.GenericSession;
import org.eclipse.buckminster.subversion.ISubversionCache;
import org.eclipse.buckminster.subversion.ISvnEntryHelper;
import org.eclipse.buckminster.subversion.RepositoryAccess;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.svn.core.SVNTeamPlugin;
import org.eclipse.team.svn.core.connector.ISVNConnector;
import org.eclipse.team.svn.core.connector.ISVNCredentialsPrompt;
import org.eclipse.team.svn.core.connector.ISVNProgressMonitor;
import org.eclipse.team.svn.core.connector.SVNEntry;
import org.eclipse.team.svn.core.connector.SVNEntryRevisionReference;
import org.eclipse.team.svn.core.connector.SVNRevision;
import org.eclipse.team.svn.core.extension.CoreExtensionsManager;
import org.eclipse.team.svn.core.extension.options.IOptionProvider;
import org.eclipse.team.svn.core.resource.IRepositoryLocation;
import org.eclipse.team.svn.core.svnstorage.SVNRemoteStorage;
import org.eclipse.team.svn.core.svnstorage.SVNRepositoryLocation.BaseCredentialsPromptWrapper;
import org.eclipse.team.svn.core.utility.SVNUtility;

/**
 * <p>
 * The Subversive repository will be able to use reader checks if a repository contains the three recommended
 * directories <code>trunk</code>, <code>tags</code>, and <code>branches</code>. A missing <code>tags</code> directory
 * is interpreted as no <code>tags</code>. A missing <code>branches</code> directory is interpreted as no branches. In
 * order to use <code>trunk</code>, <code>tags</code>, and <code>branches</code> repository identifier must contain the
 * path element <code>trunk</code>. Anything that follows the <code>trunk</code> element in the path will be considered
 * a <code>module</code> path. If no <code>trunk</code> element is present in the path, the last element will be
 * considered the <code>module</code>
 * </p>
 * <p>
 * The repository URL may also contain a query part that in turn may have four different flags:
 * <dl>
 * <dt>moduleBeforeTag</dt>
 * <dd>When resolving a tag, put the module name between the <code>tags</code> directory and the actual tag</dd>
 * <dt>moduleAfterTag</dt>
 * <dd>When resolving a tag, append the module name after the actual tag</dd>
 * <dt>moduleBeforeBranch</dt>
 * <dd>When resolving a branch, put the module name between the <code>branches</code> directory and the actual branch</dd>
 * <dt>moduleAfterBranch</dt>
 * <dd>When resolving a branch, append the module name after the actual branch</dd>
 * </dl>
 * </p>
 * A fragment in the repository URL will be treated as a sub-module. It will be appended at the end of the resolved URL.
 * 
 * @author Thomas Hallgren
 * @author Guillaume Chatelet
 */
public class SubversiveSession extends GenericSession<IRepositoryLocation, SVNEntry>
{
	private class UnattendedPromptUserPassword implements ISVNCredentialsPrompt
	{
		private int m_promptLimit = 3;

		public int askTrustSSLServer(Object location, String info, boolean allowPermanently)
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.askTrustSSLServer(location, info, allowPermanently);
		}

		public String getPassword()
		{
			return m_password;
		}

		public String getProxyHost()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.getProxyHost();
		}

		public String getProxyPassword()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.getProxyPassword();
		}

		public int getProxyPort()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.getProxyPort();
		}

		public String getProxyUserName()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.getProxyUserName();
		}

		public String getRealmToSave()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.getRealmToSave();
		}

		public int getSSHPort()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.getSSHPort();
		}

		public String getSSHPrivateKeyPassphrase()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.getSSHPrivateKeyPassphrase();
		}

		public String getSSHPrivateKeyPath()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.getSSHPrivateKeyPath();
		}

		public String getSSLClientCertPassword()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.getSSLClientCertPassword();
		}

		public String getSSLClientCertPath()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.getSSLClientCertPath();
		}

		public String getUsername()
		{
			return m_username;
		}

		public boolean isProxyAuthenticationEnabled()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.isProxyAuthenticationEnabled();
		}

		public boolean isProxyEnabled()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.isProxyEnabled();
		}

		public boolean isSaveCredentialsEnabled()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.isSaveCredentialsEnabled();
		}

		public boolean isSaveProxyPassword()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.isSaveProxyPassword();
		}

		public boolean isSSHPrivateKeyPassphraseSaved()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.isSSHPrivateKeyPassphraseSaved();
		}

		public boolean isSSHPublicKeySelected()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.isSSHPublicKeySelected();
		}

		public boolean isSSLAuthenticationEnabled()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.isSSLAuthenticationEnabled();
		}

		public boolean isSSLSavePassphrase()
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.isSSLSavePassphrase();
		}

		public boolean prompt(Object arg0, String arg1)
		{
			// We support the password prompt only if we actually know the password
			// and only a limited number of times
			//
			return m_password != null && --m_promptLimit >= 0;
		}

		public boolean promptProxy(Object location)
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.promptProxy(location);
		}

		public boolean promptSSH(Object location, String realm)
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.promptSSH(location, realm);
		}

		public boolean promptSSL(Object location, String realm)
		{
			return ISVNCredentialsPrompt.DEFAULT_PROMPT.promptSSL(location, realm);
		}
	}

	private static final SubversiveEntryHelper HELPER = new SubversiveEntryHelper();

	private static final SVNEntry[] s_emptyFolder = new SVNEntry[0];

	private static final String UNKNOWN_ROOT_PREFIX = SubversiveSession.class.getPackage().getName() + ".root."; //$NON-NLS-1$

	public static URI getURIParent(URI uri)
	{
		if(uri == null)
			return null;

		String path = uri.toString();
		if(path == null)
			return null;

		int lastSlash = path.lastIndexOf('/');
		if(lastSlash == path.length() - 1 && lastSlash > 0)
		{
			path = path.substring(0, path.length() - 1);
			lastSlash = path.lastIndexOf('/');
		}
		if(lastSlash < 0)
			return null;

		String parentPath = path.substring(0, lastSlash);
		try
		{
			return new URI(parentPath);
		}
		catch(URISyntaxException e)
		{
			return null;
		}
	}

	private ISVNConnector m_proxy;

	/**
	 * @param repositoryURI
	 *            The string representation of the URI that appoints the trunk of repository module. No branch or tag
	 *            information must be included.
	 * @param branch
	 *            The desired branch or <code>null</code> if not applicable.
	 * @param tag
	 *            The desired tag or <code>null</code> if not applicable.
	 * @param revision
	 *            The desired revision or <code>-1</code> of not applicable
	 * @param timestamp
	 *            The desired timestamp or <code>null</code> if not applicable
	 * @param context
	 *            The context used for the resolution/materialization operation
	 * @throws CoreException
	 */
	public SubversiveSession(String repositoryURI, VersionSelector branchOrTag, long revision, Date timestamp,
			RMContext context) throws CoreException
	{
		super(repositoryURI, branchOrTag, revision, timestamp, context);
	}

	public void close()
	{
		m_proxy.dispose();
	}

	public SVNEntry getDirEntry(URI uri, SVNRevision revision, IProgressMonitor monitor) throws CoreException
	{
		final URI parent = getURIParent(uri);
		if(parent == null)
			return null;

		final String path = uri.getPath();
		final String entryPath = path.substring(path.lastIndexOf('/') + 1);
		final SVNEntry[] entries = listFolder(parent, monitor);
		for(SVNEntry entry : entries)
			if(entryPath.equals(entry.path))
				return entry;
		return null;
	}

	public long getLastChangeNumber() throws CoreException
	{
		try
		{
			URI svnURL = getSVNUrl(null);
			SVNEntry root = getDirEntry(svnURL, getRevision(), null);
			if(root == null)
				throw new FileNotFoundException(svnURL.toString());
			return root.revision;
		}
		catch(Exception e)
		{
			throw BuckminsterException.wrap(e);
		}
	}

	public Date getLastTimestamp() throws CoreException
	{
		try
		{
			URI svnURL = getSVNUrl(null);
			SVNEntry root = getDirEntry(svnURL, getRevision(), null);
			if(root == null)
				throw new FileNotFoundException(svnURL.toString());
			return new Date(root.date);
		}
		catch(Exception e)
		{
			throw BuckminsterException.wrap(e);
		}
	}

	public SVNEntry getRootEntry(IProgressMonitor monitor) throws CoreException
	{
		return getDirEntry(getSVNUrl(null), getRevision(), monitor);
	}

	public ISvnEntryHelper<SVNEntry> getSvnEntryHelper()
	{
		return HELPER;
	}

	@Override
	public String toString()
	{
		try
		{
			return getSVNUrl(null).toString();
		}
		catch(CoreException e)
		{
			return super.toString();
		}
	}

	@Override
	protected void createRoots(Collection<RepositoryAccess> sourceRoots) throws CoreException
	{
		final SVNRemoteStorage storage = SVNRemoteStorage.instance();
		for(RepositoryAccess root : sourceRoots)
		{
			IRepositoryLocation location = storage.newRepositoryLocation();
			location.setUrl(root.getSvnURL().toString());
			location.setPassword(root.getPassword());
			location.setUsername(root.getUser());
			storage.addRepositoryLocation(location);
		}
		try
		{
			storage.saveConfiguration();
		}
		catch(Exception e)
		{
			throw BuckminsterException.wrap(e);
		}
	}

	@Override
	protected ISubversionCache<SVNEntry> getCache(Map<UUID, Object> userCache)
	{
		assert (m_cache == null);
		final SubversiveCache cache = new SubversiveCache();
		cache.initialize(userCache);
		return cache;
	}

	@Override
	protected SVNEntry[] getEmptyEntryList()
	{
		return s_emptyFolder;
	}

	@Override
	protected IRepositoryLocation[] getKnownRepositories()
	{
		return SVNRemoteStorage.instance().getRepositoryLocations();
	}

	@Override
	protected String getRootUrl(IRepositoryLocation location)
	{
		return location.getRoot().getUrl();
	}

	@Override
	protected String getUnknownRootPrefix()
	{
		return UNKNOWN_ROOT_PREFIX;
	}

	@Override
	protected void initializeSvn(RMContext context, URI ourRoot, IRepositoryLocation bestMatch)
	{
		final ISVNConnector proxy = getSVNProxy();
		proxy.setTouchUnresolved(false);
		proxy.setCommitMissingFiles(false);
		proxy.setSSLCertificateCacheEnabled(true);
		proxy.setUsername(m_username);
		proxy.setPassword(m_password);
		proxy.setCredentialsCacheEnabled(true);
		if(bestMatch == null)
		{
			addUnknownRoot(context.getBindingProperties(), new RepositoryAccess(ourRoot, m_username, m_password));
		}
		else
		{
			SVNUtility.configureProxy(proxy, m_repositoryLocation);
			IOptionProvider optionProvider = SVNTeamPlugin.instance().getOptionProvider();
			ISVNCredentialsPrompt externalPrompt = optionProvider.getCredentialsPrompt();
			if(externalPrompt != null)
				proxy.setPrompt(new BaseCredentialsPromptWrapper(externalPrompt, m_repositoryLocation));
		}
		// Add the UnattendedPromptUserPassword callback only in case
		// the authentication data (at least the username) is actually
		// specified in the URL
		//
		if(m_username != null)
			proxy.setPrompt(new UnattendedPromptUserPassword());
	}

	@Override
	protected SVNEntry[] innerListFolder(URI url, IProgressMonitor monitor) throws Exception
	{
		ISVNProgressMonitor svnMon = SimpleMonitorWrapper.beginTask(monitor, 100);
		return SVNUtility.list(m_proxy, new SVNEntryRevisionReference(url.toString(), null, getRevision()),
				ISVNConnector.Depth.IMMEDIATES, SVNEntry.Fields.ALL, ISVNConnector.Options.NONE, svnMon);

	}

	ISVNConnector getSVNProxy()
	{
		if(m_proxy == null)
			m_proxy = CoreExtensionsManager.instance().getSVNConnectorFactory().newInstance();
		return m_proxy;
	}
}
