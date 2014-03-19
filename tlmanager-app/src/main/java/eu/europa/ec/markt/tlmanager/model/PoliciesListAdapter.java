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

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultipleModel;
import eu.europa.ec.markt.tlmanager.view.panel.PoliciesListModel;
import eu.europa.ec.markt.tsl.jaxb.ecc.PoliciesListType;

/**
 *
 * @version $Revision$ - $Date$
 */

public class PoliciesListAdapter implements MultipleModel<PoliciesListModel> {

    private List<PoliciesListType> policiesListTypeList;
    private Map<String, PoliciesListModel> values = new HashMap<String, PoliciesListModel>();
    private String initialValueKey = null;
    private int createdEntryCounter = 0;

    /**
     * @param policiesListTypeList
     */
    public PoliciesListAdapter(List<PoliciesListType> policiesListTypeList) {
        this.policiesListTypeList = policiesListTypeList;

        initialValueKey = Util.getInitialCounterItem();

        if (policiesListTypeList != null && !policiesListTypeList.isEmpty()) {
            for (PoliciesListType policiesListType : policiesListTypeList) {
                PoliciesListModel policiesListModel = new PoliciesListModel(policiesListType);
                if (!policiesListModel.isEmpty()) {
                    setValue(Util.getCounterItem(createdEntryCounter++), policiesListModel);
                }
            }
        } else {
            createNewItem();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PoliciesListModel getValue(String key) {
        return values.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(String key, PoliciesListModel value) {
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
        // just trigger updating
        getKeyUsageTypeList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createNewItem() {
        String key = Util.getCounterItem(createdEntryCounter++);
        setValue(key, new PoliciesListModel());

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
     * @return the KeyUsageType list
     */
    public List<PoliciesListType> getKeyUsageTypeList() {
        policiesListTypeList.clear();

        for (PoliciesListModel value : values.values()) {
            policiesListTypeList.add(value.getPoliciesListType());
        }
        return policiesListTypeList;
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
        for (PoliciesListModel value : values.values()) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }
}