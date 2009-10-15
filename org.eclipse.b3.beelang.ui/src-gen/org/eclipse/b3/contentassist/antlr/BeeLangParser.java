/*
* generated by Xtext
*/
package org.eclipse.b3.contentassist.antlr;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.ui.common.editor.contentassist.antlr.AbstractContentAssistParser;
import org.eclipse.xtext.ui.common.editor.contentassist.antlr.FollowElement;
import org.eclipse.xtext.ui.common.editor.contentassist.antlr.internal.AbstractInternalContentAssistParser;

import com.google.inject.Inject;

import org.eclipse.b3.services.BeeLangGrammarAccess;

public class BeeLangParser extends AbstractContentAssistParser {
	
	@Inject
	private BeeLangGrammarAccess grammarAccess;
	
	private Map<AbstractElement, String> nameMappings;
	
	@Override
	protected org.eclipse.b3.contentassist.antlr.internal.InternalBeeLangLexer createLexer(CharStream stream) {
		return new org.eclipse.b3.contentassist.antlr.internal.InternalBeeLangLexer(stream);
	}
	
	@Override
	protected org.eclipse.b3.contentassist.antlr.internal.InternalBeeLangParser createParser() {
		org.eclipse.b3.contentassist.antlr.internal.InternalBeeLangParser result = new org.eclipse.b3.contentassist.antlr.internal.InternalBeeLangParser(null);
		result.setGrammarAccess(grammarAccess);
		return result;
	}
	
	@Override
	protected String getRuleName(AbstractElement element) {
		if (nameMappings == null) {
			nameMappings = new HashMap<AbstractElement, String>() {
				{
					put(grammarAccess.getEscapedQualifiedNameAccess().getAlternatives(), "rule__EscapedQualifiedName__Alternatives");
					put(grammarAccess.getSeparatorAccess().getAlternatives(), "rule__Separator__Alternatives");
					put(grammarAccess.getAlfanumSymAccess().getAlternatives_0(), "rule__AlfanumSym__Alternatives_0");
					put(grammarAccess.getAlfanumSymAccess().getAlternatives_1(), "rule__AlfanumSym__Alternatives_1");
					put(grammarAccess.getVersionAccess().getAlternatives(), "rule__Version__Alternatives");
					put(grammarAccess.getVersionRangeAccess().getAlternatives(), "rule__VersionRange__Alternatives");
					put(grammarAccess.getVersionRangeAccess().getAlternatives_0_0(), "rule__VersionRange__Alternatives_0_0");
					put(grammarAccess.getVersionRangeAccess().getAlternatives_0_3(), "rule__VersionRange__Alternatives_0_3");
					put(grammarAccess.getUnitAccess().getAlternatives_6(), "rule__Unit__Alternatives_6");
					put(grammarAccess.getUnitAccess().getAlternatives_6_6_2(), "rule__Unit__Alternatives_6_6_2");
					put(grammarAccess.getStringPropertyAccess().getAlternatives(), "rule__StringProperty__Alternatives");
					put(grammarAccess.getStringProperty2Access().getAlternatives(), "rule__StringProperty2__Alternatives");
					put(grammarAccess.getSynchronizedPartAccess().getAlternatives(), "rule__SynchronizedPart__Alternatives");
					put(grammarAccess.getBuildPartAccess().getAlternatives(), "rule__BuildPart__Alternatives");
					put(grammarAccess.getPathGroupAccess().getAlternatives_1(), "rule__PathGroup__Alternatives_1");
					put(grammarAccess.getPathGroupAccess().getAlternatives_1_2_2(), "rule__PathGroup__Alternatives_1_2_2");
					put(grammarAccess.getPathAccess().getAlternatives(), "rule__Path__Alternatives");
					put(grammarAccess.getClosureAccess().getAlternatives_2(), "rule__Closure__Alternatives_2");
					put(grammarAccess.getClosureAccess().getAlternatives_2_0_2(), "rule__Closure__Alternatives_2_0_2");
					put(grammarAccess.getPrerequisiteEntryAccess().getAlternatives(), "rule__PrerequisiteEntry__Alternatives");
					put(grammarAccess.getCapabilityReferencedPartAccess().getAlternatives(), "rule__CapabilityReferencedPart__Alternatives");
					put(grammarAccess.getResultPartAccess().getAlternatives(), "rule__ResultPart__Alternatives");
					put(grammarAccess.getRepositoryConfigurationAccess().getAlternatives_0(), "rule__RepositoryConfiguration__Alternatives_0");
					put(grammarAccess.getAdviceStatementAccess().getAlternatives_1(), "rule__AdviceStatement__Alternatives_1");
					put(grammarAccess.getAdvicePathElementAccess().getAlternatives(), "rule__AdvicePathElement__Alternatives");
					put(grammarAccess.getAdvicePathElementAccess().getNodeAlternatives_0_0_0(), "rule__AdvicePathElement__NodeAlternatives_0_0_0");
					put(grammarAccess.getRelationalExpressionAccess().getOperatorAlternatives_1_1_0(), "rule__RelationalExpression__OperatorAlternatives_1_1_0");
					put(grammarAccess.getAdditiveExpressionAccess().getNameAlternatives_1_1_0(), "rule__AdditiveExpression__NameAlternatives_1_1_0");
					put(grammarAccess.getMultiplicativeExpressionAccess().getNameAlternatives_1_1_0(), "rule__MultiplicativeExpression__NameAlternatives_1_1_0");
					put(grammarAccess.getUnaryOrInfixExpressionAccess().getAlternatives(), "rule__UnaryOrInfixExpression__Alternatives");
					put(grammarAccess.getUnaryExpressionAccess().getNameAlternatives_0_0(), "rule__UnaryExpression__NameAlternatives_0_0");
					put(grammarAccess.getPrimaryExpressionAccess().getAlternatives(), "rule__PrimaryExpression__Alternatives");
					put(grammarAccess.getLiteralAccess().getAlternatives(), "rule__Literal__Alternatives");
					put(grammarAccess.getBooleanLiteralAccess().getValAlternatives_0(), "rule__BooleanLiteral__ValAlternatives_0");
					put(grammarAccess.getVisibilityAccess().getAlternatives(), "rule__Visibility__Alternatives");
					put(grammarAccess.getAssertionScopeAccess().getAlternatives(), "rule__AssertionScope__Alternatives");
					put(grammarAccess.getBeeModelAccess().getGroup(), "rule__BeeModel__Group__0");
					put(grammarAccess.getQualifiedNameAccess().getGroup(), "rule__QualifiedName__Group__0");
					put(grammarAccess.getQualifiedNameAccess().getGroup_1(), "rule__QualifiedName__Group_1__0");
					put(grammarAccess.getCompoundNameAccess().getGroup(), "rule__CompoundName__Group__0");
					put(grammarAccess.getAlfanumSymAccess().getGroup(), "rule__AlfanumSym__Group__0");
					put(grammarAccess.getVersionRangeAccess().getGroup_0(), "rule__VersionRange__Group_0__0");
					put(grammarAccess.getVersionRangeAccess().getGroup_0_2(), "rule__VersionRange__Group_0_2__0");
					put(grammarAccess.getImportAccess().getGroup(), "rule__Import__Group__0");
					put(grammarAccess.getUnitAccess().getGroup(), "rule__Unit__Group__0");
					put(grammarAccess.getUnitAccess().getGroup_3(), "rule__Unit__Group_3__0");
					put(grammarAccess.getUnitAccess().getGroup_4(), "rule__Unit__Group_4__0");
					put(grammarAccess.getUnitAccess().getGroup_4_2(), "rule__Unit__Group_4_2__0");
					put(grammarAccess.getUnitAccess().getGroup_6_0(), "rule__Unit__Group_6_0__0");
					put(grammarAccess.getUnitAccess().getGroup_6_0_2(), "rule__Unit__Group_6_0_2__0");
					put(grammarAccess.getUnitAccess().getGroup_6_1(), "rule__Unit__Group_6_1__0");
					put(grammarAccess.getUnitAccess().getGroup_6_2(), "rule__Unit__Group_6_2__0");
					put(grammarAccess.getUnitAccess().getGroup_6_2_2(), "rule__Unit__Group_6_2_2__0");
					put(grammarAccess.getUnitAccess().getGroup_6_3(), "rule__Unit__Group_6_3__0");
					put(grammarAccess.getUnitAccess().getGroup_6_4(), "rule__Unit__Group_6_4__0");
					put(grammarAccess.getUnitAccess().getGroup_6_4_3(), "rule__Unit__Group_6_4_3__0");
					put(grammarAccess.getUnitAccess().getGroup_6_5(), "rule__Unit__Group_6_5__0");
					put(grammarAccess.getUnitAccess().getGroup_6_6(), "rule__Unit__Group_6_6__0");
					put(grammarAccess.getUnitAccess().getGroup_6_6_2_0(), "rule__Unit__Group_6_6_2_0__0");
					put(grammarAccess.getUnitAccess().getGroup_6_7(), "rule__Unit__Group_6_7__0");
					put(grammarAccess.getUnitAccess().getGroup_6_9(), "rule__Unit__Group_6_9__0");
					put(grammarAccess.getUnitAccess().getGroup_6_10(), "rule__Unit__Group_6_10__0");
					put(grammarAccess.getUnitAccess().getGroup_6_11(), "rule__Unit__Group_6_11__0");
					put(grammarAccess.getUnitAccess().getGroup_6_13(), "rule__Unit__Group_6_13__0");
					put(grammarAccess.getProvidedCapabilityAccess().getGroup(), "rule__ProvidedCapability__Group__0");
					put(grammarAccess.getProvidedCapabilityAccess().getGroup_0(), "rule__ProvidedCapability__Group_0__0");
					put(grammarAccess.getProvidedCapabilityAccess().getGroup_4(), "rule__ProvidedCapability__Group_4__0");
					put(grammarAccess.getRequiredCapabilityAccess().getGroup(), "rule__RequiredCapability__Group__0");
					put(grammarAccess.getRequiredCapabilityAccess().getGroup_0(), "rule__RequiredCapability__Group_0__0");
					put(grammarAccess.getRequiredCapabilityAccess().getGroup_4(), "rule__RequiredCapability__Group_4__0");
					put(grammarAccess.getStringPropertyAccess().getGroup_0(), "rule__StringProperty__Group_0__0");
					put(grammarAccess.getStringPropertyAccess().getGroup_1(), "rule__StringProperty__Group_1__0");
					put(grammarAccess.getStringPropertyAccess().getGroup_1_0(), "rule__StringProperty__Group_1_0__0");
					put(grammarAccess.getStringPropertyAccess().getGroup_1_0_1(), "rule__StringProperty__Group_1_0_1__0");
					put(grammarAccess.getStringProperty2Access().getGroup_0(), "rule__StringProperty2__Group_0__0");
					put(grammarAccess.getStringProperty2Access().getGroup_1(), "rule__StringProperty2__Group_1__0");
					put(grammarAccess.getStringProperty2Access().getGroup_1_0(), "rule__StringProperty2__Group_1_0__0");
					put(grammarAccess.getStringProperty2Access().getGroup_1_0_2(), "rule__StringProperty2__Group_1_0_2__0");
					put(grammarAccess.getSynchronizationAccess().getGroup(), "rule__Synchronization__Group__0");
					put(grammarAccess.getSynchronizationAccess().getGroup_1(), "rule__Synchronization__Group_1__0");
					put(grammarAccess.getSynchronizedPartAccess().getGroup_0(), "rule__SynchronizedPart__Group_0__0");
					put(grammarAccess.getSynchronizedPartAccess().getGroup_0_1(), "rule__SynchronizedPart__Group_0_1__0");
					put(grammarAccess.getArtifactsAccess().getGroup(), "rule__Artifacts__Group__0");
					put(grammarAccess.getArtifactsAccess().getGroup_3(), "rule__Artifacts__Group_3__0");
					put(grammarAccess.getArtifactsAccess().getGroup_3_2(), "rule__Artifacts__Group_3_2__0");
					put(grammarAccess.getPathGroupAccess().getGroup(), "rule__PathGroup__Group__0");
					put(grammarAccess.getPathGroupAccess().getGroup_0(), "rule__PathGroup__Group_0__0");
					put(grammarAccess.getPathGroupAccess().getGroup_1_0(), "rule__PathGroup__Group_1_0__0");
					put(grammarAccess.getPathGroupAccess().getGroup_1_0_1(), "rule__PathGroup__Group_1_0_1__0");
					put(grammarAccess.getPathGroupAccess().getGroup_1_1(), "rule__PathGroup__Group_1_1__0");
					put(grammarAccess.getPathGroupAccess().getGroup_1_1_3(), "rule__PathGroup__Group_1_1_3__0");
					put(grammarAccess.getPathGroupAccess().getGroup_1_2(), "rule__PathGroup__Group_1_2__0");
					put(grammarAccess.getPathGroupAccess().getGroup_1_2_2_0(), "rule__PathGroup__Group_1_2_2_0__0");
					put(grammarAccess.getPathGroupAccess().getGroup_1_3(), "rule__PathGroup__Group_1_3__0");
					put(grammarAccess.getPathAccess().getGroup_1(), "rule__Path__Group_1__0");
					put(grammarAccess.getPathAccess().getGroup_1_2(), "rule__Path__Group_1_2__0");
					put(grammarAccess.getGroupAccess().getGroup(), "rule__Group__Group__0");
					put(grammarAccess.getGroupAccess().getGroup_4(), "rule__Group__Group_4__0");
					put(grammarAccess.getGroupAccess().getGroup_4_2(), "rule__Group__Group_4_2__0");
					put(grammarAccess.getPrerequisiteAccess().getGroup(), "rule__Prerequisite__Group__0");
					put(grammarAccess.getPrerequisiteAccess().getGroup_0(), "rule__Prerequisite__Group_0__0");
					put(grammarAccess.getPrerequisiteAccess().getGroup_0_1(), "rule__Prerequisite__Group_0_1__0");
					put(grammarAccess.getPrerequisiteAccess().getGroup_0_2(), "rule__Prerequisite__Group_0_2__0");
					put(grammarAccess.getClosureAccess().getGroup(), "rule__Closure__Group__0");
					put(grammarAccess.getClosureAccess().getGroup_2_0(), "rule__Closure__Group_2_0__0");
					put(grammarAccess.getClosureAccess().getGroup_2_0_2_0(), "rule__Closure__Group_2_0_2_0__0");
					put(grammarAccess.getClosureAccess().getGroup_2_1(), "rule__Closure__Group_2_1__0");
					put(grammarAccess.getClosureAccess().getGroup_2_3(), "rule__Closure__Group_2_3__0");
					put(grammarAccess.getCapabilityReferencedPartAccess().getGroup_0(), "rule__CapabilityReferencedPart__Group_0__0");
					put(grammarAccess.getCapabilityReferencedPartAccess().getGroup_0_3(), "rule__CapabilityReferencedPart__Group_0_3__0");
					put(grammarAccess.getCapabilityReferencedPartAccess().getGroup_1(), "rule__CapabilityReferencedPart__Group_1__0");
					put(grammarAccess.getCapabilityReferencedPartAccess().getGroup_1_3(), "rule__CapabilityReferencedPart__Group_1_3__0");
					put(grammarAccess.getCompoundReferencesAccess().getGroup(), "rule__CompoundReferences__Group__0");
					put(grammarAccess.getActionAccess().getGroup(), "rule__Action__Group__0");
					put(grammarAccess.getActionAccess().getGroup_7(), "rule__Action__Group_7__0");
					put(grammarAccess.getActionAccess().getGroup_7_1(), "rule__Action__Group_7_1__0");
					put(grammarAccess.getActionAccess().getGroup_9(), "rule__Action__Group_9__0");
					put(grammarAccess.getActionAccess().getGroup_9_2(), "rule__Action__Group_9_2__0");
					put(grammarAccess.getParameterAccess().getGroup(), "rule__Parameter__Group__0");
					put(grammarAccess.getResultAccess().getGroup(), "rule__Result__Group__0");
					put(grammarAccess.getResultPartAccess().getGroup_1(), "rule__ResultPart__Group_1__0");
					put(grammarAccess.getBasicResultAccess().getGroup(), "rule__BasicResult__Group__0");
					put(grammarAccess.getBasicResultAccess().getGroup_0(), "rule__BasicResult__Group_0__0");
					put(grammarAccess.getResultGroupAccess().getGroup(), "rule__ResultGroup__Group__0");
					put(grammarAccess.getRepositoryConfigurationAccess().getGroup(), "rule__RepositoryConfiguration__Group__0");
					put(grammarAccess.getRepositoryConfigurationAccess().getGroup_0_1(), "rule__RepositoryConfiguration__Group_0_1__0");
					put(grammarAccess.getNamedAdviceAccess().getGroup(), "rule__NamedAdvice__Group__0");
					put(grammarAccess.getCompoundAdviceAccess().getGroup(), "rule__CompoundAdvice__Group__0");
					put(grammarAccess.getCompoundAdviceAccess().getGroup_1(), "rule__CompoundAdvice__Group_1__0");
					put(grammarAccess.getAdviceStatementAccess().getGroup(), "rule__AdviceStatement__Group__0");
					put(grammarAccess.getAdviceStatementAccess().getGroup_1_0(), "rule__AdviceStatement__Group_1_0__0");
					put(grammarAccess.getAdvicePathAccess().getGroup(), "rule__AdvicePath__Group__0");
					put(grammarAccess.getAdvicePathAccess().getGroup_2(), "rule__AdvicePath__Group_2__0");
					put(grammarAccess.getAdvicePathChildrenAccess().getGroup(), "rule__AdvicePathChildren__Group__0");
					put(grammarAccess.getAdvicePathElementAccess().getGroup_0(), "rule__AdvicePathElement__Group_0__0");
					put(grammarAccess.getAdvicePathElementAccess().getGroup_0_1(), "rule__AdvicePathElement__Group_0_1__0");
					put(grammarAccess.getWildcardNodeAccess().getGroup(), "rule__WildcardNode__Group__0");
					put(grammarAccess.getFilterAccess().getGroup(), "rule__Filter__Group__0");
					put(grammarAccess.getPreConditionAssertAccess().getGroup(), "rule__PreConditionAssert__Group__0");
					put(grammarAccess.getPostConditionAssertAccess().getGroup(), "rule__PostConditionAssert__Group__0");
					put(grammarAccess.getAssertionExpressionAccess().getGroup(), "rule__AssertionExpression__Group__0");
					put(grammarAccess.getAssertionExpressionAccess().getGroup_2(), "rule__AssertionExpression__Group_2__0");
					put(grammarAccess.getOrExpressionAccess().getGroup(), "rule__OrExpression__Group__0");
					put(grammarAccess.getOrExpressionAccess().getGroup_1(), "rule__OrExpression__Group_1__0");
					put(grammarAccess.getAndExpressionAccess().getGroup(), "rule__AndExpression__Group__0");
					put(grammarAccess.getAndExpressionAccess().getGroup_1(), "rule__AndExpression__Group_1__0");
					put(grammarAccess.getRelationalExpressionAccess().getGroup(), "rule__RelationalExpression__Group__0");
					put(grammarAccess.getRelationalExpressionAccess().getGroup_1(), "rule__RelationalExpression__Group_1__0");
					put(grammarAccess.getAdditiveExpressionAccess().getGroup(), "rule__AdditiveExpression__Group__0");
					put(grammarAccess.getAdditiveExpressionAccess().getGroup_1(), "rule__AdditiveExpression__Group_1__0");
					put(grammarAccess.getMultiplicativeExpressionAccess().getGroup(), "rule__MultiplicativeExpression__Group__0");
					put(grammarAccess.getMultiplicativeExpressionAccess().getGroup_1(), "rule__MultiplicativeExpression__Group_1__0");
					put(grammarAccess.getUnaryExpressionAccess().getGroup(), "rule__UnaryExpression__Group__0");
					put(grammarAccess.getInfixExpressionAccess().getGroup(), "rule__InfixExpression__Group__0");
					put(grammarAccess.getInfixExpressionAccess().getGroup_1(), "rule__InfixExpression__Group_1__0");
					put(grammarAccess.getInfixExpressionAccess().getGroup_1_4(), "rule__InfixExpression__Group_1_4__0");
					put(grammarAccess.getInfixExpressionAccess().getGroup_1_4_1(), "rule__InfixExpression__Group_1_4_1__0");
					put(grammarAccess.getParanthesizedExpressionAccess().getGroup(), "rule__ParanthesizedExpression__Group__0");
					put(grammarAccess.getBeeModelAccess().getImportsAssignment_0(), "rule__BeeModel__ImportsAssignment_0");
					put(grammarAccess.getBeeModelAccess().getBodyAssignment_1(), "rule__BeeModel__BodyAssignment_1");
					put(grammarAccess.getImportAccess().getImportClassAssignment_1(), "rule__Import__ImportClassAssignment_1");
					put(grammarAccess.getUnitAccess().getSynchronizedAssignment_0(), "rule__Unit__SynchronizedAssignment_0");
					put(grammarAccess.getUnitAccess().getNameAssignment_2(), "rule__Unit__NameAssignment_2");
					put(grammarAccess.getUnitAccess().getVersionAssignment_3_1(), "rule__Unit__VersionAssignment_3_1");
					put(grammarAccess.getUnitAccess().getImplementsAssignment_4_1(), "rule__Unit__ImplementsAssignment_4_1");
					put(grammarAccess.getUnitAccess().getImplementsAssignment_4_2_1(), "rule__Unit__ImplementsAssignment_4_2_1");
					put(grammarAccess.getUnitAccess().getProvidedCapabilityAssignment_6_0_2_0(), "rule__Unit__ProvidedCapabilityAssignment_6_0_2_0");
					put(grammarAccess.getUnitAccess().getProvidedCapabilityAssignment_6_1_1(), "rule__Unit__ProvidedCapabilityAssignment_6_1_1");
					put(grammarAccess.getUnitAccess().getRequiredCapabilitiesAssignment_6_2_2_0(), "rule__Unit__RequiredCapabilitiesAssignment_6_2_2_0");
					put(grammarAccess.getUnitAccess().getRequiredCapabilitiesAssignment_6_3_1(), "rule__Unit__RequiredCapabilitiesAssignment_6_3_1");
					put(grammarAccess.getUnitAccess().getMetaRequiredCapabilitiesAssignment_6_4_3_0(), "rule__Unit__MetaRequiredCapabilitiesAssignment_6_4_3_0");
					put(grammarAccess.getUnitAccess().getMetaRequiredCapabilitiesAssignment_6_5_2(), "rule__Unit__MetaRequiredCapabilitiesAssignment_6_5_2");
					put(grammarAccess.getUnitAccess().getUnsetPropertiesAssignment_6_6_2_0_1(), "rule__Unit__UnsetPropertiesAssignment_6_6_2_0_1");
					put(grammarAccess.getUnitAccess().getSetPropertiesAssignment_6_6_2_1(), "rule__Unit__SetPropertiesAssignment_6_6_2_1");
					put(grammarAccess.getUnitAccess().getUnsetPropertiesAssignment_6_7_2(), "rule__Unit__UnsetPropertiesAssignment_6_7_2");
					put(grammarAccess.getUnitAccess().getSetPropertiesAssignment_6_8(), "rule__Unit__SetPropertiesAssignment_6_8");
					put(grammarAccess.getUnitAccess().getAdviceAssignment_6_9_1(), "rule__Unit__AdviceAssignment_6_9_1");
					put(grammarAccess.getUnitAccess().getSynchronizeAssignment_6_10_2(), "rule__Unit__SynchronizeAssignment_6_10_2");
					put(grammarAccess.getUnitAccess().getSynchronizeAssignment_6_11_1(), "rule__Unit__SynchronizeAssignment_6_11_1");
					put(grammarAccess.getUnitAccess().getPartsAssignment_6_12(), "rule__Unit__PartsAssignment_6_12");
					put(grammarAccess.getUnitAccess().getRepositoryConfigAssignment_6_13_2(), "rule__Unit__RepositoryConfigAssignment_6_13_2");
					put(grammarAccess.getProvidedCapabilityAccess().getFilterAssignment_0_1(), "rule__ProvidedCapability__FilterAssignment_0_1");
					put(grammarAccess.getProvidedCapabilityAccess().getInterfaceAssignment_1(), "rule__ProvidedCapability__InterfaceAssignment_1");
					put(grammarAccess.getProvidedCapabilityAccess().getNameAssignment_3(), "rule__ProvidedCapability__NameAssignment_3");
					put(grammarAccess.getProvidedCapabilityAccess().getVersionAssignment_4_1(), "rule__ProvidedCapability__VersionAssignment_4_1");
					put(grammarAccess.getRequiredCapabilityAccess().getFilterAssignment_0_1(), "rule__RequiredCapability__FilterAssignment_0_1");
					put(grammarAccess.getRequiredCapabilityAccess().getInterfaceAssignment_1(), "rule__RequiredCapability__InterfaceAssignment_1");
					put(grammarAccess.getRequiredCapabilityAccess().getNameAssignment_3(), "rule__RequiredCapability__NameAssignment_3");
					put(grammarAccess.getRequiredCapabilityAccess().getRangeAssignment_4_1(), "rule__RequiredCapability__RangeAssignment_4_1");
					put(grammarAccess.getStringPropertyAccess().getImmutableAssignment_0_0(), "rule__StringProperty__ImmutableAssignment_0_0");
					put(grammarAccess.getStringPropertyAccess().getKeyAssignment_0_1(), "rule__StringProperty__KeyAssignment_0_1");
					put(grammarAccess.getStringPropertyAccess().getValueAssignment_0_3(), "rule__StringProperty__ValueAssignment_0_3");
					put(grammarAccess.getStringPropertyAccess().getKeyAssignment_1_0_0(), "rule__StringProperty__KeyAssignment_1_0_0");
					put(grammarAccess.getStringPropertyAccess().getValueAssignment_1_0_1_1(), "rule__StringProperty__ValueAssignment_1_0_1_1");
					put(grammarAccess.getStringProperty2Access().getImmutableAssignment_0_0(), "rule__StringProperty2__ImmutableAssignment_0_0");
					put(grammarAccess.getStringProperty2Access().getKeyAssignment_0_2(), "rule__StringProperty2__KeyAssignment_0_2");
					put(grammarAccess.getStringProperty2Access().getValueAssignment_0_4(), "rule__StringProperty2__ValueAssignment_0_4");
					put(grammarAccess.getStringProperty2Access().getKeyAssignment_1_0_1(), "rule__StringProperty2__KeyAssignment_1_0_1");
					put(grammarAccess.getStringProperty2Access().getValueAssignment_1_0_2_1(), "rule__StringProperty2__ValueAssignment_1_0_2_1");
					put(grammarAccess.getSynchronizationAccess().getPartrefsAssignment_0(), "rule__Synchronization__PartrefsAssignment_0");
					put(grammarAccess.getSynchronizationAccess().getPartrefsAssignment_1_1(), "rule__Synchronization__PartrefsAssignment_1_1");
					put(grammarAccess.getArtifactsAccess().getVisibilityAssignment_0(), "rule__Artifacts__VisibilityAssignment_0");
					put(grammarAccess.getArtifactsAccess().getNameAssignment_2(), "rule__Artifacts__NameAssignment_2");
					put(grammarAccess.getArtifactsAccess().getProvidedCapabilitiesAssignment_3_1(), "rule__Artifacts__ProvidedCapabilitiesAssignment_3_1");
					put(grammarAccess.getArtifactsAccess().getProvidedCapabilitiesAssignment_3_2_1(), "rule__Artifacts__ProvidedCapabilitiesAssignment_3_2_1");
					put(grammarAccess.getArtifactsAccess().getAssertsAssignment_4(), "rule__Artifacts__AssertsAssignment_4");
					put(grammarAccess.getArtifactsAccess().getPathsAssignment_6(), "rule__Artifacts__PathsAssignment_6");
					put(grammarAccess.getPathGroupAccess().getFilterAssignment_0_1(), "rule__PathGroup__FilterAssignment_0_1");
					put(grammarAccess.getPathGroupAccess().getPathsAssignment_1_0_0(), "rule__PathGroup__PathsAssignment_1_0_0");
					put(grammarAccess.getPathGroupAccess().getPathsAssignment_1_0_1_1(), "rule__PathGroup__PathsAssignment_1_0_1_1");
					put(grammarAccess.getPathGroupAccess().getBasePathAssignment_1_1_0(), "rule__PathGroup__BasePathAssignment_1_1_0");
					put(grammarAccess.getPathGroupAccess().getPathsAssignment_1_1_2(), "rule__PathGroup__PathsAssignment_1_1_2");
					put(grammarAccess.getPathGroupAccess().getPathsAssignment_1_1_3_1(), "rule__PathGroup__PathsAssignment_1_1_3_1");
					put(grammarAccess.getPathGroupAccess().getUnsetPropertiesAssignment_1_2_2_0_1(), "rule__PathGroup__UnsetPropertiesAssignment_1_2_2_0_1");
					put(grammarAccess.getPathGroupAccess().getSetPropertiesAssignment_1_2_2_1(), "rule__PathGroup__SetPropertiesAssignment_1_2_2_1");
					put(grammarAccess.getPathGroupAccess().getUnsetPropertiesAssignment_1_3_2(), "rule__PathGroup__UnsetPropertiesAssignment_1_3_2");
					put(grammarAccess.getPathGroupAccess().getSetPropertiesAssignment_1_4(), "rule__PathGroup__SetPropertiesAssignment_1_4");
					put(grammarAccess.getGroupAccess().getVisibilityAssignment_0(), "rule__Group__VisibilityAssignment_0");
					put(grammarAccess.getGroupAccess().getSynchronizedAssignment_1(), "rule__Group__SynchronizedAssignment_1");
					put(grammarAccess.getGroupAccess().getNameAssignment_3(), "rule__Group__NameAssignment_3");
					put(grammarAccess.getGroupAccess().getProvidedCapabilitiesAssignment_4_1(), "rule__Group__ProvidedCapabilitiesAssignment_4_1");
					put(grammarAccess.getGroupAccess().getProvidedCapabilitiesAssignment_4_2_1(), "rule__Group__ProvidedCapabilitiesAssignment_4_2_1");
					put(grammarAccess.getGroupAccess().getAssertsAssignment_5(), "rule__Group__AssertsAssignment_5");
					put(grammarAccess.getGroupAccess().getAssertsAssignment_6(), "rule__Group__AssertsAssignment_6");
					put(grammarAccess.getGroupAccess().getPrerequisitesAssignment_8(), "rule__Group__PrerequisitesAssignment_8");
					put(grammarAccess.getPrerequisiteAccess().getSurpressedAssignment_0_0(), "rule__Prerequisite__SurpressedAssignment_0_0");
					put(grammarAccess.getPrerequisiteAccess().getFilterAssignment_0_1_1(), "rule__Prerequisite__FilterAssignment_0_1_1");
					put(grammarAccess.getPrerequisiteAccess().getAliasAssignment_0_2_0(), "rule__Prerequisite__AliasAssignment_0_2_0");
					put(grammarAccess.getPrerequisiteAccess().getPartReferenceAssignment_0_3(), "rule__Prerequisite__PartReferenceAssignment_0_3");
					put(grammarAccess.getPrerequisiteAccess().getClosureAssignment_1(), "rule__Prerequisite__ClosureAssignment_1");
					put(grammarAccess.getClosureAccess().getUnsetPropertiesAssignment_2_0_2_0_1(), "rule__Closure__UnsetPropertiesAssignment_2_0_2_0_1");
					put(grammarAccess.getClosureAccess().getSetPropertiesAssignment_2_0_2_1(), "rule__Closure__SetPropertiesAssignment_2_0_2_1");
					put(grammarAccess.getClosureAccess().getUnsetPropertiesAssignment_2_1_2(), "rule__Closure__UnsetPropertiesAssignment_2_1_2");
					put(grammarAccess.getClosureAccess().getSetPropertiesAssignment_2_2(), "rule__Closure__SetPropertiesAssignment_2_2");
					put(grammarAccess.getClosureAccess().getAdviceAssignment_2_3_1(), "rule__Closure__AdviceAssignment_2_3_1");
					put(grammarAccess.getPartInSelfAccess().getPartNameAssignment(), "rule__PartInSelf__PartNameAssignment");
					put(grammarAccess.getCapabilityReferencedPartAccess().getInterfaceAssignment_0_0(), "rule__CapabilityReferencedPart__InterfaceAssignment_0_0");
					put(grammarAccess.getCapabilityReferencedPartAccess().getNameAssignment_0_2(), "rule__CapabilityReferencedPart__NameAssignment_0_2");
					put(grammarAccess.getCapabilityReferencedPartAccess().getRangeAssignment_0_3_1(), "rule__CapabilityReferencedPart__RangeAssignment_0_3_1");
					put(grammarAccess.getCapabilityReferencedPartAccess().getPartNameAssignment_0_5(), "rule__CapabilityReferencedPart__PartNameAssignment_0_5");
					put(grammarAccess.getCapabilityReferencedPartAccess().getInterfaceAssignment_1_0(), "rule__CapabilityReferencedPart__InterfaceAssignment_1_0");
					put(grammarAccess.getCapabilityReferencedPartAccess().getNameAssignment_1_2(), "rule__CapabilityReferencedPart__NameAssignment_1_2");
					put(grammarAccess.getCapabilityReferencedPartAccess().getRangeAssignment_1_3_1(), "rule__CapabilityReferencedPart__RangeAssignment_1_3_1");
					put(grammarAccess.getCompoundReferencesAccess().getPrerequisitesAssignment_1(), "rule__CompoundReferences__PrerequisitesAssignment_1");
					put(grammarAccess.getActionAccess().getVisibilityAssignment_0(), "rule__Action__VisibilityAssignment_0");
					put(grammarAccess.getActionAccess().getSynchronizedAssignment_1(), "rule__Action__SynchronizedAssignment_1");
					put(grammarAccess.getActionAccess().getActorParametersAssignment_7_0(), "rule__Action__ActorParametersAssignment_7_0");
					put(grammarAccess.getActionAccess().getActorParametersAssignment_7_1_1(), "rule__Action__ActorParametersAssignment_7_1_1");
					put(grammarAccess.getActionAccess().getProvidedCapabilitiesAssignment_9_1(), "rule__Action__ProvidedCapabilitiesAssignment_9_1");
					put(grammarAccess.getActionAccess().getProvidedCapabilitiesAssignment_9_2_1(), "rule__Action__ProvidedCapabilitiesAssignment_9_2_1");
					put(grammarAccess.getActionAccess().getAssertsAssignment_10(), "rule__Action__AssertsAssignment_10");
					put(grammarAccess.getActionAccess().getAssertsAssignment_11(), "rule__Action__AssertsAssignment_11");
					put(grammarAccess.getActionAccess().getResultGroupsAssignment_13(), "rule__Action__ResultGroupsAssignment_13");
					put(grammarAccess.getParameterAccess().getNameAssignment_0(), "rule__Parameter__NameAssignment_0");
					put(grammarAccess.getParameterAccess().getValueAssignment_2(), "rule__Parameter__ValueAssignment_2");
					put(grammarAccess.getResultAccess().getResultAssignment_0(), "rule__Result__ResultAssignment_0");
					put(grammarAccess.getResultAccess().getGroupAssignment_1(), "rule__Result__GroupAssignment_1");
					put(grammarAccess.getResultPartAccess().getResultAssignment_1_1(), "rule__ResultPart__ResultAssignment_1_1");
					put(grammarAccess.getBasicResultAccess().getFilterAssignment_0_1(), "rule__BasicResult__FilterAssignment_0_1");
					put(grammarAccess.getBasicResultAccess().getVisibilityAssignment_1(), "rule__BasicResult__VisibilityAssignment_1");
					put(grammarAccess.getBasicResultAccess().getNameAssignment_3(), "rule__BasicResult__NameAssignment_3");
					put(grammarAccess.getBasicResultAccess().getAssertsAssignment_4(), "rule__BasicResult__AssertsAssignment_4");
					put(grammarAccess.getBasicResultAccess().getAssertsAssignment_5(), "rule__BasicResult__AssertsAssignment_5");
					put(grammarAccess.getBasicResultAccess().getPathsAssignment_7(), "rule__BasicResult__PathsAssignment_7");
					put(grammarAccess.getBasicResultAccess().getClosureAssignment_9(), "rule__BasicResult__ClosureAssignment_9");
					put(grammarAccess.getResultGroupAccess().getAssertsAssignment_1(), "rule__ResultGroup__AssertsAssignment_1");
					put(grammarAccess.getResultGroupAccess().getAssertsAssignment_2(), "rule__ResultGroup__AssertsAssignment_2");
					put(grammarAccess.getResultGroupAccess().getPrerequisitesAssignment_4(), "rule__ResultGroup__PrerequisitesAssignment_4");
					put(grammarAccess.getResultGroupAccess().getClosureAssignment_6(), "rule__ResultGroup__ClosureAssignment_6");
					put(grammarAccess.getRepositoryConfigurationAccess().getLocationAssignment_0_0(), "rule__RepositoryConfiguration__LocationAssignment_0_0");
					put(grammarAccess.getRepositoryConfigurationAccess().getResolverClassAssignment_0_1_1(), "rule__RepositoryConfiguration__ResolverClassAssignment_0_1_1");
					put(grammarAccess.getRepositoryConfigurationAccess().getAdviceAssignment_1(), "rule__RepositoryConfiguration__AdviceAssignment_1");
					put(grammarAccess.getNamedAdviceAccess().getNameAssignment_0(), "rule__NamedAdvice__NameAssignment_0");
					put(grammarAccess.getNamedAdviceAccess().getAdviceAssignment_1(), "rule__NamedAdvice__AdviceAssignment_1");
					put(grammarAccess.getCompoundAdviceAccess().getAdviceAssignment_1_0(), "rule__CompoundAdvice__AdviceAssignment_1_0");
					put(grammarAccess.getAdviceStatementAccess().getPathAssignment_0(), "rule__AdviceStatement__PathAssignment_0");
					put(grammarAccess.getAdviceStatementAccess().getValueAssignment_1_0_1(), "rule__AdviceStatement__ValueAssignment_1_0_1");
					put(grammarAccess.getAdviceStatementAccess().getAdviceAssignment_1_1(), "rule__AdviceStatement__AdviceAssignment_1_1");
					put(grammarAccess.getAdvicePathAccess().getPathElementsAssignment_0(), "rule__AdvicePath__PathElementsAssignment_0");
					put(grammarAccess.getAdvicePathAccess().getPathElementsAssignment_1(), "rule__AdvicePath__PathElementsAssignment_1");
					put(grammarAccess.getAdvicePathAccess().getPathElementsAssignment_2_0(), "rule__AdvicePath__PathElementsAssignment_2_0");
					put(grammarAccess.getAdvicePathAccess().getPathElementAssignment_2_1(), "rule__AdvicePath__PathElementAssignment_2_1");
					put(grammarAccess.getAdvicePathElementAccess().getNodeAssignment_0_0(), "rule__AdvicePathElement__NodeAssignment_0_0");
					put(grammarAccess.getAdvicePathElementAccess().getPredicateAssignment_0_1_1(), "rule__AdvicePathElement__PredicateAssignment_0_1_1");
					put(grammarAccess.getAdvicePathElementAccess().getNodeAssignment_1(), "rule__AdvicePathElement__NodeAssignment_1");
					put(grammarAccess.getFilterAccess().getPredicateAssignment_1(), "rule__Filter__PredicateAssignment_1");
					put(grammarAccess.getPreConditionAssertAccess().getScopeAssignment_0(), "rule__PreConditionAssert__ScopeAssignment_0");
					put(grammarAccess.getPreConditionAssertAccess().getAssertsAssignment_2(), "rule__PreConditionAssert__AssertsAssignment_2");
					put(grammarAccess.getPostConditionAssertAccess().getScopeAssignment_0(), "rule__PostConditionAssert__ScopeAssignment_0");
					put(grammarAccess.getPostConditionAssertAccess().getAssertsAssignment_2(), "rule__PostConditionAssert__AssertsAssignment_2");
					put(grammarAccess.getAssertionExpressionAccess().getExprAssignment_1(), "rule__AssertionExpression__ExprAssignment_1");
					put(grammarAccess.getAssertionExpressionAccess().getMessageAssignment_2_1(), "rule__AssertionExpression__MessageAssignment_2_1");
					put(grammarAccess.getValueExpressionAccess().getValueAssignment(), "rule__ValueExpression__ValueAssignment");
					put(grammarAccess.getOrExpressionAccess().getOperatorAssignment_1_1(), "rule__OrExpression__OperatorAssignment_1_1");
					put(grammarAccess.getOrExpressionAccess().getRightAssignment_1_2(), "rule__OrExpression__RightAssignment_1_2");
					put(grammarAccess.getAndExpressionAccess().getOperatorAssignment_1_1(), "rule__AndExpression__OperatorAssignment_1_1");
					put(grammarAccess.getAndExpressionAccess().getRightAssignment_1_2(), "rule__AndExpression__RightAssignment_1_2");
					put(grammarAccess.getRelationalExpressionAccess().getOperatorAssignment_1_1(), "rule__RelationalExpression__OperatorAssignment_1_1");
					put(grammarAccess.getRelationalExpressionAccess().getRightAssignment_1_2(), "rule__RelationalExpression__RightAssignment_1_2");
					put(grammarAccess.getAdditiveExpressionAccess().getNameAssignment_1_1(), "rule__AdditiveExpression__NameAssignment_1_1");
					put(grammarAccess.getAdditiveExpressionAccess().getParamsAssignment_1_2(), "rule__AdditiveExpression__ParamsAssignment_1_2");
					put(grammarAccess.getMultiplicativeExpressionAccess().getNameAssignment_1_1(), "rule__MultiplicativeExpression__NameAssignment_1_1");
					put(grammarAccess.getMultiplicativeExpressionAccess().getParamsAssignment_1_2(), "rule__MultiplicativeExpression__ParamsAssignment_1_2");
					put(grammarAccess.getUnaryExpressionAccess().getNameAssignment_0(), "rule__UnaryExpression__NameAssignment_0");
					put(grammarAccess.getUnaryExpressionAccess().getParamsAssignment_1(), "rule__UnaryExpression__ParamsAssignment_1");
					put(grammarAccess.getInfixExpressionAccess().getNameAssignment_1_2(), "rule__InfixExpression__NameAssignment_1_2");
					put(grammarAccess.getInfixExpressionAccess().getParamsAssignment_1_4_0(), "rule__InfixExpression__ParamsAssignment_1_4_0");
					put(grammarAccess.getInfixExpressionAccess().getParamsAssignment_1_4_1_1(), "rule__InfixExpression__ParamsAssignment_1_4_1_1");
					put(grammarAccess.getBooleanLiteralAccess().getValAssignment(), "rule__BooleanLiteral__ValAssignment");
					put(grammarAccess.getIntegerLiteralAccess().getValAssignment(), "rule__IntegerLiteral__ValAssignment");
					put(grammarAccess.getNullLiteralAccess().getValAssignment(), "rule__NullLiteral__ValAssignment");
					put(grammarAccess.getStringLiteralAccess().getValAssignment(), "rule__StringLiteral__ValAssignment");
					put(grammarAccess.getGlobalVarExpressionAccess().getNameAssignment(), "rule__GlobalVarExpression__NameAssignment");
				}
			};
		}
		return nameMappings.get(element);
	}
	
	@Override
	protected Collection<FollowElement> getFollowElements(AbstractInternalContentAssistParser parser) {
		try {
			org.eclipse.b3.contentassist.antlr.internal.InternalBeeLangParser typedParser = (org.eclipse.b3.contentassist.antlr.internal.InternalBeeLangParser) parser;
			typedParser.entryRuleBeeModel();
			return typedParser.getFollowElements();
		} catch(RecognitionException ex) {
			throw new RuntimeException(ex);
		}		
	}
	
	@Override
	protected String[] getInitialHiddenTokens() {
		return new String[] { "RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT" };
	}
	
	public BeeLangGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}
	
	public void setGrammarAccess(BeeLangGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}
