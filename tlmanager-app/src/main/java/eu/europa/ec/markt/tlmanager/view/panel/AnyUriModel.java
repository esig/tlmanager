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

import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tsl.jaxb.tsl.NonEmptyMultiLangURIType;

/**
 * A model for the values of any URI
 * 
 *
 *
 */

public class AnyUriModel implements ContentModel {

    private String type = Util.DEFAULT_NO_SELECTION_ENTRY;
    private String address = "";

    /**
     * Instantiates a new AnyUriModel.
     */
    public AnyUriModel() {
    }

    /**
     * Instantiates a new AnyUriModel.
     * 
     * @param type the type
     * @param address the address
     */
    public AnyUriModel(String type, String address) {
        this.type = type;
        this.address = address;
    }

    /**
     * Instantiates a new AnyUriModel.
     * 
     * @param anyUriModel the AnyUriModel
     */
    public AnyUriModel(AnyUriModel anyUriModel) {
        this.type = anyUriModel.getType();
        this.address = anyUriModel.getAddress();
    }


    public AnyUriModel(String value) {
        final String[] split = splitUri(value);
        type = split[0];
        address = split[1];
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


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        if (!type.equals(Util.DEFAULT_NO_SELECTION_ENTRY) || !address.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        setType(Util.DEFAULT_NO_SELECTION_ENTRY);
        setAddress("");
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }
}