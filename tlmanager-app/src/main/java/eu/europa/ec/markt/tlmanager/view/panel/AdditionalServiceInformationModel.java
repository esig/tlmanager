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
package eu.europa.ec.markt.tlmanager.view.panel;

import eu.europa.ec.markt.tlmanager.util.Util;

/**
 * A model for the values to define an <code>AdditionalServiceInformationType</code>.
 * 
 *
 *
 */

public class AdditionalServiceInformationModel implements ContentModel {

    private boolean critical;
    private String additionalInformationURI = Util.DEFAULT_NO_SELECTION_ENTRY;
    private String serviceInformationClassification = "";

    /**
     * Instantiates a new additional service information model.
     */
    public AdditionalServiceInformationModel() {
    }

    /**
     * Instantiates a new additional service information model.
     * 
     * @param critical the criticality state
     * @param type the type
     * @param address the address
     */
    public AdditionalServiceInformationModel(boolean critical, String type, String address) {
        this.critical = critical;
        this.additionalInformationURI = type;
        this.serviceInformationClassification = address;
    }

    /**
     * Instantiates a new additional service information model.
     * 
     * @param additionalServiceInformationModel the additional service information model
     */
    public AdditionalServiceInformationModel(AdditionalServiceInformationModel additionalServiceInformationModel) {
        this.critical = additionalServiceInformationModel.isCritical();
        this.additionalInformationURI = additionalServiceInformationModel.getAdditionalInformationURI();
        this.serviceInformationClassification = additionalServiceInformationModel
                .getServiceInformationClassification();
    }

    /**
     * {@inheritDoc} The critical state is not considered to be valuable content.
     */
    @Override
    public boolean isEmpty() {
        if ((additionalInformationURI != null && !additionalInformationURI.equals(Util.DEFAULT_NO_SELECTION_ENTRY))
                || (serviceInformationClassification != null && !serviceInformationClassification.isEmpty())) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        setCritical(false);
        setAdditionalInformationURI(Util.DEFAULT_NO_SELECTION_ENTRY);
        setServiceInformationClassification("");
    }

    /**
     * @return the critical
     */
    public boolean isCritical() {
        return critical;
    }

    /**
     * @param critical the critical to set
     */
    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    /**
     * @return the additionalInformationURI
     */
    public String getAdditionalInformationURI() {
        return additionalInformationURI;
    }

    /**
     * @param additionalInformationURI the additionalInformationURI to set
     */
    public void setAdditionalInformationURI(String additionalInformationURI) {
        this.additionalInformationURI = additionalInformationURI;
    }

    /**
     * @return the serviceInformationClassification
     */
    public String getServiceInformationClassification() {
        return serviceInformationClassification;
    }

    /**
     * @param serviceInformationClassification the serviceInformationClassification to set
     */
    public void setServiceInformationClassification(String serviceInformationClassification) {
        this.serviceInformationClassification = serviceInformationClassification;
    }
}