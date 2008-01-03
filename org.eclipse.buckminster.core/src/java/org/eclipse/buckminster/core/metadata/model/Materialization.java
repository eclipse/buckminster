/*****************************************************************************
 * Copyright (c) 2006-2007, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.core.metadata.model;

import java.io.File;

import org.eclipse.buckminster.core.XMLConstants;
import org.eclipse.buckminster.core.cspec.model.ComponentIdentifier;
import org.eclipse.buckminster.core.cspec.model.ComponentName;
import org.eclipse.buckminster.core.cspec.model.NamedElement;
import org.eclipse.buckminster.core.metadata.StorageManager;
import org.eclipse.buckminster.core.metadata.WorkspaceInfo;
import org.eclipse.buckminster.core.version.IVersion;
import org.eclipse.buckminster.sax.ISaxable;
import org.eclipse.buckminster.sax.Utils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author Thomas Hallgren
 */
public class Materialization extends UUIDKeyed implements ISaxable
{
	public static final String TAG = "materialization";

	public static final String ATTR_LOCATION = "location";
	public static final int SEQUENCE_NUMBER = 3;

	private final IPath m_componentLocation;
	private final ComponentIdentifier m_componentIdentifier;

	public Materialization(IPath destination, ComponentIdentifier componentIdentifier)
	{
		if(destination == null || componentIdentifier == null)
			throw new NullPointerException();
		m_componentLocation = destination;
		m_componentIdentifier = componentIdentifier;
	}

	public final IPath getComponentLocation()
	{
		return m_componentLocation;
	}

	public String getDefaultTag()
	{
		return TAG;
	}

	public ComponentIdentifier getComponentIdentifier()
	{
		return m_componentIdentifier;
	}

	public Resolution getResolution() throws CoreException
	{
		return WorkspaceInfo.getResolution(m_componentIdentifier);
	}

	public boolean isPersisted(StorageManager sm) throws CoreException
	{
		return sm.getMaterializations().contains(this);
	}

	/**
	 * Returns <code>true</code> if this <code>MaterializationInfo</code> is
	 * valid. It will be considered valid if the destination appoints an
	 * existing file or a directory that is not empty.
	 * @return <code>true</code> if the destination is not empty.
	 */
	public boolean isValid()
	{
		IPath location = getComponentLocation();
		File destFile = location.toFile();

		// Check that this is a non-empty directory or a non-empty file with
		// as few system calls as possible.
		//
		String[] list = destFile.list();
		return (list == null) ? destFile.length() > 0 : list.length > 0;
	}

	public synchronized void remove(StorageManager sm) throws CoreException
	{
		WorkspaceInfo.clearCachedLocation(m_componentIdentifier);
		sm.getMaterializations().removeElement(getId());
	}

	public void store(StorageManager sm) throws CoreException
	{
		WorkspaceInfo.clearCachedLocation(m_componentIdentifier);
		sm.getMaterializations().putElement(this);
	}

	public void toSax(ContentHandler receiver) throws SAXException
	{
		receiver.startDocument();
		toSax(receiver, XMLConstants.BM_METADATA_NS, XMLConstants.BM_METADATA_PREFIX, getDefaultTag());
		receiver.endDocument();
	}

	@Override
	protected void addAttributes(AttributesImpl attrs) throws SAXException
	{
		Utils.addAttribute(attrs, ATTR_LOCATION, m_componentLocation.toPortableString());
		Utils.addAttribute(attrs, NamedElement.ATTR_NAME, m_componentIdentifier.getName());
		String tmp = m_componentIdentifier.getComponentTypeID();
		if(tmp != null)
			Utils.addAttribute(attrs, ComponentName.ATTR_COMPONENT_TYPE, tmp);

		IVersion version = m_componentIdentifier.getVersion();
		if(version != null)
		{
			Utils.addAttribute(attrs, ComponentIdentifier.ATTR_VERSION, version.toString());
			Utils.addAttribute(attrs, ComponentIdentifier.ATTR_VERSION_TYPE, version.getType().getId());
		}
	}
}

