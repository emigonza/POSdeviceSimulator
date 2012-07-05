package org.jumpmind.pos.javapos.sim;

import jpos.JposException;
import jpos.services.ScannerService111;

public class SimulatedScannerService extends AbstractSimulatedService implements
        ScannerService111 {

    public final static int DEVICE_VERSION = 1011000;

    private boolean dataEventEnabled;
    private boolean autoDisable;
    private boolean decodeData;
    private String healthCheckText;
    private byte[] scanData;

    @Override
    public void reset() {
        dataEventEnabled = false;
        autoDisable = false;
        decodeData = false;
        healthCheckText = null;
        scanData = null;
    }

    public void clearInputProperties() throws JposException {
        checkIfOpen();
    }

    public void compareFirmwareVersion(String arg0, int[] arg1)
            throws JposException {
        checkIfOpen();
    }

    public boolean getCapCompareFirmwareVersion() throws JposException {
        checkIfOpen();
        return false;
    }

    public boolean getCapUpdateFirmware() throws JposException {
        checkIfOpen();
        return false;
    }

    public void updateFirmware(String arg0) throws JposException {
        checkIfOpen();
    }

    public boolean getCapStatisticsReporting() throws JposException {
        checkIfOpen();
        return false;
    }

    public boolean getCapUpdateStatistics() throws JposException {
        checkIfOpen();
        return false;
    }

    public void resetStatistics(String arg0) throws JposException {
        checkIfOpen();
    }

    public void retrieveStatistics(String[] arg0) throws JposException {
        checkIfOpen();
    }

    public void updateStatistics(String arg0) throws JposException {
        checkIfOpen();
    }

    public int getCapPowerReporting() throws JposException {
        checkIfOpen();
        return 0;
    }

    public int getPowerState() throws JposException {
        checkIfOpen();
        return 0;
    }

    public void clearInput() throws JposException {
        checkIfOpen();
        scanData = null;
    }

    public boolean getAutoDisable() throws JposException {
        checkIfOpen();
        return autoDisable;
    }

    public int getDataCount() throws JposException {
        checkIfOpen();
        int dataCount = 0;
        if (scanData != null) {
            dataCount = 1;
        }
        return dataCount;
    }

    public boolean getDataEventEnabled() throws JposException {
        checkIfOpen();
        return dataEventEnabled;
    }

    public boolean getDecodeData() throws JposException {
        checkIfOpen();
        return decodeData;
    }

    public byte[] getScanData() throws JposException {
        checkIfOpen();
        byte[] data = scanData;
        scanData = null;
        return data;
    }

    public byte[] getScanDataLabel() throws JposException {
        checkIfOpen();
        return "Simulated Scan Data Label".getBytes();
    }

    /**
     * Indicates type of barcode scanned... defaulting to EAN 8
     */
    public int getScanDataType() throws JposException {
        checkIfOpen();
        return 103;
    }

    public void setAutoDisable(boolean autoDisable) throws JposException {
        checkIfOpen();
        this.autoDisable = autoDisable;
    }

    public void setDataEventEnabled(boolean dataEventEnabled)
            throws JposException {
        checkIfOpen();
        this.dataEventEnabled = dataEventEnabled;
    }

    public void setDecodeData(boolean decodeData) throws JposException {
        checkIfOpen();
        this.decodeData = decodeData;
    }

    public void checkHealth(int arg0) throws JposException {
        checkIfOpen();
        checkIfClaimed();
        this.healthCheckText = "Health Check Successful!";
    }

    public void directIO(int arg0, int[] arg1, Object arg2)
            throws JposException {
        checkIfOpen();
    }

    public String getCheckHealthText() throws JposException {
        checkIfOpen();
        return healthCheckText;
    }

    public String getDeviceServiceDescription() throws JposException {
        checkIfOpen();
        return "JumpMind JumpPOS Simulated Scanner for JavaPOS 1.1.1";
    }

    @Override
    public int getDeviceServiceVersion() throws JposException {
        checkIfOpen();
        return DEVICE_VERSION;
    }

    public String getPhysicalDeviceDescription() throws JposException {
        checkIfOpen();
        return "JumpMind JumpPOS Simulated Scanner for JavaPOS 1.1.1";
    }

    public String getPhysicalDeviceName() throws JposException {
        checkIfOpen();
        return "JumpMind JumpPOS Simulated Scanner";
    }

    public void setScanData(byte[] scanData) {
        this.scanData = scanData;
    }

    public boolean getCapServiceAllowManagement() throws JposException {
        return false;
    }

}
