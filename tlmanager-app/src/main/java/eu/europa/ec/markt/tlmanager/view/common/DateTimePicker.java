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

package eu.europa.ec.markt.tlmanager.view.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.europa.ec.markt.dss.common.TooltipHelper;
import eu.europa.ec.markt.tlmanager.core.Configuration;

/**
 * Component for a date and a time with the help of a {@code JXDatePicker} and a {@code JSpinner}.
 * 
 *
 * @version $Revision$ - $Date$
 */

public class DateTimePicker extends javax.swing.JPanel {

    private Date dateTime;
    private boolean changingState = false;

    /** Creates new form DateTimePicker */
    public DateTimePicker() {
        initComponents();
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

        // prevent manual editing of the textfield completely
        picker.getEditor().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                e.consume(); // ignore all
            }

            @Override
            public void keyReleased(KeyEvent e) {
                e.consume(); // ignore all
            }

            @Override
            public void keyPressed(KeyEvent e) {
                e.consume(); // ignore all
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
     * Sets the given date and time (if any). Note: the value from the spinner is only an addition.
     * 
     * @param dateTime the date and time
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
        if (dateTime != null) { // SpinnerDateModel.setValue(null) yields IllegalArgumentException
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
     * <p/>
     * The ActionListener will receive an ActionEvent when a selection has been made. Note: this watches only the
     * picker, because this is the relevant element.
     * 
     * @param l The ActionListener that is to be notified
     */
    public void addActionListener(ActionListener l) {
        picker.addActionListener(l);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(picker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(picker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(spinner, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXDatePicker picker;
    private javax.swing.JSpinner spinner;
    // End of variables declaration//GEN-END:variables
}