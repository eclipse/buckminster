/*****************************************************************************
 * Copyright (c) 2006-2008, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.core.cspec.model;

import org.eclipse.buckminster.core.helpers.LocalizedException;

/**
 * @author Thomas Hallgren
 */
public class PrerequisiteAlreadyDefinedException extends LocalizedException
{
	private static final long serialVersionUID = 1892270697205823409L;

	public PrerequisiteAlreadyDefinedException(String name, String attribute, String prerequisite)
	{
		super("CSpec %s, attribute %s already has a prerequisite named %s",
			name, attribute, prerequisite);
	}
}

