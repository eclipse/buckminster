/*******************************************************************************
 * Copyright (c) 2004, 2006
 * Thomas Hallgren, Kenneth Olwing, Mitch Sonies
 * Pontus Rydin, Nils Unden, Peer Torngren
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed above, as Initial Contributors under such license.
 * The text of such license is available at www.eclipse.org.
 *******************************************************************************/

package org.eclipse.buckminster.core.rmap.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.buckminster.core.CorePlugin;
import org.eclipse.buckminster.core.XMLConstants;
import org.eclipse.buckminster.core.common.model.Documentation;
import org.eclipse.buckminster.core.common.model.Format;
import org.eclipse.buckminster.core.cspec.model.ComponentRequest;
import org.eclipse.buckminster.core.ctype.IComponentType;
import org.eclipse.buckminster.core.ctype.MissingCSpecSourceException;
import org.eclipse.buckminster.core.helpers.TextUtils;
import org.eclipse.buckminster.core.metadata.ReferentialIntegrityException;
import org.eclipse.buckminster.core.metadata.StorageManager;
import org.eclipse.buckminster.core.metadata.model.IUUIDPersisted;
import org.eclipse.buckminster.core.reader.IReaderType;
import org.eclipse.buckminster.core.reader.IVersionFinder;
import org.eclipse.buckminster.core.resolver.NodeQuery;
import org.eclipse.buckminster.core.resolver.ResolverDecision;
import org.eclipse.buckminster.core.resolver.ResolverDecisionType;
import org.eclipse.buckminster.core.version.IVersionConverter;
import org.eclipse.buckminster.core.version.ProviderMatch;
import org.eclipse.buckminster.core.version.VersionMatch;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.buckminster.runtime.Trivial;
import org.eclipse.buckminster.sax.UUIDKeyed;
import org.eclipse.buckminster.sax.Utils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author Thomas Hallgren
 */
public class Provider extends UUIDKeyed implements IUUIDPersisted
{
	public static final String ATTR_COMPONENT_TYPES = "componentTypes";

	public static final String ATTR_ALGORITHM = "algorithm";

	public static final String ATTR_MUTABLE = "mutable";

	public static final String ATTR_READER_TYPE = "readerType";

	public static final String ATTR_SOURCE = "source";

	public static final String ATTR_SPACE = "space";

	public static final String ATTR_VERSION_CONVERTER = "versionConverter";

	public static final String TAG = "provider";

	public static final String TAG_URI = "uri";

	public static final int SEQUENCE_NUMBER = 2;

	public static final String TAG_DIGEST = "digest";

	private final Documentation m_documentation;

	private final String[] m_componentTypeIDs;

	private final boolean m_mutable;

	private final String m_readerTypeId;

	private final String m_space;

	private final boolean m_source;

	private final Format m_uri;

	private final Format m_digest;

	private final String m_digestAlgorithm;

	private final VersionConverterDesc m_versionConverter;
	
	private final SearchPath m_searchPath;

	private final URIMatcher m_uriMatcher;

	public Provider(String remoteReaderType, String[] componentTypeIDs, String uri)
	{
		this(null, remoteReaderType, componentTypeIDs, null, new Format(uri), null, null, null, false, false, null, null);
	}

	/**
	 * Creates a new fully initialized Provider
	 * @param searchPath The search path that this provider belongs to.
	 * @param remoteReaderType The reader type used by the provider
	 * @param componentTypeIDs An array of component types supported by this provider
	 * @param versionConverterDesc The description of the version converter or <code>null</code> if not applicable.
	 * @param uri The URI used by the reader type.
	 * @param digest The digest URI or <code>null</code> if not applicable
	 * @param digestAlgorithm The digest algorithm or <code>null</code> if not applicable
	 * @param space The naming authority
	 * @param mutable <code>true</code> if this provider delivers source from an SCM with read/write access. Should be false if not.
	 * @param source <code>true</code> if this provider delivers source.
	 * @param uriMatcher The URI matcher for the provider or <code>null</code> if not applicable.
	 * @param documentation Documentation in xhtml format.
	 */
	public Provider(SearchPath searchPath, String remoteReaderType, String[] componentTypeIDs,
		VersionConverterDesc versionConverterDesc, Format uri, Format digest, String digestAlgorithm, String space, boolean mutable, boolean source,
		URIMatcher uriMatcher,
		Documentation documentation)
	{
		m_searchPath = searchPath;
		m_readerTypeId = remoteReaderType;
		m_componentTypeIDs = componentTypeIDs == null ? Trivial.EMPTY_STRING_ARRAY : componentTypeIDs;
		m_versionConverter = versionConverterDesc;
		m_uri = uri;
		m_digest = digest;
		m_digestAlgorithm = digestAlgorithm;
		m_space = space;
		m_mutable = mutable;
		m_source = source;
		m_uriMatcher = uriMatcher;
		m_documentation = documentation;
	}

	public void addPrefixMappings(HashMap<String, String> prefixMappings)
	{
		prefixMappings.put("xsi", javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
	}

	public ProviderMatch findMatch(NodeQuery query, MultiStatus problemCollector, IProgressMonitor monitor)
	throws CoreException
	{
		String readerType = getReaderTypeId();
		ProviderScore score = query.getProviderScore(isMutable(), hasSource());
		if(score == ProviderScore.REJECTED)
		{
			ResolverDecision decision = query.logDecision(ResolverDecisionType.REJECTING_PROVIDER, readerType, getURI(), "Score is below threshold");
			problemCollector.add(new Status(IStatus.ERROR, CorePlugin.getID(), IStatus.OK, decision.toString(), null));
			return null;
		}

		if(m_uriMatcher != null)
			return m_uriMatcher.getMatch(this, query, monitor);

		IVersionFinder versionFinder = null;
		monitor.beginTask(null, 120);
		try
		{
			ComponentRequest request = query.getComponentRequest();
			String componentTypeID = request.getComponentTypeID();
	
			// The component request is equipped with a component type. It must
			// match the types that this provider provides.
			//
			IComponentType[] componentTypes = getComponentTypes();
			if(componentTypeID != null)
			{
				boolean found = false;
				int idx = componentTypes.length;
				while(--idx >= 0)
				{
					IComponentType ctype = componentTypes[idx];
					if(ctype.getId().equals(componentTypeID))
					{
						// Limit the component types to this one type
						//
						componentTypes = new IComponentType[] { ctype };
						found = true;
						break;
					}
				}

				if(!found)
				{
					// The ECLIPSE_PLATFORM reader is silent here since it is always consulted
					//
					if(!getReaderTypeId().equals(IReaderType.ECLIPSE_PLATFORM))
					{
						ResolverDecision decision = query.logDecision(ResolverDecisionType.REJECTING_PROVIDER, readerType, getURI(),
							String.format("Components of type %s are not supported", componentTypeID));
						problemCollector.add(new Status(IStatus.ERROR, CorePlugin.getID(), IStatus.OK, decision.toString(), null));
					}
					return null;
				}
			}
		
			VersionMatch candidate = null;
			IComponentType ctypeUsed = null;
			CoreException problem = null;
			try
			{
				for(IComponentType ctype : componentTypes)
				{
					try
					{
						versionFinder = getReaderType().getVersionFinder(this, ctype, query, MonitorUtils.subMonitor(monitor, 20));
						candidate = versionFinder.getBestVersion(MonitorUtils.subMonitor(monitor, 80));
						if(candidate == null)
							continue;
						ctypeUsed = ctype;
					}
					catch(MissingCSpecSourceException e)
					{
						continue;
					}
					break;
				}
			}
			catch(CoreException e)
			{
				problem = e;
			}

			if(candidate == null)
			{
				ResolverDecision decision = query.logDecision(ResolverDecisionType.REJECTING_PROVIDER, readerType, getURI(),
						"No component match was found");
				problemCollector.add(new Status(IStatus.ERROR, CorePlugin.getID(), IStatus.OK, decision.toString(), problem == null ? null : BuckminsterException.unwind(problem)));
				return null;
			}
			query.logDecision(ResolverDecisionType.MATCH_FOUND, candidate);
			return versionFinder.getProviderMatch(candidate, ctypeUsed, score);
		}
		finally
		{
			if(versionFinder != null)
				versionFinder.close();
			monitor.done();
		}			
	}

	public final IComponentType[] getComponentTypes() throws CoreException
	{
		CorePlugin plugin = CorePlugin.getDefault();
		int idx = m_componentTypeIDs.length;
		IComponentType[] ctypes = new IComponentType[idx];
		while(--idx >= 0)
			ctypes[idx] = plugin.getComponentType(m_componentTypeIDs[idx]);
		return ctypes;
	}

	public final String[] getComponentTypeIDs()
	{
		return m_componentTypeIDs;
	}

	public String getDefaultTag()
	{
		return TAG;
	}

	public Documentation getDocumentation()
	{
		return m_documentation;
	}

	public final IReaderType getReaderType() throws CoreException
	{
		return CorePlugin.getDefault().getReaderType(m_readerTypeId);
	}

	public final String getReaderTypeId()
	{
		return m_readerTypeId;
	}

	public final SearchPath getSearchPath()
	{
		return m_searchPath;
	}

	public final String getSpace()
	{
		return m_space;
	}

	/**
	 * @return Returns the Digest URI.
	 */
	public final String getDigest(Map<String, String> properties)
	{
		return m_digest == null ? null : m_digest.getValue(getProperties(properties));
	}

	public final String getDigestAlgorithm()
	{
		return m_digestAlgorithm;
	}

	/**
	 * @return Returns the possibly parameterized <code>Format</code> instance that represents
	 * File or Repository URI.
	 */
	public final Format getURI()
	{
		return m_uri;
	}

	/**
	 * @return Returns expanded the File or Repository URI.
	 */
	public final String getURI(Map<String, String> properties)
	{
		return m_uri.getValue(getProperties(properties));
	}

	public Map<String,String> getProperties(Map<String,String> properties)
	{
		if(m_searchPath != null)
			properties = m_searchPath.getResourceMap().getProperties(properties);
		return properties;
	}

	public URIMatcher getURIMatcher()
	{
		return m_uriMatcher;
	}

	public IVersionConverter getVersionConverter() throws CoreException
	{
		VersionConverterDesc vcd = getVersionConverterDesc();
		return vcd == null ? null : vcd.getVersionConverter();
	}

	public final VersionConverterDesc getVersionConverterDesc()
	{
		return m_versionConverter;
	}

	/**
	 * @return Returns the hasSource.
	 */
	public final boolean hasSource()
	{
		return m_source;
	}

	/**
	 * @return Returns the readOnly.
	 */
	public final boolean isMutable()
	{
		return m_mutable;
	}

	public boolean isPersisted(StorageManager sm) throws CoreException
	{
		return sm.getProviders().contains(this);
	}

	public void remove(StorageManager sm) throws CoreException
	{
		UUID thisId = getId();
		if(!sm.getResolutions().getReferencingKeys(thisId, "providerId").isEmpty())
			throw new ReferentialIntegrityException(this, "remove", "Referenced from Resolution");

		sm.getProviders().removeElement(thisId);
	}

	public void store(StorageManager sm) throws CoreException
	{
		sm.getProviders().putElement(this);
	}

	public void toSax(ContentHandler handler) throws SAXException
	{
		handler.startDocument();

		HashMap<String, String> prefixMappings = new HashMap<String, String>();
		addPrefixMappings(prefixMappings);
		for(Map.Entry<String, String> pfxMapping : prefixMappings.entrySet())
			handler.startPrefixMapping(pfxMapping.getKey(), pfxMapping.getValue());

		toSax(handler, XMLConstants.BM_RMAP_NS, XMLConstants.BM_RMAP_PREFIX, getDefaultTag());
		for(String pfx : prefixMappings.keySet())
			handler.endPrefixMapping(pfx);

		handler.endDocument();
	}

	@Override
	protected void addAttributes(AttributesImpl attrs) throws SAXException
	{
		Utils.addAttribute(attrs, ATTR_READER_TYPE, m_readerTypeId);
		if(m_componentTypeIDs.length > 0)
			Utils.addAttribute(attrs, ATTR_COMPONENT_TYPES, TextUtils.concat(m_componentTypeIDs, ","));
		if(m_space != null)
			Utils.addAttribute(attrs, ATTR_SPACE, m_space);
		Utils.addAttribute(attrs, ATTR_MUTABLE, Boolean.toString(m_mutable));
		Utils.addAttribute(attrs, ATTR_SOURCE, Boolean.toString(m_source));
	}

	@Override
	protected void emitElements(ContentHandler handler, String namespace, String prefix) throws SAXException
	{
		if(m_documentation != null)
			m_documentation.toSax(handler, namespace, prefix, m_documentation.getDefaultTag());
		m_uri.toSax(handler, namespace, prefix, TAG_URI);

		if(m_digest != null)
		{
			AttributesImpl attrs = new AttributesImpl();
			Utils.addAttribute(attrs, Format.ATTR_FORMAT, m_digest.getFormat());
			Utils.addAttribute(attrs, ATTR_ALGORITHM, m_digestAlgorithm);
			String qName = Utils.makeQualifiedName(prefix, TAG_DIGEST);
			handler.startElement(namespace, TAG_DIGEST, qName, attrs);
			handler.endElement(namespace, TAG_DIGEST, qName);
		}

		if(m_versionConverter != null)
			m_versionConverter.toSax(handler, namespace, prefix, m_versionConverter.getDefaultTag());
		if(m_uriMatcher != null)
			m_uriMatcher.toSax(handler, namespace, prefix, m_uriMatcher.getDefaultTag());
	}

	@Override
	protected String getElementNamespace(String namespace)
	{
		return XMLConstants.BM_RMAP_NS;
	}

	@Override
	protected String getElementPrefix(String prefix)
	{
		return XMLConstants.BM_RMAP_PREFIX;
	}
}
