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
import eu.europa.ec.markt.tlmanager.view.panel.PostalModel;
import eu.europa.ec.markt.tsl.jaxb.tsl.PostalAddressListType;
import eu.europa.ec.markt.tsl.jaxb.tsl.PostalAddressType;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for a list of <code>PostalAddressListType</code>. It implements the <code>LingualModel</code> and works
 * directly on the given bean.
 * 
 *
 * @version $Revision$ - $Date$
 */

public class PostalAddressListAdapter implements LingualModel<PostalModel> {

    private PostalAddressListType postalAddressList;
    private String initialValueKey = null;

    /**
     * Instantiates a new postal address list adapter.
     * 
     * @param postalAddressList the postal address list
     */
    public PostalAddressListAdapter(PostalAddressListType postalAddressList) {
        this.postalAddressList = postalAddressList;
        initialValueKey = Configuration.getInstance().getLanguageCodes().getFirstLanguage();

        handleDuplicates();
    }

    private void handleDuplicates() {
        Map<String, Integer> entries = new HashMap<String, Integer>();
        for (PostalAddressType address : postalAddressList.getPostalAddress()) {
            String lang = address.getLang();
            if (entries.containsKey(lang)) {
                Integer counter = entries.get(lang);
                address.setLang(ItemDuplicator.duplicate(lang, ++counter));
                entries.put(lang, counter);
            } else {
                entries.put(lang, 0);
            }
        }
    }

    private PostalModel createPostalModel(PostalAddressType address) {
        PostalModel pm = new PostalModel();
        pm.setCountryName(address.getCountryName());
        pm.setLocality(address.getLocality());
        pm.setPostalCode(address.getPostalCode());
        pm.setStateOrProvince(address.getStateOrProvince());
        pm.setStreetAddress(address.getStreetAddress());

        return pm;
    }

    private PostalAddressType createPostalAddressType(PostalModel model, String lang) {
        PostalAddressType address = new PostalAddressType();
        address.setCountryName(model.getCountryName());
        address.setLocality(model.getLocality());
        address.setPostalCode(model.getPostalCode());
        address.setStateOrProvince(model.getStateOrProvince());
        address.setStreetAddress(model.getStreetAddress());
        address.setLang(lang);

        return address;
    }

    /** {@inheritDoc} */
    @Override
    public PostalModel getValue(String key) {
        PostalModel pm = null;
        for (PostalAddressType address : postalAddressList.getPostalAddress()) {
            if (address.getLang().equals(key)) {
                pm = createPostalModel(address);
                break;
            }
        }

        return pm;
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(String key, PostalModel pm) {
        if (pm != null) {
            boolean addNewContent = true;
            if (pm.isEmpty()) {
                addNewContent = false;
            }
            PostalAddressType newAddress = createPostalAddressType(pm, key);
            // update any existing value
            List<PostalAddressType> postalAddresses = postalAddressList.getPostalAddress();
            for (PostalAddressType address : postalAddresses) {
                if (address.getLang().equals(key)) {
                    postalAddresses.remove(address);
                    if (addNewContent) {
                        postalAddresses.add(newAddress);
                    }
                    return;
                }
            }

            // set new value if it's not empty
            if (addNewContent) {
                postalAddressList.getPostalAddress().add(newAddress);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getInitialValueKey() {
        return initialValueKey;
    }

    /**
     * @return the addresses
     */
    public PostalAddressListType getAddresses() {
        return postalAddressList;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getKeys() {
        List<String> list = new ArrayList<String>();
        for (PostalAddressType key : postalAddressList.getPostalAddress()) {
            list.add(key.getLang());
        }
        return list;
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return postalAddressList.getPostalAddress().size();
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