/**
 * TL Manager
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 *
 * This file is part of the "TL Manager" project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.ec.markt.tlmanager.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europa.ec.markt.dss.DSSUtils;
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultipleModel;
import eu.europa.ec.markt.tsl.jaxb.xades.ObjectIdentifierType;

/**
 *
 */

public class ObjectIdentifierTypeAdapter implements MultipleModel<ObjectIdentifierType> {

    private final List<ObjectIdentifierType> objectIdentifierTypeList;
    private Map<String, ObjectIdentifierType> values = new HashMap<String, ObjectIdentifierType>();
    private String initialValueKey = null;
    private int createdEntryCounter = 0;

    /**
     * @param objectIdentifierTypeList
     */
    public ObjectIdentifierTypeAdapter(List<ObjectIdentifierType> objectIdentifierTypeList) {
        this.objectIdentifierTypeList = objectIdentifierTypeList;
        initialValueKey = Util.getInitialCounterItem();

        if (objectIdentifierTypeList != null && !objectIdentifierTypeList.isEmpty()) {
            for (ObjectIdentifierType objectIdentifierType : objectIdentifierTypeList) {
                if (!isEmpty(objectIdentifierType)) {
                    setValue(Util.getCounterItem(createdEntryCounter++), objectIdentifierType);
                }
            }
        } else {
            createNewItem();
        }
    }

    private boolean isEmpty(ObjectIdentifierType objectIdentifierType) {
        final boolean notEmpty = objectIdentifierType != null && ((objectIdentifierType.getIdentifier() != null && DSSUtils
              .isNotBlank(objectIdentifierType.getIdentifier().getValue())) || DSSUtils
              .isNotBlank(objectIdentifierType.getDescription()));
        return !notEmpty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectIdentifierType getValue(String key) {
        return values.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(String key, ObjectIdentifierType value) {
        if (value != null) {
            values.put(key, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeItem(String key) {
        values.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateBeanValues() {
        getObjectIdentifierTypeList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createNewItem() {
        String key = Util.getCounterItem(createdEntryCounter++);
        setValue(key, new ObjectIdentifierType());

        return key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInitialValueKey() {
        return initialValueKey;
    }

    /**
     * @return the ObjectIdentifierType list
     */
    public List<ObjectIdentifierType> getObjectIdentifierTypeList() {
        objectIdentifierTypeList.clear();
        for (ObjectIdentifierType value : values.values()) {
            if (!isEmpty(value)) {
                objectIdentifierTypeList.add(value);
            }
        }
        return objectIdentifierTypeList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getKeys() {
        return new ArrayList<String>(values.keySet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        int size = 0;
        for (ObjectIdentifierType value : values.values()) {
            if (!isEmpty(value)) {
                size++;
            }
        }
        return size;
    }

    @Override
    public Dimension getRecommendedDialogSize() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }
}