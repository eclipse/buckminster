/*******************************************************************************
 * Copyright (c) 2006-2013, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 ******************************************************************************/

package org.eclipse.buckminster.core.resolver;

import org.eclipse.buckminster.core.cspec.model.ComponentRequest;

/**
 * @author Thomas Hallgren
 */
public interface IResolverBackchannel {
	ResolverDecision logDecision(ComponentRequest request, ResolverDecisionType decisionType, Object... args);

	ResolverDecision logDecision(ResolverDecisionType decisionType, Object... args);
}
