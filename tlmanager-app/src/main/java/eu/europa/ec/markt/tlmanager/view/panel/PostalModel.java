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

package eu.europa.ec.markt.tlmanager.view.panel;

import eu.europa.ec.markt.tlmanager.util.Util;

/**
 * A model for the values to define a PostalAddressType
 * 
 *
 * @version $Revision$ - $Date$
 */

public class PostalModel implements ContentModel {

    private String streetAddress = "";
    private String locality = "";
    private String stateOrProvince = "";
    private String postalCode = "";
    private String countryName = Util.DEFAULT_NO_SELECTION_ENTRY;

    /**
     * Instantiates a new postal model.
     */
    public PostalModel() {
    }

    /**
     * Instantiates a new postal model.
     * 
     * @param streetAddress the street address
     * @param locality the locality
     * @param stateOrProvince the state or province
     * @param postalCode the postal code
     * @param countryName the country name
     * @param lang the language code
     */
    public PostalModel(String streetAddress, String locality, String stateOrProvince, String postalCode,
            String countryName, String lang) {
        this.streetAddress = streetAddress;
        this.locality = locality;
        this.stateOrProvince = stateOrProvince;
        this.postalCode = postalCode;
        this.countryName = countryName;
    }

    /**
     * Instantiates a new postal model.
     * 
     * @param postalModel the postal model
     */
    public PostalModel(PostalModel postalModel) {
        this.streetAddress = postalModel.getStreetAddress();
        this.locality = postalModel.getLocality();
        this.stateOrProvince = postalModel.getStateOrProvince();
        this.postalCode = postalModel.getPostalCode();
        this.countryName = postalModel.getCountryName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        if (!streetAddress.isEmpty() || !locality.isEmpty() || !stateOrProvince.isEmpty() || !postalCode.isEmpty()
                || !countryName.equals(Util.DEFAULT_NO_SELECTION_ENTRY)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        setStreetAddress("");
        setLocality("");
        setPostalCode("");
        setStateOrProvince("");
        setCountryName(Util.DEFAULT_NO_SELECTION_ENTRY);
    }

    /**
     * @return the streetAddress
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * @param streetAddress the streetAddress to set
     */
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     * @return the locality
     */
    public String getLocality() {
        return locality;
    }

    /**
     * @param locality the locality to set
     */
    public void setLocality(String locality) {
        this.locality = locality;
    }

    /**
     * @return the stateOrProvince
     */
    public String getStateOrProvince() {
        return stateOrProvince;
    }

    /**
     * @param stateOrProvince the stateOrProvince to set
     */
    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode the postalCode to set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return the countryName
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * @param countryName the countryName to set
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}