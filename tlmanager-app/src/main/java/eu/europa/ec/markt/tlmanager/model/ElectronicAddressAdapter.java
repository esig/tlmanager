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

import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tlmanager.util.ItemDuplicator;
import eu.europa.ec.markt.tlmanager.view.multivalue.LingualModel;
import eu.europa.ec.markt.tlmanager.view.panel.AnyUriModel;
import eu.europa.ec.markt.tsl.jaxb.tsl.ElectronicAddressType;
import eu.europa.ec.markt.tsl.jaxb.tsl.NonEmptyMultiLangURIType;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for a list of <code>ElectronicAddressType</code>. It implements the <code>MultipleModel</code> and uses a
 * hashmap as working copy of the managed entries. On request, the bean is updated and given back.
 * 
 *
 * @version $Revision$ - $Date$
 */

public class ElectronicAddressAdapter implements LingualModel<AnyUriModel> {

    private ElectronicAddressType addresses;
    private String initialValueKey = null;

    /**
     * Instantiates a new electronic address adapter.
     * 
     * @param addresses the addresses
     */
    public ElectronicAddressAdapter(ElectronicAddressType addresses) {
        if (addresses == null) {
            addresses = new ElectronicAddressType();
        }
        this.addresses = addresses;
        initialValueKey = Configuration.getInstance().getLanguageCodes().getFirstLanguage();

        handleDuplicates();
    }

    private void handleDuplicates() {
        Map<String, Integer> entries = new HashMap<String, Integer>();
        for (final NonEmptyMultiLangURIType uri : addresses.getURI()) {
            String lang = uri.getLang();
            if (lang == null) {
                lang = Configuration.LanguageCodes.getEnglishLanguage();
                uri.setLang(lang);
            }
            if (entries.containsKey(lang)) {
                Integer counter = entries.get(lang);
                uri.setLang(ItemDuplicator.duplicate(lang, ++counter));
                entries.put(lang, counter);
            } else {
                entries.put(lang, 0);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public AnyUriModel getValue(String key) {
        AnyUriModel anyUriModel = null;
        for (NonEmptyMultiLangURIType uri : addresses.getURI()) {
            if (uri.getLang().equals(key)) {
                anyUriModel = new AnyUriModel(uri.getValue());
                break;
            }
        }

        return anyUriModel;
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(String key, AnyUriModel anyUriModel) {
        if (anyUriModel != null) {
            boolean addNewContent = true;
            if (anyUriModel.isEmpty()) {
                addNewContent = false;
            }
            NonEmptyMultiLangURIType newNonEmptyMultiLangURIType = createNonEmptyMultiLangURIType(anyUriModel, key);
            // update any existing value
            List<NonEmptyMultiLangURIType> electronicAddressTypes = addresses.getURI();
            for (NonEmptyMultiLangURIType uri : electronicAddressTypes) {
                if (uri.getLang().equals(key)) {
                    electronicAddressTypes.remove(uri);
                    if (addNewContent) {
                        electronicAddressTypes.add(newNonEmptyMultiLangURIType);
                    }
                    return;
                }
            }

            // set new value if it's not empty
            if (addNewContent) {
                electronicAddressTypes.add(newNonEmptyMultiLangURIType);
            }
        }
    }

    private NonEmptyMultiLangURIType createNonEmptyMultiLangURIType(AnyUriModel anyUriModel, String key) {
        final NonEmptyMultiLangURIType nonEmptyMultiLangURIType = new NonEmptyMultiLangURIType();
        nonEmptyMultiLangURIType.setLang(key);
        String uri = anyUriModel.getType() + anyUriModel.getAddress();
        nonEmptyMultiLangURIType.setValue(uri);
        return nonEmptyMultiLangURIType;
    }

    /** {@inheritDoc} */
    @Override
    public String getInitialValueKey() {
        return initialValueKey;
    }

    /**
     * @return the addresses
     */
    public ElectronicAddressType getAddresses() {
        return addresses;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getKeys() {
        List<String> list = new ArrayList<String>();
        for (NonEmptyMultiLangURIType key : addresses.getURI()) {
            list.add(key.getLang());
        }
        return list;
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return addresses.getURI().size();
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