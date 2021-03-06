/*****************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *****************************************************************************/
package org.eclipse.buckminster.core.actor;

import org.eclipse.buckminster.core.Messages;
import org.eclipse.buckminster.core.cspec.model.Action;
import org.eclipse.buckminster.core.helpers.LocalizedException;
import org.eclipse.osgi.util.NLS;

public class MissingPropertyException extends LocalizedException {
	private static final long serialVersionUID = 3944567444454920623L;

	public MissingPropertyException(Action action, Object alias) {
		super(NLS.bind(Messages.action_0_is_missing_required_property_1, action.getQualifiedName(), alias));
	}
}
