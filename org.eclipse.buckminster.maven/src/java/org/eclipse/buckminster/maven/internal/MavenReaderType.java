/*******************************************************************************
 * Copyright (c) 2004, 2006
 * Thomas Hallgren, Kenneth Olwing, Mitch Sonies
 * Pontus Rydin, Nils Unden, Peer Torngren
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed above, as Initial Contributors under such license.
 * The text of such license is available at www.eclipse.org.
 *******************************************************************************/
package org.eclipse.buckminster.maven.internal;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import org.eclipse.buckminster.core.cspec.model.ComponentRequest;
import org.eclipse.buckminster.core.ctype.IComponentType;
import org.eclipse.buckminster.core.materializer.MaterializationContext;
import org.eclipse.buckminster.core.metadata.model.Resolution;
import org.eclipse.buckminster.core.reader.IComponentReader;
import org.eclipse.buckminster.core.reader.IVersionFinder;
import org.eclipse.buckminster.core.reader.URLCatalogReaderType;
import org.eclipse.buckminster.core.resolver.NodeQuery;
import org.eclipse.buckminster.core.rmap.model.Provider;
import org.eclipse.buckminster.core.version.ProviderMatch;
import org.eclipse.buckminster.core.version.VersionHelper;
import org.eclipse.buckminster.core.version.VersionMatch;
import org.eclipse.buckminster.core.version.VersionSelector;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.p2.metadata.Version;

/**
 * @author Thomas Hallgren
 */
public class MavenReaderType extends URLCatalogReaderType {
	static void appendMavenVersionName(StringBuilder bld, VersionMatch vm) throws CoreException {
		String artifactInfo = vm.getArtifactInfo();
		if (artifactInfo != null) {
			int vnSplit = artifactInfo.indexOf('/');
			if (vnSplit >= 0) {
				// Artifact info stores <filename>/<version>
				//
				bld.append(artifactInfo, vnSplit + 1, artifactInfo.length());
				return;
			}
		}

		VersionSelector vs = vm.getBranchOrTag();
		if (vs != null && vs.getType() == VersionSelector.BRANCH) {
			bld.append(vs.getName());
			bld.append('-');
		}

		Version version = vm.getVersion();
		if (version != null)
			bld.append(VersionHelper.getOriginal(version));

		Date timestamp = vm.getTimestamp();
		if (timestamp != null) {
			if (version != null)
				bld.append('-');
			bld.append(VersionHelper.toTimestampString(timestamp));
		}
	}

	static URL createURL(URI repoURI, String path) throws CoreException {
		try {
			return new URI(repoURI.getScheme(), repoURI.getAuthority(), path, null, null).toURL();
		} catch (MalformedURLException e) {
			throw BuckminsterException.wrap(e);
		} catch (URISyntaxException e) {
			throw BuckminsterException.wrap(e);
		}
	}

	static IMapEntry getGroupAndArtifact(Provider provider, ComponentRequest request) throws CoreException {
		String name = request.getName();
		return (provider instanceof MavenProvider) ? ((MavenProvider) provider).getGroupAndArtifact(name) : MavenProvider
				.getDefaultGroupAndArtifact(name);
	}

	private final LocalCache localCache;

	public MavenReaderType() {
		localCache = new LocalCache(getLocalRepoPath());
	}

	@Override
	public IPath getInstallLocation(Resolution resolution, MaterializationContext context) throws CoreException {
		IMapEntry ga = getGroupAndArtifact(resolution.getProvider(), resolution.getRequest());
		VersionMatch vs = resolution.getVersionMatch();
		StringBuilder pbld = new StringBuilder();
		appendFolder(pbld, getMaterializationFolder());
		appendArtifactFolder(pbld, ga, vs);
		return Path.fromPortableString(pbld.toString());
	}

	@Override
	public IPath getLeafArtifact(Resolution resolution, MaterializationContext context) throws CoreException {
		IMapEntry ga = getGroupAndArtifact(resolution.getProvider(), resolution.getRequest());
		VersionMatch vs = resolution.getVersionMatch();
		StringBuilder pbld = new StringBuilder();
		appendFileName(pbld, ga.getArtifactId(), vs, null);
		return Path.fromPortableString(pbld.toString());
	}

	public IPath getLocalRepoPath() {
		// TODO: Control using preference setting
		//
		return getDefaultLocalRepoPath();
	}

	@Override
	public IComponentReader getReader(ProviderMatch providerMatch, IProgressMonitor monitor) throws CoreException {
		MonitorUtils.complete(monitor);
		return new MavenReader(this, providerMatch);
	}

	@Override
	public IVersionFinder getVersionFinder(Provider provider, IComponentType ctype, NodeQuery nodeQuery, IProgressMonitor monitor)
			throws CoreException {
		MonitorUtils.complete(monitor);
		return new MavenVersionFinder(this, provider, ctype, nodeQuery);
	}

	void appendArtifactFolder(StringBuilder pbld, IMapEntry mapEntry, VersionMatch vs) throws CoreException {
		appendFolder(pbld, mapEntry.getGroupId());
		appendFolder(pbld, "jars"); //$NON-NLS-1$
	}

	void appendFileName(StringBuilder bld, String artifactID, VersionMatch vm, String extension) throws CoreException {
		String artifactInfo = vm.getArtifactInfo();
		if (extension == null && artifactInfo != null) {
			int vnSplit = artifactInfo.indexOf('/');
			if (vnSplit >= 0) {
				// Artifact info stores <filename>/<version>
				//
				bld.append(artifactInfo, 0, vnSplit);
				return;
			}

			// Old style. Artifact info just stores extension
			//
			extension = artifactInfo;
		}

		bld.append(artifactID);
		bld.append('-');
		appendMavenVersionName(bld, vm);
		bld.append(extension);
	}

	void appendFolder(StringBuilder pbld, String folder) {
		pbld.append(folder);
		if (!folder.endsWith("/")) //$NON-NLS-1$
			pbld.append('/');
	}

	void appendPathToArtifact(StringBuilder pbld, IMapEntry mapEntry, VersionMatch vs) throws CoreException {
		appendArtifactFolder(pbld, mapEntry, vs);
		appendFileName(pbld, mapEntry.getArtifactId(), vs, null);
	}

	void appendPathToPom(StringBuilder pbld, IMapEntry mapEntry, VersionMatch vs) throws CoreException {
		appendPomFolder(pbld, mapEntry, vs);
		appendFileName(pbld, mapEntry.getArtifactId(), vs, ".pom"); //$NON-NLS-1$
	}

	void appendPomFolder(StringBuilder pbld, IMapEntry mapEntry, VersionMatch vs) throws CoreException {
		appendFolder(pbld, mapEntry.getGroupId());
		appendFolder(pbld, "poms"); //$NON-NLS-1$
	}

	VersionMatch createVersionMatch(ILocationResolver resolver, IMapEntry mapEntry, String versionStr) throws CoreException {
		return MavenComponentType.createVersionMatch(versionStr, null);
	}

	IPath getArtifactPath(IMapEntry mapEntry, VersionMatch vs) throws CoreException {
		StringBuilder pbld = new StringBuilder();
		appendPathToArtifact(pbld, mapEntry, vs);
		return new Path(pbld.toString());
	}

	URL getArtifactURL(URI repoURI, IMapEntry mapEntry, VersionMatch vs) throws CoreException {
		StringBuilder pbld = new StringBuilder();
		appendFolder(pbld, repoURI.getPath());
		appendPathToArtifact(pbld, mapEntry, vs);
		return createURL(repoURI, pbld.toString());
	}

	IPath getDefaultLocalRepoPath() {
		return new Path(System.getProperty("user.home")).append(".maven").append("cache"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	LocalCache getLocalCache() {
		return localCache;
	}

	String getMaterializationFolder() {
		return "maven"; //$NON-NLS-1$
	}

	IPath getPomPath(IMapEntry mapEntry, VersionMatch vs) throws CoreException {
		StringBuilder pbld = new StringBuilder();
		appendPathToPom(pbld, mapEntry, vs);
		return new Path(pbld.toString());
	}

	URL getPomURL(URI repoURI, IMapEntry mapEntry, VersionMatch vs) throws CoreException {
		StringBuilder pbld = new StringBuilder();
		appendFolder(pbld, repoURI.getPath());
		appendPathToPom(pbld, mapEntry, vs);
		return createURL(repoURI, pbld.toString());
	}

	void setPackaging(ProviderMatch providerMatch, String packaging) {
		// Our Maven 1 proxy doesn't support this
	}
}
