package org.jumpmind.pos.javapos.sim;

import jpos.JposException;
import jpos.services.PINPadService111;

import org.jumpmind.pos.javapos.sim.ui.SimulatedDeviceWindow;
import org.jumpmind.pos.javapos.sim.ui.SimulatedPINPadPanel;

public class SimulatedPINPadService extends AbstractSimulatedService
        implements PINPadService111 {
	
	private String userEnteredPIN;

	@Override
	public void reset() {
		// TODO Auto-generated method stub	
	}

	public void clearInputProperties() throws JposException {
		// TODO Auto-generated method stub
	}

	public void compareFirmwareVersion(String firmwareFileName, int[] result)
			throws JposException {
		// TODO Auto-generated method stub
	}

	public boolean getCapCompareFirmwareVersion() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getCapUpdateFirmware() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateFirmware(String firmwareFileName) throws JposException {
		// TODO Auto-generated method stub
	}

	public boolean getCapStatisticsReporting() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean getCapUpdateStatistics() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	public void resetStatistics(String statisticsBuffer) throws JposException {
		// TODO Auto-generated method stub
	}

	public void retrieveStatistics(String[] statisticsBuffer)
			throws JposException {
		// TODO Auto-generated method stub
	}

	public void updateStatistics(String statisticsBuffer) throws JposException {
		// TODO Auto-generated method stub
	}

	public byte[] getTrack4Data() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTrack4Data(byte[] track4Data) throws JposException {
		// TODO Auto-generated method stub
	}

	public void beginEFTTransaction(String PINPadSystem, int transactionHost)
			throws JposException {
	}

	public void clearInput() throws JposException {
	}

	public void computeMAC(String inMsg, String[] outMsg) throws JposException {
		// TODO Auto-generated method stub
	}

	public void enablePINEntry() throws JposException {
	}

	public void endEFTTransaction(int completionCode) throws JposException {
	}

	public String getAccountNumber() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAdditionalSecurityInformation() throws JposException {
		// TODO Auto-generated method stub
		return "OpenSesame";
	}

	public long getAmount() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getAvailableLanguagesList() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAvailablePromptsList() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCapDisplay() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getCapKeyboard() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	public int getCapLanguage() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getCapMACCalculation() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	public int getCapPowerReporting() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getCapTone() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	public int getDataCount() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getDataEventEnabled() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	public String getEncryptedPIN() throws JposException {
		return userEnteredPIN;
	}

	public int getMaximumPINLength() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getMerchantID() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getMinimumPINLength() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getPINEntryEnabled() throws JposException {
		return false;
	}

	public int getPowerState() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getPrompt() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getPromptLanguage() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTerminalID() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getTrack1Data() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getTrack2Data() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getTrack3Data() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getTransactionType() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setAccountNumber(String accountNumber) throws JposException {
		// TODO Auto-generated method stub
		
	}

	public void setAmount(long amount) throws JposException {
		// TODO Auto-generated method stub
		
	}

	public void setDataEventEnabled(final boolean dataEventEnabled)
			throws JposException {
        invoke(new Runnable() {
            public void run() {
                SimulatedPINPadPanel panel = SimulatedPINPadPanel.getInstance();

                SimulatedDeviceWindow.getInstance().getTabbedPane().setSelectedComponent(
                		SimulatedPINPadPanel.getInstance());
                panel.setEnabled(dataEventEnabled);
            }
        });
	}

	public void setMaximumPINLength(int maximumPINLength) throws JposException {
		// TODO Auto-generated method stub
	}

	public void setMerchantID(String merchantID) throws JposException {
		// TODO Auto-generated method stub
	}

	public void setMinimumPINLength(int minimumPINLength) throws JposException {
		// TODO Auto-generated method stub
	}

	public void setPrompt(int propmpt) throws JposException {
		// TODO Auto-generated method stub
	}

	public void setPromptLanguage(String promptLanguage) throws JposException {
		// TODO Auto-generated method stub
	}

	public void setTerminalID(String terminalID) throws JposException {
		// TODO Auto-generated method stub
	}

	public void setTrack1Data(byte[] track1Data) throws JposException {
		// TODO Auto-generated method stub
	}

	public void setTrack2Data(byte[] track2Data) throws JposException {
		// TODO Auto-generated method stub
	}

	public void setTrack3Data(byte[] track3Data) throws JposException {
		// TODO Auto-generated method stub
	}

	public void setTransactionType(int transactionType) throws JposException {
		// TODO Auto-generated method stub
	}

	public void updateKey(int keyNum, String key) throws JposException {
		// TODO Auto-generated method stub
	}

	public void verifyMAC(String message) throws JposException {
		// TODO Auto-generated method stub
	}

	public void checkHealth(int level) throws JposException {
		// TODO Auto-generated method stub
	}

	public void directIO(int command, int[] data, Object object)
			throws JposException {
		// TODO Auto-generated method stub
	}

	public String getCheckHealthText() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDeviceServiceDescription() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPhysicalDeviceDescription() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPhysicalDeviceName() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserEnteredPIN() {
		return userEnteredPIN;
	}

	public void setUserEnteredPIN(String userEnteredPIN) {
		this.userEnteredPIN = userEnteredPIN;
	}

}
