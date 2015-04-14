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

import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultipleModel;
import eu.europa.ec.markt.tlmanager.view.panel.AnyUriModel;
import eu.europa.ec.markt.tsl.jaxb.tsl.NonEmptyURIListType;
import eu.europa.ec.markt.tsl.jaxb.tsl.ServiceSupplyPointsType;

/**
 * Adapter for a list of <code>NonEmptyURIListType</code>. It implements the <code>MultipleModel</code> and uses a
 * hashmap as working copy of the managed entries. On request, the bean is updated and given back.
 * 
 *
 *
 */

public class DistributionPointsAdapter implements MultipleModel<AnyUriModel> {

    private NonEmptyURIListType addresses;
    private Map<String, AnyUriModel> values = new HashMap<String, AnyUriModel>();
    private String initialValueKey = null;
    private int createdEntryCounter = 0;

    /**
     * Instantiates a new DistributionPointsAdapter.
     *
     * @param addresses the addresses
     */
    public DistributionPointsAdapter(NonEmptyURIListType addresses) {
        this.addresses = addresses;

        initialValueKey = Util.getInitialCounterItem();

        if (addresses != null && !addresses.getURI().isEmpty()) {
            for (String address : addresses.getURI()) {
                AnyUriModel aum = new AnyUriModel();
                String[] splittedUri = splitUri(address);
                if (splittedUri != null) {
                    aum.setType(splittedUri[0]);
                    aum.setAddress(splittedUri[1]);
                    setValue(Util.getCounterItem(createdEntryCounter++), aum);
                }
            }
        } else {
            createNewItem();
        }
    }

    private String[] splitUri(String uri) {
        String[] addressTypes = Configuration.getInstance().getAddressTypes();
        for (String addressType : addressTypes) {
            if (uri.startsWith(addressType)) {
                return new String[] { addressType, uri.substring(addressType.length()) };
            }
        }
        if (uri.startsWith(Util.DEFAULT_NO_SELECTION_ENTRY)) {
            return new String[] { Util.DEFAULT_NO_SELECTION_ENTRY,
                    uri.substring(Util.DEFAULT_NO_SELECTION_ENTRY.length()) };
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public AnyUriModel getValue(String key) {
        return values.get(key);
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(String key, AnyUriModel value) {
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
        // just trigger updating
        getAddresses();
    }

    /** {@inheritDoc} */
    @Override
    public String createNewItem() {
        String key = Util.getCounterItem(createdEntryCounter++);
        setValue(key, new AnyUriModel());

        return key;
    }

    /** {@inheritDoc} */
    @Override
    public String getInitialValueKey() {
        return initialValueKey;
    }

    /**
     * @return the NonEmptyURIListType
     */
    public NonEmptyURIListType getAddresses() {
        List<String> uris = addresses.getURI();
        uris.clear();

        for (AnyUriModel value : values.values()) {
            String uri = value.getType() + value.getAddress();
            uris.add(uri);
        }
        return addresses;
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
        for (AnyUriModel value : values.values()) {
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