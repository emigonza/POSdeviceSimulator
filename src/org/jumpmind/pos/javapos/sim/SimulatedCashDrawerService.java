package org.jumpmind.pos.javapos.sim;

import jpos.JposConst;
import jpos.JposException;
import jpos.services.CashDrawerService111;

import org.jumpmind.pos.javapos.sim.ui.SimulatedCashDrawerPanel;
import org.jumpmind.pos.javapos.sim.ui.SimulatedDeviceWindow;

public class SimulatedCashDrawerService extends AbstractSimulatedService
        implements CashDrawerService111 {
    private boolean cashDrawerOpened;

    @Override
    public void reset() {
        this.cashDrawerOpened = false;
    }

    public void toggleDrawer(boolean open) {
        if (open) {
            this.cashDrawerOpened = true;
            SimulatedCashDrawerPanel.getInstance().getBtnClose().setEnabled(
                    true);
            SimulatedCashDrawerPanel.getInstance().updateStatusLabel(
                    this.cashDrawerOpened);
        }

        else {
            this.cashDrawerOpened = false;
            SimulatedCashDrawerPanel.getInstance().getBtnClose().setEnabled(
                    false);
            SimulatedCashDrawerPanel.getInstance().updateStatusLabel(
                    this.cashDrawerOpened);
        }
    }

    public boolean getDrawerOpened() throws JposException {
        return this.cashDrawerOpened;
    }

    public void openDrawer() throws JposException {
        if (this.cashDrawerOpened)
            throw new JposException(JposConst.JPOS_E_FAILURE,
                    "Cash Drawer Already Opened");
        toggleDrawer(true);
        SimulatedDeviceWindow.getInstance().getTabbedPane()
                .setSelectedComponent(SimulatedCashDrawerPanel.getInstance());
    }

    public void waitForDrawerClose(int arg0, int arg1, int arg2, int arg3)
            throws JposException {

    }

    public void compareFirmwareVersion(String arg0, int[] arg1)
            throws JposException {
    }

    public boolean getCapCompareFirmwareVersion() throws JposException {
        return false;
    }

    public boolean getCapUpdateFirmware() throws JposException {
        return false;
    }

    public void updateFirmware(String arg0) throws JposException {
    }

    public boolean getCapStatisticsReporting() throws JposException {
        return false;
    }

    public boolean getCapUpdateStatistics() throws JposException {
        return false;
    }

    public void resetStatistics(String arg0) throws JposException {
    }

    public void retrieveStatistics(String[] arg0) throws JposException {
    }

    public void updateStatistics(String arg0) throws JposException {
    }

    public boolean getCapStatusMultiDrawerDetect() throws JposException {
        return false;
    }

    public int getCapPowerReporting() throws JposException {
        return 0;
    }

    public int getPowerState() throws JposException {
        return 0;
    }

    public boolean getCapStatus() throws JposException {
        return false;
    }

    public void checkHealth(int arg0) throws JposException {
    }

    public void directIO(int arg0, int[] arg1, Object arg2)
            throws JposException {
    }

    public String getCheckHealthText() throws JposException {
        return null;
    }

    public String getDeviceServiceDescription() throws JposException {
        return null;
    }

    public String getPhysicalDeviceDescription() throws JposException {
        return null;
    }

    public String getPhysicalDeviceName() throws JposException {
        return null;
    }

    public boolean getCapServiceAllowManagement() throws JposException {
        return false;
    }

}
