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

/*
 * Project: Digital Signature Services (DSS)
 * Contractor: ARHS-Developments
 *
 * $HeadURL: https://forge.aris-lux.lan/svn/dgmarktdss/trunk/apps/tlmanager/tlmanager-app/src/main/java/eu/europa/ec/markt/tlmanager/view/multivalue/content/DigitalIDMultivalueModel.java $
 * $Revision: 2519 $
 * $Date: 2013-09-10 17:26:58 +0200 (Tue, 10 Sep 2013) $
 * $Author: bouillni $
 */
package eu.europa.ec.markt.tlmanager.view.multivalue.content;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.europa.ec.markt.tlmanager.view.certificate.DigitalIdentityModel;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultipleModel;
import eu.europa.ec.markt.tsl.jaxb.tsl.DigitalIdentityListType;
import eu.europa.ec.markt.tsl.jaxb.tsl.DigitalIdentityType;

/**
 * TODO
 * <p/>
 * <p/>
 * DISCLAIMER: Project owner DG-MARKT.
 *
 * @author <a href="mailto:dgmarkt.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 2519 $ - $Date: 2013-09-10 17:26:58 +0200 (Tue, 10 Sep 2013) $
 */
public class ServiceDigitalIdentityMultivalueAdapter implements MultipleModel<DigitalIdentityModel> {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServiceDigitalIdentityMultivalueAdapter.class);

    private final DigitalIdentityListType digitalIdentityList;
    private Map<String, DigitalIdentityModel> ids = new HashMap<String, DigitalIdentityModel>();

    private int i = 1;

    /**
     * The default constructor for ServiceDigitalIdentityMultivalueAdapter.
     */
    public ServiceDigitalIdentityMultivalueAdapter(DigitalIdentityListType digitalIdentityList) {
        this.digitalIdentityList = digitalIdentityList;
        initMultiModel();
    }

    private void initMultiModel() {

        final List<DigitalIdentityType> digitalIdList = digitalIdentityList.getDigitalId();
        for (DigitalIdentityType digitalIdentityType : digitalIdList) {
            ids.put(createNewItem(), new DigitalIdentityModel(digitalIdentityType));
        }
    }

    @Override
    public DigitalIdentityModel getValue(String key) {
        return ids.get(key);
    }

    @Override
    public List<String> getKeys() {
        List<String> keys = new ArrayList<String>();
        for (String k : ids.keySet()) {
            keys.add(k);
        }
        return keys;
    }

    @Override
    public int size() {
        int size = 0;
        for (DigitalIdentityModel digitalIdentityModel : ids.values()) {
            if (digitalIdentityModel.getCertificate() != null || digitalIdentityModel.getSKI() != null || digitalIdentityModel.getSubjectName() != null) {
                size++;
            }
        }
        return size;
    }

    @Override
    public Dimension getRecommendedDialogSize() {
        return new Dimension(800, 700);
    }

    @Override
    public void setValue(String key, DigitalIdentityModel newDigitalIdentityModel) {
        LOG.info("Set value for key " + key + ": " + newDigitalIdentityModel);
        DigitalIdentityModel existingDigitalIdentityModel = ids.get(key);
        if (existingDigitalIdentityModel == null) {
            existingDigitalIdentityModel = new DigitalIdentityModel();
        }
        if (newDigitalIdentityModel != null) {
            if (newDigitalIdentityModel.getCertificate() != null) {
                existingDigitalIdentityModel.setCertificate(newDigitalIdentityModel.getCertificate());
            }
            if (newDigitalIdentityModel.getSKI() != null) {
                existingDigitalIdentityModel.setSKI(newDigitalIdentityModel.getSKI());
            }
            if (newDigitalIdentityModel.getSubjectName() != null) {
                existingDigitalIdentityModel.setSubjectName(newDigitalIdentityModel.getSubjectName());
            }
        }
        ids.put(key, existingDigitalIdentityModel);
    }

    @Override
    public String getInitialValueKey() {
        if (ids.keySet().isEmpty()) {
            return null;
        } else {
            return ids.keySet().iterator().next();
        }
    }

    @Override
    public void removeItem(String key) {
        ids.remove(key);
    }

    @Override
    public void updateBeanValues() {
        LOG.info("Update bean value");
        final List<DigitalIdentityType> digitalIdList = digitalIdentityList.getDigitalId();
        digitalIdList.clear();
        for (Iterator<Map.Entry<String, DigitalIdentityModel>> iterator = ids.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, DigitalIdentityModel> digitalIdentityModelEntry = iterator.next();
            final DigitalIdentityType digitalIdentityType = digitalIdentityModelEntry.getValue().getDigitalIdentity();
            if (digitalIdentityType != null && (digitalIdentityType.getX509Certificate() != null || digitalIdentityType.getX509SubjectName() != null || digitalIdentityType
                  .getX509SKI() != null)) {
                digitalIdList.add(digitalIdentityType);
            } else {
                iterator.remove();
            }
        }
    }

    @Override
    public String createNewItem() {
        String key = "Item " + i++;
        return key;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    public DigitalIdentityListType getDigitalIdentityList() {
        return digitalIdentityList;
    }

}
