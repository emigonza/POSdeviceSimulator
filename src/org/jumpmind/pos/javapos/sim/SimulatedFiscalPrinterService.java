package org.jumpmind.pos.javapos.sim;

import java.awt.Rectangle;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import jpos.FiscalPrinterConst;
import jpos.JposConst;
import jpos.JposException;
import jpos.services.FiscalPrinterService111;
import jpos.services.POSPrinterService12;
import jpos.services.POSPrinterService13;
import jpos.services.POSPrinterService14;
import jpos.services.POSPrinterService15;
import jpos.services.POSPrinterService16;
import jpos.services.POSPrinterService17;
import jpos.services.POSPrinterService18;
import jpos.services.POSPrinterService19;
import jpos.services.POSPrinterService110;
import jpos.services.POSPrinterService111;
import jpos.services.POSPrinterService112;

import org.apache.commons.lang.StringUtils;
import org.jumpmind.pos.javapos.sim.ui.SimulatedDeviceWindow;
import org.jumpmind.pos.javapos.sim.ui.SimulatedFiscalPrinterPanel;
import org.jumpmind.pos.javapos.sim.util.DecimalFormatter;

public class SimulatedFiscalPrinterService extends AbstractSimulatedService
        implements FiscalPrinterService111,POSPrinterService12,POSPrinterService13,POSPrinterService14,POSPrinterService15,POSPrinterService16,POSPrinterService17,POSPrinterService18,POSPrinterService19,POSPrinterService110,POSPrinterService111, POSPrinterService112 {

    // member variables
    protected List<String> headerLines = new ArrayList<String>(NUM_HEADER_LINES);
    protected List<String> trailerLines = new ArrayList<String>(NUM_TRAILER_LINES);
    protected BigDecimal runningTotalOfTenders = BigDecimal.ZERO;
    protected Date date = new Date();

    // constants
    protected static final int RECEIPT_LINE_SIZE = 40;
    protected static final SimpleDateFormat dateFormatter = new SimpleDateFormat(
            "ddMMyyyyhhmm");
    protected static final String RECEIPT_INDENT = "           ";
    protected static final Locale receiptLocale = new Locale("es", "ES");
    protected static final String TOTAL_TEXT = "Totale";
    protected static final String CHANGE_DUE_TEXT = "Resto";

    // fiscal printer properties
    protected static final int NUM_HEADER_LINES = 5;
    protected static final int NUM_TRAILER_LINES = 2;
    protected static final boolean CAP_ADDITIONAL_HEADER = true;
    protected static final boolean CAP_ADDITIONAL_LINES = true;
    protected static final boolean CAP_ADDITIONAL_TRAILER = true;
    protected static final boolean CAP_CHANGE_DUE = true;
    protected static final boolean CAP_CHECK_TOTAL = false;
    protected static final boolean CAP_DOUBLE_WIDTH = false;
    protected static final boolean CAP_DUPLICATE_RECEIPT = false;
    protected static final boolean CAP_FISCAL_RECEIPT_STATION = false;
    protected static final boolean CAP_FISCAL_RECEIPT_TYPE = false;
    protected static final boolean CAP_FIXED_OUTPUT = false;
    protected static final boolean CAP_HAS_VAT_TABLE = false;
    protected static final boolean CAP_INDEPENDENT_HEADER = true;
    protected static final boolean CAP_ITEM_LIST = false;
    protected static final boolean CAP_MULTI_CONTRACTOR = false;
    protected static final boolean CAP_NON_FISCAL_MODE = true;
    protected static final boolean CAP_POST_PRE_LINE = true;
    protected static final boolean CAP_PREDEFINED_PAYMENT_LINES = false;
    protected static final boolean CAP_REMAINING_FISCAL_MEMORY = true;
    protected static final boolean CAP_SET_HEADER = true;
    protected static final boolean CAP_SET_POS_ID = true;
    protected static final boolean CAP_SET_STORE_FISCAL_ID = true;
    protected static final boolean CAP_SET_TRAILER = true;
    protected static final boolean CAP_SLP_FISCAL_DOCUMENT = true;
    protected static final boolean CAP_SLP_PRESENT = true;
    protected int fiscalReceiptStation = FiscalPrinterConst.FPTR_RS_RECEIPT;
    protected int fiscalReceiptType = FiscalPrinterConst.FPTR_RT_SALES;
    protected boolean asyncMode = false;
    protected boolean checkTotal = false;
    protected boolean dayOpened = false;
    protected boolean duplicateReceipt = false;
    protected String changeDue;
    protected int printerState = FiscalPrinterConst.FPTR_PS_MONITOR;
    protected int dateType = FiscalPrinterConst.FPTR_DT_RTC;
    protected int quantityDecimalPlaces = 0;
    protected int amountDecimalPlaces = 0;
    protected String additionalHeader;
    protected String additionalTrailer;
    protected String preLine;
    protected String postLine;
    protected String storeFiscalId;
    protected String posId;
    protected String cashierId;

    protected void displayText(final String newText) {
        System.out.println ("Fiscal Printer displayText:"+newText);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SimulatedDeviceWindow.getInstance().getTabbedPane()
                        .setSelectedComponent(
                                SimulatedFiscalPrinterPanel.getInstance());

                StyledDocument doc = SimulatedFiscalPrinterPanel.getInstance()
                        .getTextArea().getStyledDocument();

                SimulatedFiscalPrinterPanel.getInstance().getTextArea()
                        .getSize().width = RECEIPT_LINE_SIZE;
                try {
                    doc.insertString(doc.getLength(), newText, doc
                            .getStyle("text"));
                    SimulatedFiscalPrinterPanel
                            .getInstance()
                            .getTextArea()
                            .scrollRectToVisible(
                                    new Rectangle(
                                            0,
                                            SimulatedFiscalPrinterPanel
                                                    .getInstance()
                                                    .getTextArea().getHeight() - 2,
                                            1, 1));

                } catch (BadLocationException e) {
                    logger.error(e, e);
                }
            }
        });
    }

    public void beginFiscalDocument(int documentAmount) throws JposException {
        
        System.out.println ("beginFiscalDocument:"+documentAmount);
        if (!getCapSlpFiscalDocument()
                || printerState != FiscalPrinterConst.FPTR_PS_MONITOR) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Cannot print fiscal document at this time.");
        }

        dayOpened = true;

        printerState = FiscalPrinterConst.FPTR_PS_FISCAL_DOCUMENT;

    }

    public void beginFiscalReceipt(boolean printHeader) throws JposException {

        System.out.println ("beginFiscalReceipt:"+printHeader);
        if (getCapFiscalReceiptStation()) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Switching of fiscal receipt stations method not supported.");
        }

        if (getCapFiscalReceiptType()) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Switching of fiscal receipt types method not supported.");
        }

        dayOpened = true;

        sendMarkerToPrinter("Begin Fiscal Receipt");

        if (printHeader && getCapIndependentHeader()) {
            for (String headerLine : headerLines) {
                sendToPrinterAsCenteredLine(headerLine);
            }
        }

        if (getCapSetStoreFiscalID()) {
            sendToPrinterAsCenteredLine(storeFiscalId);
        }

        if (getCapAdditionalHeader()) {
            sendToPrinterAsCenteredLine(getAdditionalHeader());
        }

        printerState = FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT;

        runningTotalOfTenders = BigDecimal.ZERO;

    }

    public void beginFixedOutput(int arg0, int arg1) throws JposException {
        System.out.println ("beginFixedOutput");
        if (!getCapFixedOutput()) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Fixed output method not supported.");
        }

        printerState = FiscalPrinterConst.FPTR_PS_FIXED_OUTPUT;

    }

    public void beginInsertion(int arg0) throws JposException {

        System.out.println ("beginInsertion");
        if (!getCapSlpPresent()) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "No slip exists to print to.");
        }

        if (printerState != FiscalPrinterConst.FPTR_PS_FISCAL_DOCUMENT) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Must call beginFiscalDocument() first!");
        }

        sendMarkerToPrinter("Begin Insertion");

    }

    public void beginItemList(int arg0) throws JposException {
        System.out.println ("beginItemList");
        if (!getCapItemList()) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Item list mode not allowed.");
        }

        printerState = FiscalPrinterConst.FPTR_PS_ITEM_LIST;

    }

    public void beginNonFiscal() throws JposException {
        
        System.out.println ("beginNonFiscal");
        if (!getCapNonFiscalMode()) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Non-fiscal mode not allowed.");
        }

        printerState = FiscalPrinterConst.FPTR_PS_NONFISCAL;

    }

    public void beginRemoval(int arg0) throws JposException {
        
        System.out.println ("beginRemoval");
        if (!getCapSlpPresent()) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "No slip exists to print to.");
        }

        if (printerState != FiscalPrinterConst.FPTR_PS_FISCAL_DOCUMENT) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Must call beginFiscalDocument() first!");
        }

        sendMarkerToPrinter("Begin Removal");

    }

    public void beginTraining() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "beginTraining method not supported.");
    }

    public void checkHealth(int arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "checkHealth method not supported.");
    }

    public void clearError() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "clearError method not supported.");
    }

    public void clearOutput() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "clearOutput method not supported.");
    }

    public void compareFirmwareVersion(String arg0, int[] arg1)
            throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "compareFirmwareVersion method not supported.");
    }

    public void directIO(int arg0, int[] arg1, Object arg2)
            throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "directIO method not supported.");
    }

    public void endFiscalDocument() throws JposException {
        System.out.println ("endFiscalDocument");
        if (!getCapSlpFiscalDocument()) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Printing of fiscal documents method not supported.");
        }

        sendMarkerToPrinter("End Fiscal Document");

        printerState = FiscalPrinterConst.FPTR_PS_MONITOR;

    }

    public void endFiscalReceipt(boolean printHeader) throws JposException {
        System.out.println ("endFiscalReceipt");
        if (printHeader) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Printing of header as fiscal receipt is ending is method not supported.");
        }

        for (String trailerLine : trailerLines) {
            sendToPrinterAsCenteredLine(trailerLine);
        }

        sendToPrinterAsCenteredLine("Wiegman Fiscalization Services");
        sendToPrinterAsCenteredLine(Long.toString(date.getTime()));

        if (getCapAdditionalTrailer()) {
            sendToPrinterAsCenteredLine(getAdditionalTrailer());
        }

        sendMarkerToPrinter("End Fiscal Receipt");

        this.runningTotalOfTenders = BigDecimal.ZERO;

        printerState = FiscalPrinterConst.FPTR_PS_MONITOR;

    }

    public void endFixedOutput() throws JposException {

        printerState = FiscalPrinterConst.FPTR_PS_MONITOR;

    }

    public void endInsertion() throws JposException {

        if (!getCapSlpPresent()) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "No slip exists to print to.");
        }

        if (printerState != FiscalPrinterConst.FPTR_PS_FISCAL_DOCUMENT) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Must call beginFiscalDocument() first!");
        }

        sendMarkerToPrinter("End Insertion");

    }

    public void endItemList() throws JposException {

        printerState = FiscalPrinterConst.FPTR_PS_MONITOR;

    }

    public void endNonFiscal() throws JposException {

        printerState = FiscalPrinterConst.FPTR_PS_MONITOR;

    }

    public void endRemoval() throws JposException {

        if (!getCapSlpPresent()) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "No slip exists to print to.");
        }

        if (printerState != FiscalPrinterConst.FPTR_PS_FISCAL_DOCUMENT) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Must call beginFiscalDocument() first!");
        }

        sendMarkerToPrinter("End Removal");

    }

    public void endTraining() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "endTraining method not supported.");
    }

    public int getActualCurrency() throws JposException {
        return 0;
    }

    public String getAdditionalHeader() throws JposException {
        return additionalHeader;
    }

    public String getAdditionalTrailer() throws JposException {
        return additionalTrailer;
    }

    public int getAmountDecimalPlace() throws JposException {
        return amountDecimalPlaces;
    }

    public int getAmountDecimalPlaces() throws JposException {
        return amountDecimalPlaces;
    }

    public boolean getAsyncMode() throws JposException {
        return asyncMode;
    }

    public boolean getCapAdditionalHeader() throws JposException {
        return CAP_ADDITIONAL_HEADER;
    }

    public boolean getCapAdditionalLines() throws JposException {
        return CAP_ADDITIONAL_LINES;
    }

    public boolean getCapAdditionalTrailer() throws JposException {
        return CAP_ADDITIONAL_TRAILER;
    }

    public boolean getCapAmountAdjustment() throws JposException {
        return false;
    }

    public boolean getCapAmountNotPaid() throws JposException {
        return false;
    }

    public boolean getCapChangeDue() throws JposException {
        return CAP_CHANGE_DUE;
    }

    public boolean getCapCheckTotal() throws JposException {
        return CAP_CHECK_TOTAL;
    }

    public boolean getCapCompareFirmwareVersion() throws JposException {
        return false;
    }

    public boolean getCapCoverSensor() throws JposException {
        return false;
    }

    public boolean getCapDoubleWidth() throws JposException {
        return CAP_DOUBLE_WIDTH;
    }

    public boolean getCapDuplicateReceipt() throws JposException {
        return CAP_DUPLICATE_RECEIPT;
    }

    public boolean getCapEmptyReceiptIsVoidable() throws JposException {
        return false;
    }

    public boolean getCapFiscalReceiptStation() throws JposException {
        return CAP_FISCAL_RECEIPT_STATION;
    }

    public boolean getCapFiscalReceiptType() throws JposException {
        return CAP_FISCAL_RECEIPT_TYPE;
    }

    public boolean getCapFixedOutput() throws JposException {
        return CAP_FIXED_OUTPUT;
    }

    public boolean getCapHasVatTable() throws JposException {
        return CAP_HAS_VAT_TABLE;
    }

    public boolean getCapIndependentHeader() throws JposException {
        return CAP_INDEPENDENT_HEADER;
    }

    public boolean getCapItemList() throws JposException {
        return CAP_ITEM_LIST;
    }

    public boolean getCapJrnEmptySensor() throws JposException {
        return false;
    }

    public boolean getCapJrnNearEndSensor() throws JposException {
        return false;
    }

    public boolean getCapJrnPresent() throws JposException {
        return false;
    }

    public boolean getCapMultiContractor() throws JposException {
        return CAP_MULTI_CONTRACTOR;
    }

    public boolean getCapNonFiscalMode() throws JposException {
        return CAP_NON_FISCAL_MODE;
    }

    public boolean getCapOnlyVoidLastItem() throws JposException {
        return false;
    }

    public boolean getCapOrderAdjustmentFirst() throws JposException {
        return false;
    }

    public boolean getCapPackageAdjustment() throws JposException {
        return false;
    }

    public boolean getCapPercentAdjustment() throws JposException {
        return false;
    }

    public boolean getCapPositiveAdjustment() throws JposException {
        return false;
    }

    public boolean getCapPositiveSubtotalAdjustment() throws JposException {
        return false;
    }

    public boolean getCapPostPreLine() throws JposException {
        return CAP_POST_PRE_LINE;
    }

    public boolean getCapPowerLossReport() throws JposException {
        return false;
    }

    public int getCapPowerReporting() throws JposException {
        return 0;
    }

    public boolean getCapPredefinedPaymentLines() throws JposException {
        return CAP_PREDEFINED_PAYMENT_LINES;
    }

    public boolean getCapReceiptNotPaid() throws JposException {
        return false;
    }

    public boolean getCapRecEmptySensor() throws JposException {
        return false;
    }

    public boolean getCapRecNearEndSensor() throws JposException {
        return false;
    }

    public boolean getCapRecPresent() throws JposException {
        return false;
    }

    public boolean getCapRemainingFiscalMemory() throws JposException {
        return false;
    }

    public boolean getCapReservedWord() throws JposException {
        return false;
    }

    public boolean getCapSetCurrency() throws JposException {
        return false;
    }

    public boolean getCapSetHeader() throws JposException {
        return CAP_SET_HEADER;
    }

    public boolean getCapSetPOSID() throws JposException {
        return CAP_SET_POS_ID;
    }

    public boolean getCapSetStoreFiscalID() throws JposException {
        return CAP_SET_STORE_FISCAL_ID;
    }

    public boolean getCapSetTrailer() throws JposException {
        return CAP_SET_TRAILER;
    }

    public boolean getCapSetVatTable() throws JposException {
        return false;
    }

    public boolean getCapSlpEmptySensor() throws JposException {
        return false;
    }

    public boolean getCapSlpFiscalDocument() throws JposException {
        return CAP_SLP_FISCAL_DOCUMENT;
    }

    public boolean getCapSlpFullSlip() throws JposException {
        return false;
    }

    public boolean getCapSlpNearEndSensor() throws JposException {
        return false;
    }

    public boolean getCapSlpPresent() throws JposException {
        return CAP_SLP_PRESENT;
    }

    public boolean getCapSlpValidation() throws JposException {
        return false;
    }

    public boolean getCapStatisticsReporting() throws JposException {
        return false;
    }

    public boolean getCapSubAmountAdjustment() throws JposException {
        return false;
    }

    public boolean getCapSubPercentAdjustment() throws JposException {
        return false;
    }

    public boolean getCapSubtotal() throws JposException {
        return false;
    }

    public boolean getCapTotalizerType() throws JposException {
        return false;
    }

    public boolean getCapTrainingMode() throws JposException {
        return false;
    }

    public boolean getCapUpdateFirmware() throws JposException {
        return false;
    }

    public boolean getCapUpdateStatistics() throws JposException {
        return false;
    }

    public boolean getCapValidateJournal() throws JposException {
        return false;
    }

    public boolean getCapXReport() throws JposException {
        return false;
    }

    public String getChangeDue() throws JposException {
        return changeDue;
    }

    public String getCheckHealthText() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getCheckHealthText method not supported.");
    }

    public boolean getCheckTotal() throws JposException {
        return checkTotal;
    }

    public int getContractorId() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getContractorId method not supported.");
    }

    public int getCountryCode() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getCountryCode method not supported.");
    }

    public boolean getCoverOpen() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getCoverOpen method not supported.");
    }

    public void getData(int arg0, int[] arg1, String[] arg2)
            throws JposException {
        // TODO - implement print bitmap
    }

    public void getDate(String[] arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getDate method not supported.");
    }

    public int getDateType() throws JposException {
        return dateType;
    }

    public boolean getDayOpened() throws JposException {
        return dayOpened;
    }

    public int getDescriptionLength() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getDescriptionLength method not supported.");
    }

    public String getDeviceServiceDescription() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getDeviceServiceDescription method not supported.");
    }

    public boolean getDuplicateReceipt() throws JposException {
        return duplicateReceipt;
    }

    public int getErrorLevel() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getErrorLevel method not supported.");
    }

    public int getErrorOutID() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getErrorOutID method not supported.");
    }

    public int getErrorState() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getErrorState method not supported.");
    }

    public int getErrorStation() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getErrorStation method not supported.");
    }

    public String getErrorString() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getErrorString method not supported.");
    }

    public int getFiscalReceiptStation() throws JposException {
        return fiscalReceiptStation;
    }

    public int getFiscalReceiptType() throws JposException {
        return fiscalReceiptType;
    }

    public boolean getFlagWhenIdle() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getFlagWhenIdle method not supported.");
    }

    public boolean getJrnEmpty() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getJrnEmpty method not supported.");
    }

    public boolean getJrnNearEnd() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getJrnNearEnd method not supported.");
    }

    public int getMessageLength() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getMessageLength method not supported.");
    }

    public int getMessageType() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getMessageType method not supported.");
    }

    public int getNumHeaderLines() throws JposException {
        return NUM_HEADER_LINES;
    }

    public int getNumTrailerLines() throws JposException {
        return NUM_TRAILER_LINES;
    }

    public int getNumVatRates() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getNumVatRates method not supported.");
    }

    public int getOutputID() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getOutputID method not supported.");
    }

    public String getPhysicalDeviceDescription() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getPhysicalDeviceDescription method not supported.");
    }

    public String getPhysicalDeviceName() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getPhysicalDeviceName method not supported.");
    }

    public String getPostLine() throws JposException {

        if (!getCapPostPreLine()) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "PostLine statements method not supported.");
        }

        return postLine;

    }

    public int getPowerState() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getPowerState method not supported.");
    }

    public String getPredefinedPaymentLines() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getPredefinedPaymentLines method not supported.");
    }

    public String getPreLine() throws JposException {

        if (!getCapPostPreLine()) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "PreLine statements method not supported.");
        }

        return preLine;

    }

    public int getPrinterState() throws JposException {
        return printerState;
    }

    public int getQuantityDecimalPlaces() throws JposException {
        return quantityDecimalPlaces;
    }

    public int getQuantityLength() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getQuantityLength method not supported.");
    }

    public boolean getRecEmpty() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getRecEmpty method not supported.");
    }

    public boolean getRecNearEnd() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getRecNearEnd method not supported.");
    }

    public int getRemainingFiscalMemory() throws JposException {
        if (!CAP_REMAINING_FISCAL_MEMORY) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "getRemainingFiscalMemory method not supported.");
        } else {
            return 5;
        }
    }

    public String getReservedWord() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getReservedWord method not supported.");
    }

    public int getSlipSelection() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getSlipSelection method not supported.");
    }

    public boolean getSlpEmpty() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getSlpEmpty method not supported.");
    }

    public boolean getSlpNearEnd() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getSlpNearEnd method not supported.");
    }

    public void getTotalizer(int arg0, int arg1, String[] arg2)
            throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getTotalizer method not supported.");
    }

    public int getTotalizerType() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getTotalizerType method not supported.");
    }

    public boolean getTrainingModeActive() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getTrainingModeActive method not supported.");
    }

    public void getVatEntry(int arg0, int arg1, int[] arg2)
            throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "getVatEntry method not supported.");
    }

    public void printDuplicateReceipt() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printDuplicateReceipt method not supported.");
    }

    public void printFiscalDocumentLine(String arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printFiscalDocumentLine method not supported.");
    }

    public void printFixedOutput(int arg0, int arg1, String arg2)
            throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printFixedOutput method not supported.");
    }

    public void printNormal(int station, String value) throws JposException {
        if (printerState != FiscalPrinterConst.FPTR_PS_NONFISCAL) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Printer must be in non-fiscal mode.");
        }

        sendToPrinterAsLine(value);

    }

    public void printPeriodicTotalsReport(String arg0, String arg1)
            throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printPeriodicTotalsReport method not supported.");
    }

    public void printPowerLossReport() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printPowerLossReport method not supported.");
    }

    public void printRecCash(long amount) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printRecCash method not supported.");
    }

    public void printRecItem(String description, long price, int quantity,
            int vatInfo, long unitPrice, String unitName) throws JposException {

        if (printerState != FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT
                && printerState != FiscalPrinterConst.FPTR_PS_FISCAL_DOCUMENT) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Printer must be in fiscal mode.");
        }

        if (getCapPostPreLine() && getPreLine() != null) {
            sendToPrinterAsLine(" " + getPreLine());
            setPreLine(null);
        }

        sendToPrinterAsLine(description, DecimalFormatter.formatCurrency(
                new BigDecimal(price), receiptLocale, true));

        if (getCapPostPreLine() && getPostLine() != null) {
            sendToPrinterAsLine(" " + getPostLine());
            setPostLine(null);
        }

    }

    public void printRecItemAdjustment(int adjustmentType, String description,
            long amount, int vatInfo) throws JposException {

        if (printerState != FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT
                && printerState != FiscalPrinterConst.FPTR_PS_FISCAL_DOCUMENT) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Printer must be in fiscal mode.");
        }

        sendToPrinterAsIndentedLine("  " + description, DecimalFormatter
                .formatCurrency(new BigDecimal(amount), receiptLocale, true));
    }

    public void printRecItemAdjustmentVoid(int arg0, String arg1, long arg2,
            int arg3) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printRecItemAdjustmentVoid method not supported.");
    }

    public void printRecItemFuel(String arg0, long arg1, int arg2, int arg3,
            long arg4, String arg5, long arg6, String arg7)
            throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printRecItemFuel method not supported.");
    }

    public void printRecItemFuelVoid(String arg0, long arg1, int arg2, long arg3)
            throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printRecItemFuelVoid method not supported.");
    }

    public void printRecItemVoid(String arg0, long arg1, int arg2, int arg3,
            long arg4, String arg5) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printRecItemVoid method not supported.");
    }

    public void printRecMessage(String message) throws JposException {

        if (printerState != FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT
                && printerState != FiscalPrinterConst.FPTR_PS_FISCAL_DOCUMENT
                && printerState != FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT_TOTAL
                && printerState != FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT_ENDING) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Printer must be in fiscal mode.");
        }

        sendToPrinter(message);
    }

    public void printRecNotPaid(String arg0, long arg1) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printRecNotPaid method not supported.");
    }

    public void printRecPackageAdjustment(int arg0, String arg1, String arg2)
            throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printRecPackageAdjustment method not supported.");
    }

    public void printRecPackageAdjustVoid(int arg0, String arg1)
            throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printRecPackageAdjustVoid method not supported.");
    }

    public void printRecRefund(String description, long amount, int vatInfo)
            throws JposException {
        if (printerState != FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT
                && printerState != FiscalPrinterConst.FPTR_PS_FISCAL_DOCUMENT) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Printer must be in fiscal mode.");
        }

        sendToPrinterAsLine(description, DecimalFormatter.formatCurrency(
                new BigDecimal(amount * -1), receiptLocale, true));
    }

    public void printRecRefundVoid(String arg0, long arg1, int arg2)
            throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printRecRefundVoid method not supported.");
    }

    public void printRecSubtotal(long arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printRecSubtotal method not supported.");
    }

    public void printRecSubtotalAdjustment(int arg0, String arg1, long arg2)
            throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printRecSubtotalAdjustment method not supported.");
    }

    public void printRecSubtotalAdjustVoid(int arg0, long arg1)
            throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printRecSubtotalAdjustVoid method not supported.");
    }

    public void printRecTaxID(String arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printRecTaxID method not supported.");
    }

    public void printRecTotal(long total, long payment, String description)
            throws JposException {

        if (printerState != FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT
                && printerState != FiscalPrinterConst.FPTR_PS_FISCAL_DOCUMENT
                && printerState != FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT_TOTAL) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Printer must be in fiscal mode.");
        }

        if (this.runningTotalOfTenders.compareTo(BigDecimal.ZERO) == 0) {
            sendToPrinterAsIndentedLine(TOTAL_TEXT, DecimalFormatter
                    .formatCurrency(new BigDecimal(total), receiptLocale, true));
            sendCarriageReturnsToPrinter(1);
        }

        if (getCapPostPreLine() && getPreLine() != null) {
            sendToPrinterAsLine(" " + getPreLine());
            setPreLine(null);
        }

        BigDecimal paymentDecimal = new BigDecimal(payment);

        sendToPrinterAsIndentedLine(description, DecimalFormatter
                .formatCurrency(paymentDecimal, receiptLocale, true));

        this.runningTotalOfTenders = this.runningTotalOfTenders
                .add(paymentDecimal);

        if (getCapPostPreLine() && getPostLine() != null) {
            sendToPrinterAsLine(" " + getPostLine());
            setPostLine(null);
        }

        BigDecimal totalDecimal = new BigDecimal(total);

        if (totalDecimal.compareTo(this.runningTotalOfTenders) <= 0) {
            printerState = FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT_ENDING;
            sendCarriageReturnsToPrinter(1);
            sendToPrinterAsIndentedLine(CHANGE_DUE_TEXT, DecimalFormatter
                    .formatCurrency(this.runningTotalOfTenders
                            .subtract(totalDecimal), receiptLocale, true));
        } else {
            printerState = FiscalPrinterConst.FPTR_PS_FISCAL_RECEIPT_TOTAL;
        }

    }

    public void printRecVoid(String arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printRecVoid method not supported.");
    }

    public void printRecVoidItem(String description, long price, int quantity,
            int adjustmentType, long adjustment, int vatInfo)
            throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printRecVoidItem method not supported.");
    }

    public void printReport(int arg0, String arg1, String arg2)
            throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printReport method not supported.");
    }

    public void printXReport() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "printXReport method not supported.");
    }

    public void printZReport() throws JposException {

        if (printerState != FiscalPrinterConst.FPTR_PS_MONITOR) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Printer must be in fiscal mode.");
        }

        printerState = FiscalPrinterConst.FPTR_PS_REPORT;

        dayOpened = false;

        sendMarkerToPrinter("(PRINT Z REPORT)");

        resetPrinter();

        printerState = FiscalPrinterConst.FPTR_PS_MONITOR;

    }

    @Override
    public void reset() {
    }

    public void resetPrinter() throws JposException {
        printerState = FiscalPrinterConst.FPTR_PS_MONITOR;
        this.runningTotalOfTenders = BigDecimal.ZERO;
    }

    public void resetStatistics(String arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "resetStatistics method not supported.");
    }

    public void retrieveStatistics(String[] arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "retrieveStatistics method not supported.");
    }

    public void setAdditionalHeader(String additionalHeader)
            throws JposException {

        if (!getCapAdditionalHeader()) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Not allowed to set additional header.");
        }

        this.additionalHeader = additionalHeader;

    }

    public void setAdditionalTrailer(String additionalTrailer)
            throws JposException {

        if (!getCapAdditionalTrailer()) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Not allowed to set additional trailer.");
        }

        this.additionalTrailer = additionalTrailer;

    }

    public void setAsyncMode(boolean asyncMode) throws JposException {
        this.asyncMode = asyncMode;
    }

    public void setChangeDue(String changeDue) throws JposException {
        this.changeDue = changeDue;
    }

    public void setCheckTotal(boolean checkTotal) throws JposException {
        this.checkTotal = checkTotal;
    }

    public void setContractorId(int arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "setContractorId method not supported.");
    }

    public void setCurrency(int arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "setCurrency method not supported.");
    }

    public void setDate(String dateString) throws JposException {

        if (dayOpened) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Setting the date must only be called when the day is not opened.");
        }

        try {
            this.date = dateFormatter.parse(dateString);
        } catch (ParseException e) {
            throw new JposException(JposConst.JPOS_E_EXTENDED,
                    FiscalPrinterConst.JPOS_EFPTR_BAD_DATE,
                    "Invalid date format.");
        }
    }

    public void setDateType(int dateType) throws JposException {
        this.dateType = dateType;
    }

    public void setDuplicateReceipt(boolean duplicateReceipt)
            throws JposException {
        this.duplicateReceipt = duplicateReceipt;
    }

    public void setFiscalReceiptStation(int fiscalReceiptStation)
            throws JposException {

        if (getCapFiscalReceiptStation()) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Switching of fiscal receipt stations method not supported.");
        }

        this.fiscalReceiptStation = fiscalReceiptStation;

    }

    public void setFiscalReceiptType(int fiscalReceiptType)
            throws JposException {

        if (getCapFiscalReceiptType()) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Switching of fiscal receipt types method not supported.");
        }

        this.fiscalReceiptType = fiscalReceiptType;

    }

    public void setFlagWhenIdle(boolean arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "setFlagWhenIdle method not supported.");
    }

    public void setHeaderLine(int lineNumber, String lineValue,
            boolean printWithDoubleWidth) throws JposException {

        if (!CAP_SET_HEADER || dayOpened || lineNumber > NUM_HEADER_LINES) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Cannot set header line.");
        }

        if (printWithDoubleWidth) {
            if (!CAP_DOUBLE_WIDTH) {
                // do nothing
            } else {
                throw new JposException(JposConst.JPOS_E_ILLEGAL,
                        "Printing with double width is method not supported.");
            }
        }

        headerLines.add(lineNumber - 1, lineValue);

    }

    public void setMessageType(int arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "setMessageType method not supported.");
    }

    public void setPOSID(String posId, String cashierId) throws JposException {

        if (!CAP_SET_POS_ID || dayOpened) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Cannot set POS ID.");
        }

        this.posId = posId;
        this.cashierId = cashierId;

    }

    public void setPostLine(String postLine) throws JposException {
        this.postLine = postLine;
    }

    public void setPreLine(String preLine) throws JposException {
        this.preLine = preLine;
    }

    public void setSlipSelection(int arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "setSlipSelection method not supported.");
    }

    public void setStoreFiscalID(String storeFiscalId) throws JposException {

        if (!CAP_SET_STORE_FISCAL_ID || dayOpened) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Cannot set store fiscal ID.");
        }

        this.storeFiscalId = storeFiscalId;

    }

    public void setTotalizerType(int arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "setTotalizerType method not supported.");
    }

    public void setTrailerLine(int lineNumber, String lineValue,
            boolean printWithDoubleWidth) throws JposException {

        if (!CAP_SET_TRAILER || dayOpened || lineNumber > NUM_TRAILER_LINES) {
            throw new JposException(JposConst.JPOS_E_ILLEGAL,
                    "Cannot set trailer line.");
        }

        if (printWithDoubleWidth) {
            if (!CAP_DOUBLE_WIDTH) {
                // do nothing
            } else {
                throw new JposException(JposConst.JPOS_E_ILLEGAL,
                        "Printing with double width is method not supported.");
            }
        }

        trailerLines.add(lineNumber - 1, lineValue);

    }

    public void setVatTable() throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "setVatTable method not supported.");
    }

    public void setVatValue(int arg0, String arg1) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "setVatValue method not supported.");
    }

    public void updateFirmware(String arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "updateFirmware method not supported.");
    }

    public void updateStatistics(String arg0) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "updateStatistics method not supported.");
    }

    public void verifyItem(String arg0, int arg1) throws JposException {
        throw new JposException(JposConst.JPOS_E_ILLEGAL,
                "verifyItem method not supported.");
    }

    protected void sendToPrinter(String value) {
        displayText(value);
    }

    protected void sendToPrinterAsLine(String value) {
        sendToPrinter(value == null ? "\n" : value + "\n");
    }

    protected void sendToPrinterAsLine(String leftValue, String rightValue) {
        sendToPrinterAsLine(leftValue
                + StringUtils.repeat(" ", RECEIPT_LINE_SIZE
                        - leftValue.length() - rightValue.length())
                + rightValue);
    }

    protected void sendToPrinterAsIndentedLine(String left, String right) {
        sendToPrinterAsLine(RECEIPT_INDENT + left, right);
    }

    protected void sendToPrinterAsCenteredLine(String value) {
        sendToPrinterAsLine(StringUtils.center(value, RECEIPT_LINE_SIZE));
    }

    protected void sendMarkerToPrinter(String markerValue) {
        sendToPrinterAsLine(StringUtils.center(markerValue, RECEIPT_LINE_SIZE,
                '-'));
    }

    protected void sendCarriageReturnsToPrinter(int numberOfCarriageReturns) {
        for (int i = 0; i < numberOfCarriageReturns; i++) {
            sendToPrinterAsLine(null);
        }
    }

    // String getText(String key) {
    // return ResourceBundleUtil.getGroupText("Receipt", new
    // String[]{BundleConstantsIfc.RECEIPT_BUNDLE_NAME,
    // BundleConstantsIfc.COMMON_BUNDLE_NAME},
    // LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT)).getProperty(key);
    // }

    public boolean getCapServiceAllowManagement() throws JposException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getCapCharacterSet() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getCapConcurrentJrnRec() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapConcurrentJrnSlp() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapConcurrentRecSlp() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapJrn2Color() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapJrnBold() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapJrnDhigh() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapJrnDwide() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapJrnDwideDhigh() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapJrnItalic() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapJrnUnderline() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapRec2Color() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapRecBarCode() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapRecBitmap() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapRecBold() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapRecDhigh() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapRecDwide() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapRecDwideDhigh() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapRecItalic() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapRecLeft90() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapRecPapercut() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapRecRight90() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapRecRotate180() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapRecStamp() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapRecUnderline() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapSlp2Color() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapSlpBarCode() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapSlpBitmap() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapSlpBold() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapSlpDhigh() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapSlpDwide() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapSlpDwideDhigh() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapSlpFullslip() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapSlpItalic() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapSlpLeft90() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapSlpRight90() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapSlpRotate180() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapSlpUnderline() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapTransaction() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getCharacterSet() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setCharacterSet(int characterSet) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getCharacterSetList() throws JposException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getFontTypefaceList() throws JposException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getJrnLetterQuality() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setJrnLetterQuality(boolean jrnLetterQuality) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getJrnLineChars() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setJrnLineChars(int jrnLineChars) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getJrnLineCharsList() throws JposException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getJrnLineHeight() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setJrnLineHeight(int jrnLineHeight) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getJrnLineSpacing() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setJrnLineSpacing(int jrnLineSpacing) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getJrnLineWidth() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMapMode() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setMapMode(int mapMode) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getRecBarCodeRotationList() throws JposException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getRecLetterQuality() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setRecLetterQuality(boolean recLetterQuality) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getRecLineChars() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setRecLineChars(int recLineChars) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getRecLineCharsList() throws JposException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRecLineHeight() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setRecLineHeight(int recLineHeight) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getRecLineSpacing() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setRecLineSpacing(int recLineSpacing) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getRecLinesToPaperCut() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getRecLineWidth() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getRecSidewaysMaxChars() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getRecSidewaysMaxLines() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getRotateSpecial() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setRotateSpecial(int rotateSpecial) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getSlpBarCodeRotationList() throws JposException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getSlpLetterQuality() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setSlpLetterQuality(boolean recLetterQuality) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getSlpLineChars() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setSlpLineChars(int recLineChars) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getSlpLineCharsList() throws JposException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getSlpLineHeight() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setSlpLineHeight(int recLineHeight) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getSlpLinesNearEndToEnd() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSlpLineSpacing() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setSlpLineSpacing(int recLineSpacing) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getSlpLineWidth() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSlpMaxLines() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSlpSidewaysMaxChars() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSlpSidewaysMaxLines() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void cutPaper(int percentage) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void printBarCode(int station, String data, int symbology, int height, int width,
            int alignment, int textPosition) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void printBitmap(int station, String fileName, int width, int alignment)
            throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void printImmediate(int station, String data) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void printTwoNormal(int stations, String data1, String data2) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void rotatePrint(int station, int rotation) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBitmap(int bitmapNumber, int station, String fileName, int width, int alignment)
            throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setLogo(int location, String data) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void transactionPrint(int station, int control) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void validateData(int station, String data) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void printMemoryBitmap(int station, byte[] data, int type, int width, int alignment)
            throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean getCapConcurrentPageMode() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapRecPageMode() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCapSlpPageMode() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getPageModeArea() throws JposException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getPageModeDescriptor() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getPageModeHorizontalPosition() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setPageModeHorizontalPosition(int position) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getPageModePrintArea() throws JposException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPageModePrintArea(String area) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getPageModePrintDirection() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setPageModePrintDirection(int direction) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getPageModeStation() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setPageModeStation(int station) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getPageModeVerticalPosition() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setPageModeVerticalPosition(int position) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clearPrintArea() throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void pageModePrint(int control) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean getCapMapCharacterSet() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getMapCharacterSet() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setMapCharacterSet(boolean mapCharacterSet) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getRecBitmapRotationList() throws JposException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSlpBitmapRotationList() throws JposException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getCapJrnCartridgeSensor() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCapJrnColor() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCapRecCartridgeSensor() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCapRecColor() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCapRecMarkFeed() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getCapSlpBothSidesPrint() throws JposException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getCapSlpCartridgeSensor() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCapSlpColor() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCartridgeNotify() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setCartridgeNotify(int notify) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getJrnCartridgeState() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getJrnCurrentCartridge() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setJrnCurrentCartridge(int cartridge) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getRecCartridgeState() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getRecCurrentCartridge() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setRecCurrentCartridge(int cartridge) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getSlpCartridgeState() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSlpCurrentCartridge() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setSlpCurrentCartridge(int cartridge) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getSlpPrintSide() throws JposException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void changePrintSide(int side) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void markFeed(int type) throws JposException
    {
        // TODO Auto-generated method stub
        
    }

}
