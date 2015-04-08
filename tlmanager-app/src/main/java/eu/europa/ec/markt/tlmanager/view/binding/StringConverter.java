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

import eu.europa.ec.markt.tlmanager.util.Util;

import org.jdesktop.beansbinding.Converter;

/**
 * Does the conversion between a <code>String</code> and a <code>String</code> by usage of
 * <code>Util.filterEndlines()</code>
 * 
 *
 *
 */

public class StringConverter extends Converter<String, String> {

    /** {@inheritDoc} */
    @Override
    public String convertForward(String source) {
        return Util.filterEndlines((String) source);
    }

    /** {@inheritDoc} */
    @Override
    public String convertReverse(String target) {
        return target;
    }
}