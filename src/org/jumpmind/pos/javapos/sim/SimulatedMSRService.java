package org.jumpmind.pos.javapos.sim;

import jpos.JposException;
import jpos.services.MSRService111;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jumpmind.pos.javapos.sim.beans.MSRCardBean;

public class SimulatedMSRService extends AbstractSimulatedService implements
        MSRService111 {
    static final Log logger = LogFactory.getLog(SimulatedMSRService.class);

    private MSRCardBean selectedCard;

    private int tracksToWrite = 2;
    private boolean transmitSentinels;
    private boolean autoDisable;
    private boolean dataEventEnabled;
    private boolean decodeData;
    private int errorReportingType;
    private boolean parseDecodeData = true;
    private int tracksToRead = 2;

    public MSRCardBean getSelectedCard() {
        return selectedCard;
    }

    public void setSelectedCard(MSRCardBean selectedCard) {
        this.selectedCard = selectedCard;
    }

    public SimulatedMSRService() {
    }

    @Override
    public void reset() {
        this.selectedCard = new MSRCardBean();
    }

    public String getAccountNumber() throws JposException {
        return this.selectedCard != null ? this.selectedCard.getAccountNumber()
                : null;
    }

    public String getExpirationDate() throws JposException {
        return this.selectedCard != null ? this.selectedCard
                .getExpirationDate() : null;
    }

    public String getFirstName() throws JposException {
        return this.selectedCard != null ? this.selectedCard.getFirstName()
                : null;
    }

    public String getMiddleInitial() throws JposException {
        return this.selectedCard != null ? this.selectedCard.getMiddleInitial()
                : null;
    }

    public String getServiceCode() throws JposException {
        return this.selectedCard != null ? this.selectedCard.getServiceCode()
                : null;
    }

    public String getSuffix() throws JposException {
        return this.selectedCard != null ? this.selectedCard.getSuffix() : null;
    }

    public String getSurname() throws JposException {
        return this.selectedCard != null ? this.selectedCard.getSurName()
                : null;
    }

    public String getTitle() throws JposException {
        return this.selectedCard != null ? this.selectedCard.getTitle() : null;
    }

    public byte[] getTrack1Data() throws JposException {
        return this.selectedCard != null
                && this.selectedCard.getTrack1Data() != null ? this.selectedCard
                .getTrack1Data().getBytes()
                : new byte[0];
    }

    public byte[] getTrack1DiscretionaryData() throws JposException {
        return this.selectedCard != null
                && this.selectedCard.getTrack1DiscretionaryData() != null ? this.selectedCard
                .getTrack1DiscretionaryData().getBytes()
                : new byte[0];
    }

    public byte[] getTrack2Data() throws JposException {
        return this.selectedCard != null
                && this.selectedCard.getTrack2Data() != null ? this.selectedCard
                .getTrack2Data().getBytes()
                : new byte[0];
    }

    public byte[] getTrack2DiscretionaryData() throws JposException {
        return this.selectedCard != null
                && this.selectedCard.getTrack2DiscretionaryData() != null ? this.selectedCard
                .getTrack2DiscretionaryData().getBytes()
                : new byte[0];
    }

    public byte[] getTrack3Data() throws JposException {
        return this.selectedCard != null
                && this.selectedCard.getTrack3Data() != null ? this.selectedCard
                .getTrack3Data().getBytes()
                : new byte[0];
    }

    public byte[] getTrack4Data() throws JposException {
        return this.selectedCard != null
                && this.selectedCard.getTrack4Data() != null ? this.selectedCard
                .getTrack4Data().getBytes()
                : new byte[0];
    }

    public int getDataCount() throws JposException {
        return this.selectedCard != null ? this.selectedCard.getDataCount() : 0;
    }

    public void clearInputProperties() throws JposException {

    }

    public int getCapWritableTracks() throws JposException {

        return 0;
    }

    public int getEncodingMaxLength() throws JposException {

        return 0;
    }

    public int getTracksToWrite() throws JposException {
        return this.tracksToWrite;
    }

    public void setTracksToWrite(int arg0) throws JposException {
        this.tracksToWrite = arg0;
    }

    public void writeTracks(byte[][] arg0, int arg1) throws JposException {

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

    public boolean getCapTransmitSentinels() throws JposException {

        return false;
    }

    public boolean getTransmitSentinels() throws JposException {
        return this.transmitSentinels;
    }

    public void setTransmitSentinels(boolean arg0) throws JposException {
        this.transmitSentinels = arg0;
    }

    public int getCapPowerReporting() throws JposException {

        return 0;
    }

    public int getPowerState() throws JposException {

        return 0;
    }

    public void clearInput() throws JposException {

    }

    public boolean getAutoDisable() throws JposException {
        return this.autoDisable;
    }

    public boolean getCapISO() throws JposException {

        return false;
    }

    public boolean getCapJISOne() throws JposException {

        return false;
    }

    public boolean getCapJISTwo() throws JposException {

        return false;
    }

    public boolean getDataEventEnabled() throws JposException {
        return this.dataEventEnabled;
    }

    public boolean getDecodeData() throws JposException {
        return this.decodeData;
    }

    public int getErrorReportingType() throws JposException {
        return this.errorReportingType;
    }

    public boolean getParseDecodeData() throws JposException {

        return this.parseDecodeData;
    }

    public int getTracksToRead() throws JposException {
        return this.tracksToRead;
    }

    public void setAutoDisable(boolean arg0) throws JposException {
        this.autoDisable = arg0;
    }

    public void setDataEventEnabled(boolean arg0) throws JposException {
        this.dataEventEnabled = arg0;
    }

    public void setDecodeData(boolean arg0) throws JposException {
        this.decodeData = arg0;
    }

    public void setErrorReportingType(int arg0) throws JposException {
        this.errorReportingType = arg0;
    }

    public void setParseDecodeData(boolean arg0) throws JposException {
        this.parseDecodeData = arg0;
    }

    public void setTracksToRead(int arg0) throws JposException {
        this.tracksToRead = arg0;
    }

    public void checkHealth(int arg0) throws JposException {

    }

    public void directIO(int arg0, int[] arg1, Object arg2)
            throws JposException {

    }

    public String getCheckHealthText() throws JposException {

        return "";
    }

    public String getDeviceServiceDescription() throws JposException {

        return "";
    }

    public String getPhysicalDeviceDescription() throws JposException {

        return null;
    }

    public String getPhysicalDeviceName() throws JposException {

        return "";
    }

    public boolean getCapServiceAllowManagement() throws JposException {
        return false;
    }
}
