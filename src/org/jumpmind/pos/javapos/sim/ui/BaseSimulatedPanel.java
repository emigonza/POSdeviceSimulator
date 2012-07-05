package org.jumpmind.pos.javapos.sim.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;

import jpos.services.EventCallbacks;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Element;

public class BaseSimulatedPanel extends JPanel {

    final Log logger = LogFactory.getLog(getClass());
    private static final long serialVersionUID = 1L;
    private boolean initialized;
    private EventCallbacks callbacks;

    public void addToGridBag(int x, int y, int colspan, Component comp,
            GridBagConstraints c, Container container) {
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = colspan;

        container.add(comp, c);
    }

    public String readElement(Element e, String tag) {
        String val = null;
        if (e != null && e.getChild(tag) != null) {
            val = e.getChild(tag).getValue();
        }
        return val;
    }

    public int readElementInt(Element e, String tag) {
        int val = 0;
        if (e != null && e.getChild(tag) != null) {
            try {
                String str = e.getChild(tag).getValue();
                if (!StringUtils.isBlank(str)) {
                    val = new Integer(str).intValue();
                }
            } catch (Exception ex) {
                logger
                        .error("Unable to read int  value from XML, was not an integer for tag "
                                + tag);
            }
        }
        return val;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public EventCallbacks getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(EventCallbacks callbacks) {
        this.callbacks = callbacks;
    }

}
