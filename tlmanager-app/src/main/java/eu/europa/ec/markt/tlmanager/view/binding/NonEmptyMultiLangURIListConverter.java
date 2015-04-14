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

import eu.europa.ec.markt.tlmanager.model.NonEmptyMultiLangURIListAdapter;
import eu.europa.ec.markt.tsl.jaxb.tsl.NonEmptyMultiLangURIListType;

import org.jdesktop.beansbinding.Converter;

/**
 * Does the conversion between a <code>NonEmptyMultiLangURIListType</code> and a
 * <code>NonEmptyMultiLangURIListAdapter</code>
 * 
 *
 *
 */

public class NonEmptyMultiLangURIListConverter extends
        Converter<NonEmptyMultiLangURIListType, NonEmptyMultiLangURIListAdapter> {

    /** {@inheritDoc} */
    @Override
    public NonEmptyMultiLangURIListAdapter convertForward(NonEmptyMultiLangURIListType source) {
        return new NonEmptyMultiLangURIListAdapter(source);
    }

    /** {@inheritDoc} */
    @Override
    public NonEmptyMultiLangURIListType convertReverse(NonEmptyMultiLangURIListAdapter target) {
        return target.getMultiLangUris();
    }
}