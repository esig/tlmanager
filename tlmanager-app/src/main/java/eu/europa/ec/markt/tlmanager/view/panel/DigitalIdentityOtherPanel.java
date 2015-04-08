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

package eu.europa.ec.markt.tlmanager.view.panel;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.markt.dss.DSSUtils;
import eu.europa.ec.markt.dss.validation102853.CertificateToken;
import eu.europa.ec.markt.tlmanager.core.Configuration;
import eu.europa.ec.markt.tlmanager.util.Util;
import eu.europa.ec.markt.tlmanager.view.certificate.DigitalIdentityModel;
import eu.europa.ec.markt.tlmanager.view.common.ContentDialogCloser;
import eu.europa.ec.markt.tsl.jaxb.tsl.AnyType;

/**
 * A panel which allows uploading a certificate and displays its data.
 *
 * @version $Revision: 2497 $ - $Date: 2013-09-05 17:30:51 +0200 (Thu, 05 Sep 2013) $
 */

public class DigitalIdentityOtherPanel extends JPanel implements ContentDialogCloser {

	private static final Logger LOG = LoggerFactory.getLogger(DigitalIdentityCertificatePanel.class);
	private static final ResourceBundle uiKeys = ResourceBundle.getBundle("eu/europa/ec/markt/tlmanager/uiKeysComponents", Configuration.getInstance().getLocale());

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JTextArea other;
	private javax.swing.JLabel otherLabel;
	private javax.swing.JScrollPane otherScrollPane;
	// End of variables declaration//GEN-END:variables
	
	private JFileChooser fileChooser;
	private DigitalIdentityModel digitalIdentityModel;
	private CertificateToken certificate;

	/**
	 * The default constructor for DigitalIdentityPanel.
	 */
	public DigitalIdentityOtherPanel() {
		this.fileChooser = new JFileChooser();

		initComponents();

	}

	/**
	 * Another constructor for DigitalIdentityPanel.
	 */
	public DigitalIdentityOtherPanel(JFileChooser fileChooser) {
		this.fileChooser = fileChooser;

		initComponents();

	}

	/**
	 *
	 */
	public void refresh() {
		// clean data
		other.setText("");

		AnyType otherValue = null;
		if (digitalIdentityModel != null) {
			otherValue = digitalIdentityModel.getOTHER();
		}

		if (otherValue != null) {
			if (otherValue.getContent()!=null){
				ArrayList list = new ArrayList(otherValue.getContent());
				for (int i = 0; i < list.size(); i++) {
					other.setText(list.get(i).toString());
				}
			}
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
	 * content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		otherScrollPane = new javax.swing.JScrollPane();
		other = new javax.swing.JTextArea();
		otherLabel = new javax.swing.JLabel();

		setName("DigitalIdentityPanel"); // NOI18N

		other.setColumns(5);
		other.setLineWrap(true);
		other.setRows(3);
		other.setWrapStyleWord(true);
		otherScrollPane.setViewportView(other);

		otherLabel.setText(uiKeys.getString("DigitalIdentityPanel.other")); // NOI18N

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(otherLabel)
						.addGap(51, 51, 51)
						.addComponent(otherScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
						.addContainerGap())
				);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(otherLabel)
								.addComponent(otherScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE))
								.addContainerGap())
				);
	}// </editor-fold>//GEN-END:initComponents


	private void closeButtonActionPerformed(ActionEvent evt) {// GEN-FIRST:event_closeButtonActionPerformed
		boolean closed = Util.closeDialog(evt);
		if (closed) {
			dialogWasClosed();
		}
	}// GEN-LAST:event_closeButtonActionPerformed


	/*
	 * (non-Javadoc)
	 *
	 * @see eu.europa.ec.markt.tlmanager.view.common.ContentDialogCloser#dialogWasClosed()
	 */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dialogWasClosed() {
		digitalIdentityModel.updateDigitalIdentity();
	}

	/**
	 * @return the digitalIdentityModel
	 */
	public DigitalIdentityModel getDigitalIdentityModel() {
		return digitalIdentityModel;
	}

	/**
	 * Sets the certificate model. Used by Binding !
	 *
	 * @param digitalIdentityModel the new certificate model
	 */
	public void setDigitalIdentityModel(DigitalIdentityModel digitalIdentityModel) {
		this.digitalIdentityModel = digitalIdentityModel;
		refresh();
	}

	/**
	 * Provides the current content information.
	 *
	 * @return the content information
	 */
	public AnyType retrieveContentInformation() {
		AnyType at = new AnyType();

		final String otherText = other.getText();
		if (DSSUtils.isNotBlank(otherText)) {
			at.getContent().add(otherText);
			return at;
		}

		return null;
	}

	void setCertificate(CertificateToken certificate) {
		this.certificate = certificate;
	}

}
