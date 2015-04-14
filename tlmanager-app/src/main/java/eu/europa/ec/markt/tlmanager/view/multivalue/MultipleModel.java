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
package eu.europa.ec.markt.tlmanager.view.multivalue;

/**
 * Interface for wrapping all multiple value related value models.
 * 
 *
 *
 */

public interface MultipleModel<T> extends MultivalueModel<T> {
    /**
     * Removes the item that is associated to the specified key
     * 
     * @param key the key
     */
    public void removeItem(String key);

    /**
     * In case there is an additional bean as value holder (besides the key/value based structure that holds the
     * (temporary) values coming from the ui), the method can be used to refresh its values.
     */
    public void updateBeanValues();

    /**
     * Creates a new Item in the model and returns the associated key for it.
     */
    public String createNewItem();
}