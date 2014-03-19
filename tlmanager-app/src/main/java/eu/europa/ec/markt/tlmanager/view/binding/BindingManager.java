/*
 * DSS - Digital Signature Services
 *
 * Copyright (C) 2013 European Commission, Directorate-General Internal Market and Services (DG MARKT), B-1049 Bruxelles/Brussel
 *
 * Developed by: 2013 ARHS Developments S.A. (rue Nicolas Bové 2B, L-1253 Luxembourg) http://www.arhs-developments.com
 *
 * This file is part of the "DSS - Digital Signature Services" project.
 *
 * "DSS - Digital Signature Services" is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * DSS is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * "DSS - Digital Signature Services".  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.markt.tlmanager.view.binding;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Binding.SyncFailure;
import org.jdesktop.beansbinding.Binding.ValueResult;
import org.jdesktop.beansbinding.BindingListener;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.Converter;
import org.jdesktop.beansbinding.PropertyStateEvent;
import org.jdesktop.beansbinding.Validator;
import org.jdesktop.swingx.JXDatePicker;

import eu.europa.ec.markt.dss.DSSUtils;
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.certificate.CertificateProperty;
import eu.europa.ec.markt.tlmanager.view.common.DateTimePicker;
import eu.europa.ec.markt.tlmanager.view.common.DefaultDocumentListener;
import eu.europa.ec.markt.tlmanager.view.multivalue.MandatoryLabelHandler;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultivaluePanel;

/**
 * A class for organising the work with bindings.
 * Each binding has a name that serves as key in a <code>HashMap</code>.
 *
 * @version $Revision$ - $Date$
 */

public class BindingManager implements BindingListener {

    /**
     * The Constant LOG.
     */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BindingManager.class);

    /**
     * The bindings.
     */
    private Map<String, Binding> bindings;

    /**
     * The Constant BP_TEXT.
     */
    private static final BeanProperty BP_TEXT = BeanProperty.create("text"); // JTextField

    /**
     * The Constant BP_COMBO.
     */
    private static final BeanProperty BP_COMBO = BeanProperty.create("selectedItem"); // JComboBox

    /**
     * The Constant BP_MVALUE.
     */
    private static final BeanProperty BP_MVALUE = BeanProperty.create("multivalueModel"); // MultivaluePanel

    /**
     * The Constant BP_DATEPICKER.
     */
    private static final BeanProperty BP_DATEPICKER = BeanProperty.create("date"); // JXDatePicker

    /**
     * The Constant BP_DATETIMEPICKER.
     */
    private static final BeanProperty BP_DATETIMEPICKER = BeanProperty.create("dateTime"); // DateTimePicker

    /**
     * The Constant BP_CHECKBOX.
     */
    private static final BeanProperty BP_CHECKBOX = BeanProperty.create("selected"); // JCheckBox

    /**
     * The Constant BP_CERTIFICATE_PROPERTY.
     */
    private static final BeanProperty BP_CERTIFICATE_PROPERTY = BeanProperty.create("certificatePropertyModel"); // CertificateProperty

    private MandatoryLabelHandler labelHandler;

    private boolean bindingInProgress;

    /**
     * The default constructor for BindingManager.
     *
     * @param labelHandler
     */
    public BindingManager(MandatoryLabelHandler labelHandler) {
        this.labelHandler = labelHandler;
        init();
    }

    /**
     * Inits the bindings.
     */
    private void init() {
        if (bindings == null) {
            bindings = new HashMap<String, Binding>();
        }
    }

    /**
     * Retrieve binding.
     *
     * @param name the name
     * @return the binding
     */
    private Binding retrieveBinding(String name) {
        Binding binding = bindings.get(name);
        if (binding == null) {
            LOG.error("Binding not found for name: " + name);
        }

        return binding;
    }

    /**
     * Amends the source for a previously created binding.
     *
     * @param source the new source object
     * @param name   the name of the binding
     */
    public void amendSourceForBinding(Object source, String name) {
        bindingInProgress = true;
        if (source == null) {
            LOG.error(">>>Bound object may not be null: " + name);
        }
        Binding binding = retrieveBinding(name);
        if (binding != null) {
            binding.setSourceObject(source);

            LOG.trace("Bind " + source + " (" + name + ")");
            ValueResult sourceValueForTarget = binding.getSourceValueForTarget();
            if (sourceValueForTarget.failed()) {
                SyncFailure failure = sourceValueForTarget.getFailure();
                LOG.error(">>>>>>Binding failure");
                if (failure != null) {
                    LOG.error(">>>" + failure.getType() + " for " + name);
                }
                LOG.error(">>>>>>");
            }
        }
    }

    /**
     * Determine bean property for a component and sets it up with appropriate listeners.
     *
     * @param component the component
     * @return the bean property
     */
    private BeanProperty setupComponent(final JComponent component) {
        if (component instanceof JTextField) {
            final JTextField tf = (JTextField) component;
            tf.getDocument().addDocumentListener(new DefaultDocumentListener() {
                @Override
                protected void changed() {
                    if (component.getParent() instanceof JComboBox) {
                        JComboBox comboBox = (JComboBox) component.getParent();
                        labelHandler.handleLabelStateFor(comboBox, Util.DEFAULT_NO_SELECTION_ENTRY.equals(tf.getText()));
                    } else {
                        labelHandler.handleLabelStateFor(component, DSSUtils.isEmpty(tf.getText()));
                    }
                    labelHandler.handleLabelStateFor(component, tf.getText().isEmpty());
                }
            });
            return BP_TEXT;
        } else if (component instanceof JComboBox) {
            final JComboBox box = (JComboBox) component;
            addListenerForEditableCombobox(box, new EditableComboboxListener() {
                @Override
                public void itemChanged(Object item) {
                    labelHandler.handleLabelStateFor(component, Util.DEFAULT_NO_SELECTION_ENTRY.equals(item));
                }
            });
            return BP_COMBO;
        } else if (component instanceof MultivaluePanel) {
            return BP_MVALUE;
        } else if (component instanceof JXDatePicker) {
            final JXDatePicker picker = (JXDatePicker) component;
            picker.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (picker.getDate() != null) {
                        labelHandler.handleLabelStateFor(component, false);
                    } else {
                        labelHandler.handleLabelStateFor(component, true);
                    }
                }
            });
            return BP_DATEPICKER;
        } else if (component instanceof DateTimePicker) {
            final DateTimePicker picker = (DateTimePicker) component;
            picker.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (picker.getDateTime() != null) {
                        labelHandler.handleLabelStateFor(component, false);
                    } else {
                        labelHandler.handleLabelStateFor(component, true);
                    }
                }
            });
            return BP_DATETIMEPICKER;
        } else if (component instanceof JCheckBox) {
            return BP_CHECKBOX;
        } else if (component instanceof CertificateProperty) {
            return BP_CERTIFICATE_PROPERTY;
        }

        return null;
    }

    /**
     * Shortcut for {@link #createBindingForComponent(javax.swing.JComponent component, String sourceBeanProperty, String name)} followed by {@link #appendConverter(org.jdesktop.beansbinding.Converter converter, String name)}
     *
     * @param component
     * @param sourceBeanProperty
     * @param name
     * @param converter
     */
    public void createBindingForComponent(JComponent component, String sourceBeanProperty, String name, Converter converter) {
        createBindingForComponent(component, sourceBeanProperty, name);
        appendConverter(converter, name);
    }


    private static interface EditableComboboxListener {
        void itemChanged(Object item);
    }

    private void addListenerForEditableCombobox(final JComboBox jComboBox, final EditableComboboxListener listener) {
        jComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                final Object selectedItem = jComboBox.getSelectedItem();
                listener.itemChanged(selectedItem);
            }
        });
        if (jComboBox.isEditable()) {
            ((JTextComponent) jComboBox.getEditor().getEditorComponent()).getDocument().addDocumentListener(new DefaultDocumentListener() {
                protected void changed() {
                    final Object item = jComboBox.getEditor().getItem();
                    listener.itemChanged(item);
                }
            });
        }
    }

    /**
     * Creates the binding for component.
     *
     * @param component          the component
     * @param sourceBeanProperty the source bean property
     * @param name               the name
     */
    public void createBindingForComponent(JComponent component, String sourceBeanProperty, String name) {
        LOG.trace("Create binding for " + component + " on " + sourceBeanProperty + " with name " + name);
        if (component instanceof JComboBox) {
            JComboBox jComboBox = (JComboBox) component;
            if (jComboBox.isEditable()) {
                component = (JComponent) jComboBox.getEditor().getEditorComponent();
            }
        }
        BeanProperty bp = setupComponent(component);
        if (bp != null) {

            Binding binding = Bindings.createAutoBinding(UpdateStrategy.READ_WRITE, null, BeanProperty.create(sourceBeanProperty), // bean
                  component, bp, name); // ui
            bindings.put(name, binding);
            binding.addBindingListener(this);
        } else {
            LOG.error(">>>Unable to determine type of given component {} - No BeanProperty could be set!", component);
        }
    }

    /**
     * Appends a converter to a previously created binding.
     *
     * @param converter the converter
     * @param name      the name of the binding
     */
    public void appendConverter(Converter converter, String name) {
        if (converter == null) {
            LOG.error(">>>Provided converter may not be null for name: " + name);
        } else {
            Binding binding = retrieveBinding(name);
            if (binding != null) {
                binding.setConverter(converter);
            }
        }
    }

    /**
     * Appends a validator to a previously created binding.
     *
     * @param validator the validator
     * @param name      the name of the binding
     */
    public void appendValidator(Validator validator, String name) {
        if (validator == null) {
            LOG.error(">>>Provided validator may not be null for name: " + name);
        } else {
            Binding binding = retrieveBinding(name);
            if (binding != null) {
                binding.setValidator(validator);
            }
        }
    }

    /**
     * Adds the binding.
     *
     * @param binding the binding
     */
    public void addBinding(Binding binding) {
        String name = binding.toString();
        if (binding.getName() != null && !binding.getName().isEmpty()) {
            name = binding.getName();
        }
        addBinding(binding, name);
    }

    /**
     * Adds the binding.
     *
     * @param binding the binding
     * @param name    the name
     */
    private void addBinding(Binding binding, String name) {
        bindings.put(name, binding);
        LOG.info("Added a binding called {} to list of {} bindings", new Object[]{name, bindings.size()});
    }

    /**
     * Removes the binding.
     *
     * @param name the name
     */
    private void removeBinding(String name) {
        if (bindings.remove(name) != null) {
            LOG.info("Removed a binding called {} from the list of {} bindings", new Object[]{name, bindings.size()});
        } else {
            LOG.info("Binding {} could not be removed from the list of {} bindings", new Object[]{name, bindings.size()});
        }
    }

    /**
     * Iterate through all stored bindings and unbinds them.
     */
    public void unbindAll() {
        for (Binding binding : bindings.values()) {
            if (binding.isBound()) {
                try {
                    binding.unbind();
                } catch (Exception ex) {
                    LOG.error(">>>BindingManager.unbindAll(): Exception because of " + binding.getName() + ":" + ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * Iterate through all stored bindings and binds them.
     */
    public void bindAll() {
        for (Binding binding : bindings.values()) {
            try {
                binding.bind();
            } catch (Exception ex) {
                LOG.error(">>>BindingManager.bindAll(): Exception because of " + binding.getName() + ": " + ex.getMessage(), ex);
            }
        }
        bindingInProgress = false;
    }

    /**
     * Gives information about whether any binding is not yet finalized with {@link #bindAll()}.
     *
     * @return the bindingInProgress status
     */
    public boolean isBindingInProgress() {
        return bindingInProgress;
    }

    boolean doPrintouts = false;

    // Methods for BindingListener

    /**
     * Binding became bound.
     *
     * @param binding the binding {@inheritDoc}
     */
    @Override
    public void bindingBecameBound(Binding binding) {
        if (doPrintouts) {
            // System.out.println(binding.getName() + ": bindingBecameBound");
        }
    }

    /**
     * Binding became unbound.
     *
     * @param binding the binding {@inheritDoc}
     */
    @Override
    public void bindingBecameUnbound(Binding binding) {
        if (doPrintouts) {
            // System.out.println(binding.getName() + ": bindingBecameUnbound");
        }
    }

    /**
     * Sync failed.
     *
     * @param binding the binding
     * @param failure the failure {@inheritDoc}
     */
    @Override
    public void syncFailed(Binding binding, SyncFailure failure) {
        labelHandler.handleLabelStateFor((Component) binding.getTargetObject(), true);
        if (doPrintouts) {
            // System.out.println(binding.getName() + ": syncFailed");
        }
    }

    /**
     * Synced.
     *
     * @param binding the binding {@inheritDoc}
     */
    @Override
    public void synced(Binding binding) {
        labelHandler.handleLabelStateFor((Component) binding.getTargetObject(), false);
        if (doPrintouts) {
            // System.out.println(binding.getName() + ": synced");
        }
    }

    /**
     * Source changed.
     *
     * @param binding the binding
     * @param event   the event {@inheritDoc}
     */
    @Override
    public void sourceChanged(Binding binding, PropertyStateEvent event) {
        if (doPrintouts) {
            // System.out.println(binding.getName() + ": sourceChanged");
        }
    }

    /**
     * Target changed.
     *
     * @param binding the binding
     * @param event   the event {@inheritDoc}
     */
    @Override
    public void targetChanged(Binding binding, PropertyStateEvent event) {
        if (doPrintouts) {
            // System.out.println(binding.getName() + ": targetChanged");
        }
    }
}