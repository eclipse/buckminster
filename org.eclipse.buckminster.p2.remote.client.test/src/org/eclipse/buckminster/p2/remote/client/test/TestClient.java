package org.eclipse.buckminster.p2.remote.client.test;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.buckminster.p2.remote.FacadeAlreadyExistsException;
import org.eclipse.buckminster.p2.remote.IRepositoryFacade;
import org.eclipse.buckminster.p2.remote.IRepositoryServer;
import org.eclipse.buckminster.p2.remote.NoSuchFacadeException;
import org.eclipse.buckminster.p2.remote.client.RemoteInputStream;
import org.eclipse.buckminster.p2.remote.client.RemoteRepositoryFactory;
import org.eclipse.buckminster.runtime.IOUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.internal.p2.console.ProvisioningHelper;
import org.eclipse.equinox.internal.provisional.p2.metadata.query.InstallableUnitQuery;
import org.eclipse.equinox.internal.provisional.p2.metadata.repository.IMetadataRepository;
import org.eclipse.equinox.internal.provisional.p2.query.Collector;
import org.eclipse.equinox.internal.provisional.p2.query.Query;
import org.osgi.framework.Bundle;

public class TestClient extends TestCase
{
	public static final String SERVER_URL = "http://localhost:8080/json-rpc";

	public static Test suite() throws Exception
	{
		Bundle[] bundles = Platform.getBundles("org.eclipse.equinox.p2.exemplarysetup", null);
		bundles[0].start();

		byte[] a1 = new byte[] { (byte)0x8b };
		byte[] a2 = new String(a1, "US-ASCII").getBytes("US-ASCII");
		if(a1.length == a2.length)
			System.out.print("Lenghts are equal");

		IRepositoryServer server = RemoteRepositoryFactory.connect(new URI(SERVER_URL));
		server.hardReset();

		TestSuite suite = new TestSuite("Tests for org.eclipse.buckminster.p2.remote.client");
		suite.addTest(new TestClient("testCreateMetadata", server));
		suite.addTest(new TestClient("testGetMetadata", server));
		suite.addTest(new TestClient("testRegisterMirror", server));
		suite.addTest(new TestClient("testRegisterMirror2", server));
		suite.addTest(new TestClient("testDeleteMetadata", server));
		return suite;
	}

	private final IRepositoryServer m_server;

	public TestClient(String testName, IRepositoryServer server)
	{
		super(testName);
		m_server = server;
	}

	public void testCreateMetadata() throws Exception
	{
		IRepositoryFacade metatest1;
		m_server.deleteMetadataRepositoryFacade("metatest-1");
		try
		{
			metatest1 = m_server.getMetadataRepositoryFacade("metatest-1");
			assertTrue("Repository should not be possible to obtain since it does not exist", true);
		}
		catch(NoSuchFacadeException e)
		{
		}
		metatest1 = m_server.createMetadataRepositoryFacade("metatest-1");
		InputStream input = new GZIPInputStream(new RemoteInputStream(metatest1.getRepositoryData()));
		IOUtils.copy(input, System.out, null);
		input.close();
	}

	public void testDeleteMetadata() throws Exception
	{
		assertTrue("Expected facade does not exist", m_server.deleteMetadataRepositoryFacade("metatest-1"));
	}

	public void testGetMetadata() throws Exception
	{
		IRepositoryFacade metatest1;
		try
		{
			metatest1 = m_server.createMetadataRepositoryFacade("metatest-1");
			assertTrue("Repository should not be possible to create since it already exists", true);
		}
		catch(FacadeAlreadyExistsException e)
		{
		}
		metatest1 = m_server.getMetadataRepositoryFacade("metatest-1");
		InputStream input = new GZIPInputStream(new RemoteInputStream(metatest1.getRepositoryData()));
		IOUtils.copy(input, System.out, null);
		input.close();
	}

	public void testRegisterMirror() throws Exception
	{
		IRepositoryFacade metatest1 = m_server.getMetadataRepositoryFacade("metatest-1");
		metatest1.registerMirror(new URI("http://download.eclipse.org/tools/buckminster/updates-3.4/"), null);

		IMetadataRepository repo = ProvisioningHelper.getMetadataRepository(new URL(SERVER_URL
			+ "#metatest-1"));
		assertNotNull("Unable to obtain remote repository", repo);

		Query query = new InstallableUnitQuery("org.eclipse.buckminster.runtime");
		Collector collector = new Collector();
		repo.query(query, collector, null);
		assertTrue("Did not find expected bundle", collector.size() > 0);
	}

	public void testRegisterMirror2() throws Exception
	{
		IRepositoryFacade metatest1 = m_server.getMetadataRepositoryFacade("metatest-1");
		metatest1.registerMirror(new URI("http://download.eclipse.org/tools/mylyn/update/ganymede/"), null);

		IMetadataRepository repo = ProvisioningHelper.getMetadataRepository(new URL(SERVER_URL
			+ "#metatest-1"));
		assertNotNull("Unable to obtain remote repository", repo);

		Query query = new InstallableUnitQuery("org.eclipse.mylyn");
		Collector collector = new Collector();
		repo.query(query, collector, null);
		assertTrue("Did not find expected bundle", collector.size() > 0);
	}
}
