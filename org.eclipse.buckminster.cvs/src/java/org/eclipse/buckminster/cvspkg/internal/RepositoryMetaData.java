/*******************************************************************************
 * Copyright (c) 2004, 2006
 * Thomas Hallgren, Kenneth Olwing, Mitch Sonies
 * Pontus Rydin, Nils Unden, Peer Torngren
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed above, as Initial Contributors under such license.
 * The text of such license is available at www.eclipse.org.
 *******************************************************************************/
package org.eclipse.buckminster.cvspkg.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.eclipse.buckminster.cvspkg.CVSPlugin;
import org.eclipse.buckminster.cvspkg.Messages;
import org.eclipse.buckminster.runtime.BuckminsterException;
import org.eclipse.buckminster.runtime.IOUtils;
import org.eclipse.buckminster.runtime.Logger;
import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.internal.ccvs.core.CVSTag;
import org.eclipse.team.internal.ccvs.core.client.Command;
import org.eclipse.team.internal.ccvs.core.client.RLog;
import org.eclipse.team.internal.ccvs.core.client.Session;

@SuppressWarnings("restriction")
public class RepositoryMetaData implements Serializable {
	private static final long serialVersionUID = 6869163410872011769L;

	private static final HashMap<UUID, RepositoryMetaData> metaDataCache = new HashMap<UUID, RepositoryMetaData>();

	public static RepositoryMetaData getMetaData(CVSSession cvsSession, CVSTag fixedTag, IProgressMonitor monitor) throws CoreException {
		String repository = cvsSession.getRepository();
		RepositoryMetaData result = RepositoryMetaData.load(fixedTag, repository);

		Date now = new Date(System.currentTimeMillis());

		if (result != null) {
			Date resultTime = result.getTimestamp();
			if (fixedTag != null && fixedTag.getType() == CVSTag.DATE && CVSReader.getTagDate(fixedTag).compareTo(resultTime) <= 0)
				//
				// Meta-data is newer then requested fixed date.
				//
				return result;

			// TODO: This value should be configurable. 5 minutes is reasonable
			// but
			// it might be too much in some cases.
			//
			if (now.getTime() - resultTime.getTime() < 300000)
				return result;
		}

		monitor.beginTask(null, 100);
		monitor.subTask(NLS.bind(Messages.obtaining_meta_data_for_repository_0, repository));
		Session session = cvsSession.getReaderSession(MonitorUtils.subMonitor(monitor, 10));

		try {
			Logger logger = CVSPlugin.getLogger();
			ArrayList<Command.LocalOption> opts = new ArrayList<Command.LocalOption>();

			if (result == null) {
				logger.debug("Initial metadata fetch for %s", repository); //$NON-NLS-1$
				if (fixedTag != null && !fixedTag.equals(CVSTag.DEFAULT))
					opts.add(RLog.getCurrentTag(fixedTag));
			} else {
				if (fixedTag != null) {
					switch (fixedTag.getType()) {
						case CVSTag.DATE:
							// No use scanning longer then this.
							//
							now = fixedTag.asDate();
							if (fixedTag.asDate().compareTo(result.getTimestamp()) <= 0)
								return result;
							fixedTag = null;
							break;
						case CVSTag.BRANCH:
						case CVSTag.VERSION:
							opts.add(RLog.getCurrentTag(fixedTag));
							break;
					}
				}
				logger.debug("Metadata refetch from " + result.getTimestamp() + " for " + repository); //$NON-NLS-1$ //$NON-NLS-2$
				opts.add(RLog.makeTagOption(new CVSTag(result.getTimestamp()), new CVSTag(now)));
				opts.add(RLog.ONLY_INCLUDE_CHANGES);
			}

			String[] args = new String[] { cvsSession.getModuleName() };
			MetaDataCollector collector = new MetaDataCollector();
			IStatus status = new RLog().execute(session, Command.NO_GLOBAL_OPTIONS, opts.toArray(new Command.LocalOption[opts.size()]), args,
					collector, new SubProgressMonitor(monitor, 90, SubProgressMonitor.SUPPRESS_SUBTASK_LABEL));

			if (!status.isOK())
				throw new CVSException(status);

			if (result == null) {
				if (collector.getLastModificationTime() == null)
					throw new CVSException(NLS.bind(Messages.found_no_metadata_for_0, repository));
				result = new RepositoryMetaData(collector, now);
			} else
				result = result.merge(collector, now);

			result.store(fixedTag, repository);
			return result;
		} finally {
			monitor.done();
		}
	}

	public static RepositoryMetaData load(CVSTag fixedTag, String repository) throws CoreException {
		UUID id = getRepositoryId(fixedTag, repository);
		RepositoryMetaData rmd = metaDataCache.get(id);
		if (rmd != null)
			return rmd;

		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(new FileInputStream(getStateFile(id)));
			rmd = (RepositoryMetaData) input.readObject();
			metaDataCache.put(id, rmd);
			return rmd;
		} catch (FileNotFoundException e) {
			return null;
		} catch (InvalidClassException e) {
			// This happens if the serial version UUID has changed. We
			// allow that but consider the old data non-existent.
			//
			return null;
		} catch (ClassNotFoundException e) {
			throw BuckminsterException.wrap(e);
		} catch (IOException e) {
			throw BuckminsterException.wrap(e);
		} finally {
			IOUtils.close(input);
		}
	}

	private static Set<String> concat(String[] names, Set<String> moreNames) {
		HashSet<String> result = new HashSet<String>(names.length + moreNames.size());
		for (String name : names)
			result.add(name);
		result.addAll(moreNames);
		return result;
	}

	private static UUID getRepositoryId(CVSTag fixedTag, String repository) {
		if (fixedTag != null && fixedTag.getType() != CVSTag.DATE)
			repository = repository + '#' + fixedTag;
		return UUID.nameUUIDFromBytes(repository.getBytes());

	}

	private static File getStateFile(UUID id) {
		return CVSPlugin.getDefault().getStateLocation().append(id.toString()).toFile();
	}

	private static String[] orderedArray(Set<String> names) {
		String[] ordered = names.toArray(new String[names.size()]);
		Arrays.sort(ordered);
		return ordered;
	}

	private final String[] branchNames;

	private final String[] tagNames;

	private final Date lastModification;

	private final Date timestamp;

	public RepositoryMetaData(MetaDataCollector collector, Date timestamp) {
		this(collector.getBranchNames(), collector.getTagNames(), collector.getLastModificationTime(), timestamp);
	}

	private RepositoryMetaData(Set<String> branches, Set<String> tags, Date lastModification, Date timestamp) {
		this.branchNames = orderedArray(branches);
		this.tagNames = orderedArray(tags);
		this.lastModification = lastModification;
		this.timestamp = timestamp;
	}

	public final String[] getBranchNames() {
		return branchNames;
	}

	public final Date getLastModification() {
		return lastModification;
	}

	public final String[] getTagNames() {
		return tagNames;
	}

	public final Date getTimestamp() {
		return timestamp;
	}

	public RepositoryMetaData merge(MetaDataCollector collector, Date ts) {
		Date lastModTime = collector.getLastModificationTime();
		if (lastModTime == null)
			lastModTime = lastModification;

		return new RepositoryMetaData(concat(branchNames, collector.getBranchNames()), concat(tagNames, collector.getTagNames()), lastModTime, ts);
	}

	public void store(CVSTag fixedTag, String repository) throws CoreException {
		UUID id = getRepositoryId(fixedTag, repository);
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream(new FileOutputStream(getStateFile(id)));
			output.writeObject(this);
			metaDataCache.put(id, this);
		} catch (IOException e) {
			throw BuckminsterException.wrap(e);
		} finally {
			IOUtils.close(output);
		}
	}
}
