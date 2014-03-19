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
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultipleModel;
import eu.europa.ec.markt.tlmanager.view.panel.AnyUriModel;
import eu.europa.ec.markt.tsl.jaxb.tsl.ServiceSupplyPointsType;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for a list of <code>ServiceSupplyPointsType</code>. It implements the <code>MultipleModel</code> and uses a
 * hashmap as working copy of the managed entries. On request, the bean is updated and given back.
 * 
 *
 * @version $Revision$ - $Date$
 */

public class ServiceSupplyPointsAdapter implements MultipleModel<AnyUriModel> {

    private ServiceSupplyPointsType supplyPoints;
    private Map<String, AnyUriModel> values = new HashMap<String, AnyUriModel>();
    private String initialValueKey = null;
    private int createdEntryCounter = 0;

    /**
     * Instantiates a new service supply points adapter.
     * 
     * @param addresses the addresses
     */
    public ServiceSupplyPointsAdapter(ServiceSupplyPointsType addresses) {
        this.supplyPoints = addresses;

        initialValueKey = Util.getInitialCounterItem();

        if (addresses != null && !addresses.getServiceSupplyPoint().isEmpty()) {
            for (String address : addresses.getServiceSupplyPoint()) {
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
     * @return the supply points
     */
    public ServiceSupplyPointsType getAddresses() {
        List<String> uris = supplyPoints.getServiceSupplyPoint();
        uris.clear();

        for (AnyUriModel value : values.values()) {
            String uri = value.getType() + value.getAddress();
            uris.add(uri);
        }
        return supplyPoints;
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