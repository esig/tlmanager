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

import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.jdesktop.beansbinding.Converter;

/**
 * Does the conversion between a <code>XMLGregorianCalendar</code> and a <code>Date</code>
 * 
 *
 *
 */

public class XMLGregorianCalendarConverter extends Converter<XMLGregorianCalendar, Date> {

    /** {@inheritDoc} */
    @Override
    public Date convertForward(XMLGregorianCalendar source) {
        return source.toGregorianCalendar().getTime();
    }

    /** {@inheritDoc} */
    @Override
    public XMLGregorianCalendar convertReverse(Date target) {
        return Util.createXMGregorianCalendar(target);
    }
}