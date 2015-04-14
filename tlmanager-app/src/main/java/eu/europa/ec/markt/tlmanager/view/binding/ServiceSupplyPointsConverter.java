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
package eu.europa.ec.markt.tlmanager.view.binding;

import eu.europa.ec.markt.tlmanager.model.ServiceSupplyPointsAdapter;
import eu.europa.ec.markt.tsl.jaxb.tsl.ServiceSupplyPointsType;

import org.jdesktop.beansbinding.Converter;

/**
 * Does the conversion between a <code>ServiceSupplyPointsType</code> and a <code>ServiceSupplyPointsAdapter</code>
 * 
 *
 *
 */

public class ServiceSupplyPointsConverter extends Converter<ServiceSupplyPointsType, ServiceSupplyPointsAdapter> {

    /** {@inheritDoc} */
    @Override
    public ServiceSupplyPointsAdapter convertForward(ServiceSupplyPointsType source) {
        return new ServiceSupplyPointsAdapter(source);
    }

    /** {@inheritDoc} */
    @Override
    public ServiceSupplyPointsType convertReverse(ServiceSupplyPointsAdapter target) {
        return target.getAddresses();
    }
}