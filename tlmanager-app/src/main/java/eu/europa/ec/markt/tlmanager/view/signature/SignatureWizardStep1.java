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
package eu.europa.ec.markt.tlmanager.view.signature;

import java.awt.*;

import javax.swing.event.ChangeListener;

import org.openide.WizardDescriptor.ValidatingPanel;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;

import eu.europa.ec.markt.tlmanager.core.signature.SignatureManager;

/**
 * The first step of the signature wizard.
 *
 *
 */

public class SignatureWizardStep1 implements ValidatingPanel<Object> {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SignatureWizardStep1.class);

    private SignatureManager manager;
    private SignatureStep1 panel;
    private boolean overrideEnabled = false;

    /**
     * The default constructor for SignatureWizardStep1.
     */
    public SignatureWizardStep1(SignatureManager manager) {
        this.manager = manager;
        panel = new SignatureStep1(this);
    }


    /**
     * Enables overriding the blocking behaviour of validation in case of errors.
     *
     * @param override true if it is ok to override
     */
    public void overrideEnabled(boolean override) {
        overrideEnabled = override;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void addChangeListener(ChangeListener arg0) {
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Component getComponent() {
        return panel;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public HelpCtx getHelp() {
        return null;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void readSettings(Object arg0) {
        panel.setValidationMessages(manager.retrieveValidationMessages());
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void removeChangeListener(ChangeListener arg0) {
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void storeSettings(Object arg0) {
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void validate() throws WizardValidationException {
        if (manager.isValidationErroneous() && !overrideEnabled) {

            // don't allow to continue
            throw new WizardValidationException(null, "As the validation contains errors, it is not possible to continue!", null);
        }
    }
}