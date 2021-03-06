/*****************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.core.cspec.model;

import org.eclipse.buckminster.core.Messages;
import org.eclipse.buckminster.core.helpers.LocalizedException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.NLS;

/**
 * @author Thomas Hallgren
 */
public class PathAlreadyDefinedException extends LocalizedException {
	private static final long serialVersionUID = 2296533812994158158L;

	public PathAlreadyDefinedException(String name, String attribute, IPath path) {
		super(NLS.bind(Messages.CSpec_0_attribute_1_already_defines_the_path_2, new Object[] { name, attribute, path.toPortableString() }));
	}
}
