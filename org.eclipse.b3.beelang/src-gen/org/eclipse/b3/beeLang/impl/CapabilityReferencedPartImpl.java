/**
 * <copyright>
 * </copyright>
 *
 */
package org.eclipse.b3.beeLang.impl;

import org.eclipse.b3.beeLang.BeeLangPackage;
import org.eclipse.b3.beeLang.CapabilityReferencedPart;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Capability Referenced Part</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.b3.beeLang.impl.CapabilityReferencedPartImpl#getInterface <em>Interface</em>}</li>
 *   <li>{@link org.eclipse.b3.beeLang.impl.CapabilityReferencedPartImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.b3.beeLang.impl.CapabilityReferencedPartImpl#getRange <em>Range</em>}</li>
 *   <li>{@link org.eclipse.b3.beeLang.impl.CapabilityReferencedPartImpl#getPartName <em>Part Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CapabilityReferencedPartImpl extends PrerequisiteEntryImpl implements CapabilityReferencedPart
{
  /**
   * The default value of the '{@link #getInterface() <em>Interface</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInterface()
   * @generated
   * @ordered
   */
  protected static final String INTERFACE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getInterface() <em>Interface</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInterface()
   * @generated
   * @ordered
   */
  protected String interface_ = INTERFACE_EDEFAULT;

  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getRange() <em>Range</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRange()
   * @generated
   * @ordered
   */
  protected static final String RANGE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getRange() <em>Range</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRange()
   * @generated
   * @ordered
   */
  protected String range = RANGE_EDEFAULT;

  /**
   * The default value of the '{@link #getPartName() <em>Part Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPartName()
   * @generated
   * @ordered
   */
  protected static final String PART_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getPartName() <em>Part Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPartName()
   * @generated
   * @ordered
   */
  protected String partName = PART_NAME_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CapabilityReferencedPartImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return BeeLangPackage.Literals.CAPABILITY_REFERENCED_PART;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getInterface()
  {
    return interface_;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInterface(String newInterface)
  {
    String oldInterface = interface_;
    interface_ = newInterface;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, BeeLangPackage.CAPABILITY_REFERENCED_PART__INTERFACE, oldInterface, interface_));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, BeeLangPackage.CAPABILITY_REFERENCED_PART__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getRange()
  {
    return range;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRange(String newRange)
  {
    String oldRange = range;
    range = newRange;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, BeeLangPackage.CAPABILITY_REFERENCED_PART__RANGE, oldRange, range));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getPartName()
  {
    return partName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPartName(String newPartName)
  {
    String oldPartName = partName;
    partName = newPartName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, BeeLangPackage.CAPABILITY_REFERENCED_PART__PART_NAME, oldPartName, partName));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case BeeLangPackage.CAPABILITY_REFERENCED_PART__INTERFACE:
        return getInterface();
      case BeeLangPackage.CAPABILITY_REFERENCED_PART__NAME:
        return getName();
      case BeeLangPackage.CAPABILITY_REFERENCED_PART__RANGE:
        return getRange();
      case BeeLangPackage.CAPABILITY_REFERENCED_PART__PART_NAME:
        return getPartName();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case BeeLangPackage.CAPABILITY_REFERENCED_PART__INTERFACE:
        setInterface((String)newValue);
        return;
      case BeeLangPackage.CAPABILITY_REFERENCED_PART__NAME:
        setName((String)newValue);
        return;
      case BeeLangPackage.CAPABILITY_REFERENCED_PART__RANGE:
        setRange((String)newValue);
        return;
      case BeeLangPackage.CAPABILITY_REFERENCED_PART__PART_NAME:
        setPartName((String)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case BeeLangPackage.CAPABILITY_REFERENCED_PART__INTERFACE:
        setInterface(INTERFACE_EDEFAULT);
        return;
      case BeeLangPackage.CAPABILITY_REFERENCED_PART__NAME:
        setName(NAME_EDEFAULT);
        return;
      case BeeLangPackage.CAPABILITY_REFERENCED_PART__RANGE:
        setRange(RANGE_EDEFAULT);
        return;
      case BeeLangPackage.CAPABILITY_REFERENCED_PART__PART_NAME:
        setPartName(PART_NAME_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case BeeLangPackage.CAPABILITY_REFERENCED_PART__INTERFACE:
        return INTERFACE_EDEFAULT == null ? interface_ != null : !INTERFACE_EDEFAULT.equals(interface_);
      case BeeLangPackage.CAPABILITY_REFERENCED_PART__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case BeeLangPackage.CAPABILITY_REFERENCED_PART__RANGE:
        return RANGE_EDEFAULT == null ? range != null : !RANGE_EDEFAULT.equals(range);
      case BeeLangPackage.CAPABILITY_REFERENCED_PART__PART_NAME:
        return PART_NAME_EDEFAULT == null ? partName != null : !PART_NAME_EDEFAULT.equals(partName);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (interface: ");
    result.append(interface_);
    result.append(", name: ");
    result.append(name);
    result.append(", range: ");
    result.append(range);
    result.append(", partName: ");
    result.append(partName);
    result.append(')');
    return result.toString();
  }

} //CapabilityReferencedPartImpl