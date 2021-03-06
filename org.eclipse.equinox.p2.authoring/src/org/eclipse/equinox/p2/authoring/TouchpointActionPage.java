/*******************************************************************************
 * Copyright (c) 2008
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the individual
 * copyright holders listed below, as Initial Contributors under such license.
 * The text of such license is available at 
 * http://www.eclipse.org/legal/epl-v10.html.
 * 
 * Contributors:
 * 		Henrik Lindberg
 *******************************************************************************/

package org.eclipse.equinox.p2.authoring;

import java.util.EventObject;

import org.eclipse.equinox.p2.authoring.forms.EditAdapter;
import org.eclipse.equinox.p2.authoring.forms.Mutator;
import org.eclipse.equinox.p2.authoring.forms.RichDetailsPage;
import org.eclipse.equinox.p2.authoring.forms.validators.IEditValidator;
import org.eclipse.equinox.p2.authoring.forms.validators.NullValidator;
import org.eclipse.equinox.p2.authoring.internal.IEditEventBusProvider;
import org.eclipse.equinox.p2.authoring.internal.IEditorEventBus;
import org.eclipse.equinox.p2.authoring.internal.IEditorListener;
import org.eclipse.equinox.p2.authoring.internal.InstallableUnitBuilder;
import org.eclipse.equinox.p2.authoring.internal.ModelChangeEvent;
import org.eclipse.equinox.p2.authoring.internal.InstallableUnitBuilder.Parameter;
import org.eclipse.equinox.p2.authoring.internal.InstallableUnitBuilder.TouchpointActionBuilder;
import org.eclipse.equinox.p2.authoring.internal.InstallableUnitBuilder.TouchpointTypeBuilder;
import org.eclipse.equinox.p2.authoring.spi.ITouchpointActionDescriptor;
import org.eclipse.equinox.p2.authoring.spi.ITouchpointActionParameterDescriptor;
import org.eclipse.equinox.p2.authoring.spi.ITouchpointTypeDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * A Detail page for p2 Touchpoint Action tree nodes.
 * 
 * @author Henrik Lindberg
 * 
 */
public class TouchpointActionPage extends RichDetailsPage

{
	private static final String ACTION_TEXT = "actionText";
	
	/** The currently selected input. */
	private TouchpointActionBuilder m_input;
	
	private static int MAX_PARAMETERS = 5;
	/** The dynamic labels, changed depending on the displayed action */
	private Label m_labels[] = new Label[MAX_PARAMETERS];

	/** The dynamic labels, changed depending on the displayed action */
	private Text m_texts[] = new Text[MAX_PARAMETERS];
	
	/** Parameter info from meta data descriptors, updated based on selected type, and
	 * selected action.
	 */
	private ParameterInfo m_params[]; 
	
	/** Composite that needs re-layout when labels are changed. */
	private Composite m_sectionClient;
	
	/** The current action descriptor. */
	private ITouchpointActionDescriptor m_actionDesc;
	
	/** A label showing a warning message if the selected action is not applicable for the touchpoint type */
	private Label m_warningLabel;
	
	/** The validator to use for parameter values */
	private ParameterValidator m_parameterValidator;

	private TouchpointTypeBuilder m_lastTouchpointType;

	public TouchpointActionPage()
	{
		// initialize the parameter info with default stuff
		m_params = new ParameterInfo[MAX_PARAMETERS];
		for(int i = 0; i < m_params.length;i++)
			m_params[i] = new ParameterInfo("param"+Integer.toString(i+1));
		m_parameterValidator = new ParameterValidator();
	}

	public void createContents(Composite parent)
	{
		TableWrapLayout lo = new TableWrapLayout();
		lo.leftMargin = 0;
		lo.rightMargin = 0;
		lo.topMargin = 0;
		lo.numColumns = 1;
		parent.setLayout(lo);
		
		FormToolkit toolkit = m_mform.getToolkit();

		Section section = toolkit.createSection(parent, //
				// Section.DESCRIPTION | //
						Section.TITLE_BAR | //
						Section.EXPANDED);

		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP) ;
		td.colspan = 1;
		section.setLayoutData(td);
		section.setText("Touchpoint Action");
		m_sectionClient = toolkit.createComposite(section);
		GridLayout layout = new GridLayout(2, false);
		m_sectionClient.setLayout(layout);

		FormColors colors = toolkit.getColors();
		Color headerColor = colors.getColor("org.eclipse.ui.forms.TITLE");
		
		// Include a label that is used to signal that this action is not described in the 
		// currently selected touchpoint.
		m_warningLabel = toolkit.createLabel(m_sectionClient, "");
		m_warningLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP,true,false,2,1));
		m_warningLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		m_warningLabel.setText("Action not applicable to selected touchpoint type.");
		m_warningLabel.setVisible(false);
		
		// -- ACTION NAME
		Label label = toolkit.createLabel(m_sectionClient, "Action:");
		label.setForeground(headerColor);
		Text actionText = toolkit.createText(m_sectionClient, "");
		actionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		m_editAdapters.createEditAdapter(ACTION_TEXT, actionText, //$NON-NLS-1$
				NullValidator.instance(),
				new Mutator()
				{
					@Override
					public String getValue()
					{
						return m_input != null && m_input.getActionKey() != null
								? m_input.getActionKey()
								: ""; //$NON-NLS-1$
					}

					@Override
					public void setValue(String input) throws Exception
					{
						// Do nothing - action text can not be changed
					}
				});
		// The action key/action name can not be changed - disable it
		m_editAdapters.getAdapter(ACTION_TEXT).setEnabled(false);
		
		// -- LABEL TEXT
		for(int i = 0; i < MAX_PARAMETERS; i++)
		{
		m_labels[i] = toolkit.createLabel(m_sectionClient, m_params[i].label + ":");
		m_labels[i].setForeground(headerColor);
		m_texts[i] = toolkit.createText(m_sectionClient, "");
		m_texts[i].setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		m_editAdapters.createEditAdapter(getIndexedEditAdapterKey(i), m_texts[i], //$NON-NLS-1$
				m_parameterValidator,
				new IndexedMutator(i));
		}
			
		section.setClient(m_sectionClient);
		
		// Add listener to touchpoint type change so that labels and errors can be displayed
		//
		IEditorEventBus eventBus = ((IEditEventBusProvider)toolkit).getEventBus();
		eventBus.addListener(new IEditorListener(){

			public void notify(EventObject o)
			{
				if(!(o instanceof ModelChangeEvent))
					return;
				Object detail = ((ModelChangeEvent)o).getDetail();
				if(detail instanceof InstallableUnitBuilder)
				{
					TouchpointTypeBuilder type = ((InstallableUnitBuilder)detail).getTouchpointType();
					if(type != m_lastTouchpointType)
						refreshLabels();
				}
			}
			
		});
		

	}
	private String getIndexedEditAdapterKey(int index)
	{
		return "text"+Integer.toString(index);
	}
	
	/**
	 * Mutator for an indexed parameter
	 * @author Henrik Lindberg
	 *
	 */
	private class IndexedMutator extends Mutator
	{
		private final int m_index;
		IndexedMutator(int index)
		{
			m_index = index;
		}
		@Override
		public String getValue()
		{
			// disabled fields are not in use.
			if(!m_editAdapters.getAdapter(getIndexedEditAdapterKey(m_index)).isEnabled())
				return "";
			return m_input != null && m_input.getParameter(m_params[m_index].name) != null
					? m_input.getParameter(m_params[m_index].name)
					: ""; //$NON-NLS-1$
		}

		@Override
		public void setValue(String input) throws Exception
		{
			if(m_input == null)
				return;
			// disabled fields are not in use
			if(!m_editAdapters.getAdapter(getIndexedEditAdapterKey(m_index)).isEnabled())
				return;
			m_input.setParameter(m_params[m_index].name, input == null ? "" : input); //$NON-NLS-1$
		}
	}
	
	/**
	 * The ParameterValidator filters out "," from the input.
	 * @author Henrik Lindberg
	 */
	public class ParameterValidator implements IEditValidator
	{

		public String inputFilter(String input)
		{
			if(input.indexOf(',') == -1)
				return null;
			else return input.replace(",", "");
		}

		public boolean isValid(String input, EditAdapter editAdapter)
		{
			editAdapter.clearMessages();
			return true;
		}	
	}

	public void setFocus()
	{
		// sets focus on first parameter (may be hidden - but that is ok).
		m_texts[0].setFocus();
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection)
	{
		IStructuredSelection ssel = (IStructuredSelection)selection;
		m_input = null; // clear old input
		if(ssel.size() == 1 && ssel.getFirstElement() instanceof TouchpointActionBuilder)
		{
			m_input = (TouchpointActionBuilder)ssel.getFirstElement();
			refreshLabels();
		}
		refresh();
	}
	/**
	 * Update the labels to reflect the action parameters, and hide unused parameter fields
	 */
	private void refreshLabels()
	{		
		Parameter[] parameters = m_input.getParameters();
		
		IFormPage formPage = (IFormPage)m_mform.getContainer();
		m_lastTouchpointType = ((InstallableUnitEditor)formPage.getEditor()).getInstallableUnit().getTouchpointType();
		ITouchpointTypeDescriptor desc = P2AuthoringUIPlugin.getDefault().getTouchpointType(m_lastTouchpointType);
		m_actionDesc = desc.getActionDescriptor(m_input.getActionKey());
		
		int i = 0;
		for(; i < parameters.length && i < MAX_PARAMETERS; i++)
		{
			// the param name is always from the IU data.
			m_params[i].name = parameters[i].getName();

			// pick up label and type from meta data descriptor
			if(m_actionDesc != null)
			{
				ITouchpointActionParameterDescriptor paramDesc = m_actionDesc.getParameter(parameters[i].getName());
				m_params[i].label = paramDesc.getLabel();
				m_params[i].type = paramDesc.getType();
			}
			else
			{
				m_params[i].label = m_params[i].name;
				m_params[i].type = ITouchpointActionParameterDescriptor.TYPE_STRING;
			}
			m_warningLabel.setVisible(m_actionDesc == null);

			// TODO: set the validation type for the text field
			// TODO: hook advanced (browse) function to applicable types
			m_labels[i].setText(m_params[i].label + ":");
			m_labels[i].setVisible(true);
			m_texts[i].setVisible(true);
			m_editAdapters.getAdapter(getIndexedEditAdapterKey(i)).setEnabled(true);
		}
		for(; i < MAX_PARAMETERS; i++)
		{
			m_labels[i].setVisible(false);
			m_texts[i].setVisible(false);
			m_editAdapters.getAdapter(getIndexedEditAdapterKey(i)).setEnabled(false);
		}
		// Labels may have different width
		m_sectionClient.layout();
	}
	/** Convenient structure for parameter info */
	private static class ParameterInfo
	{
		public String label;
		public String name;
		public String type; 
		ParameterInfo(String paramName)
		{
			label = name = paramName;
			type = ITouchpointActionParameterDescriptor.TYPE_STRING;
		}
	}
}
