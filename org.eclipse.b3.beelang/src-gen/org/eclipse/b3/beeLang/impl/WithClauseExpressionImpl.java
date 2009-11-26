/**
 * <copyright>
 * </copyright>
 *
 */
package org.eclipse.b3.beeLang.impl;

import org.eclipse.b3.beeLang.BeeLangPackage;
import org.eclipse.b3.beeLang.Expression;
import org.eclipse.b3.beeLang.WithClause;
import org.eclipse.b3.beeLang.WithClauseExpression;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>With Clause Expression</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.b3.beeLang.impl.WithClauseExpressionImpl#getWithclause <em>Withclause</em>}</li>
 *   <li>{@link org.eclipse.b3.beeLang.impl.WithClauseExpressionImpl#getExpr <em>Expr</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WithClauseExpressionImpl extends ExpressionImpl implements WithClauseExpression
{
  /**
   * The cached value of the '{@link #getWithclause() <em>Withclause</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getWithclause()
   * @generated
   * @ordered
   */
  protected WithClause withclause;

  /**
   * The cached value of the '{@link #getExpr() <em>Expr</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getExpr()
   * @generated
   * @ordered
   */
  protected Expression expr;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected WithClauseExpressionImpl()
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
    return BeeLangPackage.Literals.WITH_CLAUSE_EXPRESSION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WithClause getWithclause()
  {
    return withclause;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetWithclause(WithClause newWithclause, NotificationChain msgs)
  {
    WithClause oldWithclause = withclause;
    withclause = newWithclause;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, BeeLangPackage.WITH_CLAUSE_EXPRESSION__WITHCLAUSE, oldWithclause, newWithclause);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setWithclause(WithClause newWithclause)
  {
    if (newWithclause != withclause)
    {
      NotificationChain msgs = null;
      if (withclause != null)
        msgs = ((InternalEObject)withclause).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - BeeLangPackage.WITH_CLAUSE_EXPRESSION__WITHCLAUSE, null, msgs);
      if (newWithclause != null)
        msgs = ((InternalEObject)newWithclause).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - BeeLangPackage.WITH_CLAUSE_EXPRESSION__WITHCLAUSE, null, msgs);
      msgs = basicSetWithclause(newWithclause, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, BeeLangPackage.WITH_CLAUSE_EXPRESSION__WITHCLAUSE, newWithclause, newWithclause));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression getExpr()
  {
    return expr;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetExpr(Expression newExpr, NotificationChain msgs)
  {
    Expression oldExpr = expr;
    expr = newExpr;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, BeeLangPackage.WITH_CLAUSE_EXPRESSION__EXPR, oldExpr, newExpr);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setExpr(Expression newExpr)
  {
    if (newExpr != expr)
    {
      NotificationChain msgs = null;
      if (expr != null)
        msgs = ((InternalEObject)expr).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - BeeLangPackage.WITH_CLAUSE_EXPRESSION__EXPR, null, msgs);
      if (newExpr != null)
        msgs = ((InternalEObject)newExpr).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - BeeLangPackage.WITH_CLAUSE_EXPRESSION__EXPR, null, msgs);
      msgs = basicSetExpr(newExpr, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, BeeLangPackage.WITH_CLAUSE_EXPRESSION__EXPR, newExpr, newExpr));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case BeeLangPackage.WITH_CLAUSE_EXPRESSION__WITHCLAUSE:
        return basicSetWithclause(null, msgs);
      case BeeLangPackage.WITH_CLAUSE_EXPRESSION__EXPR:
        return basicSetExpr(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
      case BeeLangPackage.WITH_CLAUSE_EXPRESSION__WITHCLAUSE:
        return getWithclause();
      case BeeLangPackage.WITH_CLAUSE_EXPRESSION__EXPR:
        return getExpr();
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
      case BeeLangPackage.WITH_CLAUSE_EXPRESSION__WITHCLAUSE:
        setWithclause((WithClause)newValue);
        return;
      case BeeLangPackage.WITH_CLAUSE_EXPRESSION__EXPR:
        setExpr((Expression)newValue);
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
      case BeeLangPackage.WITH_CLAUSE_EXPRESSION__WITHCLAUSE:
        setWithclause((WithClause)null);
        return;
      case BeeLangPackage.WITH_CLAUSE_EXPRESSION__EXPR:
        setExpr((Expression)null);
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
      case BeeLangPackage.WITH_CLAUSE_EXPRESSION__WITHCLAUSE:
        return withclause != null;
      case BeeLangPackage.WITH_CLAUSE_EXPRESSION__EXPR:
        return expr != null;
    }
    return super.eIsSet(featureID);
  }

} //WithClauseExpressionImpl