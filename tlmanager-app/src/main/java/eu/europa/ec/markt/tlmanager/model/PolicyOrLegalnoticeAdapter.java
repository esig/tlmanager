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

import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tlmanager.util.ItemDuplicator;
import eu.europa.ec.markt.tlmanager.view.multivalue.LingualModel;
import eu.europa.ec.markt.tsl.jaxb.tsl.MultiLangStringType;
import eu.europa.ec.markt.tsl.jaxb.tsl.PolicyOrLegalnoticeType;

/**
 * Adapter for a list of <code>PolicyOrLegalnoticeType</code>. It implements the <code>LingualModel</code> and works
 * directly on the given bean.
 * 
 * NOTE: Only the "@XmlElement(name = "TSLLegalNotice")" is of importance.
 * 
 *
 * @version $Revision$ - $Date$
 */

public class PolicyOrLegalnoticeAdapter implements LingualModel<String> {

    private PolicyOrLegalnoticeType multiLangStrings;
    private String initialValueKey = null;

    /**
     * Instantiates a new policy or legalnotice adapter.
     * 
     * @param multiLangStrings the multi lang strings
     */
    public PolicyOrLegalnoticeAdapter(PolicyOrLegalnoticeType multiLangStrings) {
        this.multiLangStrings = multiLangStrings;
        initialValueKey = Configuration.getInstance().getLanguageCodes().getFirstLanguage();

        handleDuplicates();
    }

    private void handleDuplicates() {
        Map<String, Integer> entries = new HashMap<String, Integer>();
        for (MultiLangStringType multi : multiLangStrings.getTSLLegalNotice()) {
            String lang = multi.getLang();
            if (entries.containsKey(lang)) {
                Integer counter = entries.get(lang);
                multi.setLang(ItemDuplicator.duplicate(lang, ++counter));
                entries.put(lang, counter);
            } else {
                entries.put(lang, 0);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getValue(String key) {
        for (MultiLangStringType s : multiLangStrings.getTSLLegalNotice()) {
            String lang = s.getLang();
            if (lang != null && lang.equals(key)) {
                return s.getValue();
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(String key, String strValue) {
        for (MultiLangStringType s : multiLangStrings.getTSLLegalNotice()) {
            String lang = s.getLang();
            if (lang != null && lang.equals(key)) {
                if (strValue.isEmpty()) {
                    multiLangStrings.getTSLLegalNotice().remove(s);
                } else {
                    s.setValue(strValue);
                }
                return;
            }
        }
        if (!strValue.isEmpty()) {
            MultiLangStringType s = new MultiLangStringType();
            s.setLang(key);
            s.setValue(strValue);
            multiLangStrings.getTSLLegalNotice().add(s);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getInitialValueKey() {
        return initialValueKey;
    }

    /**
     * Gets the multi lang strings.
     * 
     * @return the multi lang strings
     */
    public PolicyOrLegalnoticeType getMultiLangStrings() {
        return multiLangStrings;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getKeys() {
        List<String> list = new ArrayList<String>();
        for (MultiLangStringType key : multiLangStrings.getTSLLegalNotice()) {
            if (key.getLang() != null) {
                list.add(key.getLang());
            }
        }
        return list;
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        int size = 0;
        for (MultiLangStringType value : multiLangStrings.getTSLLegalNotice()) {
            if (value.getValue() != null && !value.getValue().isEmpty()) {
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