/*****************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/

package org.eclipse.buckminster.jnlp.p2.wizard.install;

import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.ARTIFACT_UNKNOWN_TEXT;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.ERROR_CODE_AUTHENTICATOR_EXCEPTION;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.ERROR_CODE_MALFORMED_PROPERTY_EXCEPTION;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.ERROR_CODE_MATERIALIZATION_EXCEPTION;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.ERROR_CODE_MISSING_PROPERTY_EXCEPTION;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.ERROR_CODE_NO_AUTHENTICATOR_EXCEPTION;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.ERROR_CODE_REMOTE_IO_EXCEPTION;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.ERROR_CODE_RUNTIME_EXCEPTION;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.ERROR_HELP_URL;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.ERROR_WINDOW_TITLE;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.LOCALPROP_ENABLE_TP_WIZARD;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.MATERIALIZATOR_PROPERTIES;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.MATERIALIZERS;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.META_AREA;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_ARTIFACT_DESCRIPTION;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_ARTIFACT_DOCUMENTATION;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_ARTIFACT_NAME;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_ARTIFACT_VERSION;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_BASE_PATH_URL;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_CSPEC_ID;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_CSPEC_NAME;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_CSPEC_TYPE;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_CSPEC_VERSION_STRING;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_DRAFT;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_ECLIPSE_DISTRO_TOOLS_33_UPDATE_SITE_URL;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_ECLIPSE_DISTRO_TOOLS_34_UPDATE_SITE_URL;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_ERROR_URL;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_FOLDER_PATH;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_HELP_URL;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_HOME_PAGE_URL;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_LEARN_MORE_CLOUDFEEDS_URL;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_LEARN_MORE_CLOUDREADER_URL;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_LEARN_MORE_URL;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_LOGIN_KEY;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_LOGIN_REQUIRED;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_PROFILE_TEXT;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_SERVICE_PROVIDER;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_SUPPORT_EMAIL;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_WINDOW_ICON;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_WINDOW_TITLE;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.PROP_WIZARD_ICON;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.VALUE_FALSE;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.VALUE_TRUE;
import static org.eclipse.buckminster.jnlp.p2.MaterializationConstants.WINDOW_TITLE_UNKNOWN;
import static org.eclipse.buckminster.jnlp.p2.installer.P2PropertyKeys.PROP_OPML;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.buckminster.core.CorePlugin;
import org.eclipse.buckminster.core.helpers.BMProperties;
import org.eclipse.buckminster.core.parser.IParser;
import org.eclipse.buckminster.jnlp.componentinfo.IComponentInfoProvider;
import org.eclipse.buckminster.jnlp.distroprovider.IRemoteDistroProvider;
import org.eclipse.buckminster.jnlp.distroprovider.cloudsmith.TransferUtils;
import org.eclipse.buckminster.jnlp.p2.HelpLinkErrorDialog;
import org.eclipse.buckminster.jnlp.p2.JNLPException;
import org.eclipse.buckminster.jnlp.p2.JNLPPlugin;
import org.eclipse.buckminster.jnlp.p2.MaterializationConstants;
import org.eclipse.buckminster.jnlp.p2.MaterializationUtils;
import org.eclipse.buckminster.jnlp.p2.MissingPropertyException;
import org.eclipse.buckminster.jnlp.p2.P2MaterializerRunnable;
import org.eclipse.buckminster.jnlp.p2.installer.InstallDescription;
import org.eclipse.buckminster.jnlp.p2.installer.InstallDescriptionParser;
import org.eclipse.buckminster.jnlp.p2.installer.P2PropertyKeys;
import org.eclipse.buckminster.jnlp.p2.progress.MaterializationProgressProvider;
import org.eclipse.buckminster.jnlp.p2.ui.general.wizard.AdvancedWizard;
import org.eclipse.buckminster.jnlp.p2.ui.general.wizard.AdvancedWizardDialog;
import org.eclipse.buckminster.jnlp.p2.wizard.ILoginHandler;
import org.eclipse.buckminster.opml.IOPML;
import org.eclipse.buckminster.opml.model.OPML;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.IOUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.internal.provisional.p2.core.Version;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author Thomas Hallgren
 * 
 */
@SuppressWarnings("restriction")
public class InstallWizard extends AdvancedWizard implements ILoginHandler
{
	static private final String DISTROPROVIDER_EXTPOINT = "org.eclipse.buckminster.jnlp.p2.distroProvider";

	static private final String COMPONENTINFO_EXTPOINT = "org.eclipse.buckminster.jnlp.p2.componentInfo";

	static private final String ATTRIBUTE_CLASS = "class";

	static private final String LEARNMORE_EXTPOINT = "org.eclipse.buckminster.jnlp.learnmore";

	static private final String ATTRIBUTE_STRING = "string";

	static private final String ATTRIBUTE_URL = "url";

	static private final String UNIVERSAL_ERROR_MESSAGE = "Materialization failures typically occur because a distro's publisher failed to keep it current,"
			+ " or because you experienced a network interruption while downloading.";

	private static MultiStatus createMultiStatusFromStatus(IStatus status)
	{
		return new MultiStatus(status.getPlugin(), status.getCode(), status.getMessage(), status.getException());
	}

	private static Map<String, String> getDefaultLocalProperties()
	{
		Map<String, String> defaultLocalProperties = new HashMap<String, String>();
		defaultLocalProperties.put(LOCALPROP_ENABLE_TP_WIZARD, VALUE_TRUE);
		return defaultLocalProperties;
	}

	private static IPath getLocalPropertiesLocation()
	{
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().append(META_AREA).append(
				MATERIALIZATOR_PROPERTIES);
	}

	private static BMProperties readLocalProperties()
	{
		BMProperties localProperties = null;
		InputStream in = null;
		try
		{
			in = new FileInputStream(getLocalPropertiesLocation().toFile());
			localProperties = new BMProperties(in);
		}
		catch(FileNotFoundException e)
		{
			localProperties = new BMProperties(getDefaultLocalProperties());
		}
		catch(IOException e)
		{
			localProperties = new BMProperties(getDefaultLocalProperties());
			e.printStackTrace();
		}
		finally
		{
			IOUtils.close(in);
		}

		return localProperties;
	}

	private Image m_brandingImage;

	private String m_brandingString;

	private String m_artifactName;

	private String m_artifactVersion;

	private String m_artifactDescription;

	private String m_artifactDocumentation;

	// private Image m_materializationImage; // unused

	private String m_windowTitle;

	private Image m_windowImage;

	private Image m_wizardImage;

	private String m_helpURL;

	private String m_moreInfoURL;

	private String m_errorURL = ERROR_HELP_URL;

	private String m_supportEmail;

	private boolean m_loginRequired;

	private String m_learnMoreURL;

	private String m_learnMoreCloudfeedsURL;

	private String m_learnMoreCloudreaderURL;

	private String m_basePathURL;

	private String m_homePageURL;

	private String m_serviceProvider;

	private String m_folderPath;

	private String m_loginKey;

	private String m_loginKeyUserName;

	private boolean m_draft;

	private Properties m_distroP2Properties;

	private IOPML m_opml;

	private Long m_cspecId;

	private String m_cspecName;

	private String m_cspecType;

	private Version m_cspecVersion;

	private String m_cspecVersionString;

	private String m_eclipseDistroTools34UpdateSiteURL;

	private String m_eclipseDistroTools33UpdateSiteURL;

	private boolean m_loginPageRequested = false;

	private LoginPage m_loginPage;

	private FolderRestrictionPage m_folderRestrictionPage;

	private SimpleDownloadPage m_downloadPage;

	private OperationPage m_operationPage;

	private DonePage m_donePage;

	private FeedsPage m_infoPage;

	private final Map<String, String> m_properties;

	private final BMProperties m_localProperties;

	private final boolean m_startedFromIDE;

	private IRemoteDistroProvider m_distroProvider;

	private IComponentInfoProvider m_infoProvider;

	private String m_infoPageURL;

	private String m_authenticatorUserName;

	private String m_authenticatorPassword;

	private IPath m_installLocation;

	private String m_profileName;

	private final List<LearnMoreItem> m_learnMores;

	private boolean m_materializationFinished = false;

	private boolean m_problemInProperties = false;

	public InstallWizard(Map<String, String> properties)
	{
		this(properties, false);
	}

	public InstallWizard(Map<String, String> properties, boolean startedFromIDE)
	{
		setNeedsProgressMonitor(true);

		m_properties = properties;

		m_startedFromIDE = startedFromIDE;

		readProperties(properties);

		m_localProperties = readLocalProperties();

		m_distroProvider = createDistroProvider();

		m_infoProvider = createComponentInfoProvider();

		m_learnMores = createLearnMores();
	}

	@Override
	public void createPageControls(Composite pageContainer)
	{
		try
		{
			super.createPageControls(pageContainer);
		}
		catch(final JNLPException e)
		{
			m_problemInProperties = true;

			final IStatus status = e.getCause() == null
					? BuckminsterException.wrap(e).getStatus()
					: BuckminsterException.wrap(e.getCause()).getStatus();

			CorePlugin.logWarningsAndErrors(status);
			Display.getDefault().syncExec(new Runnable()
			{
				public void run()
				{
					HelpLinkErrorDialog.openError(null, m_windowImage, MaterializationConstants.ERROR_WINDOW_TITLE,
							e.getMessage(), status, e.getErrorCode(), true, m_supportEmail, "Materialization Error");
				}
			});
		}
	}

	@Override
	public void dispose()
	{
		if(m_brandingImage != null)
		{
			JNLPPlugin.unregister(JNLPPlugin.OBJECT_BRANDING_IMAGE);
			m_brandingImage.dispose();
			m_brandingImage = null;
		}

		if(m_windowImage != null)
		{
			JNLPPlugin.unregister(JNLPPlugin.OBJECT_WINDOW_IMAGE);
			m_windowImage.dispose();
			m_windowImage = null;
		}

		if(m_wizardImage != null)
		{
			JNLPPlugin.unregister(JNLPPlugin.OBJECT_WIZARD_IMAGE);
			m_wizardImage.dispose();
			m_wizardImage = null;
		}
	}

	public void enableWizardNextTime(boolean enable)
	{
		getLocalProperties().put(LOCALPROP_ENABLE_TP_WIZARD, enable
				? VALUE_TRUE
				: VALUE_FALSE);
	}

	public String getAuthenticatorCurrentUserName()
	{
		IRemoteDistroProvider auth = getDistroProvider();
		return auth == null
				? ""
				: auth.getCurrenlyLoggedUserName();
	}

	public String getAuthenticatorLoginKey()
	{
		return m_loginKey;
	}

	public String getAuthenticatorLoginKeyUserName()
	{
		return m_loginKeyUserName;
	}

	public String getAuthenticatorPassword()
	{
		return m_authenticatorPassword;
	}

	public String getAuthenticatorUserName()
	{
		return m_authenticatorUserName;
	}

	public String getComponentInfoPageURL()
	{
		return m_infoPageURL;
	}

	public IComponentInfoProvider getComponentInfoProvider()
	{
		return m_infoProvider;
	}

	public String getCSpecName()
	{
		return m_cspecName;
	}

	public String getCSpecType()
	{
		return m_cspecType;
	}

	public Version getCSpecVersion()
	{
		if(m_cspecVersion == null)
			m_cspecVersion = new Version(m_cspecVersionString);

		return m_cspecVersion;
	}

	public String getCSpecVersionString()
	{
		return m_cspecVersionString;
	}

	public IRemoteDistroProvider getDistroProvider()
	{
		return m_distroProvider;
	}

	public String getEclipseDistroTools33UpdateSiteURL()
	{
		return m_eclipseDistroTools33UpdateSiteURL;
	}

	public String getEclipseDistroTools34UpdateSiteURL()
	{
		return m_eclipseDistroTools34UpdateSiteURL;
	}

	public String getErrorURL()
	{
		return m_errorURL;
	}

	public String getFolderPath()
	{
		return m_folderPath;
	}

	@Override
	public String getHelpURL()
	{
		return m_helpURL;
	}

	public BMProperties getLocalProperties()
	{
		return m_localProperties;
	}

	@Override
	public String getMoreInfoURL()
	{
		return m_moreInfoURL;
	}

	public String getServiceProvider()
	{
		return m_serviceProvider;
	}

	@Override
	public Image getWindowImage()
	{
		return m_windowImage;
	}

	@Override
	public String getWindowTitle()
	{
		return m_windowTitle;
	}

	@Override
	public Image getWizardImage()
	{
		return m_wizardImage;
	}

	public boolean isStartedFromIDE()
	{
		return m_startedFromIDE;
	}

	@Override
	public boolean performCancel()
	{
		try
		{
			// disable progress provider
			Job.getJobManager().setProgressProvider(null);
			OperationPage operationPage = (OperationPage)getPage(MaterializationConstants.STEP_OPERATION);
			if(operationPage != null)
				((MaterializationProgressProvider)operationPage.getProgressProvider()).setEnabled(false);

			if(m_distroProvider != null)
			{
				try
				{
					m_distroProvider.releaseConnection();
				}
				catch(Throwable e)
				{
					// do nothing - session might be invalidated
				}
			}
		}
		catch(Throwable e)
		{
			// it should always finish
			e.printStackTrace();
		}

		/*
		 * Starts Eclipse installation wizard - Eclipse SDK + Buckminster + Spaces + RSS Owl
		 * if(isMaterializationFinished()) { String materializerID =
		 * getMaterializationSpecBuilder().getMaterializerID();
		 * 
		 * if((materializerID == IMaterializer.TARGET_PLATFORM || materializerID == IMaterializer.WORKSPACE) &&
		 * VALUE_TRUE.equals(m_localProperties.get(LOCALPROP_ENABLE_TP_WIZARD)))
		 * MaterializationUtils.startTPWizard(this, getShell());
		 * 
		 * saveLocalProperties(); }
		 */

		return true;
	}

	@Override
	public boolean performFinish()
	{
		WizardPage originalPage = (WizardPage)getContainer().getCurrentPage();

		originalPage.setErrorMessage(null);
		try
		{
			getContainer().showPage(m_operationPage);

			if(!m_startedFromIDE)
			{
				((MaterializationProgressProvider)m_operationPage.getProgressProvider()).setEnabled(true);
				Job.getJobManager().setProgressProvider(m_operationPage.getProgressProvider());
			}

			getContainer().run(true, true, new P2MaterializerRunnable(createInstallDescription()));

			getContainer().showPage(m_operationPage);

			m_materializationFinished = true;

			if(getComponentInfoProvider() != null)
				try
				{
					m_infoPageURL = getComponentInfoProvider().prepareHTML(getProperties(), getOPML(),
							MaterializationUtils.getDefaultDestination(null));
				}
				catch(Exception e)
				{
					m_infoPageURL = null;
					final IStatus status = BuckminsterException.wrap(e).getStatus();
					CorePlugin.logWarningsAndErrors(status);
					HelpLinkErrorDialog.openError(null, m_windowImage, MaterializationConstants.ERROR_WINDOW_TITLE,
							"Cannot create an HTML page with additional distro infomation", status,
							ERROR_CODE_RUNTIME_EXCEPTION, true, m_supportEmail, "Materialization Error");
				}

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

			// final IStatus status = BuckminsterException.wrap(e).getStatus();
			final IStatus status = BuckminsterException.fromMessage(BuckminsterException.wrap(e),
					UNIVERSAL_ERROR_MESSAGE).getStatus();

			CorePlugin.logWarningsAndErrors(status);
			HelpLinkErrorDialog.openError(null, m_windowImage, MaterializationConstants.ERROR_WINDOW_TITLE,
					"This distro failed to materialize", status, ERROR_CODE_MATERIALIZATION_EXCEPTION, true,
					m_supportEmail, "Materialization Error");
		}
		finally
		{
			if(!m_startedFromIDE)
			{
				Job.getJobManager().setProgressProvider(null);
				((MaterializationProgressProvider)m_operationPage.getProgressProvider()).setEnabled(false);
			}
		}

		return false;
	}

	public void removeAuthenticatorLoginKey()
	{
		m_loginKey = null;
		m_loginKeyUserName = null;
	}

	public void setAuthenticatorPassword(String password)
	{
		m_authenticatorPassword = password;
	}

	public void setAuthenticatorUserName(String userName)
	{
		m_authenticatorUserName = userName;
	}

	public void setDistroProvider(IRemoteDistroProvider distroProvider)
	{
		m_distroProvider = distroProvider;
	}

	@Override
	protected void addAdvancedPages()
	{
		addAdvancedPage(new StartPage());

		if(!m_problemInProperties)
		{
			m_loginPage = new LoginPage(m_distroProvider == null
					? "Virtual Distro Provider"
					: getServiceProvider());
			addAdvancedPage(m_loginPage);

			m_downloadPage = new SimpleDownloadPage();
			addAdvancedPage(m_downloadPage);

			m_folderRestrictionPage = new FolderRestrictionPage();
			addAdvancedPage(m_folderRestrictionPage);

			m_operationPage = new OperationPage();
			addAdvancedPage(m_operationPage);

			m_donePage = new DonePage();
			addAdvancedPage(m_donePage);

			m_infoPage = new FeedsPage();
			addAdvancedPage(m_infoPage);
		}
	}

	protected void saveLocalProperties()
	{
		OutputStream out = null;
		try
		{
			File propFile = getLocalPropertiesLocation().toFile();
			if(!propFile.exists())
				propFile.createNewFile();
			out = new FileOutputStream(propFile);
			m_localProperties.store(out, null);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			IOUtils.close(out);
		}
	}

	int checkFolderReadAccess() throws Exception
	{
		if(m_loginRequired && (m_distroProvider == null || m_folderPath == null))
			return IRemoteDistroProvider.FOLDER_ACCESS_FORBIDDEN;

		// if authenticator is null - get smacked later (can only end up here without an
		// authenticator if loginRequired is false anyway).
		return m_distroProvider.checkFolderReadAccess(m_folderPath);
	}

	String getArtifactDescription()
	{
		return m_artifactDescription;
	}

	String getArtifactDocumentation()
	{
		return m_artifactDocumentation;
	}

	String getArtifactName()
	{
		return m_artifactName;
	}

	String getArtifactVersion()
	{
		return m_artifactVersion;
	}

	Image getBrandingImage()
	{
		return m_brandingImage;
	}

	String getBrandingString()
	{
		return m_brandingString;
	}

	Properties getDistroP2Properties()
	{
		return m_distroP2Properties;
	}

	IWizardPage getDownloadPage()
	{
		return m_downloadPage;
	}

	IWizardPage getFolderRestrictionPage()
	{
		return m_folderRestrictionPage;
	}

	IWizardPage getInfoPage()
	{
		return m_infoPage;
	}

	IPath getInstallLocation()
	{
		return m_installLocation;
	}

	String getLearnMoreCloudfeedsURL()
	{
		return m_learnMoreCloudfeedsURL;
	}

	String getLearnMoreCloudreaderURL()
	{
		return m_learnMoreCloudreaderURL;
	}

	List<LearnMoreItem> getLearnMores()
	{
		return m_learnMores;
	}

	String getLearnMoreURL()
	{
		return m_learnMoreURL;
	}

	IWizardPage getLoginPage()
	{
		return m_loginPage;
	}

	String[] getMaterializers()
	{
		return MATERIALIZERS;
	}

	IOPML getOPML()
	{
		return m_opml;
	}

	String getProfileName()
	{
		return m_profileName;
	}

	Map<String, String> getProperties()
	{
		return m_properties;
	}

	String getServiceProviderHomePageURL()
	{
		return m_homePageURL;
	}

	boolean isDraft()
	{
		return m_draft;
	}

	boolean isFolderRestrictionPageNeeded()
	{
		try
		{
			int result = checkFolderReadAccess();

			if(result == IRemoteDistroProvider.FOLDER_ACCESS_FORBIDDEN
					|| result == IRemoteDistroProvider.FOLDER_ACCESS_INVITATION_EXISTS
					|| result == IRemoteDistroProvider.FOLDER_ACCESS_INVITATION_EXISTS_EMAIL_NOT_VERIFIED)
			{
				m_folderRestrictionPage.setStatus(result);

				return true;
			}
		}
		catch(Exception e1)
		{
			// no information - try to get the artifact
		}

		return false;
	}

	boolean isLoggedIn()
	{
		boolean isLoggedIn = false;

		try
		{
			isLoggedIn = m_distroProvider.isLoggedIn();
		}
		catch(Exception e1)
		{
			// nothing isLoggedIn == false
		}

		return isLoggedIn;
	}

	boolean isLoginPageRequested()
	{
		return m_loginPageRequested;
	}

	boolean isLoginRequired()
	{
		return m_loginRequired;
	}

	// // Seems to be never used
	// Image getMaterializationImage()
	// {
	// return m_materializationImage;
	// }
	boolean isMaterializationFinished()
	{
		return m_materializationFinished;
	}

	boolean isMaterializerInitialized()
	{
		return m_distroP2Properties != null;
	}

	boolean isProblemInProperties()
	{
		return m_problemInProperties;
	}

	void resetMaterializerInitialization()
	{
		m_distroP2Properties = null;
	}

	void retrieveStackInfo() throws InterruptedException
	{
		if(m_distroP2Properties == null)
		{
			try
			{
				((AdvancedWizardDialog)getContainer()).disableNavigation();

				getContainer().run(true, true, new IRunnableWithProgress()
				{

					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
					{
						try
						{
							m_distroP2Properties = m_distroProvider.getDistroP2Properties(m_draft, m_cspecId, monitor);
							m_opml = null;

							String opmlString = m_distroP2Properties.getProperty(PROP_OPML);

							if(opmlString != null)
							{
								IParser<OPML> opmlParser = CorePlugin.getDefault().getParserFactory().getOPMLParser(
										true);
								m_opml = opmlParser.parse("byte image", new ByteArrayInputStream(
										TransferUtils.decompress(opmlString.getBytes("UTF-8"))));
							}
						}
						catch(InterruptedException e)
						{
							throw e;
						}
						catch(Exception e)
						{
							throw new InvocationTargetException(e);
						}

						m_installLocation = Path.fromOSString(MaterializationUtils.getDefaultDestination(m_artifactName));
					}
				});
			}
			catch(InterruptedException e)
			{
				throw e;
			}
			catch(Exception e)
			{
				if(e instanceof JNLPException)
					throw (JNLPException)e;

				Throwable originalException = e;

				if(e instanceof InvocationTargetException && e.getCause() != null)
					originalException = e.getCause();

				throw new JNLPException("Cannot read distro specification", ERROR_CODE_REMOTE_IO_EXCEPTION,
						originalException);
			}
			finally
			{
				getContainer().updateButtons();
			}
		}

		if(m_distroP2Properties.size() == 0)
			throw new JNLPException("Materialization properties were not retrieved",
					ERROR_CODE_MISSING_PROPERTY_EXCEPTION);
	}

	void setDistroP2Properties(Properties properties)
	{
		m_distroP2Properties = properties;
	}

	void setInstallLocation(IPath location)
	{
		m_installLocation = location;
	}

	void setLoginPageRequested(boolean loginPageRequested)
	{
		m_loginPageRequested = loginPageRequested;
	}

	void setOPML(OPML opml)
	{
		m_opml = opml;
	}

	void setProfileName(String profileName)
	{
		m_profileName = profileName;
	}

	private IComponentInfoProvider createComponentInfoProvider()
	{
		IExtensionRegistry er = Platform.getExtensionRegistry();
		IConfigurationElement[] elems = er.getConfigurationElementsFor(COMPONENTINFO_EXTPOINT);
		IComponentInfoProvider infoProvider = null;

		try
		{
			if(elems.length != 1)
				return null;

			try
			{
				infoProvider = (IComponentInfoProvider)elems[0].createExecutableExtension(ATTRIBUTE_CLASS);
			}
			catch(Throwable e)
			{
				throw new JNLPException("Cannot create component info provider", ERROR_CODE_RUNTIME_EXCEPTION, e);
			}
		}
		catch(JNLPException e)
		{
			m_problemInProperties = true;

			IStatus status = BuckminsterException.wrap(e.getCause() != null
					? e.getCause()
					: e).getStatus();
			CorePlugin.logWarningsAndErrors(status);
			HelpLinkErrorDialog.openError(null, m_windowImage, ERROR_WINDOW_TITLE, e.getMessage(), status,
					e.getErrorCode(), true, m_supportEmail, "Materialization Error");
		}

		return infoProvider;
	}

	private IRemoteDistroProvider createDistroProvider()
	{
		IExtensionRegistry er = Platform.getExtensionRegistry();
		IConfigurationElement[] elems = er.getConfigurationElementsFor(DISTROPROVIDER_EXTPOINT);
		IRemoteDistroProvider distroProvider = null;

		try
		{
			if(elems.length != 1)
			{
				throw new JNLPException("Distro provider is not available", ERROR_CODE_NO_AUTHENTICATOR_EXCEPTION);
			}

			try
			{
				distroProvider = (IRemoteDistroProvider)elems[0].createExecutableExtension(ATTRIBUTE_CLASS);
				distroProvider.initialize(m_basePathURL);

				if(m_loginKey != null)
				{
					int result = distroProvider.login(m_loginKey);

					if(result == IRemoteDistroProvider.LOGIN_UNKNOW_KEY)
						m_loginKey = null;

					if(result == IRemoteDistroProvider.LOGIN_OK)
						m_loginKeyUserName = distroProvider.getCurrenlyLoggedUserName();
					else
						m_loginKeyUserName = null;
				}
			}
			catch(Throwable e)
			{
				throw new JNLPException("Cannot connect to the remote server", ERROR_CODE_AUTHENTICATOR_EXCEPTION, e);
			}
		}
		catch(JNLPException e)
		{
			m_problemInProperties = true;

			IStatus status = BuckminsterException.wrap(e.getCause() != null
					? e.getCause()
					: e).getStatus();
			CorePlugin.logWarningsAndErrors(status);
			HelpLinkErrorDialog.openError(null, m_windowImage, ERROR_WINDOW_TITLE, e.getMessage(), status,
					e.getErrorCode(), true, m_supportEmail, "Materialization Error");
		}

		return distroProvider;
	}

	private InstallDescription createInstallDescription()
	{
		InstallDescription installDescription = InstallDescriptionParser.createDescription(getDistroP2Properties());

		installDescription.setInstallLocation(getInstallLocation());
		installDescription.setAgentLocation(getInstallLocation().append("p2")); //$NON-NLS-1$
		installDescription.setBundleLocation(getInstallLocation());
		installDescription.getProfileProperties().put(P2PropertyKeys.PROP_PROFILE_NAME, getProfileName());

		return installDescription;
	}

	private List<LearnMoreItem> createLearnMores()
	{
		List<LearnMoreItem> learnMores = new ArrayList<LearnMoreItem>();

		// Learn more items from properties
		if(m_learnMoreURL != null)
		{
			learnMores.add(new LearnMoreItem("Create your own virtual distribution", m_learnMoreURL));
		}

		if(m_homePageURL != null)
		{
			learnMores.add(new LearnMoreItem("Search your components at " + m_serviceProvider, m_homePageURL));
		}

		// Learn more items from extension
		IExtensionRegistry er = Platform.getExtensionRegistry();
		IConfigurationElement[] elems = er.getConfigurationElementsFor(LEARNMORE_EXTPOINT);

		for(IConfigurationElement elem : elems)
		{
			learnMores.add(new LearnMoreItem(elem.getAttribute(ATTRIBUTE_STRING), elem.getAttribute(ATTRIBUTE_URL)));
		}

		return learnMores;
	}

	/**
	 * Wizard page doesn't display message text (the second line in title area) if the wizard image is too small This
	 * function creates a new image that is 64 pixels high - adds to the original image transparent stripe
	 * 
	 * @param origImage
	 *            original image
	 * @return new image
	 */
	private Image getNormalizedWizardImage(Image origImage)
	{
		final int WIZARD_IMAGE_HEIGHT = 64;

		ImageData origImageData = origImage.getImageData();

		if(origImageData.height >= WIZARD_IMAGE_HEIGHT)
		{
			return origImage;
		}

		ImageData newImageData = new ImageData(origImageData.width, WIZARD_IMAGE_HEIGHT, origImageData.depth,
				origImageData.palette);

		newImageData.alpha = origImageData.alpha;

		ImageData transparencyMask = origImageData.getTransparencyMask();
		boolean testTransparency = origImageData.getTransparencyType() == SWT.TRANSPARENCY_MASK
				|| origImageData.getTransparencyType() == SWT.TRANSPARENCY_PIXEL;

		for(int y = 0; y < origImageData.height; y++)
		{
			for(int x = 0; x < origImageData.width; x++)
			{
				if(testTransparency && transparencyMask.getPixel(x, y) == 0)
				{
					newImageData.setAlpha(x, y, 0);
				}
				else
				{
					newImageData.setPixel(x, y, origImageData.getPixel(x, y));
					newImageData.setAlpha(x, y, origImageData.getAlpha(x, y));
				}
			}
		}

		for(int y = origImageData.height; y < WIZARD_IMAGE_HEIGHT; y++)
		{
			for(int x = 0; x < origImageData.width; x++)
			{
				newImageData.setAlpha(x, y, 0);
			}
		}

		return new Image(Display.getDefault(), newImageData);
	}

	private void readProperties(Map<String, String> properties)
	{
		class ErrorEntry
		{
			private IStatus m_status;

			private String m_errorCode;

			public ErrorEntry(IStatus status, String errorCode)
			{
				super();
				m_status = status;
				m_errorCode = errorCode;
			}

			public String getErrorCode()
			{
				return m_errorCode;
			}

			public IStatus getStatus()
			{
				return m_status;
			}
		}

		List<ErrorEntry> errorList = new ArrayList<ErrorEntry>();

		String tmp = properties.get(PROP_BASE_PATH_URL);

		if(tmp == null)
		{
			Throwable e = new MissingPropertyException(PROP_BASE_PATH_URL);
			errorList.add(new ErrorEntry(BuckminsterException.wrap(e).getStatus(),
					ERROR_CODE_MISSING_PROPERTY_EXCEPTION));
		}

		m_basePathURL = tmp;

		tmp = properties.get(PROP_ARTIFACT_NAME);
		if(tmp == null)
		{
			Throwable e = new MissingPropertyException(PROP_ARTIFACT_NAME);
			errorList.add(new ErrorEntry(BuckminsterException.wrap(e).getStatus(),
					ERROR_CODE_MISSING_PROPERTY_EXCEPTION));
			tmp = ARTIFACT_UNKNOWN_TEXT;
		}

		m_artifactName = tmp;

		m_artifactVersion = properties.get(PROP_ARTIFACT_VERSION);
		m_artifactDescription = properties.get(PROP_ARTIFACT_DESCRIPTION);

		if(m_artifactDescription != null)
			try
			{
				m_artifactDescription = new String(Base64.decodeBase64(m_artifactDescription.getBytes()), "UTF-8");
			}
			catch(UnsupportedEncodingException e1)
			{
				m_artifactDescription = null;
			}

		m_artifactDocumentation = properties.get(PROP_ARTIFACT_DOCUMENTATION);

		if(m_artifactDocumentation != null)
			try
			{
				m_artifactDocumentation = new String(Base64.decodeBase64(m_artifactDocumentation.getBytes()), "UTF-8");
			}
			catch(UnsupportedEncodingException e1)
			{
				m_artifactDocumentation = null;
			}

		// Branding image is not wanted
		m_brandingImage = null;

		tmp = properties.get(PROP_PROFILE_TEXT);
		if(tmp == null)
		{
			Throwable e = new MissingPropertyException(PROP_PROFILE_TEXT);
			errorList.add(new ErrorEntry(BuckminsterException.wrap(e).getStatus(),
					ERROR_CODE_MISSING_PROPERTY_EXCEPTION));
			tmp = ARTIFACT_UNKNOWN_TEXT;
		}
		else
		{
			try
			{
				m_brandingString = new String(Base64.decodeBase64(tmp.getBytes()), "UTF-8");
			}
			catch(UnsupportedEncodingException e1)
			{
				m_artifactDocumentation = ARTIFACT_UNKNOWN_TEXT;
			}
		}

		tmp = properties.get(PROP_WINDOW_TITLE);
		if(tmp == null)
		{
			Throwable e = new MissingPropertyException(PROP_WINDOW_TITLE);
			errorList.add(new ErrorEntry(BuckminsterException.wrap(e).getStatus(),
					ERROR_CODE_MISSING_PROPERTY_EXCEPTION));
			tmp = WINDOW_TITLE_UNKNOWN;
		}
		m_windowTitle = tmp;
		JNLPPlugin.register(JNLPPlugin.OBJECT_WINDOW_TITLE, m_windowTitle);

		tmp = properties.get(PROP_WINDOW_ICON);
		m_windowImage = null;
		if(tmp != null)
		{
			try
			{
				m_windowImage = ImageDescriptor.createFromURL(new URL(tmp)).createImage();
				JNLPPlugin.register(JNLPPlugin.OBJECT_WINDOW_IMAGE, m_windowImage);
			}
			catch(MalformedURLException e)
			{
				errorList.add(new ErrorEntry(BuckminsterException.wrap(e).getStatus(),
						ERROR_CODE_MALFORMED_PROPERTY_EXCEPTION));
			}
		}

		tmp = properties.get(PROP_WIZARD_ICON);
		m_wizardImage = null;
		if(tmp != null)
		{
			try
			{
				m_wizardImage = getNormalizedWizardImage(ImageDescriptor.createFromURL(new URL(tmp)).createImage());
				JNLPPlugin.register(JNLPPlugin.OBJECT_WIZARD_IMAGE, m_wizardImage);
			}
			catch(MalformedURLException e)
			{
				errorList.add(new ErrorEntry(BuckminsterException.wrap(e).getStatus(),
						ERROR_CODE_MALFORMED_PROPERTY_EXCEPTION));
			}
		}

		// // Loads an image that is never used
		// tmp = properties.get(PROP_MATERIALIZATION_IMAGE);
		// m_materializationImage = null;
		// if(tmp != null)
		// {
		// try
		// {
		// m_materializationImage = ImageDescriptor.createFromURL(new URL(tmp)).createImage();
		// }
		// catch(MalformedURLException e)
		// {
		// errorList.add(new ErrorEntry(BuckminsterException.wrap(e).getStatus(),
		// ERROR_CODE_MALFORMED_PROPERTY_EXCEPTION));
		// }
		// }

		m_helpURL = properties.get(PROP_HELP_URL);
		// TODO use different helpURL and moreInfoURL, now there is just helpURL
		m_moreInfoURL = m_helpURL;
		// m_moreInfoURL = properties.get(PROP_MORE_INFO_URL);

		m_errorURL = properties.get(PROP_ERROR_URL);
		if(m_errorURL == null)
		{
			m_errorURL = ERROR_HELP_URL;
		}

		m_supportEmail = properties.get(PROP_SUPPORT_EMAIL);

		m_loginRequired = false;
		tmp = properties.get(PROP_LOGIN_REQUIRED);
		if("true".equalsIgnoreCase(tmp))
		{
			m_loginRequired = true;
		}

		m_learnMoreURL = properties.get(PROP_LEARN_MORE_URL);

		m_homePageURL = properties.get(PROP_HOME_PAGE_URL);

		m_serviceProvider = properties.get(PROP_SERVICE_PROVIDER);

		m_folderPath = properties.get(PROP_FOLDER_PATH);

		tmp = properties.get(PROP_DRAFT);
		if(tmp == null)
		{
			Throwable e = new MissingPropertyException(PROP_DRAFT);
			errorList.add(new ErrorEntry(BuckminsterException.wrap(e).getStatus(),
					ERROR_CODE_MISSING_PROPERTY_EXCEPTION));
		}
		m_draft = VALUE_TRUE.equalsIgnoreCase(tmp);

		tmp = properties.get(PROP_CSPEC_ID);
		if(tmp == null)
		{
			Throwable e = new MissingPropertyException(PROP_CSPEC_ID);
			errorList.add(new ErrorEntry(BuckminsterException.wrap(e).getStatus(),
					ERROR_CODE_MISSING_PROPERTY_EXCEPTION));
		}

		try
		{
			m_cspecId = new Long(tmp);
		}
		catch(NumberFormatException e)
		{
			errorList.add(new ErrorEntry(BuckminsterException.wrap(e).getStatus(),
					ERROR_CODE_MISSING_PROPERTY_EXCEPTION));
		}

		m_cspecName = properties.get(PROP_CSPEC_NAME);
		if(m_cspecName == null)
		{
			Throwable e = new MissingPropertyException(PROP_CSPEC_NAME);
			errorList.add(new ErrorEntry(BuckminsterException.wrap(e).getStatus(),
					ERROR_CODE_MISSING_PROPERTY_EXCEPTION));
		}

		m_cspecType = properties.get(PROP_CSPEC_TYPE);
		if(m_cspecType == null)
		{
			Throwable e = new MissingPropertyException(PROP_CSPEC_TYPE);
			errorList.add(new ErrorEntry(BuckminsterException.wrap(e).getStatus(),
					ERROR_CODE_MISSING_PROPERTY_EXCEPTION));
		}

		m_cspecVersionString = properties.get(PROP_CSPEC_VERSION_STRING);
		if(m_cspecVersionString == null)
		{
			Throwable e = new MissingPropertyException(PROP_CSPEC_VERSION_STRING);
			errorList.add(new ErrorEntry(BuckminsterException.wrap(e).getStatus(),
					ERROR_CODE_MISSING_PROPERTY_EXCEPTION));
		}

		m_eclipseDistroTools34UpdateSiteURL = properties.get(PROP_ECLIPSE_DISTRO_TOOLS_34_UPDATE_SITE_URL);
		m_eclipseDistroTools33UpdateSiteURL = properties.get(PROP_ECLIPSE_DISTRO_TOOLS_33_UPDATE_SITE_URL);

		m_learnMoreCloudfeedsURL = properties.get(PROP_LEARN_MORE_CLOUDFEEDS_URL);
		m_learnMoreCloudreaderURL = properties.get(PROP_LEARN_MORE_CLOUDREADER_URL);

		if(errorList.size() > 0)
		{
			m_problemInProperties = true;

			final IStatus topStatus;

			if(errorList.size() == 1)
			{
				topStatus = errorList.get(0).getStatus();
			}
			else
			{
				topStatus = createMultiStatusFromStatus(errorList.get(0).getStatus());
				for(int i = 1; i < errorList.size(); i++)
				{
					((MultiStatus)topStatus).add(errorList.get(i).getStatus());
				}
			}

			final String topErrorCode = errorList.get(0).getErrorCode();

			CorePlugin.logWarningsAndErrors(topStatus);
			Display.getDefault().syncExec(new Runnable()
			{
				public void run()
				{
					HelpLinkErrorDialog.openError(null, m_windowImage, ERROR_WINDOW_TITLE,
							"Error while reading materialization information", topStatus, topErrorCode, true,
							m_supportEmail, "Materialization Error");
				}
			});
		}

		m_loginKey = properties.get(PROP_LOGIN_KEY);
	}

	private void showOriginalPage(IWizardPage originalPage)
	{
		WizardPage originalPreviousPage = (WizardPage)originalPage.getPreviousPage();
		getContainer().showPage(originalPage);
		originalPage.setPreviousPage(originalPreviousPage);
	}
}

class LearnMoreItem
{
	private String m_string;

	private String m_url;

	public LearnMoreItem(String string, String url)
	{
		m_string = string;
		m_url = url;
	}

	public String getString()
	{
		return m_string;
	}

	public String getUrl()
	{
		return m_url;
	}
}
