/*******************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text or
 * such license is available at www.eclipse.org.
 ******************************************************************************/

package org.eclipse.buckminster.runtime;

/**
 * @author Filip Hrbek
 * 
 */
public interface IFileInfo {
	public static final String PROPERTY_CONTENT_TYPE = "contentType"; //$NON-NLS-1$

	public static final String PROPERTY_LAST_MODIFIED = "lastModified"; //$NON-NLS-1$

	public static final String PROPERTY_NAME = "name"; //$NON-NLS-1$

	public static final String PROPERTY_SIZE = "size"; //$NON-NLS-1$

	/**
	 * @return the contentType (may return null)
	 */
	String getContentType();

	/**
	 * @return the timestamp when the file was last modified
	 */
	long getLastModified();

	/**
	 * @return the name (may return null)
	 */
	String getRemoteName();

	/**
	 * @return the size or -1 if the size is not known
	 */
	long getSize();
}
