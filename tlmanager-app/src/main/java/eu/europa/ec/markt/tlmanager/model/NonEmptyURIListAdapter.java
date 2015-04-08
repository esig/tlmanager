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
package eu.europa.ec.markt.tlmanager.model;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import eu.europa.ec.markt.dss.DSSUtils;
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultipleModel;
import eu.europa.ec.markt.tsl.jaxb.tsl.NonEmptyURIListType;

/**
 * Adapter for a list of <code>NonEmptyURIListType</code>. It implements the <code>MultipleModel</code> and uses a
 * hashmap as working copy of the managed entries. On request, the bean is updated and given back.
 *
 *
 *
 */

public class NonEmptyURIListAdapter implements MultipleModel<String> {

	private NonEmptyURIListType multiUris;
	private Map<String, String> values = new HashMap<String, String>();
	private String initialValueKey = null;
	private int createdEntryCounter = 0;

	/**
	 * The default constructor for NonEmptyURIListAdapter.
	 *
	 * @param multiUris the <code>NonEmptyURIListType</code>
	 */
	public NonEmptyURIListAdapter(NonEmptyURIListType multiUris) {
		this.multiUris = multiUris;
		initialValueKey = Util.getInitialCounterItem();
		if ((multiUris != null) && (multiUris.getURI() != null) && !multiUris.getURI().isEmpty()) {
			for (String uri : multiUris.getURI()) {
				if (uri != null) {
					uri = Util.filterEndlines(uri);
					setValue(Util.getCounterItem(createdEntryCounter++), uri);
				}
			}
		} else {
			createNewItem();
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getValue(String key) {
		return values.get(key);
	}

	/** {@inheritDoc} */
	@Override
	public void setValue(String key, String value) {
		values.put(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public void removeItem(String key) {
		values.remove(key);
	}

	/** {@inheritDoc} */
	@Override
	public void updateBeanValues() {
		multiUris.getURI().clear();
		for (String value : values.values()) {
			if (DSSUtils.isNotBlank(value) && ! StringUtils.equals(value,Util.DEFAULT_NO_SELECTION_ENTRY)) {
				multiUris.getURI().add(value);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public String createNewItem() {
		String key = Util.getCounterItem(createdEntryCounter++);
		setValue(key, Util.DEFAULT_NO_SELECTION_ENTRY);

		return key;
	}

	/** {@inheritDoc} */
	@Override
	public String getInitialValueKey() {
		return initialValueKey;
	}

	/**
	 * @return the multiUris
	 */
	public NonEmptyURIListType getMultiUris() {
		return multiUris;
	}

	/** {@inheritDoc} */
	@Override
	public List<String> getKeys() {
		return new ArrayList<String>(values.keySet());
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		int size = 0;
		for (String value : values.values()) {
			if (!StringUtils.equals(Util.DEFAULT_NO_SELECTION_ENTRY,value)) {
				size++;
			}
		}
		return size;
	}

	@Override
	public Dimension getRecommendedDialogSize() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}


}