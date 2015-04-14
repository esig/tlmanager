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
package eu.europa.ec.markt.tlmanager.view.multivalue.content;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.europa.ec.markt.tlmanager.view.certificate.DigitalIdentityModel;
import eu.europa.ec.markt.tlmanager.view.multivalue.MultipleModel;
import eu.europa.ec.markt.tsl.jaxb.tsl.DigitalIdentityListType;
import eu.europa.ec.markt.tsl.jaxb.tsl.DigitalIdentityType;

/**
 * TODO
 *
 *
 *
 *
 *
 *
 */
public class ServiceDigitalIdentityMultivalueAdapter implements MultipleModel<DigitalIdentityModel> {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServiceDigitalIdentityMultivalueAdapter.class);

	private final DigitalIdentityListType digitalIdentityList;
	private Map<String, DigitalIdentityModel> ids = new HashMap<String, DigitalIdentityModel>();


	private int i = 1;
	private boolean historyPanel;

	/**
	 * The default constructor for ServiceDigitalIdentityMultivalueAdapter.
	 */
	public ServiceDigitalIdentityMultivalueAdapter(DigitalIdentityListType digitalIdentityList) {
		this(digitalIdentityList,false);
	}

	public ServiceDigitalIdentityMultivalueAdapter(DigitalIdentityListType digitalIdentityList, boolean historyPanel) {
		this.digitalIdentityList = digitalIdentityList;
		this.historyPanel = historyPanel;
		initMultiModel();
	}

	private void initMultiModel() {

		final List<DigitalIdentityType> digitalIdList = digitalIdentityList.getDigitalId();
		for (DigitalIdentityType digitalIdentityType : digitalIdList) {
			ids.put(createNewItem(), new DigitalIdentityModel(digitalIdentityType, isHistoricalPanel()));
		}

	}

	@Override
	public DigitalIdentityModel getValue(String key) {
		return ids.get(key);
	}

	@Override
	public List<String> getKeys() {
		List<String> keys = new ArrayList<String>();
		for (String k : ids.keySet()) {
			keys.add(k);
		}
		return keys;
	}

	@Override
	public int size() {
		int size = 0;
		for (DigitalIdentityModel digitalIdentityModel : ids.values()) {
			if ((digitalIdentityModel.getCertificate() != null) || (digitalIdentityModel.getSKI() != null) || (digitalIdentityModel.getSubjectName() != null)||(digitalIdentityModel.getOTHER()!=null)) {
				size++;
			}
		}
		return size;
	}

	@Override
	public Dimension getRecommendedDialogSize() {
		return new Dimension(800, 700);
	}

	@Override
	public void setValue(String key, DigitalIdentityModel newDigitalIdentityModel) {
		LOG.info("Set value for key " + key + ": " + newDigitalIdentityModel);
		DigitalIdentityModel existingDigitalIdentityModel = ids.get(key);
		if (existingDigitalIdentityModel == null) {
			existingDigitalIdentityModel = new DigitalIdentityModel();
		}

		if (newDigitalIdentityModel != null) {
			if (newDigitalIdentityModel.getCertificate() != null) {
				existingDigitalIdentityModel.setCertificate(newDigitalIdentityModel.getCertificate());
			}
			if (newDigitalIdentityModel.getSKI() != null) {
				existingDigitalIdentityModel.setSKI(newDigitalIdentityModel.getSKI());
			}
			if (newDigitalIdentityModel.getSubjectName() != null) {
				existingDigitalIdentityModel.setSubjectName(newDigitalIdentityModel.getSubjectName());
			}
			if (newDigitalIdentityModel.getOTHER()!=null){
				existingDigitalIdentityModel.setOTHER(newDigitalIdentityModel.getOTHER());
			}
		}
		ids.put(key, existingDigitalIdentityModel);
	}

	@Override
	public String getInitialValueKey() {
		if (ids.keySet().isEmpty()) {
			return null;
		} else {
			return ids.keySet().iterator().next();
		}
	}

	@Override
	public void removeItem(String key) {
		ids.remove(key);
	}

	@Override
	public void updateBeanValues() {
		LOG.info("Update bean values");
		final List<DigitalIdentityType> digitalIdList = digitalIdentityList.getDigitalId();
		digitalIdList.clear();
		for (Iterator<Map.Entry<String, DigitalIdentityModel>> iterator = ids.entrySet().iterator(); iterator.hasNext(); ) {
			Map.Entry<String, DigitalIdentityModel> digitalIdentityModelEntry = iterator.next();
			final DigitalIdentityType digitalIdentityType = digitalIdentityModelEntry.getValue().getDigitalIdentity();
			if ((digitalIdentityType != null) && ((digitalIdentityType.getX509Certificate() != null) || (digitalIdentityType.getX509SubjectName() != null) || (digitalIdentityType
					.getX509SKI() != null) || (digitalIdentityType.getOther()!=null))) {
				digitalIdList.add(digitalIdentityType);
			} else {
				iterator.remove();
			}
		}
	}

	@Override
	public String createNewItem() {
		String key = "Item " + i++;

		return key;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	public DigitalIdentityListType getDigitalIdentityList() {
		return digitalIdentityList;
	}

	public boolean isHistoricalPanel(){
		return this.historyPanel;
	}

}
