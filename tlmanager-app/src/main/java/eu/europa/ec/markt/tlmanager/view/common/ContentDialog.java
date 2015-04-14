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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JRootPane;

/**
 * A Dialog which disappears if Escape is pressed.
 * 
 *
 *
 */

public class ContentDialog extends JDialog {

    private ContentDialogCloser dialogContent;

    /**
     * Instantiates a new content dialog.
     * 
     * @param owner the owner
     * @param title the title
     * @param modal the modal
     */
    public ContentDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    /**
     * @param dialogContent the dialogContent to set
     */
    public void setDialogContent(ContentDialogCloser dialogContent) {
        this.dialogContent = dialogContent;
    }

    protected JRootPane createRootPane() {
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                dialogContent.dialogWasClosed();
                setVisible(false);
            }
        };
        JRootPane rootPane = new JRootPane();
        // KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        // rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }

    /* (non-Javadoc)
     * @see java.awt.Dialog#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean arg0) {
        int widthWindow = this.getWidth();
        int heightWindow = this.getHeight();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int X = (screen.width / 2) - (widthWindow / 2); // Center horizontally.
        int Y = (screen.height / 2) - (heightWindow / 2); // Center vertically.
        setLocation(X, Y);
        super.setVisible(arg0);
    }

}