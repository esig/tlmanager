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

import javax.xml.bind.JAXBElement;

import eu.europa.ec.markt.dss.DSSUtils;
import eu.europa.ec.markt.tlmanager.core.QNames;
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultipleModel;
import eu.europa.ec.markt.tlmanager.view.panel.TSPInformationExtensionModel;
import eu.europa.ec.markt.tsl.jaxb.tsl.ExtensionType;
import eu.europa.ec.markt.tsl.jaxb.tsl.ExtensionsListType;
import eu.europa.ec.markt.tsl.jaxb.xades.IdentifierType;
import eu.europa.ec.markt.tsl.jaxb.xades.ObjectIdentifierType;

/**
 * Adapter for a list of <code>ExtensionsListType</code>. It implements the <code>MultipleModel</code> and uses a
 * hashmap as working copy of the managed entries. On request, the bean is updated and given back.
 * 
 *
 *
 */

public class TSPInformationExtensionAdapter implements MultipleModel<TSPInformationExtensionModel> {

    private ExtensionsListType extensions;
    private Map<String, TSPInformationExtensionModel> values = new HashMap<String, TSPInformationExtensionModel>();
    private String initialValueKey = null;
    private int createdEntryCounter = 0;

    /**
     * The default constructor for TSPInformationExtensionAdapter.
     * 
     * @param extensions the extensions
     */
    public TSPInformationExtensionAdapter(ExtensionsListType extensions) {
        this.extensions = extensions;

        initialValueKey = Util.getInitialCounterItem();

        List<ExtensionType> extensionTypes = extensions.getExtension();
        if (!extensionTypes.isEmpty()) {
            for (ExtensionType extension : extensionTypes) {
                TSPInformationExtensionModel model = new TSPInformationExtensionModel();
                model.setCritical(extension.isCritical());
                JAXBElement<?> element = Util.extractJAXBElement(extension);
                ObjectIdentifierType oid = (ObjectIdentifierType) element.getValue();
                IdentifierType identifier = oid.getIdentifier();
                if (identifier != null) {
                    model.setExtension(identifier.getValue());
                    setValue(Util.getCounterItem(createdEntryCounter++), model);
                }
            }
        } else {
            createNewItem();
        }
    }

    /** {@inheritDoc} */
    @Override
    public TSPInformationExtensionModel getValue(String key) {
        return values.get(key);
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(String key, TSPInformationExtensionModel value) {
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
        extensions.getExtension().clear();
        extensions.getExtension().addAll(getExtensions());
    }

    /** {@inheritDoc} */
    @Override
    public String createNewItem() {
        String key = Util.getCounterItem(createdEntryCounter++);
        setValue(key, new TSPInformationExtensionModel());

        return key;
    }

    /** {@inheritDoc} */
    @Override
    public String getInitialValueKey() {
        return initialValueKey;
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
        for (TSPInformationExtensionModel value : values.values()) {
            if (!value.isEmpty()) {
                size++;
            }
        }
        return size;
    }

    @Override
    public Dimension getRecommendedDialogSize() {
        return null;
    }

    private List<ExtensionType> getExtensions() {
        List<ExtensionType> extensions = new ArrayList<ExtensionType>();

        for (TSPInformationExtensionModel value : values.values()) {
            if (DSSUtils.isNotBlank(value.getExtension())) {
                ExtensionType extensionType = new ExtensionType();
                extensionType.setCritical(value.isCritical());
                ObjectIdentifierType oid = new ObjectIdentifierType();
                IdentifierType identifier = new IdentifierType();
                identifier.setValue(value.getExtension());
                oid.setIdentifier(identifier);

                JAXBElement<ObjectIdentifierType> element = new JAXBElement<ObjectIdentifierType>(
                      QNames._ObjectIdentifier_QNAME, ObjectIdentifierType.class, null, oid);
                extensionType.getContent().add(element);
                extensions.add(extensionType);
            }
        }

        return extensions;
    }

    /**
     * Rebuild the <code>ExtensionsListType</code> by going through the value map.
     * 
     * @return the <code>ExtensionsListType</code>
     */
    public ExtensionsListType getExtensionsListType() {
        ExtensionsListType extensions = new ExtensionsListType();
        extensions.getExtension().addAll(getExtensions());

        return extensions;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

}