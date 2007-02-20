/*****************************************************************************
 * (c) 2004-2006
 * Thomas Hallgren, Kenneth Olwing, Mitch Sonies
 * Pontus Rydin, Nils Unden, Peer Torngren
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed above, as Initial Contributors under such license.
 * The text of such license is available at www.eclipse.org.
 ****************************************************************************/

package org.eclipse.buckminster.ant.taskdefs;

import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.buckminster.ant.types.FileSetGroup;

/**
 * @author Thomas Hallgren
 */
public class Zip extends org.apache.tools.ant.taskdefs.Zip
{
	private ArrayList<FileSetGroup> m_fileSetGroups;

	private ArrayList<FileSetGroup> m_zipGroupFileSetGroups;

	/**
	 * Adds a nested <code>&lt;filesetgroup&gt;</code> element.
	 */
	public void add(FileSetGroup fsGroup) throws BuildException
	{
		if(m_fileSetGroups == null)
			m_fileSetGroups = new ArrayList<FileSetGroup>();
		m_fileSetGroups.add(fsGroup);
	}

	/**
	 * Adds a nested <code>&lt;filesetgroup&gt;</code> element
	 * targeted for zipgroupfilesets.
	 */
    public void addZipGroupFilesetGroup(FileSetGroup setGroup)
    {
    	if(m_zipGroupFileSetGroups == null)
    		m_zipGroupFileSetGroups = new ArrayList<FileSetGroup>();
    	m_zipGroupFileSetGroups.add(setGroup);
    }

	@Override
	public void execute() throws BuildException
	{
    	if(m_fileSetGroups != null)
    	{
    		for(FileSetGroup fsg : m_fileSetGroups)
	    		for(FileSet fs : fsg.getFileSets())
	    			this.addFileset(fs);
    		m_fileSetGroups = null;
    	}
    	if(m_zipGroupFileSetGroups != null)
    	{
    		for(FileSetGroup fsg : m_zipGroupFileSetGroups)
	    		for(FileSet fs : fsg.getFileSets())
	    			this.addZipGroupFileset(fs);
    		m_zipGroupFileSetGroups = null;
    	}
		super.execute();
	}
}
