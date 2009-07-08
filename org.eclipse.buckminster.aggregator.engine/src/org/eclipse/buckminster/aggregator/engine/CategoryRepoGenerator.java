package org.eclipse.buckminster.aggregator.engine;

import org.eclipse.buckminster.aggregator.p2.MetadataRepository;
import org.eclipse.buckminster.aggregator.p2.P2Factory;
import org.eclipse.buckminster.runtime.Buckminster;
import org.eclipse.buckminster.runtime.Logger;
import org.eclipse.buckminster.runtime.MonitorUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.internal.provisional.p2.metadata.repository.IMetadataRepository;
import org.eclipse.equinox.p2.publisher.IPublisherAction;
import org.eclipse.equinox.p2.publisher.Publisher;
import org.eclipse.equinox.p2.publisher.PublisherInfo;

public class CategoryRepoGenerator extends BuilderPhase
{
	public CategoryRepoGenerator(Builder builder)
	{
		super(builder);
	}

	@Override
	public void run(IProgressMonitor monitor) throws CoreException
	{
		Logger log = Buckminster.getLogger();
		long now = System.currentTimeMillis();
		MonitorUtils.begin(monitor, 100);
		String info = "Starting generation of categories";
		MonitorUtils.subTask(monitor, info);
		log.info(info);
		try
		{
			MetadataRepository mdr = P2Factory.eINSTANCE.createMetadataRepository();

			PublisherInfo pubInfo = new PublisherInfo();
			pubInfo.setMetadataRepository(mdr);
			Publisher publisher = new Publisher(pubInfo);
			IStatus result = publisher.publish(createActions(mdr), MonitorUtils.subMonitor(monitor, 90));
			if(result.getSeverity() == IStatus.ERROR)
				throw new CoreException(result);

			getBuilder().setCategoriesRepo(mdr);
		}
		finally
		{
			MonitorUtils.done(monitor);
		}
		log.info("Done. Took %d ms", Long.valueOf(System.currentTimeMillis() - now));
	}

	private IPublisherAction[] createActions(IMetadataRepository mdr)
	{
		return new IPublisherAction[] { new CategoriesAction(getBuilder()) };
	}
}
