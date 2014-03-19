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

package eu.europa.ec.markt.tlmanager.view.multivalue.content;

import java.awt.*;
import java.util.ResourceBundle;

import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tlmanager.view.multivalue.DefaultMultivalueModel;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultipleModel;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultivalueModel;

/**
 * A base class for managing different content for a <code>MultivaluePanel</code>.
 *
 * @version $Revision$ - $Date$
 */

public abstract class MultiContent<T> {
    private static final ResourceBundle uiKeys = ResourceBundle.getBundle("eu/europa/ec/markt/tlmanager/uiKeysComponents", Configuration.getInstance().getLocale());
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MultiContent.class);

    private MultivalueModel<T> multiValueModel;
    protected String currentKey;
    protected boolean justOpened = true;

    /**
     * The default constructor for MultiContent.
     */
    public MultiContent() {
        multiValueModel = new DefaultMultivalueModel<T>(); // which is doing nothing
    }

    /**
     * Gets the component.
     *
     * @return the component
     */
    public abstract Component getComponent();

    /**
     * Returns the current value of the component. It depends on the kind of the component to determine what the value
     * is.
     *
     * @param clearOnExit true if the component has to clear the content
     * @return the current value of the component.
     */
    protected abstract T retrieveComponentValue(boolean clearOnExit);

    /**
     * Updates the current value of the component for the current key.
     */
    protected abstract void updateValue();

    /**
     * Refreshes the current key.
     */
    public void refresh() {
        String key = currentKey;
        if (key == null) {
            key = multiValueModel.getInitialValueKey();
        }
        updateValue();
        currentKey = key;
    }

    /*
     * DEV_NOTE about the 'justOpened' flag: this method is called directly from the item selection listener in the
     * multivaluePanel, which means, it's called when the multicontent panel is displayed for the first time. there is
     * no content (component value) available at that time and componentValue will be "" (or null, depending on the
     * implementation). if this value is set in the model the actual value (if any) is lost! this is against the whole
     * 'strategy' of setting/saving the values upon updating the content, when another item is selected. the flag
     * prevents this behaviour.
     */

    /**
     * Updates the content of the component depending on the value retrieved from the model. Note: the currentKey is
     * updated, but before doing that, it is used to store the component value, which may be changed by calling
     * updateValue().
     */
    public void updateContent(String key) {
        if (!justOpened) {
            T componentValue = retrieveComponentValue(false);
            setValue(currentKey, componentValue);
        } else {
            justOpened = false;
        }
        currentKey = key;
        updateValue();
    }

    /**
     * Sets a value in the collection.
     *
     * @param key   the key
     * @param value the value
     */
    public void setValue(String key, T value) {
        LOG.trace( "Set for item {} the value {} in {}", new Object[]{key, value, this});

        if (key != null) {
            multiValueModel.setValue(key, value);
        }
    }

    /**
     * Returns a value for a given key.
     *
     * @param key the key
     * @return an object
     */
    public T getValue(String key) {
        LOG.trace( "Get item value for {} in {}", new Object[]{key, this});
        return multiValueModel.getValue(key);
    }

    /**
     * Creates a new item in the value model and sets it to "".
     *
     * @return the key to access that item
     */
    public String createNewItem() {
        LOG.info("Create item");
        setCurrentValue();
        String key = ((MultipleModel) multiValueModel).createNewItem();
        currentKey = key;
        return key;
    }

    /**
     * Removes a given item from the value model.
     *
     * @param key the key of the value to remove
     */
    public void removeItem(String key) {
        ((MultipleModel) multiValueModel).removeItem(key);
    }

    /**
     * Sets the current value in the <code>MultivalueModel</code>
     */
    public void setCurrentValue() {
        setValue(currentKey, retrieveComponentValue(true));
    }

    /**
     * Updates the values right before exiting the enclosing component of the component.
     */
    public void updateOnExit() {
        setCurrentValue();
        if (multiValueModel instanceof MultipleModel) {
            ((MultipleModel) multiValueModel).updateBeanValues();
        }
        justOpened = true;
    }

    /**
     * Provides information of how many values can be found in the <code>MultivalueModel</code>
     *
     * @return the number of values in the model
     */
    public String retrieveContentInformation() {
        String entry = uiKeys.getString("MultiContent.content.singular");
        String entries = uiKeys.getString("MultiContent.content.plural");
        int size = multiValueModel.size();
        if (size == 1) {
            entries = entry;
        }
        return size + " " + entries;
    }

    /**
     * @param multiValueModel the multiValueModel to set
     */
    public void setMultiValueModel(MultivalueModel<T> multiValueModel) {
        this.multiValueModel = multiValueModel;
    }

    /**
     * @return the multiValueModel
     */
    public MultivalueModel<T> getMultiValueModel() {
        return multiValueModel;
    }
}