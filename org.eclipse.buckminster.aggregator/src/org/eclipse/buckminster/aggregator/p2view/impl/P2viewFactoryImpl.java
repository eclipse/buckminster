/**
 * Copyright (c) 2006-2009, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text of
 * such license is available at www.eclipse.org.
 *
 * $Id$
 */
package org.eclipse.buckminster.aggregator.p2view.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.buckminster.aggregator.AggregatorFactory;
import org.eclipse.buckminster.aggregator.LabelProvider;
import org.eclipse.buckminster.aggregator.Property;
import org.eclipse.buckminster.aggregator.p2.InstallableUnit;
import org.eclipse.buckminster.aggregator.p2.MetadataRepository;
import org.eclipse.buckminster.aggregator.p2.ProvidedCapability;
import org.eclipse.buckminster.aggregator.p2.RequiredCapability;
import org.eclipse.buckminster.aggregator.p2.TouchpointData;
import org.eclipse.buckminster.aggregator.p2view.*;
import org.eclipse.buckminster.aggregator.util.CapabilityNamespace;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class P2viewFactoryImpl extends EFactoryImpl implements P2viewFactory
{
	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static P2viewPackage getPackage()
	{
		return P2viewPackage.eINSTANCE;
	}

	/**
	 * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static P2viewFactory init()
	{
		try
		{
			P2viewFactory theP2viewFactory = (P2viewFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/buckminster/2009/aggregator/p2view");
			if(theP2viewFactory != null)
			{
				return theP2viewFactory;
			}
		}
		catch(Exception exception)
		{
			EcorePlugin.INSTANCE.log(exception);
		}
		return new P2viewFactoryImpl();
	}

	/**
	 * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public P2viewFactoryImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass)
	{
		switch(eClass.getClassifierID())
		{
		case P2viewPackage.METADATA_REPOSITORY_STRUCTURED_VIEW:
			return createMetadataRepositoryStructuredView();
		case P2viewPackage.INSTALLABLE_UNITS:
			return createInstallableUnits();
		case P2viewPackage.CATEGORIES:
			return createCategories();
		case P2viewPackage.FEATURES:
			return createFeatures();
		case P2viewPackage.PRODUCTS:
			return createProducts();
		case P2viewPackage.BUNDLES:
			return createBundles();
		case P2viewPackage.FRAGMENTS:
			return createFragments();
		case P2viewPackage.MISCELLANEOUS:
			return createMiscellaneous();
		case P2viewPackage.CATEGORY:
			return createCategory();
		case P2viewPackage.FEATURE:
			return createFeature();
		case P2viewPackage.PRODUCT:
			return createProduct();
		case P2viewPackage.BUNDLE:
			return createBundle();
		case P2viewPackage.FRAGMENT:
			return createFragment();
		case P2viewPackage.OTHER_IU:
			return createOtherIU();
		case P2viewPackage.PROPERTIES:
			return createProperties();
		case P2viewPackage.REQUIRED_CAPABILITIES:
			return createRequiredCapabilities();
		case P2viewPackage.PROVIDED_CAPABILITIES:
			return createProvidedCapabilities();
		case P2viewPackage.TOUCHPOINTS:
			return createTouchpoints();
		case P2viewPackage.IU_DETAILS:
			return createIUDetails();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Bundle createBundle()
	{
		BundleImpl bundle = new BundleImpl();
		return bundle;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public Bundle createBundle(InstallableUnit iu)
	{
		BundleImpl bundle = new BundleImpl(iu);
		return bundle;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Bundles createBundles()
	{
		BundlesImpl bundles = new BundlesImpl();
		return bundles;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Categories createCategories()
	{
		CategoriesImpl categories = new CategoriesImpl();
		return categories;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Category createCategory()
	{
		CategoryImpl category = new CategoryImpl();
		return category;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public Category createCategory(InstallableUnit iu)
	{
		CategoryImpl category = new CategoryImpl(iu);
		return category;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Feature createFeature()
	{
		FeatureImpl feature = new FeatureImpl();
		return feature;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public Feature createFeature(InstallableUnit iu)
	{
		FeatureImpl feature = new FeatureImpl(iu);
		return feature;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Features createFeatures()
	{
		FeaturesImpl features = new FeaturesImpl();
		return features;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Fragment createFragment()
	{
		FragmentImpl fragment = new FragmentImpl();
		return fragment;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public Fragment createFragment(InstallableUnit iu)
	{
		FragmentImpl fragment = new FragmentImpl(iu);
		return fragment;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Fragments createFragments()
	{
		FragmentsImpl fragments = new FragmentsImpl();
		return fragments;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public InstallableUnits createInstallableUnits()
	{
		InstallableUnitsImpl installableUnits = new InstallableUnitsImpl();
		return installableUnits;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public IUDetails createIUDetails()
	{
		IUDetailsImpl iuDetails = new IUDetailsImpl();
		return iuDetails;
	}

	public IUDetails createIUDetails(InstallableUnit iu)
	{
		IUDetailsImpl iuDetails = new IUDetailsImpl();

		List<RequiredCapability> rcList = new ArrayList<RequiredCapability>();
		for(RequiredCapability rc : iu.getRequiredCapabilityList())
		{
			if(rc.getLabel() == null)
			{
				CapabilityNamespace cn = CapabilityNamespace.byId(rc.getNamespace());

				if(cn == CapabilityNamespace.UNKNOWN)
					rc.setLabel(rc.getNamespace() + ":" + " " + rc.getName());
				else
					rc.setLabel(cn.getLabel() + " " + rc.getName());
			}

			rcList.add(rc);
		}

		if(rcList.size() > 0)
		{
			iuDetails.setRequiredCapabilitiesContainer(createRequiredCapabilities());
			Collections.sort(rcList, LabelProvider.COMPARATOR);
			iuDetails.getRequiredCapabilitiesContainer().getRequiredCapabilities().addAll(rcList);
		}

		List<ProvidedCapability> pcList = new ArrayList<ProvidedCapability>();
		for(ProvidedCapability pc : iu.getProvidedCapabilityList())
		{
			if(pc.getLabel() == null)
			{
				CapabilityNamespace cn = CapabilityNamespace.byId(pc.getNamespace());

				if(cn == CapabilityNamespace.UNKNOWN)
					pc.setLabel(pc.getNamespace() + ":" + " " + pc.getName());
				else
					pc.setLabel(cn.getLabel() + " " + pc.getName());
			}

			pcList.add(pc);
		}

		if(pcList.size() > 0)
		{
			iuDetails.setProvidedCapabilitiesContainer(createProvidedCapabilities());
			Collections.sort(pcList, LabelProvider.COMPARATOR);
			iuDetails.getProvidedCapabilitiesContainer().getProvidedCapabilities().addAll(pcList);
		}

		List<Property> propList = new ArrayList<Property>();
		for(Map.Entry<String, String> property : iu.getPropertyMap().entrySet())
			propList.add(AggregatorFactory.eINSTANCE.createProperty(property.getKey(), property.getValue()));
		if(propList.size() > 0)
		{
			iuDetails.setPropertiesContainer(createProperties());
			Collections.sort(propList);
			iuDetails.getPropertiesContainer().getPropertyList().addAll(propList);
		}

		if(iu.getTouchpointType() != null)
		{
			if(iuDetails.getTouchpointsContainer() == null)
				iuDetails.setTouchpointsContainer(createTouchpoints());

			iuDetails.getTouchpointsContainer().setTouchpointType(iu.getTouchpointType());
		}

		for(TouchpointData tpData : iu.getTouchpointDataList())
		{
			if(iuDetails.getTouchpointsContainer() == null)
				iuDetails.setTouchpointsContainer(createTouchpoints());

			iuDetails.getTouchpointsContainer().getTouchpointDataList().add(tpData);
		}

		iuDetails.setUpdateDescriptor(iu.getUpdateDescriptor());
		iuDetails.setCopyright(iu.getCopyright());
		iuDetails.setLicense(iu.getLicense());

		return iuDetails;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public MetadataRepositoryStructuredView createMetadataRepositoryStructuredView()
	{
		MetadataRepositoryStructuredViewImpl metadataRepositoryStructuredView = new MetadataRepositoryStructuredViewImpl();
		return metadataRepositoryStructuredView;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public MetadataRepositoryStructuredView createMetadataRepositoryStructuredView(MetadataRepository metadataRepository)
	{
		MetadataRepositoryStructuredViewImpl metadataRepositoryStructuredView = new MetadataRepositoryStructuredViewImpl(
				metadataRepository);
		return metadataRepositoryStructuredView;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Miscellaneous createMiscellaneous()
	{
		MiscellaneousImpl miscellaneous = new MiscellaneousImpl();
		return miscellaneous;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public OtherIU createOtherIU()
	{
		OtherIUImpl otherIU = new OtherIUImpl();
		return otherIU;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public OtherIU createOtherIU(InstallableUnit iu)
	{
		OtherIUImpl otherIU = new OtherIUImpl(iu);
		return otherIU;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Product createProduct()
	{
		ProductImpl product = new ProductImpl();
		return product;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public Product createProduct(InstallableUnit iu)
	{
		ProductImpl product = new ProductImpl(iu);
		return product;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Products createProducts()
	{
		ProductsImpl products = new ProductsImpl();
		return products;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Properties createProperties()
	{
		PropertiesImpl properties = new PropertiesImpl();
		return properties;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ProvidedCapabilities createProvidedCapabilities()
	{
		ProvidedCapabilitiesImpl providedCapabilities = new ProvidedCapabilitiesImpl();
		return providedCapabilities;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public RequiredCapabilities createRequiredCapabilities()
	{
		RequiredCapabilitiesImpl requiredCapabilities = new RequiredCapabilitiesImpl();
		return requiredCapabilities;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Touchpoints createTouchpoints()
	{
		TouchpointsImpl touchpoints = new TouchpointsImpl();
		return touchpoints;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public P2viewPackage getP2viewPackage()
	{
		return (P2viewPackage)getEPackage();
	}

} // P2viewFactoryImpl
