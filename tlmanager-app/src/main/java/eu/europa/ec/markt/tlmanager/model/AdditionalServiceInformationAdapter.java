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

import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tlmanager.core.QNames;
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultipleModel;
import eu.europa.ec.markt.tlmanager.view.panel.AdditionalServiceInformationModel;
import eu.europa.ec.markt.tsl.jaxb.tsl.AdditionalServiceInformationType;
import eu.europa.ec.markt.tsl.jaxb.tsl.ExtensionType;
import eu.europa.ec.markt.tsl.jaxb.tsl.NonEmptyMultiLangURIType;

/**
 * Adapter for a list of <code>AdditionalServiceInformationType</code>. It implements the <code>MultipleModel</code> and
 * uses a hashmap as working copy of the managed entries. On request, the bean is updated and given back.
 * 
 *
 *
 */

public class AdditionalServiceInformationAdapter implements MultipleModel<AdditionalServiceInformationModel> {

    private List<ExtensionType> extensionTypes;
    private Map<String, AdditionalServiceInformationModel> values = new HashMap<String, AdditionalServiceInformationModel>();
    private String initialValueKey = null;
    private int createdEntryCounter = 0;

    /**
     * Instantiates a new additional service information adapter.
     * 
     * @param extensionTypes the additional service information extension
     */
    public AdditionalServiceInformationAdapter(List<ExtensionType> extensionTypes) {
        this.extensionTypes = extensionTypes;
        initialValueKey = Util.getInitialCounterItem();
        // Note: There may be other Extensions in the provided list besides the ASI
        List<ExtensionType> matchingExtensionTypes = Util.extractMatching(extensionTypes,
                QNames._AdditionalServiceInformation_QNAME.getLocalPart(), false);
        if (!matchingExtensionTypes.isEmpty()) {
            for (ExtensionType extension : matchingExtensionTypes) {
                AdditionalServiceInformationModel model = new AdditionalServiceInformationModel();
                model.setCritical(extension.isCritical());
                JAXBElement<?> element = Util.extractJAXBElement(extension);
                AdditionalServiceInformationType value = (AdditionalServiceInformationType) element.getValue();
                NonEmptyMultiLangURIType uri = value.getURI();
                // uri.getLang() // here, lang will always be 'en'
                model.setAdditionalInformationURI(uri.getValue());
                model.setServiceInformationClassification(value.getInformationValue());
                setValue(Util.getCounterItem(createdEntryCounter++), model);
            }
        } else {
            createNewItem();
        }
    }

    /** {@inheritDoc} */
    @Override
    public AdditionalServiceInformationModel getValue(String key) {
        return values.get(key);
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(String key, AdditionalServiceInformationModel value) {
        values.put(key, value);
    }

    /** {@inheritDoc} */
    @Override
    public void removeItem(String key) {
        values.remove(key);
    }

    /** {@inheritDoc} */
    @Override
    public void updateBeanValues() {
        List<ExtensionType> nevv = getExtensionTypes();
        extensionTypes.clear();
        extensionTypes.addAll(nevv);
    }

    /** {@inheritDoc} */
    @Override
    public String createNewItem() {
        String key = Util.getCounterItem(createdEntryCounter++);
        setValue(key, new AdditionalServiceInformationModel());

        return key;
    }

    /** {@inheritDoc} */
    @Override
    public String getInitialValueKey() {
        return initialValueKey;
    }

    /**
     * Rebuild the list of <code>ExtensionType</code> by going through the value map. Other <code>ExtensionType</code>,
     * that do not match the AdditionalServiceInformation_QName have to stay unharmed and have to be carried over.
     *
     * @return the list of all <code>ExtensionType</code>
     */
    public List<ExtensionType> getExtensionTypes() {
        List<ExtensionType> list = new ArrayList<ExtensionType>();

        AdditionalServiceInformationType additionalServiceInformation = new AdditionalServiceInformationType();

        for (AdditionalServiceInformationModel value : values.values()) {
            ExtensionType extensionType = new ExtensionType();
            extensionType.setCritical(value.isCritical());
            AdditionalServiceInformationType asi = new AdditionalServiceInformationType();
            asi.setInformationValue(value.getServiceInformationClassification());

            NonEmptyMultiLangURIType nemlut = new NonEmptyMultiLangURIType();
            nemlut.setLang(Configuration.LanguageCodes.getEnglishLanguage());
            nemlut.setValue(value.getAdditionalInformationURI());

            asi.setURI(nemlut);
            JAXBElement<AdditionalServiceInformationType> element = new JAXBElement<AdditionalServiceInformationType>(
                    QNames._AdditionalServiceInformation_QNAME, AdditionalServiceInformationType.class, null, asi);
            extensionType.getContent().add(element);
            list.add(extensionType);
        }

        List<ExtensionType> notMatching = Util.extractMatching(extensionTypes,
                QNames._AdditionalServiceInformation_QNAME.getLocalPart(), true);
        list.addAll(notMatching); // these are the extensiontypes, which have to be just carried along

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
        for (AdditionalServiceInformationModel value : values.values()) {
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

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

}