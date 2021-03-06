/*****************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.pde.cspecgen.feature;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.buckminster.core.cspec.IComponentRequest;
import org.eclipse.buckminster.core.cspec.builder.ActionBuilder;
import org.eclipse.buckminster.core.cspec.builder.CSpecBuilder;
import org.eclipse.buckminster.core.cspec.builder.ComponentRequestBuilder;
import org.eclipse.buckminster.core.cspec.builder.GroupBuilder;
import org.eclipse.buckminster.core.cspec.model.ComponentRequest;
import org.eclipse.buckminster.core.cspec.model.UpToDatePolicy;
import org.eclipse.buckminster.core.ctype.IComponentType;
import org.eclipse.buckminster.core.helpers.FilterUtils;
import org.eclipse.buckminster.core.query.model.ComponentQuery;
import org.eclipse.buckminster.core.reader.ICatalogReader;
import org.eclipse.buckminster.core.version.VersionHelper;
import org.eclipse.buckminster.osgi.filter.Filter;
import org.eclipse.buckminster.pde.MatchRule;
import org.eclipse.buckminster.pde.cspecgen.CSpecGenerator;
import org.eclipse.buckminster.pde.internal.EclipsePlatformReaderType;
import org.eclipse.buckminster.pde.tasks.VersionConsolidator;
import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.buckminster.runtime.Trivial;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.internal.p2.publisher.eclipse.IProductDescriptor;
import org.eclipse.equinox.p2.metadata.IVersionedId;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.PluginModelManager;
import org.eclipse.pde.internal.core.ifeature.IFeature;
import org.eclipse.pde.internal.core.ifeature.IFeatureChild;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.core.ifeature.IFeaturePlugin;

@SuppressWarnings("restriction")
public abstract class CSpecFromFeature extends CSpecGenerator {

	private static final String SOURCE_SUFFIX = ".source"; //$NON-NLS-1$

	private static final String SOURCE_FEATURE_SUFFIX = ".source.feature"; //$NON-NLS-1$

	private static final IFeatureChild[] noFeatures = new IFeatureChild[0];

	public static String getIdWithoutSource(String sourceId) {
		if (sourceId.endsWith(SOURCE_SUFFIX))
			return sourceId.substring(0, sourceId.length() - SOURCE_SUFFIX.length());
		if (sourceId.endsWith(SOURCE_FEATURE_SUFFIX))
			return sourceId.substring(0, sourceId.length() - SOURCE_FEATURE_SUFFIX.length()) + ".feature"; //$NON-NLS-1$
		return null;
	}

	private static boolean isListOK(String list, Object item) {
		if (list == null || list.length() == 0)
			return true;
		StringTokenizer tokens = new StringTokenizer(list, ","); //$NON-NLS-1$
		while (tokens.hasMoreTokens())
			if (item.equals("*") || item.equals(tokens.nextElement())) //$NON-NLS-1$
				return true;
		return false;
	}

	protected final IFeature feature;

	protected CSpecFromFeature(CSpecBuilder cspecBuilder, ICatalogReader reader, IFeature feature) {
		super(cspecBuilder, reader);
		this.feature = feature;
	}

	@Override
	public void generate(IProgressMonitor monitor) throws CoreException {
		CSpecBuilder cspec = getCSpec();
		cspec.setName(feature.getId());
		cspec.setVersion(VersionHelper.parseVersion(feature.getVersion()));
		cspec.setComponentTypeID(IComponentType.ECLIPSE_FEATURE);
		cspec.setFilter(FilterUtils.createFilter(feature.getOS(), feature.getWS(), feature.getArch(), feature.getNL()));

		// This feature and all included features. Does not imply copying since
		// the group will reference the features where they are found.
		//
		cspec.addGroup(ATTRIBUTE_FEATURE_REFS, true); // without self
		cspec.addGroup(ATTRIBUTE_SOURCE_FEATURE_REFS, true).setFilter(SOURCE_FILTER);

		// All bundles imported by this feature and all included features. Does
		// not imply copying since the group will reference the bundles where
		// they
		// are found.
		//
		cspec.addGroup(ATTRIBUTE_BUNDLE_JARS, true);

		// Source of all bundles imported by this feature and all included
		// features.
		//
		cspec.addGroup(ATTRIBUTE_SOURCE_BUNDLE_JARS, true).setFilter(SOURCE_FILTER);

		// This feature, its imported bundles, all included features, and their
		// imported bundles copied into a site structure (with a plugins and a
		// features folder).
		//
		cspec.addGroup(ATTRIBUTE_FEATURE_EXPORTS, true);
		cspec.addGroup(ATTRIBUTE_SITE_FEATURE_EXPORTS, true);

		cspec.addGroup(ATTRIBUTE_PRODUCT_CONFIG_EXPORTS, true);
		generateRemoveDirAction("build", OUTPUT_DIR, true, ATTRIBUTE_FULL_CLEAN); //$NON-NLS-1$

		IComponentRequest licenseFeature = addFeatures();
		addPlugins();

		MonitorUtils.begin(monitor, 100);

		createFeatureJarAction(licenseFeature, MonitorUtils.subMonitor(monitor, 20));
		createFeatureSourceJarAction();
		createFeatureExportsAction();

		GroupBuilder featureJars = cspec.addGroup(ATTRIBUTE_FEATURE_JARS, true); // including
																					// self
		featureJars.addLocalPrerequisite(ATTRIBUTE_FEATURE_JAR);
		featureJars.addLocalPrerequisite(ATTRIBUTE_FEATURE_REFS);

		GroupBuilder sourceFeatureJars = cspec.addGroup(ATTRIBUTE_SOURCE_FEATURE_JARS, true); // including
																								// self
		sourceFeatureJars.setFilter(SOURCE_FILTER);
		sourceFeatureJars.addLocalPrerequisite(ATTRIBUTE_SOURCE_FEATURE_JAR);
		sourceFeatureJars.addLocalPrerequisite(ATTRIBUTE_SOURCE_FEATURE_REFS);

		createSiteActions(MonitorUtils.subMonitor(monitor, 80));
		MonitorUtils.done(monitor);
	}

	@Override
	protected void addProductFeatures(IProductDescriptor productDescriptor) throws CoreException {
		if (!productDescriptor.useFeatures())
			return;

		List<IVersionedId> features = productDescriptor.getFeatures();
		if (features.size() == 0)
			return;

		ComponentQuery query = getReader().getNodeQuery().getComponentQuery();
		CSpecBuilder cspec = getCSpec();
		ActionBuilder fullClean = cspec.getRequiredAction(ATTRIBUTE_FULL_CLEAN);
		GroupBuilder featureRefs = cspec.getRequiredGroup(ATTRIBUTE_FEATURE_REFS);
		GroupBuilder featureSourceRefs = cspec.getRequiredGroup(ATTRIBUTE_SOURCE_FEATURE_REFS);
		GroupBuilder bundleJars = cspec.getRequiredGroup(ATTRIBUTE_BUNDLE_JARS);
		GroupBuilder sourceBundleJars = cspec.getRequiredGroup(ATTRIBUTE_SOURCE_BUNDLE_JARS);

		String self = cspec.getName();
		for (IVersionedId productFeature : features) {
			if (productFeature.getId().equals(self))
				continue;

			ComponentRequestBuilder dep = createDependency(productFeature, IComponentType.ECLIPSE_FEATURE);
			if (skipComponent(query, dep))
				continue;

			cspec.addDependency(dep);
			featureRefs.addExternalPrerequisite(dep, ATTRIBUTE_FEATURE_JARS);
			featureSourceRefs.addExternalPrerequisite(dep, ATTRIBUTE_SOURCE_FEATURE_JARS);
			bundleJars.addExternalPrerequisite(dep, ATTRIBUTE_BUNDLE_JARS);
			sourceBundleJars.addExternalPrerequisite(dep, ATTRIBUTE_SOURCE_BUNDLE_JARS);
			fullClean.addExternalPrerequisite(dep, ATTRIBUTE_FULL_CLEAN);
		}
	}

	@Override
	protected String getPropertyFileName() {
		return FEATURE_PROPERTIES_FILE;
	}

	@Override
	protected boolean isFeature() {
		return true;
	}

	/**
	 * Adds all feature dependencies. If a license feature is referenced, it is
	 * also added.
	 * 
	 * @return The license feature if any, otherwise <code>null</code>.
	 * @throws CoreException
	 */
	IComponentRequest addFeatures() throws CoreException {
		String licenseFeatureID = Trivial.trim(feature.getLicenseFeatureID());
		IFeatureChild[] features = feature.getIncludedFeatures();
		if (features == null)
			features = noFeatures;

		if (features.length == 0 && licenseFeatureID == null)
			return null;

		ComponentQuery query = getReader().getNodeQuery().getComponentQuery();
		CSpecBuilder cspec = getCSpec();
		ActionBuilder fullClean = cspec.getRequiredAction(ATTRIBUTE_FULL_CLEAN);
		GroupBuilder featureRefs = cspec.getRequiredGroup(ATTRIBUTE_FEATURE_REFS);
		GroupBuilder featureSourceRefs = cspec.getRequiredGroup(ATTRIBUTE_SOURCE_FEATURE_REFS);
		GroupBuilder bundleJars = cspec.getRequiredGroup(ATTRIBUTE_BUNDLE_JARS);
		GroupBuilder sourceBundleJars = cspec.getRequiredGroup(ATTRIBUTE_SOURCE_BUNDLE_JARS);
		GroupBuilder productConfigExports = cspec.getRequiredGroup(ATTRIBUTE_PRODUCT_CONFIG_EXPORTS);
		for (IFeatureChild includedFeature : features) {
			ComponentRequestBuilder dep = createDependency(includedFeature);
			if (skipComponent(query, dep))
				continue;

			boolean sourceInTP = false;
			cspec.addDependency(dep);
			String idWithoutSource = getIdWithoutSource(dep.getName());
			if (idWithoutSource == null) {
				featureRefs.addExternalPrerequisite(dep, ATTRIBUTE_FEATURE_JARS);
				bundleJars.addExternalPrerequisite(dep, ATTRIBUTE_BUNDLE_JARS);
				fullClean.addExternalPrerequisite(dep, ATTRIBUTE_FULL_CLEAN);
				productConfigExports.addExternalPrerequisite(dep, ATTRIBUTE_PRODUCT_CONFIG_EXPORTS);
			} else {
				// Watch out for source for self
				//
				if (idWithoutSource.equals(cspec.getName())) {
					continue;
				}
				IFeatureModel sourceFeature = EclipsePlatformReaderType.getBestFeature(dep.getName(), dep.getVersionRange(), null);
				if (sourceFeature != null) {
					// This one is present in the target platform so we include
					// it just like any
					// other feature.
					featureRefs.addExternalPrerequisite(dep, ATTRIBUTE_FEATURE_JARS);
					bundleJars.addExternalPrerequisite(dep, ATTRIBUTE_BUNDLE_JARS);
					sourceInTP = true;
				}
			}
			if (!sourceInTP) {
				// For source generation
				sourceBundleJars.addExternalPrerequisite(dep, ATTRIBUTE_SOURCE_BUNDLE_JARS);
				featureSourceRefs.addExternalPrerequisite(dep, ATTRIBUTE_SOURCE_FEATURE_JARS);
			}
		}

		if (licenseFeatureID == null)
			return null;

		ComponentRequestBuilder dep = new ComponentRequestBuilder();
		dep.setName(licenseFeatureID);
		dep.setComponentTypeID(IComponentType.ECLIPSE_FEATURE);

		Version licenseFeatureVersion = VersionHelper.parseVersion(feature.getLicenseFeatureVersion());
		if (licenseFeatureVersion != null)
			dep.setVersionRange(VersionHelper.exactRange(licenseFeatureVersion));

		if (skipComponent(query, dep))
			return null;

		cspec.addDependency(dep);
		featureRefs.addExternalPrerequisite(dep, ATTRIBUTE_FEATURE_JARS);
		bundleJars.addExternalPrerequisite(dep, ATTRIBUTE_BUNDLE_JARS);
		fullClean.addExternalPrerequisite(dep, ATTRIBUTE_FULL_CLEAN);
		return dep;
	}

	void addPlugins() throws CoreException {
		String brandingPluginName = feature.getPlugin();
		IFeaturePlugin[] plugins = feature.getPlugins();
		if (plugins != null && plugins.length > 0) {
			Map<String, ? extends Object> props = getReader().getNodeQuery().getProperties();
			Object os = props.get(org.eclipse.buckminster.core.TargetPlatform.TARGET_OS);
			Object ws = props.get(org.eclipse.buckminster.core.TargetPlatform.TARGET_WS);
			Object arch = props.get(org.eclipse.buckminster.core.TargetPlatform.TARGET_ARCH);

			ComponentQuery query = getReader().getNodeQuery().getComponentQuery();
			CSpecBuilder cspec = getCSpec();
			ActionBuilder fullClean = cspec.getRequiredAction(ATTRIBUTE_FULL_CLEAN);
			GroupBuilder bundleJars = cspec.getRequiredGroup(ATTRIBUTE_BUNDLE_JARS);
			GroupBuilder sourceBundleJars = cspec.getRequiredGroup(ATTRIBUTE_SOURCE_BUNDLE_JARS);
			GroupBuilder productConfigExports = cspec.getRequiredGroup(ATTRIBUTE_PRODUCT_CONFIG_EXPORTS);
			PluginModelManager manager = PDECore.getDefault().getModelManager();

			boolean hasBogusFragments = false;
			String id = feature.getId();
			if (VersionConsolidator.getBooleanProperty(getProperties(), "buckminster.handle.incomplete.platform.features", false)) { //$NON-NLS-1$
				hasBogusFragments = "org.eclipse.platform".equals(id) //$NON-NLS-1$
						|| "org.eclipse.equinox.executable".equals(id) //$NON-NLS-1$
						|| "org.eclipse.rcp".equals(id); //$NON-NLS-1$
			} else {
				// We still need this here due to
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=319345
				hasBogusFragments = "org.eclipse.equinox.executable".equals(id); //$NON-NLS-1$
			}

			for (IFeaturePlugin plugin : plugins) {
				if (!(isListOK(plugin.getOS(), os) && isListOK(plugin.getWS(), ws) && isListOK(plugin.getArch(), arch))) {
					// Only include this if we can find it in the target
					// platform
					//
					if (manager.findEntry(plugin.getId()) == null)
						continue;
				}

				if (hasBogusFragments && (plugin.getOS() != null || plugin.getWS() != null || plugin.getArch() != null)) {
					// Only include this if we can find it in the target
					// platform.
					// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=213437
					//
					if (manager.findEntry(plugin.getId()) == null)
						continue;
				}

				ComponentRequestBuilder dep = createDependency(plugin);
				if (brandingPluginName != null && brandingPluginName.equals(dep.getName()))
					brandingPluginName = null;

				if (skipComponent(query, dep))
					continue;

				if (!addDependency(dep))
					continue;

				bundleJars.addExternalPrerequisite(dep, ATTRIBUTE_BUNDLE_AND_FRAGMENTS);

				// We either add this bundles action to generate it's own
				// source, or we add an already existing source bundle. Let's
				// check with the target platform
				if (!plugin.getId().endsWith(SOURCE_SUFFIX)) {
					String sourceId = plugin.getId() + SOURCE_SUFFIX;
					if (manager.findEntry(sourceId) == null)
						sourceBundleJars.addExternalPrerequisite(dep, ATTRIBUTE_BUNDLE_AND_FRAGMENTS_SOURCE);
					else {
						ComponentRequestBuilder sourceDep = new ComponentRequestBuilder();
						sourceDep.setName(sourceId);
						sourceDep.setComponentTypeID(IComponentType.OSGI_BUNDLE);
						sourceDep.setVersionRange(dep.getVersionRange());
						addDependency(sourceDep);
						sourceBundleJars.addExternalPrerequisite(sourceDep, ATTRIBUTE_BUNDLE_JAR);
					}
				}
				fullClean.addExternalPrerequisite(dep, ATTRIBUTE_FULL_CLEAN);
				productConfigExports.addExternalPrerequisite(dep, ATTRIBUTE_PRODUCT_CONFIG_EXPORTS);
			}
		}

		if (brandingPluginName != null) {
			// We have a branding plugin that we have no other dependency to.
			// This is also a valid dependency so let's add it.
			ComponentRequestBuilder dep = getCSpec().createDependencyBuilder();
			dep.setName(brandingPluginName);
			dep.setComponentTypeID(IComponentType.OSGI_BUNDLE);
			addDependency(dep);
		}
	}

	ComponentRequestBuilder createDependency(IFeatureChild featureChild) throws CoreException {
		Filter filter = FilterUtils.createFilter(featureChild.getOS(), featureChild.getWS(), featureChild.getArch(), featureChild.getNL());
		if (featureChild.isOptional())
			filter = ComponentRequest.P2_OPTIONAL_FILTER.addFilterWithAnd(filter);
		return createDependency(featureChild.getId(), IComponentType.ECLIPSE_FEATURE, featureChild.getVersion(),
				MatchRule.fromPDE(featureChild.getMatch()), filter);
	}

	ComponentRequestBuilder createDependency(IFeaturePlugin plugin) throws CoreException {
		Filter filter = FilterUtils.createFilter(plugin.getOS(), plugin.getWS(), plugin.getArch(), plugin.getNL());
		return createDependency(plugin.getId(), IComponentType.OSGI_BUNDLE, plugin.getVersion(), MatchRule.NONE, filter);
	}

	abstract void createFeatureJarAction(IComponentRequest licenseFeature, IProgressMonitor monitor) throws CoreException;

	abstract void createFeatureSourceJarAction() throws CoreException;

	abstract void createSiteActions(IProgressMonitor monitor) throws CoreException;

	private ActionBuilder createCopyFeaturesAction() throws CoreException {
		// Copy all features (including this one) to the features directory.
		//
		ActionBuilder copyFeatures = addAntAction(ACTION_COPY_FEATURES, TASK_COPY_GROUP, false);
		copyFeatures.addLocalPrerequisite(ATTRIBUTE_FEATURE_JARS);
		copyFeatures.addLocalPrerequisite(ATTRIBUTE_SOURCE_FEATURE_JARS);
		copyFeatures.setPrerequisitesAlias(ALIAS_REQUIREMENTS);
		copyFeatures.setProductAlias(ALIAS_OUTPUT);
		copyFeatures.setProductBase(OUTPUT_DIR_SITE.append(FEATURES_FOLDER));
		copyFeatures.setUpToDatePolicy(UpToDatePolicy.MAPPER);
		return copyFeatures;
	}

	private void createFeatureExportsAction() throws CoreException {
		GroupBuilder featureExports = getCSpec().getRequiredGroup(ATTRIBUTE_FEATURE_EXPORTS);
		featureExports.addLocalPrerequisite(createCopyFeaturesAction());
		featureExports.addLocalPrerequisite(createCopyPluginsAction());
		featureExports.setPrerequisiteRebase(OUTPUT_DIR_SITE);
	}
}
