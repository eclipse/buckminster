package org.eclipse.buckminster.core.materializer;

import org.eclipse.buckminster.core.helpers.FileUtils;
import org.eclipse.buckminster.core.mspec.ConflictResolution;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

public class ExternalDataArea {
	private static final String META_AREA = ".metadata"; //$NON-NLS-1$

	private static final String PLUGIN_DATA = ".plugins"; //$NON-NLS-1$

	private static final String PREFERENCES_FILE_NAME = "pref_store.ini"; //$NON-NLS-1$

	private final IPath location; // The location of the instance data

	public ExternalDataArea(IPath location, ConflictResolution strategy) throws CoreException {
		this.location = location;
		FileUtils.prepareDestination(location.append(META_AREA).toFile(), strategy, new NullProgressMonitor());
	}

	public IPath getInstanceDataLocation() {
		return location;
	}

	public IPath getMetadataLocation() {
		return location.append(META_AREA);
	}

	public IPath getPreferenceLocation(String bundleName, boolean create) throws IllegalStateException {
		IPath result = getStateLocation(bundleName);
		if (create)
			result.toFile().mkdirs();
		return result.append(PREFERENCES_FILE_NAME);
	}

	public IPath getStateLocation(String bundleName) {
		return getMetadataLocation().append(PLUGIN_DATA).append(bundleName);
	}
}
