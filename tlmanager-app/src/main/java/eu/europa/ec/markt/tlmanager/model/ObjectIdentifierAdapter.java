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

package eu.europa.ec.markt.tlmanager.model;

import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultipleModel;
import eu.europa.ec.markt.tsl.jaxb.xades.IdentifierType;
import eu.europa.ec.markt.tsl.jaxb.xades.ObjectIdentifierType;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for a list of <code>ObjectIdentifierType</code>. It implements the <code>MultipleModel</code> and uses a
 * hashmap as working copy of the managed entries. On request, the bean is updated and given back.
 * 
 *
 * @version $Revision$ - $Date$
 */

public class ObjectIdentifierAdapter implements MultipleModel<String> {

    private List<ObjectIdentifierType> objectIdentifier;
    private Map<String, String> values = new HashMap<String, String>();
    private String initialValueKey = null;
    private int createdEntryCounter = 0;

    /**
     * The default constructor for ObjectIdentifierAdapter.
     * 
     * @param source
     */
    public ObjectIdentifierAdapter(List<ObjectIdentifierType> source) {
        this.objectIdentifier = source;
        initialValueKey = Util.getInitialCounterItem();
        if (!source.isEmpty()) {
            for (ObjectIdentifierType objectIdentifier : source) {
                IdentifierType identifier = objectIdentifier.getIdentifier();
                if (identifier != null) {
                    String value = identifier.getValue();
                    setValue(Util.getCounterItem(createdEntryCounter++), value);
                }
            }
        } else {
            createNewItem();
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getValue(String key) {
        return values.get(key);
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(String key, String value) {
        if (value != null) {
            values.put(key, value);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeItem(String key) {
        values.remove(key);
    }

    /** {@inheritDoc} */
    @Override
    public void updateBeanValues() {
        objectIdentifier.clear();
        objectIdentifier.addAll(getObjectIdentifier());
    }

    /** {@inheritDoc} */
    @Override
    public String createNewItem() {
        String key = Util.getCounterItem(createdEntryCounter++);
        setValue(key, "");

        return key;
    }

    /** {@inheritDoc} */
    @Override
    public String getInitialValueKey() {
        return initialValueKey;
    }

    /**
     * @return
     */
    public List<ObjectIdentifierType> getObjectIdentifier() {
        List<ObjectIdentifierType> list = new ArrayList<ObjectIdentifierType>();

        for (String value : values.values()) {
            IdentifierType id = new IdentifierType();
            id.setValue(value);
            ObjectIdentifierType oid = new ObjectIdentifierType();
            oid.setIdentifier(id);
            list.add(oid);
        }

        return list;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getKeys() {
        return new ArrayList<String>(values.keySet());
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        int size = 0;
        for (String value : values.values()) {
            if (value != null && !value.isEmpty()) {
                size++;
            }
        }
        return size;
    }

    @Override
    public Dimension getRecommendedDialogSize() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

}