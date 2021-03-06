/*******************************************************************************
 * Copyright (c) 2004, 2005
 * Thomas Hallgren, Kenneth Olwing, Mitch Sonies
 * Pontus Rydin, Nils Unden, Peer Torngren
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed above, as Initial Contributors under such license.
 * The text of such license is available at www.eclipse.org.
 *******************************************************************************/
package org.eclipse.buckminster.cmdline.parser;

import org.eclipse.buckminster.cmdline.Messages;
import org.eclipse.osgi.util.NLS;

/**
 * @author kolwing
 * 
 */
public class InvalidOptionValueException extends AbstractOptionParsingException {
	private static final long serialVersionUID = 4827165311898239651L;

	public InvalidOptionValueException(String optionName, String message) {
		super(NLS.bind(Messages.InvalidOptionValueException_The_value_for_option_0_is_invalid, optionName) + message);
	}

}
