package org.jumpmind.pos.javapos.sim;

import java.awt.Rectangle;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import jpos.JposException;
import jpos.services.LineDisplayService111;

import org.jumpmind.pos.javapos.sim.ui.SimulatedDeviceWindow;
import org.jumpmind.pos.javapos.sim.ui.SimulatedLineDisplayPanel;

public class SimulatedLineDisplayService extends AbstractSimulatedService
        implements LineDisplayService111 {

    @Override
    public void reset() {
    }

    public void appendText(final String newText) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SimulatedDeviceWindow.getInstance().getTabbedPane()
                        .setSelectedComponent(
                                SimulatedLineDisplayPanel.getInstance());

                StyledDocument doc = SimulatedLineDisplayPanel.getInstance()
                        .getTextArea().getStyledDocument();
                try {
                    doc.insertString(doc.getLength(), newText, doc
                            .getStyle("text"));
                    SimulatedLineDisplayPanel.getInstance().getTextArea()
                            .scrollRectToVisible(
                                    new Rectangle(0, SimulatedLineDisplayPanel
                                            .getInstance().getTextArea()
                                            .getHeight() - 2, 1, 1));

                } catch (BadLocationException e) {
                    logger.error(e, e);
                }
            }
        });
    }

    public void compareFirmwareVersion(String s, int[] ai) throws JposException {
    }

    public boolean getCapCompareFirmwareVersion() throws JposException {
        return false;
    }

    public boolean getCapUpdateFirmware() throws JposException {
        return false;
    }

    public void updateFirmware(String s) throws JposException {
    }

    public boolean getCapStatisticsReporting() throws JposException {
        return false;
    }

    public boolean getCapUpdateStatistics() throws JposException {
        return false;
    }

    public void resetStatistics(String s) throws JposException {
    }

    public void retrieveStatistics(String[] as) throws JposException {
    }

    public void updateStatistics(String s) throws JposException {
    }

    public void displayBitmap(String s, int i, int j, int k)
            throws JposException {
    }

    public boolean getCapBitmap() throws JposException {
        return false;
    }

    public boolean getCapMapCharacterSet() throws JposException {
        return false;
    }

    public boolean getCapScreenMode() throws JposException {
        return false;
    }

    public boolean getMapCharacterSet() throws JposException {
        return false;
    }

    public int getMaximumX() throws JposException {
        return 0;
    }

    public int getMaximumY() throws JposException {
        return 0;
    }

    public int getScreenMode() throws JposException {
        return 0;
    }

    public String getScreenModeList() throws JposException {
        return "";
    }

    public void setBitmap(int i, String s, int j, int k, int l)
            throws JposException {
    }

    public void setMapCharacterSet(boolean flag) throws JposException {
    }

    public void setScreenMode(int i) throws JposException {
    }

    public void defineGlyph(int i, byte[] abyte0) throws JposException {
    }

    public int getBlinkRate() throws JposException {
        return 0;
    }

    public boolean getCapBlinkRate() throws JposException {
        return false;
    }

    public int getCapCursorType() throws JposException {
        return 0;
    }

    public boolean getCapCustomGlyph() throws JposException {
        return false;
    }

    public int getCapReadBack() throws JposException {
        return 0;
    }

    public int getCapReverse() throws JposException {
        return 0;
    }

    public int getCursorType() throws JposException {
        return 0;
    }

    public String getCustomGlyphList() throws JposException {
        return "";
    }

    public int getGlyphHeight() throws JposException {
        return 0;
    }

    public int getGlyphWidth() throws JposException {
        return 0;
    }

    public void readCharacterAtCursor(int[] ai) throws JposException {
    }

    public void setBlinkRate(int i) throws JposException {
    }

    public void setCursorType(int i) throws JposException {
    }

    public int getCapPowerReporting() throws JposException {
        return 0;
    }

    public int getPowerState() throws JposException {
        return 0;
    }

    public void clearDescriptors() throws JposException {
    }

    public void clearText() throws JposException {
        SimulatedLineDisplayPanel.getInstance().getTextArea().setText("");
    }

    public void createWindow(int i, int j, int k, int l, int i1, int j1)
            throws JposException {
    }

    public void destroyWindow() throws JposException {
    }

    public void displayText(String s, int i) throws JposException {
        appendText(s);
    }

    public void displayTextAt(int i, int j, String s, int k)
            throws JposException {
        appendText("\n" + s);
    }

    public int getCapBlink() throws JposException {
        return 0;
    }

    public boolean getCapBrightness() throws JposException {
        return false;
    }

    public int getCapCharacterSet() throws JposException {
        return 0;
    }

    public boolean getCapDescriptors() throws JposException {
        return false;
    }

    public boolean getCapHMarquee() throws JposException {
        return false;
    }

    public boolean getCapICharWait() throws JposException {
        return false;
    }

    public boolean getCapVMarquee() throws JposException {
        return false;
    }

    public int getCharacterSet() throws JposException {
        return 0;
    }

    public String getCharacterSetList() throws JposException {
        return "";
    }

    public int getColumns() throws JposException {
        return 0;
    }

    public int getCurrentWindow() throws JposException {
        return 0;
    }

    public int getCursorColumn() throws JposException {
        return 0;
    }

    public int getCursorRow() throws JposException {
        return 0;
    }

    public boolean getCursorUpdate() throws JposException {
        return false;
    }

    public int getDeviceBrightness() throws JposException {
        return 0;
    }

    public int getDeviceColumns() throws JposException {
        return 0;
    }

    public int getDeviceDescriptors() throws JposException {
        return 0;
    }

    public int getDeviceRows() throws JposException {
        return 0;
    }

    public int getDeviceWindows() throws JposException {
        return 0;
    }

    public int getInterCharacterWait() throws JposException {
        return 0;
    }

    public int getMarqueeFormat() throws JposException {
        return 0;
    }

    public int getMarqueeRepeatWait() throws JposException {
        return 0;
    }

    public int getMarqueeType() throws JposException {
        return 0;
    }

    public int getMarqueeUnitWait() throws JposException {
        return 0;
    }

    public int getRows() throws JposException {
        return 0;
    }

    public void refreshWindow(int i) throws JposException {
    }

    public void scrollText(int i, int j) throws JposException {
    }

    public void setCharacterSet(int i) throws JposException {
    }

    public void setCurrentWindow(int i) throws JposException {
    }

    public void setCursorColumn(int i) throws JposException {
    }

    public void setCursorRow(int i) throws JposException {
    }

    public void setCursorUpdate(boolean flag) throws JposException {
    }

    public void setDescriptor(int i, int j) throws JposException {
    }

    public void setDeviceBrightness(int i) throws JposException {
    }

    public void setInterCharacterWait(int i) throws JposException {
    }

    public void setMarqueeFormat(int i) throws JposException {
    }

    public void setMarqueeRepeatWait(int i) throws JposException {
    }

    public void setMarqueeType(int i) throws JposException {
    }

    public void setMarqueeUnitWait(int i) throws JposException {
    }

    public void checkHealth(int i) throws JposException {
    }

    public void directIO(int i, int[] ai, Object obj) throws JposException {
    }

    public String getCheckHealthText() throws JposException {
        return "";
    }

    public String getDeviceServiceDescription() throws JposException {
        return "";
    }

    public String getPhysicalDeviceDescription() throws JposException {
        return "";
    }

    public String getPhysicalDeviceName() throws JposException {
        return "";
    }

    public boolean getCapServiceAllowManagement() throws JposException {
        return false;
    }

}
