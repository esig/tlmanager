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
package eu.europa.ec.markt.tlmanager.view.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

import eu.europa.ec.markt.dss.common.TooltipHelper;
import eu.europa.ec.markt.tlmanager.core.Configuration;

/**
 * Component for a date and a time with the help of a {@code JXDatePicker} and a
 * {@code JSpinner}.
 * 
 *
 *
 */
public class DateTimePicker extends javax.swing.JPanel {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DateTimePicker.class);

	private static final String YYYY_MM_DD = "yyyy-MM-dd";
	private static final String TOOLTIP_TEXT = "yyyy-mm-dd";

	private Date dateTime;
	private boolean changingState = false;

	/** Creates new form DateTimePicker */
	public DateTimePicker() {
		initComponents();

		DateFormatter df = new DateFormatter(new SimpleDateFormat(YYYY_MM_DD));
		DefaultFormatterFactory factory = new DefaultFormatterFactory(df);
		picker.getEditor().setFormatterFactory(factory);

		picker.getEditor().setToolTipText(TOOLTIP_TEXT);

		addListeners();

		this.setToolTipText(Configuration.getInstance().getTimeZoneName());
		TooltipHelper.registerComponentAtTooltipManager(this);
	}

	private void addListeners() {
		picker.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setDateTime(picker.getDate());
			}
		});

		picker.getEditor().addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				char keyChar = e.getKeyChar();
				if (Character.isDigit(keyChar) || (KeyEvent.VK_MINUS == e.getKeyCode()) || isCtrlV(e)) {
					String text = picker.getEditor().getText();
					if (text.matches("\\d{4}-\\d{2}-\\d{2}")) {
						SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
						try {
							Date date = sdf.parse(text);
							setDateTime(date);
						} catch (ParseException ex) {
							setDateTime(null);
						}
					}
				} else {
					e.consume();
				}
			}

			/**
			 * This methods detects the ctrl+v (paste action)
			 * 
			 * @param e
			 *            the key event
			 * @return true if ctrl+v is detected
			 */
			private boolean isCtrlV(KeyEvent e) {
				return (e.isControlDown() && (KeyEvent.VK_V == e.getKeyCode()));
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

		});

		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (dateTime != null && !changingState) {
					SpinnerDateModel sdm = (SpinnerDateModel) spinner.getModel();
					// set the hour/minute from the spinner to the current date
					Calendar calModel = Calendar.getInstance();
					calModel.setTime(sdm.getDate());

					Calendar calNew = Calendar.getInstance();
					calNew.setTime(dateTime);
					calNew.set(Calendar.HOUR_OF_DAY, calModel.get(Calendar.HOUR_OF_DAY));
					calNew.set(Calendar.MINUTE, calModel.get(Calendar.MINUTE));
					calNew.set(Calendar.SECOND, calModel.get(Calendar.SECOND));

					setDateTime(calNew.getTime());
				}
			}
		});
	}

	/**
	 * Returns the currently selected date and time (if any).
	 * 
	 * @return Date
	 */
	public Date getDateTime() {
		return dateTime;
	}

	/**
	 * Sets the given date and time (if any). Note: the value from the spinner
	 * is only an addition.
	 * 
	 * @param dateTime
	 *            the date and time
	 */
	public void setDateTime(Date dateTime) {
		if (dateTime == null) {
			resetFields();
		}
		Date old = getDateTime();
		this.dateTime = dateTime;
		setFields(dateTime);
		firePropertyChange("dateTime", old, getDateTime());
	}

	private void setFields(Date dateTime) {
		changingState = true;
		picker.setDate(dateTime);
		if (dateTime != null) {
			SpinnerDateModel sdm = (SpinnerDateModel) spinner.getModel();
			sdm.setValue(dateTime);
		}
		changingState = false;
	}

	private void resetFields() {
		picker.setDate(null);
		SpinnerDateModel sdm = (SpinnerDateModel) spinner.getModel();
		sdm.setValue(emptySpinner().getTime());
	}

	private Calendar emptySpinner() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		return cal;
	}

	/**
	 * Adds an ActionListener.
	 *
	 * The ActionListener will receive an ActionEvent when a selection has been
	 * made. Note: this watches only the picker, because this is the relevant
	 * element.
	 * 
	 * @param l
	 *            The ActionListener that is to be notified
	 */
	public void addActionListener(ActionListener l) {
		picker.addActionListener(l);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		picker = new org.jdesktop.swingx.JXDatePicker();
		spinner = new javax.swing.JSpinner();

		setPreferredSize(new java.awt.Dimension(200, 26));

		picker.setName("picker"); // NOI18N

		spinner.setModel(new javax.swing.SpinnerDateModel(emptySpinner().getTime(), null, null, java.util.Calendar.HOUR_OF_DAY));
		spinner.setEditor(new javax.swing.JSpinner.DateEditor(spinner, "HH:mm:ss"));
		spinner.setMinimumSize(new java.awt.Dimension(160, 22));
		spinner.setName("spinner"); // NOI18N
		spinner.setPreferredSize(new java.awt.Dimension(160, 22));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addComponent(picker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(picker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)));
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private org.jdesktop.swingx.JXDatePicker picker;
	private javax.swing.JSpinner spinner;
	// End of variables declaration//GEN-END:variables
}