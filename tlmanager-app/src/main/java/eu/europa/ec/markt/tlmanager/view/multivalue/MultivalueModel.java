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

package eu.europa.ec.markt.tlmanager.view.multivalue;

import java.awt.*;
import java.util.List;

/**
 * Interface for a value model.
 * 
 *
 * @version $Revision$ - $Date$
 */

public interface MultivalueModel<T> {

    /**
     * Returns an object for a given key.
     * 
     * @param key the key
     * 
     * @return an object
     */
    public T getValue(String key);

    /**
     * Returns all keys in the model,
     * 
     * @return all keys
     */
    public List<String> getKeys();

    /**
     * @return true if the model has no values
     */
    public boolean isEmpty();

    /**
     * Sets the value for the given key in the collection.
     * 
     * @param key the key
     * @param value the value
     */
    public void setValue(String key, T value);

    /**
     * Returns the key, that is associated to the initial value.
     */
    public String getInitialValueKey();

    /**
     *
     * @return the number of items in this multi value model
     */
    public int size();

    public Dimension getRecommendedDialogSize();
}