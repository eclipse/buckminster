package org.eclipse.buckminster.subversion;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.buckminster.core.CorePlugin;
import org.eclipse.buckminster.core.RMContext;
import org.eclipse.buckminster.core.helpers.TextUtils;
import org.eclipse.buckminster.core.version.VersionSelector;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.Logger;
import org.eclipse.buckminster.runtime.Trivial;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.osgi.util.NLS;

public abstract class GenericSession<REPO_LOCATION_TYPE, SVN_ENTRY_TYPE, SVN_REVISION_TYPE> implements ISubversionSession<SVN_ENTRY_TYPE, SVN_REVISION_TYPE>
{
	public abstract SVN_REVISION_TYPE getSVNRevision(long revision, Date timestamp);

	protected final VersionSelector m_branchOrTag;

	protected REPO_LOCATION_TYPE m_repositoryLocation;

	protected final IPath m_module;

	protected final boolean m_moduleAfterBranch;

	protected final boolean m_moduleAfterTag;

	protected final boolean m_moduleBeforeBranch;

	protected final boolean m_moduleBeforeTag;

	protected final boolean m_trunkStructure;

	protected final String m_password;

	private final SVN_REVISION_TYPE m_revision;

	protected final IPath m_subModule;

	protected final String m_urlLeadIn;

	protected final String m_username;

	protected final ISubversionCache<SVN_ENTRY_TYPE> m_cache;

	public GenericSession(String repositoryURI, VersionSelector branchOrTag, long revision, Date timestamp,
			RMContext context) throws CoreException
	{
		m_revision = getSVNRevision(revision, timestamp);
		m_branchOrTag = branchOrTag;

		Map<UUID, Object> userCache = context.getUserCache();
		m_cache = getCache(userCache);

		try
		{
			URI uri = new URI(repositoryURI);

			// Find the repository root, i.e. the point just above 'trunk'.
			//
			IPath fullPath = new Path(uri.getPath());
			String[] pathSegments = fullPath.segments();
			int idx = pathSegments.length;
			while(--idx >= 0)
				if(pathSegments[idx].equals("trunk")) //$NON-NLS-1$
					break;

			if(idx >= 0)
				m_trunkStructure = true;
			else
			{
				m_trunkStructure = false;
				idx = pathSegments.length - 1; // Last element is considered the module name
			}

			if(m_branchOrTag != null && !m_trunkStructure)
				//
				// No use continuing with this session since there's no hope finding
				// the desired branch or tag.
				//
				throw BuckminsterException.fromMessage(NLS.bind(Messages.branch_or_tag_0_not_found, m_branchOrTag));

			int relPathLen = pathSegments.length - idx;

			StringBuilder bld = new StringBuilder();
			String scheme = uri.getScheme();
			if(scheme != null)
			{
				bld.append(scheme);
				bld.append("://"); //$NON-NLS-1$
			}

			String username = null;
			String password = null;
			String authority = uri.getAuthority();
			if(authority != null)
			{
				int atIdx = authority.indexOf('@');
				if(atIdx > 0)
				{
					String authentication = authority.substring(0, atIdx);
					authority = authority.substring(atIdx + 1);

					int upSplit = authentication.indexOf(':');
					if(upSplit > 0)
					{
						username = authentication.substring(0, upSplit);
						if("null".equals(username)) //$NON-NLS-1$
							username = null;
						password = authentication.substring(upSplit + 1);
						if("null".equals(password)) //$NON-NLS-1$
							password = null;
					}
				}
				bld.append(authority);
			}
			m_username = username;
			m_password = password;

			if(fullPath.getDevice() != null)
				bld.append('/');

			bld.append(fullPath.removeLastSegments(relPathLen));
			String urlLeadIn = bld.toString();

			// Anything after 'trunk' is considered a module path. The module
			// path
			// will be used when finding branches and tags.
			//
			IPath modulePath = null;
			if(m_trunkStructure)
			{
				if(relPathLen > 1)
				{
					modulePath = fullPath.removeFirstSegments(idx + 1);
					modulePath = modulePath.setDevice(null);
				}
			}
			else
				modulePath = Path.fromPortableString(fullPath.lastSegment());
			m_module = modulePath;

			String tmp = uri.getFragment();
			IPath subModule = null;
			if(tmp != null)
				subModule = new Path(tmp).makeRelative();
			m_subModule = subModule;

			boolean moduleBeforeTag = false;
			boolean moduleAfterTag = false;
			boolean moduleBeforeBranch = false;
			boolean moduleAfterBranch = false;
			if(m_trunkStructure)
			{
				for(String entry : TextUtils.decodeToQueryPairs(uri.getQuery()))
				{
					if(entry.equalsIgnoreCase("moduleBeforeTag")) //$NON-NLS-1$
						moduleBeforeTag = true;
					else if(entry.equalsIgnoreCase("moduleAfterTag")) //$NON-NLS-1$
						moduleAfterTag = true;
					else if(entry.equalsIgnoreCase("moduleBeforeBranch")) //$NON-NLS-1$
						moduleBeforeBranch = true;
					else if(entry.equalsIgnoreCase("moduleAfterBranch")) //$NON-NLS-1$
						moduleAfterBranch = true;
				}
			}
			m_moduleBeforeTag = moduleBeforeTag;
			m_moduleAfterTag = moduleAfterTag;
			m_moduleBeforeBranch = moduleBeforeBranch;
			m_moduleAfterBranch = moduleAfterBranch;

			// Let's see if our SVNRootUrl matches any of the known
			// repositories.
			//

			int rank = 0;
			URI ourRoot = new URI(urlLeadIn);
			REPO_LOCATION_TYPE bestMatch = null;
			for(REPO_LOCATION_TYPE location : getKnownRepositories())
			{
				URI repoRoot = new URI(getRootUrl(location));
				if(!Trivial.equalsAllowNull(repoRoot.getHost(), ourRoot.getHost()))
					continue;

				String repoProto = repoRoot.getScheme().toLowerCase();
				String ourProto = ourRoot.getScheme().toLowerCase();

				// We let the protocol svn or http match a repo that uses svn+ssh or https
				//
				boolean repoIsSSH = repoProto.equals("svn+ssh") || repoProto.equals("https"); //$NON-NLS-1$ //$NON-NLS-2$
				if(rank > 200 && !repoIsSSH)
					continue;

				if(!(repoProto.equals(ourProto) || (repoProto.equals("svn") && ourProto.equals("http")) //$NON-NLS-1$ //$NON-NLS-2$
						|| (repoProto.equals("http") && ourProto.equals("svn")) //$NON-NLS-1$ //$NON-NLS-2$
				|| ((ourProto.equals("svn") || ourProto.equals("http")) && repoIsSSH))) //$NON-NLS-1$ //$NON-NLS-2$
					continue;

				String[] ourPath = Path.fromPortableString(ourRoot.getPath()).segments();
				String[] repoPath = Path.fromPortableString(repoRoot.getPath()).segments();

				idx = repoPath.length;
				final int top = ourPath.length;
				if(idx > top)
					//
					// repoPath is too qualified for our needs
					//
					continue;

				while(--idx >= 0)
					if(!ourPath[idx].equals(repoPath[idx]))
						break;

				if(idx >= 0)
					//
					// repoPath is not a prefix of ourPath
					//
					continue;

				urlLeadIn = repoRoot.toString();
				int diff = top - repoPath.length;
				if(diff > 0)
				{
					int myRank = (repoIsSSH
							? 400
							: 200) - diff;
					if(rank > myRank)
						continue;

					// Append the rest of our path
					//
					bld.setLength(0);
					bld.append(urlLeadIn);
					for(idx = repoPath.length; idx < top; ++idx)
					{
						bld.append('/');
						bld.append(ourPath[idx]);
					}
					urlLeadIn = bld.toString();
				}
				rank = (repoIsSSH
						? 400
						: 200) - diff;
				bestMatch = location;
				if(rank == 400)
					break;
			}

			m_urlLeadIn = urlLeadIn;
			m_repositoryLocation = bestMatch;
			synchronized(userCache)
			{
				initializeSvn(context, ourRoot, bestMatch);
			}
		}
		catch(URISyntaxException e)
		{
			throw BuckminsterException.wrap(e);
		}
	}

	abstract protected void initializeSvn(RMContext context, URI ourRoot, REPO_LOCATION_TYPE bestMatch)
			throws CoreException;

	abstract protected ISubversionCache<SVN_ENTRY_TYPE> getCache(Map<UUID, Object> userCache);

	abstract protected REPO_LOCATION_TYPE[] getKnownRepositories() throws CoreException;

	abstract protected String getUnknownRootPrefix();

	abstract protected String getRootUrl(REPO_LOCATION_TYPE location);

	final protected void clearUnknownRoots(Map<String, String> properties)
	{
		synchronized(properties)
		{
			final Iterator<String> keys = properties.keySet().iterator();
			while(keys.hasNext())
			{
				String key = keys.next();
				if(key.startsWith(getUnknownRootPrefix()))
					keys.remove();
			}
		}
	}

	final public void createCommonRoots(RMContext context) throws CoreException
	{
		final List<RepositoryAccess> unknownRoots = getUnknownRoots(context.getBindingProperties());
		if(unknownRoots.size() == 0)
			return;

		Collection<RepositoryAccess> sourceRoots = unknownRoots;
		if(unknownRoots.size() > 1)
		{
			// Get all common roots with a segment count of at least 1
			//
			for(;;)
			{
				Collection<RepositoryAccess> commonRoots = getCommonRootsStep(sourceRoots);
				if(commonRoots == sourceRoots)
					break;

				// Common roots were found. Iterate again to find commons
				// amongst the commons
				//
				sourceRoots = commonRoots;
			}
		}

		// Create the needed repositories so that Subclipse doesn't create every single
		// root for us.
		//

		createRoots(sourceRoots);
		clearUnknownRoots(context.getBindingProperties());
	}

	abstract protected void createRoots(Collection<RepositoryAccess> sourceRoots) throws CoreException;

	private static Collection<RepositoryAccess> getCommonRootsStep(Collection<RepositoryAccess> source)
			throws CoreException
	{
		Collection<RepositoryAccess> commonRoots = null;
		for(RepositoryAccess repoAccess : source)
		{
			URI url = repoAccess.getSvnURL();
			String[] urlSegs = Path.fromPortableString(url.getPath()).segments();
			for(RepositoryAccess repoAccessCmp : source)
			{
				if(repoAccess == repoAccessCmp)
					continue;

				URI cmp = repoAccessCmp.getSvnURL();
				if(!(Trivial.equalsAllowNull(url.getHost(), cmp.getHost())
						&& Trivial.equalsAllowNull(url.getScheme(), cmp.getScheme()) && url.getPort() == cmp.getPort()))
					continue;

				String[] cmpSegs = Path.fromPortableString(cmp.getPath()).segments();
				int maxSegs = urlSegs.length;
				if(maxSegs > cmpSegs.length)
					maxSegs = cmpSegs.length;

				int idx;
				for(idx = 0; idx < maxSegs; ++idx)
					if(!urlSegs[idx].equals(cmpSegs[idx]))
						break;

				if(idx < 1)
					continue;

				String user = repoAccess.getUser();
				String cmpUser = repoAccessCmp.getUser();
				if(user == null)
					user = cmpUser;
				else
				{
					if(!(cmpUser == null || user.equals(cmpUser)))
						continue;
				}

				String password = repoAccess.getPassword();
				String cmpPassword = repoAccessCmp.getPassword();
				if(password == null)
					password = cmpPassword;
				else
				{
					if(!(cmpPassword == null || password.equals(cmpPassword)))
						continue;
				}

				StringBuilder bld = new StringBuilder();
				bld.append(url.getScheme());
				bld.append("://"); //$NON-NLS-1$
				if(url.getHost() != null)
				{
					bld.append(url.getHost());
					if(url.getPort() != -1)
					{
						bld.append(":"); //$NON-NLS-1$
						bld.append(url.getPort());
					}
				}
				for(int pdx = 0; pdx < idx; ++pdx)
				{
					String seg = urlSegs[pdx];
					bld.append('/');
					if(idx > 0 && seg.equals("trunk") || seg.equals("tags") || seg.equals("branches")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						//
						// Assume that common root is above this folder
						//
						break;

					bld.append(seg);
				}
				try
				{
					if(commonRoots == null)
						commonRoots = new HashSet<RepositoryAccess>();
					commonRoots.add(new RepositoryAccess(new URI(bld.toString()), user, password));
				}
				catch(URISyntaxException e)
				{
					throw BuckminsterException.wrap(e);
				}
			}
		}

		if(commonRoots == null)
			//
			// No common roots found
			//
			return source;

		// Add all SVNUrl's for which we don't have a common root
		//
		Set<RepositoryAccess> rogueRoots = null;
		for(RepositoryAccess repoAccess : source)
		{
			boolean found = false;
			URI url = repoAccess.getSvnURL();
			String[] urlSegs = Path.fromPortableString(url.getPath()).segments();
			for(RepositoryAccess repoAccessCmp : commonRoots)
			{
				URI cmp = repoAccessCmp.getSvnURL();
				if(!(Trivial.equalsAllowNull(url.getHost(), cmp.getHost())
						&& Trivial.equalsAllowNull(url.getScheme(), cmp.getScheme()) && url.getPort() == cmp.getPort()))
					continue;

				String[] cmpSegs = Path.fromPortableString(cmp.getPath()).segments();
				int maxSegs = cmpSegs.length;
				if(maxSegs > urlSegs.length)
					continue;

				int idx;
				for(idx = 0; idx < maxSegs; ++idx)
					if(!urlSegs[idx].equals(cmpSegs[idx]))
						break;

				if(idx < maxSegs)
					continue;

				String user = repoAccess.getUser();
				String cmpUser = repoAccessCmp.getUser();
				if(!(user == null || cmpUser == null || user.equals(cmpUser)))
					continue;

				String password = repoAccess.getPassword();
				String cmpPassword = repoAccessCmp.getPassword();
				if(!(password == null || cmpPassword == null || password.equals(cmpPassword)))
					continue;

				found = true;
				break;
			}
			if(found)
				continue;

			if(rogueRoots == null)
				rogueRoots = new HashSet<RepositoryAccess>();
			rogueRoots.add(repoAccess);
		}
		if(rogueRoots != null)
			commonRoots.addAll(rogueRoots);
		return commonRoots;
	}

	final protected void addUnknownRoot(Map<String, String> properties, RepositoryAccess ra)
	{
		synchronized(properties)
		{
			final String unknownRootPrefix = getUnknownRootPrefix();
			int maxNum = -1;
			final String raStr = ra.toString();
			for(Map.Entry<String, String> entries : properties.entrySet())
			{
				String key = entries.getKey();
				if(key.startsWith(unknownRootPrefix))
				{
					int lastDot = key.lastIndexOf('.');
					if(lastDot < 0)
						continue;

					try
					{
						int keyNum = Integer.parseInt(key.substring(lastDot + 1));
						if(maxNum < keyNum)
							maxNum = keyNum;
					}
					catch(NumberFormatException e)
					{
						continue;
					}
					if(entries.getValue().equals(raStr))
						//
						// Entry is already present. Don't recreate
						//
						return;
				}
			}
			properties.put(unknownRootPrefix + (maxNum + 1), raStr);
		}
	}

	final protected List<RepositoryAccess> getUnknownRoots(Map<String, String> properties)
	{
		synchronized(properties)
		{
			List<RepositoryAccess> unknownRoots = null;
			for(Map.Entry<String, String> entries : properties.entrySet())
			{
				String key = entries.getKey();
				if(key.startsWith(getUnknownRootPrefix()))
				{
					RepositoryAccess ra;
					try
					{
						ra = new RepositoryAccess(entries.getValue());
					}
					catch(URISyntaxException e)
					{
						// Bogus entry
						continue;
					}
					if(unknownRoots == null)
						unknownRoots = new ArrayList<RepositoryAccess>();
					unknownRoots.add(ra);
				}
			}
			if(unknownRoots == null)
				unknownRoots = Collections.emptyList();
			return unknownRoots;
		}
	}

	final public boolean hasTrunkStructure()
	{
		return m_trunkStructure;
	}

	final public REPO_LOCATION_TYPE getRepositoryLocation()
	{
		return m_repositoryLocation;
	}

	final public URI getSVNUrl() throws CoreException
	{
		return getSVNUrl(null);
	}

	final public URI getSVNUrl(String fileName) throws CoreException
	{
		StringBuilder bld = new StringBuilder();
		bld.append(m_urlLeadIn);

		if(m_branchOrTag == null)
		{
			if(m_trunkStructure)
				bld.append("/trunk"); //$NON-NLS-1$

			if(m_module != null)
			{
				bld.append('/');
				bld.append(m_module);
			}
		}
		else if(m_branchOrTag.getType() == VersionSelector.BRANCH)
		{
			bld.append("/branches"); //$NON-NLS-1$
			if(m_moduleBeforeBranch && m_module != null)
			{
				bld.append('/');
				bld.append(m_module);
			}
			bld.append('/');
			bld.append(m_branchOrTag.getName());
			if(m_moduleAfterBranch && m_module != null)
			{
				bld.append('/');
				bld.append(m_module);
			}
		}
		else
		{
			bld.append("/tags"); //$NON-NLS-1$
			if(m_moduleBeforeTag && m_module != null)
			{
				bld.append('/');
				bld.append(m_module);
			}
			bld.append('/');
			bld.append(m_branchOrTag.getName());
			if(m_moduleAfterTag && m_module != null)
			{
				bld.append('/');
				bld.append(m_module);
			}
		}

		if(m_subModule != null)
		{
			bld.append('/');
			bld.append(m_subModule);
		}
		if(fileName != null)
		{
			bld.append('/');
			bld.append(fileName);
		}
		try
		{
			return new URI(bld.toString());
		}
		catch(URISyntaxException e)
		{
			throw BuckminsterException.wrap(e);
		}
	}

	final public SVN_REVISION_TYPE getRevision()
	{
		return m_revision;
	}

	/**
	 * Returns the directory where it's expected to find a list of branches or tags.
	 * 
	 * @param branches
	 *            true if branches, false if tags.
	 * @return The SVNUrl appointing the branches or tags directory.
	 * @throws MalformedURLException
	 */
	final public URI getSVNRootUrl(boolean branches) throws CoreException
	{
		StringBuilder bld = new StringBuilder();
		bld.append(m_urlLeadIn);

		if(branches)
		{
			bld.append("/branches"); //$NON-NLS-1$
			if(m_moduleBeforeBranch && m_module != null)
			{
				bld.append('/');
				bld.append(m_module);
			}
		}
		else
		{
			bld.append("/tags"); //$NON-NLS-1$
			if(m_moduleBeforeTag && m_module != null)
			{
				bld.append('/');
				bld.append(m_module);
			}
		}
		try
		{
			return new URI(bld.toString());
		}
		catch(URISyntaxException e)
		{
			throw BuckminsterException.wrap(e);
		}
	}

	final public SVN_ENTRY_TYPE[] listFolder(URI url, IProgressMonitor monitor) throws CoreException
	{
		// Synchronizing on an interned string should make it impossible for two
		// sessions to request the same entry from the remote server
		//
		String key = GenericCache.cacheKey(url, getRevision()).intern();
		synchronized(key)
		{
			SVN_ENTRY_TYPE[] list = m_cache.get(key);
			if(list != null)
				return list;

			Logger logger = CorePlugin.getLogger();

			try
			{
				logger.debug("Listing remote folder %s", key); //$NON-NLS-1$
				list = innerListFolder(url, monitor);
				if(list == null || list.length == 0)
				{
					logger.debug("Remote folder had no entries %s", key); //$NON-NLS-1$
					list = getEmptyEntryList();
				}
				m_cache.put(key, list);
				return list;
			}
			catch(Exception e)
			{
				final boolean hasParts = SvnExceptionHandler.hasParts(e, Messages.exception_part_non_existent,
						Messages.exception_part_not_found);
				if(hasParts)
				{
					logger.debug(Messages.remote_folder_does_not_exist_0, key);
					return getEmptyEntryList();
				}
				throw BuckminsterException.wrap(e);
			}
			finally
			{
				monitor.done();
			}
		}
	}

	abstract protected SVN_ENTRY_TYPE[] innerListFolder(URI url, IProgressMonitor monitor) throws Exception;

	abstract protected SVN_ENTRY_TYPE[] getEmptyEntryList();
}
