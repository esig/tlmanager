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
package eu.europa.ec.markt.tlmanager.core.validation;

import eu.europa.ec.markt.tlmanager.core.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Logs and maintains all messages that are created during validation.
 * 
 *
 *
 */

public class ValidationLogger {
    private enum LEVEL {
        Info, Warning, Error
    };

    private static final ResourceBundle uiKeys = ResourceBundle.getBundle("eu/europa/ec/markt/tlmanager/uiKeysCore",
            Configuration.getInstance().getLocale());

    private List<Message> messages = new ArrayList<Message>();

    private boolean hasWarned = false;

    private boolean hasErrors = false;

    /**
     * Gets the list of all logged messages.
     * 
     * @return the messages
     */
    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    private Icon resolveIcon(LEVEL level) {
        if (level != null) {
            return new ImageIcon(getClass().getResource("/icons/validation_" + level.toString() + ".png"));
        }

        return null;
    }

    /**
     * Returns the list of validation messages and creates appropriate icons, if they got none.
     * 
     * @return the validation messages
     */
    public List<Message> getValidationMessages() {
        for (Message msg : messages) {
            if (msg.getLabel() == null) {
                Icon icon = resolveIcon(msg.getLevel());
                JLabel label = new JLabel(msg.getMessage(), icon, SwingConstants.CENTER);
                msg.setLabel(label);
            }
        }

        return messages;
    }

    /**
     * Remove all messages that have one of the provided objects set as parent panel object.
     * 
     * @param objects the objects of the messages to remove
     * 
     * @return true if at least one message was removed
     */
    public boolean removeMessagesForObjects(List<Object> objects) {
        List<Message> toRemove = new ArrayList<ValidationLogger.Message>();
        for (Message msg : messages) {
            if (objects.contains(msg.getParentPanelObject())) {
                toRemove.add(msg);
            }
        }

        return messages.removeAll(toRemove);
    }

    /**
     * Creates a standard prefix out of two given strings.
     * 
     * @param name the name
     * @param field the field
     * 
     * @return the prefix
     */
    public String getPrefix(String name, String field) {
        return name + ": " + field + " ";
    }

    /**
     * Creates a standard message that says that something may not be empty.
     * 
     * @param name the name
     * @param field the field
     * 
     * @return the message
     */
    public String getEmptyMessage(String name, String field) {
        return getPrefix(name, field) + uiKeys.getString("Validation.mandatory.mayNotBeEmpty"); 
    }

    /**
     * Logs a message.
     * 
     * @param level the message priority level
     * @param message he message to log
     * @param obj the reference object
     */
    private void log(LEVEL level, String message, Object obj) {
        messages.add(new Message(level, message, obj));
    }

    /**
     * Logs an information.
     * 
     * @param message the message
     */
    public void info(String message) {
        info(message, null);
    }

    /**
     * Logs a warning.
     * 
     * @param message the message
     */
    public void warn(String message) {
        warn(message, null);
    }

    /**
     * Logs an error.
     * 
     * @param message the message
     */
    public void error(String message) {
        error(message, null);
    }

    /**
     * Logs an information.
     * 
     * @param message the message
     * @param obj the reference object
     */
    public void info(String message, Object obj) {
        log(LEVEL.Info, message, obj);
    }

    /**
     * Logs a warning.
     * 
     * @param message the message
     * @param obj the reference object
     */
    public void warn(String message, Object obj) {
        log(LEVEL.Warning, message, obj);
        hasWarned = true;
    }

    /**
     * Logs an error.
     * 
     * @param message the message
     * @param obj the reference object
     */
    public void error(String message, Object obj) {
        log(LEVEL.Error, message, obj);
        hasErrors = true;
    }

    /**
     * Checks for warnings.
     * 
     * @return true, if successful
     */
    public boolean hasWarnings() {
        return hasWarned;
    }

    /**
     * Checks for errors.
     * 
     * @return true, if successful
     */
    public boolean hasErrors() {
        return hasErrors;
    }

    /**
     * A class for a single Message.
     */
    public static class Message {
        private final LEVEL level;
        private final String message; // Free-text explanatory message.
        private Object parentPanelObject;
        private JLabel label;

        /**
         * Instantiates a new message.
         * 
         * @param level the level
         * @param message the message
         * @param parentPanelObject
         */
        public Message(LEVEL level, String message, Object parentPanelObject) {
            this.level = level;
            this.message = message;
            this.parentPanelObject = parentPanelObject;
        }

        /**
         * Gets the message level (Info, Warning, Error).
         * 
         * @return the message level (Info, Warning, Error)
         */
        public LEVEL getLevel() {
            return level;
        }

        /**
         * @return the parentPanelObject
         */
        public Object getParentPanelObject() {
            return parentPanelObject;
        }

        /**
         * @param parentPanelObject the parentPanelObject to set
         */
        public void setParentPanelObject(Object parentPanelObject) {
            this.parentPanelObject = parentPanelObject;
        }

        /**
         * Gets the free-text explanatory message.
         * 
         * @return the free-text explanatory message
         */
        public String getMessage() {
            return message;
        }

        /**
         * @return the label
         */
        public JLabel getLabel() {
            return label;
        }

        /**
         * @param label the label to set
         */
        public void setLabel(JLabel label) {
            this.label = label;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return String.format("%s: %s", level.toString(), message);
        }
    }
}