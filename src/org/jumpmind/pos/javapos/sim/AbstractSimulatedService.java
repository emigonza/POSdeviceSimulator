package org.jumpmind.pos.javapos.sim;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import jpos.JposConst;
import jpos.JposException;
import jpos.services.EventCallbacks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jumpmind.pos.javapos.sim.ui.SimulatedCashDrawerPanel;
import org.jumpmind.pos.javapos.sim.ui.SimulatedDeviceWindow;
import org.jumpmind.pos.javapos.sim.ui.SimulatedLineDisplayPanel;
import org.jumpmind.pos.javapos.sim.ui.SimulatedMICRPanel;
import org.jumpmind.pos.javapos.sim.ui.SimulatedMSRPanel;
import org.jumpmind.pos.javapos.sim.ui.SimulatedPINPadPanel;
import org.jumpmind.pos.javapos.sim.ui.SimulatedPOSPrinterPanel;
import org.jumpmind.pos.javapos.sim.ui.SimulatedFiscalPrinterPanel;
import org.jumpmind.pos.javapos.sim.ui.SimulatedScannerPanel;

public abstract class AbstractSimulatedService {
    public final static int DEVICE_VERSION = 1011000;
    protected final Log logger = LogFactory.getLog(getClass());

    private JFrame window;
    protected EventCallbacks callbacks;

    private int state = JposConst.JPOS_S_CLOSED;
    private boolean open;
    private boolean claimed;
    private boolean enabled;
    private boolean freezeEvents;
    private int powerNotify;

    public abstract void reset();

    public int getDeviceServiceVersion() throws JposException {
        return DEVICE_VERSION;
    }

    protected void invoke(Runnable runnable) throws JposException {
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                runnable.run();
            } else {
                SwingUtilities.invokeAndWait(runnable);
            }
        } catch (Exception e) {
            throw new JposException(JposConst.JPOS_E_FAILURE);
        }
    }

    protected void checkIfOpen() throws JposException {
        if (!open)
            throw new JposException(JposConst.JPOS_E_CLOSED, "Service is not open.");
    }

    protected void checkIfClaimed() throws JposException {
        if (!claimed)
            throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "Device is not claimed.");
    }

    public void deleteInstance() throws JposException {
        checkIfOpen();
    }

    public void open(String s, EventCallbacks eventcallbacks) throws JposException {
        if (open) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL, "Service is already open.");
        }
        this.open = true;
        this.state = JposConst.JPOS_S_IDLE;
        this.callbacks = eventcallbacks;

        if (!SimulatedDeviceWindow.getInstance().isInitialized()) {
            SimulatedDeviceWindow.getInstance().init();
        }
    }

    public void close() throws JposException {
        checkIfOpen();
        this.open = false;
        this.state = JposConst.JPOS_S_CLOSED;

        // Also need to reset all the member variables
        callbacks = null;
        enabled = false;
        freezeEvents = false;
        claimed = false;
        reset();
    }

    public void claim(int arg0) throws JposException {
        checkIfOpen();
        claimed = true;
    }

    public void release() throws JposException {
        checkIfOpen();
        checkIfClaimed();
        this.claimed = false;
        this.enabled = false;
        this.state = JposConst.JPOS_S_IDLE;
    }

    public int getState() throws JposException {
        return this.state;
    }

    public boolean getClaimed() throws JposException {
        return claimed;
    }

    public boolean getDeviceEnabled() throws JposException {
        return enabled;
    }

    public void setDeviceEnabled(boolean enabled) throws JposException {
        checkIfOpen();
        checkIfClaimed();
        this.enabled = enabled;

        if (this instanceof SimulatedMSRService) {
            invoke(new Runnable() {
                public void run() {
                    if (!SimulatedMSRPanel.getInstance().isInitialized()) {
                        SimulatedMSRPanel.getInstance().init();
                    }
                    SimulatedDeviceWindow.getInstance().getTabbedPane().setSelectedComponent(
                            SimulatedMSRPanel.getInstance());
                }
            });
            SimulatedMSRPanel.getInstance().setCallbacks(this.callbacks);
            SimulatedMSRPanel.getInstance().setDeviceCallback((SimulatedMSRService) this);

        } else if (this instanceof SimulatedPOSPrinterService) {
            invoke(new Runnable() {
                public void run() {
                    if (!SimulatedPOSPrinterPanel.getInstance().isInitialized()) {
                        SimulatedPOSPrinterPanel.getInstance().init();
                    }
                    SimulatedDeviceWindow.getInstance().getTabbedPane().setSelectedComponent(
                            SimulatedPOSPrinterPanel.getInstance());
                }
            });
        
        } else if (this instanceof SimulatedFiscalPrinterService) {
            invoke(new Runnable() {
                public void run() {
                    if (!SimulatedFiscalPrinterPanel.getInstance().isInitialized()) {
                        SimulatedFiscalPrinterPanel.getInstance().init();
                    }
                    SimulatedDeviceWindow.getInstance().getTabbedPane().setSelectedComponent(
                            SimulatedFiscalPrinterPanel.getInstance());
                }
            });

        } else if (this instanceof SimulatedCashDrawerService) {
            invoke(new Runnable() {
                public void run() {
                    if (!SimulatedCashDrawerPanel.getInstance().isInitialized()) {
                        SimulatedCashDrawerPanel.getInstance().init();
                    }
                    SimulatedDeviceWindow.getInstance().getTabbedPane().setSelectedComponent(
                            SimulatedCashDrawerPanel.getInstance());
                }
            });
            SimulatedCashDrawerPanel.getInstance().setCallbacks(this.callbacks);
            SimulatedCashDrawerPanel.getInstance().setDeviceCallback(
                    (SimulatedCashDrawerService) this);

        } else if (this instanceof SimulatedMICRService) {
            invoke(new Runnable() {
                public void run() {
                    if (!SimulatedMICRPanel.getInstance().isInitialized()) {
                        SimulatedMICRPanel.getInstance().init();
                    }
                    SimulatedDeviceWindow.getInstance().getTabbedPane().setSelectedComponent(
                            SimulatedMICRPanel.getInstance());
                }
            });
            SimulatedMICRPanel.getInstance().setCallbacks(this.callbacks);
            SimulatedMICRPanel.getInstance().setDeviceCallback((SimulatedMICRService) this);
        } else if (this instanceof SimulatedLineDisplayService) {
            invoke(new Runnable() {
                public void run() {
                    if (!SimulatedLineDisplayPanel.getInstance().isInitialized()) {
                        SimulatedLineDisplayPanel.getInstance().init();
                    }
                    SimulatedDeviceWindow.getInstance().getTabbedPane().setSelectedIndex(
                            SimulatedDeviceWindow.TAB_LINE_DISPLAY);
                }
            });
            SimulatedLineDisplayPanel.getInstance().setCallbacks(this.callbacks);
            SimulatedLineDisplayPanel.getInstance().setDeviceCallback(
                    (SimulatedLineDisplayService) this);
        } else if (this instanceof SimulatedPINPadService) {
            invoke(new Runnable() {
                public void run() {
                    if (!SimulatedPINPadPanel.getInstance().isInitialized()) {
                    	SimulatedPINPadPanel.getInstance().init();
                    }
                    SimulatedDeviceWindow.getInstance().getTabbedPane().setSelectedIndex(
                            SimulatedDeviceWindow.TAB_PIN_PAD);
                }
            });
            SimulatedPINPadPanel.getInstance().setCallbacks(this.callbacks);
            SimulatedPINPadPanel.getInstance().setDeviceCallback((SimulatedPINPadService) this);
        } else if (this instanceof SimulatedScannerService) {
            invoke(new Runnable() {
                public void run() {
                    if (!SimulatedScannerPanel.getInstance().isInitialized()) {
                        SimulatedScannerPanel.getInstance().init();
                        SimulatedDeviceWindow.getInstance().getTabbedPane().setSelectedIndex(
                                SimulatedDeviceWindow.TAB_SCANNER);
                    }
                }
            });

            SimulatedScannerPanel.getInstance().setCallbacks(this.callbacks);
            SimulatedScannerPanel.getInstance().setDeviceCallback((SimulatedScannerService) this);

        }
    }

    public void setFreezeEvents(boolean freezeEvents) throws JposException {
        checkIfOpen();
        this.freezeEvents = freezeEvents;
    }

    public boolean getFreezeEvents() throws JposException {
        return freezeEvents;
    }

    public int getPowerNotify() throws JposException {
        return powerNotify;
    }

    public void setPowerNotify(int powerNotify) {
        this.powerNotify = powerNotify;
    }

    public JFrame getWindow() {
        return window;
    }

    public void setWindow(JFrame window) {
        this.window = window;
    }

    public EventCallbacks getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(EventCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    public void addToGridBag(int x, int y, int colspan, Component comp, GridBagConstraints c,
            Container container) {
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = colspan;

        container.add(comp, c);
    }

}
