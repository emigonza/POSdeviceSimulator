package org.jumpmind.pos.javapos.sim;

import jpos.JposException;
import jpos.services.MICRService111;

import org.jumpmind.pos.javapos.sim.beans.MICRBean;

public class SimulatedMICRService extends AbstractSimulatedService implements
        MICRService111 {
    private MICRBean micr;

    @Override
    public void reset() {
        this.micr = new MICRBean();
    }

    public void clearInputProperties() throws JposException {
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

    public int getCapPowerReporting() throws JposException {
        return 0;
    }

    public int getPowerState() throws JposException {
        return 0;
    }

    public void beginInsertion(int arg0) throws JposException {
    }

    public void beginRemoval(int arg0) throws JposException {
    }

    public void clearInput() throws JposException {
    }

    public void endInsertion() throws JposException {
    }

    public void endRemoval() throws JposException {
    }

    public String getAccountNumber() throws JposException {
        return getMicr() != null ? getMicr().getAccountNumber() : null;
    }

    public String getAmount() throws JposException {
        return getMicr() != null ? getMicr().getAmount() : null;
    }

    public boolean getAutoDisable() throws JposException {
        return false;
    }

    public String getBankNumber() throws JposException {
        return getMicr() != null ? getMicr().getBankNumber() : null;
    }

    public boolean getCapValidationDevice() throws JposException {
        return false;
    }

    public int getCheckType() throws JposException {
        return getMicr() != null ? getMicr().getCheckType() : 0;
    }

    public int getCountryCode() throws JposException {
        return getMicr() != null ? getMicr().getCountryCode() : 0;
    }

    public int getDataCount() throws JposException {
        return 0;
    }

    public boolean getDataEventEnabled() throws JposException {
        return false;
    }

    public String getEPC() throws JposException {
        return getMicr() != null ? getMicr().getEpc() : null;
    }

    public String getRawData() throws JposException {
        return getMicr() != null ? getMicr().getRawData() : null;
    }

    public String getSerialNumber() throws JposException {
        return getMicr() != null ? getMicr().getSerialNumber() : null;
    }

    public String getTransitNumber() throws JposException {
        return getMicr() != null ? getMicr().getTransitNumber() : null;
    }

    public void setAutoDisable(boolean arg0) throws JposException {
    }

    public void setDataEventEnabled(boolean arg0) throws JposException {
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

    public MICRBean getMicr() {
        return micr;
    }

    public void setMicr(MICRBean micr) {
        this.micr = micr;
    }

    public boolean getCapServiceAllowManagement() throws JposException {
        return false;
    }

}
