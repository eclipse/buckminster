/*******************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ******************************************************************************/
package org.eclipse.buckminster.pde.tasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.buckminster.pde.IPDEConstants;
import org.eclipse.buckminster.pde.internal.FeatureModelReader;
import org.eclipse.buckminster.pde.internal.model.EditableFeatureModel;
import org.eclipse.buckminster.pde.internal.model.ExternalBundleModel;
import org.eclipse.buckminster.runtime.IOUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.internal.build.IBuildPropertiesConstants;
import org.eclipse.pde.internal.core.bundle.BundleFragmentModel;
import org.eclipse.pde.internal.core.bundle.BundlePluginModel;
import org.eclipse.pde.internal.core.feature.FeatureChild;
import org.eclipse.pde.internal.core.feature.FeaturePlugin;
import org.eclipse.pde.internal.core.ibundle.IBundlePluginModelBase;
import org.eclipse.pde.internal.core.ifeature.IFeature;
import org.eclipse.pde.internal.core.ifeature.IFeatureChild;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.core.ifeature.IFeaturePlugin;

@SuppressWarnings("restriction")
public class SourceFeatureCreator implements IPDEConstants, IBuildPropertiesConstants
{
	private static final String FEATURE_SUFFIX = ".feature"; //$NON-NLS-1$

	private static final String SOURCE_SUFFIX = ".source"; //$NON-NLS-1$

	private final File m_inputFile;

	private final File m_outputFile;

	private List<File> m_featuresAndBundles;

	public SourceFeatureCreator(File inputFile, File outputFile, List<File> featuresAndBundles)
	{
		m_inputFile = inputFile;
		m_outputFile = outputFile;
		m_featuresAndBundles = featuresAndBundles;
	}

	public void run() throws CoreException, FileNotFoundException
	{
		IFeature originalFeature = FeatureModelReader.readEditableFeatureModel(m_inputFile).getFeature();
		EditableFeatureModel featureModel = new EditableFeatureModel(m_outputFile);
		featureModel.setDirty(true);
		IFeature sourceFeature = featureModel.getFeature();

		String originalId = originalFeature.getId();
		StringBuilder sourceIdBld = new StringBuilder();
		if(originalId.endsWith(FEATURE_SUFFIX))
		{
			sourceIdBld.append(originalId, 0, originalId.length() - FEATURE_SUFFIX.length());
			sourceIdBld.append(SOURCE_SUFFIX);
			sourceIdBld.append(FEATURE_SUFFIX);
		}
		else
		{
			sourceIdBld.append(originalId);
			sourceIdBld.append(SOURCE_SUFFIX);
		}
		sourceFeature.setId(sourceIdBld.toString());
		sourceFeature.setVersion(originalFeature.getVersion());
		sourceFeature.setLabel("Source bundles for " + originalFeature.getLabel()); //$NON-NLS-1$

		sourceFeature.setOS(originalFeature.getOS());
		sourceFeature.setArch(originalFeature.getArch());
		sourceFeature.setWS(originalFeature.getWS());
		sourceFeature.setNL(originalFeature.getNL());
		sourceFeature.setProviderName(originalFeature.getProviderName());
		sourceFeature.setURL(originalFeature.getURL());
		for(File featureOrBundle : m_featuresAndBundles)
		{
			InputStream input = null;
			try
			{
				try
				{
					input = FeatureConsolidator.getInput(featureOrBundle, FEATURE_FILE);
					IFeatureModel model = FeatureModelReader.readFeatureModel(input);
					IFeature feature = model.getFeature();

					FeatureChild fc = new FeatureChild();
					fc.loadFrom(model.getFeature());
					fc.setArch(feature.getArch());
					fc.setOS(feature.getOS());
					fc.setWS(feature.getWS());
					fc.setNL(feature.getNL());
					fc.setLabel(feature.getLabel());
					fc.setModel(featureModel);

					sourceFeature.addIncludedFeatures(new IFeatureChild[] { fc });
					continue;
				}
				catch(FileNotFoundException e)
				{
				}
				try
				{
					input = FeatureConsolidator.getInput(featureOrBundle, BUNDLE_FILE);
					ExternalBundleModel model = new ExternalBundleModel();
					model.load(input, true);
					IBundlePluginModelBase bmodel = model.isFragmentModel()
							? new BundleFragmentModel()
							: new BundlePluginModel();

					bmodel.setEnabled(true);
					bmodel.setBundleModel(model);

					FeaturePlugin fp = new FeaturePlugin();
					IPluginBase plugin = bmodel.getPluginBase();
					fp.loadFrom(plugin);
					fp.setModel(featureModel);
					fp.setUnpack(false);

					// Load arch etc. from corresponding original plug-in (if we find it)
					//
					String ver = plugin.getVersion();
					String id = plugin.getId();
					if(id.endsWith(SOURCE_SUFFIX))
					{
						String origId = id.substring(0, id.length() - SOURCE_SUFFIX.length());
						for(IFeaturePlugin originalPlugin : originalFeature.getPlugins())
						{
							if(originalPlugin.getId().equals(origId) && originalPlugin.getVersion().equals(ver))
							{
								fp.setArch(originalPlugin.getArch());
								fp.setOS(originalPlugin.getOS());
								fp.setWS(originalPlugin.getWS());
								fp.setNL(originalPlugin.getNL());
								break;
							}
						}
					}
					sourceFeature.addPlugins(new IFeaturePlugin[] { fp });
					continue;
				}
				catch(FileNotFoundException e)
				{
				}
			}
			finally
			{
				IOUtils.close(input);
			}
		}
		featureModel.save();
	}
}
