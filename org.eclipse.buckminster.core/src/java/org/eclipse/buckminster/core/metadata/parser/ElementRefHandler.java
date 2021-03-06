/*****************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.core.metadata.parser;

import java.util.UUID;

import org.eclipse.buckminster.core.parser.ExtensionAwareHandler;
import org.eclipse.buckminster.sax.AbstractHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Thomas Hallgren
 */
public class ElementRefHandler extends ExtensionAwareHandler {
	public static final String ATTR_REFID = "refId"; //$NON-NLS-1$

	private final String tag;

	private UUID refId;

	public ElementRefHandler(AbstractHandler parent, String name) {
		super(parent);
		tag = name;
	}

	@Override
	public String getTAG() {
		return tag;
	}

	@Override
	public void handleAttributes(Attributes attrs) throws SAXException {
		refId = UUID.fromString(this.getStringValue(attrs, ATTR_REFID));
	}

	final UUID getRefId() {
		return refId;
	}
}
