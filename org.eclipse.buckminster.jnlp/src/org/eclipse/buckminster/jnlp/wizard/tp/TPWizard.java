/*******************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ******************************************************************************/

package org.eclipse.buckminster.jnlp.wizard.tp;

import static org.eclipse.buckminster.jnlp.MaterializationConstants.ARTIFACT_TYPE_MSPEC;
import static org.eclipse.buckminster.jnlp.MaterializationConstants.ERROR_CODE_FILE_IO_EXCEPTION;
import static org.eclipse.buckminster.jnlp.MaterializationConstants.ERROR_CODE_MALFORMED_PROPERTY_EXCEPTION;
import static org.eclipse.buckminster.jnlp.MaterializationConstants.ERROR_CODE_MATERIALIZATION_EXCEPTION;
import static org.eclipse.buckminster.jnlp.MaterializationConstants.LOCALPROP_ENABLE_TP_WIZARD;
import static org.eclipse.buckminster.jnlp.MaterializationConstants.VALUE_TRUE;
import static org.eclipse.buckminster.jnlp.MaterializationConstants.VALUE_FALSE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import org.eclipse.buckminster.core.CorePlugin;
import org.eclipse.buckminster.core.metadata.model.BillOfMaterials;
import org.eclipse.buckminster.core.mspec.builder.MaterializationNodeBuilder;
import org.eclipse.buckminster.core.mspec.builder.MaterializationSpecBuilder;
import org.eclipse.buckminster.core.mspec.model.MaterializationSpec;
import org.eclipse.buckminster.core.parser.IParser;
import org.eclipse.buckminster.core.version.IVersion;
import org.eclipse.buckminster.core.version.IVersionType;
import org.eclipse.buckminster.core.version.VersionFactory;
import org.eclipse.buckminster.jnlp.HelpLinkErrorDialog;
import org.eclipse.buckminster.jnlp.JNLPException;
import org.eclipse.buckminster.jnlp.MaterializationConstants;
import org.eclipse.buckminster.jnlp.MaterializationUtils;
import org.eclipse.buckminster.jnlp.MaterializerRunnable;
import org.eclipse.buckminster.jnlp.progress.MaterializationProgressProvider;
import org.eclipse.buckminster.jnlp.ui.general.wizard.AdvancedWizard;
import org.eclipse.buckminster.jnlp.wizard.install.InstallWizard;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.IOUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.TargetPlatform;
import org.eclipse.swt.graphics.Image;

/**
 * @author Karel Brezina
 * 
 */
public class TPWizard extends AdvancedWizard
{
	private static final String TP_WINDOW_TITLE = "Setup Eclipse Installation";

	private static final String ECLIPSE_FOLDER_NAME = "eclipse";
	
	private static final String OSGI_3_4_0 = "3.4.0";
	
	private static final String OS_WIN = "win32";

	private static final String OS_LINUX = "linux";

	private static final String OS_SOLARIS = "solaris";

	private static final String OS_HPUX = "hpux";

	private static final String OS_AIX = "aix";

	private static final String OS_MACOSX = "macosx";

	private static final String OSARCH_X86_64 = "x86_64";
	
	private static final String OSARCH_PPC = "ppc";
	
	private static final String PLATFORM_WIN32 = "win32";
	
	private static final String PLATFORM_WIN32_X86_64 = "win32-x86_64";

	private static final String PLATFORM_LINUX_GTK = "linux-gtk";
	
	private static final String PLATFORM_LINUX_GTK_X86_64 = "linux-gtk-x86_64";
	
	private static final String PLATFORM_LINUX_GTK_PPC = "linux-gtk-ppc";

	private static final String PLATFORM_SOLARIS_GTK = "solaris-gtk";
	
	private static final String PLATFORM_HPUX_MOTIF_IA64_32 = "hpux-motif-ia64_32";
	
	private static final String PLATFORM_AIX_MOTIF = "aix-motif";
	
	private static final String PLATFORM_MACOSX_CARBON = "macosx-carbon";
	
	private InstallWizard m_installWizard;

	private TPNewOrCurrentPage m_newOrCurrentPage;

	private TPNewRecommendedPage m_newRecommendedPage;
	
	private TPNewLocationPage m_newLocationPage;
	
	private TPBackupFolderPage m_backupFolderPage;

	private TPToolSelectionPage m_toolSelectionPage;
	
	private TPOperationPage m_operationPage;

	private TPDonePage m_donePage;

	private boolean m_newEclipse;
	
	private boolean m_materializationFinished = false;

	public TPWizard(InstallWizard installWizard)
	{
		m_installWizard = installWizard;
		setNeedsProgressMonitor(true);
	}

	public void enableWizardNextTime(boolean enable)
	{
		m_installWizard.getLocalProperties().put(LOCALPROP_ENABLE_TP_WIZARD, enable
				? VALUE_TRUE
				: VALUE_FALSE);
	}

	@Override
	public String getWindowTitle()
	{
		return TP_WINDOW_TITLE;
	}

	@Override
	public boolean performFinish()
	{
		WizardPage originalPage = (WizardPage)getContainer().getCurrentPage();

		originalPage.setErrorMessage(null);
		try
		{
			getContainer().showPage(m_operationPage);

			if(!m_installWizard.isStartedFromIDE())
			{
				((MaterializationProgressProvider)m_operationPage.getProgressProvider()).setEnabled(true);
				Job.getJobManager().setProgressProvider(m_operationPage.getProgressProvider());
			}

			IVersion eclipseVersion = null;
			
			if(isNewEclipse())
			{
				File eclipseFolder = new File(getEclipseFolder());
				
				if(eclipseFolder.exists())
				{
					File backupFolder = MaterializationUtils.getBackupFolder(eclipseFolder);
					eclipseFolder.renameTo(backupFolder);
				}
				
				MaterializationSpecBuilder sdkBuilder = getMspec(m_installWizard.getEclipseSDKURL());
				sdkBuilder.setInstallLocation(Path.fromOSString(getNewEclipseDestinationFolder()));
				
				enableCurrentPlatform(sdkBuilder);
				
				MaterializerRunnable mr = new MaterializerRunnable(sdkBuilder.createMaterializationSpec());
				getContainer().run(true, true, mr);
				
				eclipseVersion = getProvidedEclipseVersion();
			}
			else
			{
				eclipseVersion = getCurrentEclipseVersion();
			}

			if(isDistroToolsSelected())
			{
				IVersion osgi340Version = VersionFactory.createVersion(IVersionType.OSGI, OSGI_3_4_0);
				MaterializationSpecBuilder toolsBuilder;
				if(eclipseVersion.compareTo(osgi340Version) >= 0)
					toolsBuilder = getMspec(m_installWizard.getEclipseDistroTools34URL());
				else
					toolsBuilder = getMspec(m_installWizard.getEclipseDistroTools33URL());
				
				toolsBuilder.setInstallLocation(Path.fromOSString(getEclipseFolder()));
				
				MaterializerRunnable mr = new MaterializerRunnable(toolsBuilder.createMaterializationSpec());
				getContainer().run(true, true, mr);
			}
			
			m_materializationFinished = true;

			getContainer().showPage(m_donePage);
		}
		catch(InterruptedException e)
		{
			showOriginalPage(originalPage);
			originalPage.setErrorMessage("Operation cancelled");
		}
		catch(Exception e)
		{
			showOriginalPage(originalPage);

			final IStatus status = BuckminsterException.wrap(e).getStatus();
			CorePlugin.logWarningsAndErrors(status);
			HelpLinkErrorDialog.openError(null, m_installWizard.getWindowImage(), MaterializationConstants.ERROR_WINDOW_TITLE,
					"Materialization failed", MaterializationConstants.ERROR_HELP_TITLE, m_installWizard.getErrorURL(),
					ERROR_CODE_MATERIALIZATION_EXCEPTION, status);
		}
		finally
		{
			if(!m_installWizard.isStartedFromIDE())
			{
				Job.getJobManager().setProgressProvider(null);
				((MaterializationProgressProvider)m_operationPage.getProgressProvider()).setEnabled(false);
			}
		}

		return false;
	}

	private void enableCurrentPlatform(MaterializationSpecBuilder sdkBuilder)
	{
		String os = TargetPlatform.getOS();
		String arch = TargetPlatform.getOSArch();

		String platform = null;
		if(OS_WIN.equals(os))
		{
			platform = PLATFORM_WIN32;
			
			if(OSARCH_X86_64.equals(arch))
				platform = PLATFORM_WIN32_X86_64;
		}
		else if(OS_LINUX.equals(os))
		{
			platform = PLATFORM_LINUX_GTK;
			
			if(OSARCH_X86_64.equals(arch))
				platform = PLATFORM_LINUX_GTK_X86_64;
			else if(OSARCH_PPC.equals(arch))
				platform = PLATFORM_LINUX_GTK_PPC;
		}
		else if(OS_SOLARIS.equals(os))
			platform = PLATFORM_SOLARIS_GTK;
		else if(OS_HPUX.equals(os))
			platform = PLATFORM_HPUX_MOTIF_IA64_32;
		else if(OS_AIX.equals(os))
			platform = PLATFORM_AIX_MOTIF;
		else if(OS_MACOSX.equals(os))
			platform = PLATFORM_MACOSX_CARBON;

		sdkBuilder.getNodes().clear();
		MaterializationNodeBuilder includePlatformNode = sdkBuilder.addNodeBuilder();
		includePlatformNode.setNamePattern(Pattern.compile(".*" + platform + "$"));
		includePlatformNode.setExclude(false);
		includePlatformNode.setUnpack(true);
		MaterializationNodeBuilder ecludeAllNode = sdkBuilder.addNodeBuilder();
		ecludeAllNode.setNamePattern(Pattern.compile(".*"));
		ecludeAllNode.setExclude(true);
	}

	private void showOriginalPage(IWizardPage originalPage)
	{
		WizardPage originalPreviousPage = (WizardPage)originalPage.getPreviousPage();
		getContainer().showPage(originalPage);
		originalPage.setPreviousPage(originalPreviousPage);
	}

	@Override
	protected void addAdvancedPages()
	{
		addAdvancedPage(new TPIntroPage());

		m_newOrCurrentPage = new TPNewOrCurrentPage();
		addAdvancedPage(m_newOrCurrentPage);

		m_newRecommendedPage = new TPNewRecommendedPage();
		addAdvancedPage(m_newRecommendedPage);

		m_newLocationPage = new TPNewLocationPage();
		addAdvancedPage(m_newLocationPage);

		m_backupFolderPage = new TPBackupFolderPage();
		addAdvancedPage(m_backupFolderPage);

		m_toolSelectionPage = new TPToolSelectionPage();
		addAdvancedPage(m_toolSelectionPage);

		m_operationPage = new TPOperationPage();
		addAdvancedPage(m_operationPage);

		m_donePage = new TPDonePage();
		addAdvancedPage(m_donePage);
	}

	@Override
	protected Image getWindowImage()
	{
		return m_installWizard.getWindowImage();
	}

	@Override
	protected Image getWizardImage()
	{
		return m_installWizard.getWizardImage();
	}

	IVersion getCurrentEclipseVersion()
	{
		return m_newOrCurrentPage.getCurrentEclipseVersion();
	}

	IVersion getProvidedEclipseVersion()
	{
		return m_installWizard.getEclipseSDKVersion();
	}

	boolean isMaterializationFinished()
	{
		return m_materializationFinished;
	}

	boolean isNewEclipse()
	{
		return m_newEclipse;
	}

	void setNewEclipse(boolean newEclipse)
	{
		m_newEclipse = newEclipse;
	}

	boolean isBuckminsterInstalled()
	{
		return !isNewEclipse() && m_newOrCurrentPage.isBuckminsterInstalled();
	}
	
	boolean isSpacesInstalled()
	{
		return !isNewEclipse() && m_newOrCurrentPage.isSpacesInstalled();
	}
	
	boolean isRSSOwlInstalled()
	{
		return !isNewEclipse() && m_newOrCurrentPage.isRSSOwlInstalled();
	}
	
	String getNewEclipseDestinationFolder()
	{
		return m_newLocationPage.getDestinationFolder();
	}

	String getEclipseFolder()
	{
		if(isNewEclipse())
			return getNewEclipseDestinationFolder() == null
					? null
					: (getNewEclipseDestinationFolder() + File.separatorChar + ECLIPSE_FOLDER_NAME);

		return m_newOrCurrentPage.getEclipseFolder();
	}
	
	boolean isDistroToolsSelected()
	{
		return m_toolSelectionPage.isDistroToolsSelected();
	}

	IWizardPage getNewLocationPage()
	{
		return m_newLocationPage;
	}

	IWizardPage getToolsSelectionPage()
	{
		return m_toolSelectionPage;
	}

	IWizardPage getNewRecommendedPage()
	{
		return m_newRecommendedPage;
	}

	IWizardPage getBackupFolderPage()
	{
		return m_backupFolderPage;
	}
	
	private MaterializationSpecBuilder getMspec(String mspecURL) throws JNLPException
	{
		InputStream stream = null;
		MaterializationSpecBuilder mspec = new MaterializationSpecBuilder();

		try
		{
			URL url = new URL(mspecURL);
			stream = url.openStream();
			
			IParser<MaterializationSpec> parser = CorePlugin.getDefault().getParserFactory()
					.getMaterializationSpecParser(true);

			mspec.initFrom(parser.parse(ARTIFACT_TYPE_MSPEC, stream));
			
			BillOfMaterials bom = getBOM(mspec.getURL());
			
			MaterializationUtils.excludeCSsiteComponents(mspec, bom);
		
			File cachedBOMFile;
			try
			{
				cachedBOMFile = File.createTempFile("jnlp", ".bom");
				cachedBOMFile.deleteOnExit();
			}
			catch(IOException e)
			{
				throw new JNLPException("Cannot create a temp file", ERROR_CODE_FILE_IO_EXCEPTION, e);
			}

			MaterializationUtils.saveBOM(bom, cachedBOMFile);
			
			try
			{
				mspec.setURL(cachedBOMFile.toURI().toURL());
			}
			catch(MalformedURLException e)
			{
				throw new JNLPException("Cannot create URL link to a temp file", ERROR_CODE_MALFORMED_PROPERTY_EXCEPTION, e);
			}
		}
		catch(Exception e)
		{
			throw new JNLPException("Cannot read materialization specification",
					ERROR_CODE_MALFORMED_PROPERTY_EXCEPTION, e);
		}
		finally
		{
			IOUtils.close(stream);
		}
		
		return mspec;
	}
	
	private BillOfMaterials getBOM(URL bomURL)
	{
		InputStream stream = null;
		BillOfMaterials bom = null;

		try
		{
			stream = bomURL.openStream();

			IParser<BillOfMaterials> parser = CorePlugin.getDefault().getParserFactory().getBillOfMaterialsParser(
					true);

			bom = parser.parse(bomURL.toString(), stream);
		}
		catch(Exception e)
		{
			throw new JNLPException("Cannot read materialization specification",
					ERROR_CODE_MALFORMED_PROPERTY_EXCEPTION, e);
		}
		finally
		{
			IOUtils.close(stream);
		}

		return bom;
	}
}
