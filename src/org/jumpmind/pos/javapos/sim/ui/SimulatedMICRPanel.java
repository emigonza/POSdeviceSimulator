package org.jumpmind.pos.javapos.sim.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jpos.MICR;
import jpos.events.DataEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jumpmind.pos.javapos.sim.SimulatedMICRService;
import org.jumpmind.pos.javapos.sim.SimulatedMSRService;
import org.jumpmind.pos.javapos.sim.beans.MICRBean;

public class SimulatedMICRPanel extends BaseSimulatedPanel {
    static final Log logger = LogFactory.getLog(SimulatedMICRPanel.class);
    private static final long serialVersionUID = 1L;
    private static SimulatedMICRPanel me;
    private Map<String, MICRBean> items = new HashMap<String, MICRBean>();
    private MICRBean selectedItem;

    private SimulatedMICRService deviceCallback;

    private SimulatedMICRPanel() {
    }

    public static SimulatedMICRPanel getInstance() {
        if (me == null) {
            me = new SimulatedMICRPanel();
        }
        return me;
    }

    public MICRBean getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(MICRBean selectedItem) {
        this.selectedItem = selectedItem;
    }

    public void init() {
        setInitialized(true);

        this.selectedItem = new MICRBean();
        this.setFocusable(false);
        this.setBackground(Color.LIGHT_GRAY);
        this.setName("SimulatedMICR");

        JButton button1 = new JButton("Enter");
        button1.setName("Enter");
        button1.setSize(200, 20);

        loadItems();

        final JLabel lblAccountNumber = new JLabel("Account Number : ");
        lblAccountNumber.setName("AccountNumberLabel");
        final JTextField txtAccountNumber = new JTextField("");
        txtAccountNumber.setName("AccountNumber");
        final JLabel lblAmount = new JLabel("Amount : ");
        lblAmount.setName("AmountLabel");
        final JTextField txtAmount = new JTextField("");
        txtAmount.setName("Amount");
        final JLabel lblBankNumber = new JLabel("BankNumber : ");
        lblBankNumber.setName("BankNumberLabel");
        final JTextField txtBankNumber = new JTextField("");
        txtBankNumber.setName("BankNumber");
        final JLabel lblCheckType = new JLabel("CheckType : ");
        lblCheckType.setName("CheckTypeLabel");
        final JTextField txtCheckType = new JTextField("");
        txtCheckType.setName("CheckType");
        final JLabel lblCountryCode = new JLabel("CountryCode : ");
        lblCountryCode.setName("CountryCodeLabel");
        final JTextField txtCountryCode = new JTextField("");
        txtCountryCode.setName("CountryCode");
        final JLabel lblEpc = new JLabel("Epc : ");
        lblEpc.setName("EpcLabel");
        final JTextField txtEpc = new JTextField("");
        txtEpc.setName("Epc");
        final JLabel lblRawData = new JLabel("RawData : ");
        lblRawData.setName("RawDataLabel");
        final JTextField txtRawData = new JTextField("");
        txtRawData.setName("RawData");
        final JLabel lblSerialNumber = new JLabel("Serial Number : ");
        lblSerialNumber.setName("SerialNumberLabel");
        final JTextField txtSerialNumber = new JTextField("");
        txtSerialNumber.setName("SerialNumber");
        final JLabel lblTransitNumber = new JLabel("Transit Number : ");
        lblTransitNumber.setName("TransitNumberLabel");
        final JTextField txtTransitNumber = new JTextField("");
        txtTransitNumber.setName("TransitNumber");
        
        JComboBox cbItems = new JComboBox(loadItemMICRBeans());
        cbItems.setName("MICRItems");
        
        cbItems.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                String label = (String) cb.getSelectedItem();
                MICRBean item = items.get(label);

                txtAccountNumber.setText(item.getAccountNumber());
                txtAmount.setText(item.getAmount());
                txtBankNumber.setText(item.getBankNumber());
                try {
                    txtCheckType.setText(new Integer(item.getCheckType())
                            .toString());
                } catch (Exception ex) {
                    logger
                            .warn("Unable to set check type, not a valid integer.");
                }
                try {
                    txtCountryCode.setText(new Integer(item.getCountryCode())
                            .toString());
                } catch (Exception ex) {
                    logger
                            .warn("Unable to set country code, not a valid integer.");
                }
                txtEpc.setText(item.getEpc());
                txtRawData.setText(item.getRawData());
                txtSerialNumber.setText(item.getSerialNumber());
                txtTransitNumber.setText(item.getTransitNumber());
            }
        });

        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (getCallbacks() != null) {
                    MICR micr = new MICR();
                    DataEvent evt = new DataEvent(micr, 1);
                    evt.getSource();

                    selectedItem.setAccountNumber(txtAccountNumber.getText());
                    selectedItem.setAmount(txtAmount.getText());
                    selectedItem.setBankNumber(txtBankNumber.getText());
                    try {
                        selectedItem.setCheckType(new Integer(txtCheckType
                                .getText()).intValue());
                    } catch (Exception ex) {
                        logger
                                .warn("Unable to set check type, not a valid integer.");
                    }

                    try {
                        selectedItem.setCountryCode(new Integer(txtCountryCode
                                .getText()).intValue());
                    } catch (Exception ex) {
                        logger
                                .warn("Unable to set country code, not a valid integer.");
                    }
                    selectedItem.setEpc(txtEpc.getText());
                    selectedItem.setRawData(txtRawData.getText());
                    selectedItem.setSerialNumber(txtSerialNumber.getText());
                    selectedItem.setTransitNumber(txtTransitNumber.getText());

                    getDeviceCallback().setMicr(selectedItem);
                    getCallbacks().fireDataEvent(new DataEvent(evt, 1));
                }
            }
        });

        JLabel header = new JLabel(
                "<html>Select an item from the drop down or enter values manually.</html>");

        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        addToGridBag(0, 0, 2, header, c, this);
        addToGridBag(0, 1, 2, cbItems, c, this);
        addToGridBag(0, 2, 1, lblAccountNumber, c, this);
        addToGridBag(1, 2, 1, txtAccountNumber, c, this);
        addToGridBag(0, 3, 1, lblAmount, c, this);
        addToGridBag(1, 3, 1, txtAmount, c, this);
        addToGridBag(0, 4, 1, lblBankNumber, c, this);
        addToGridBag(1, 4, 1, txtBankNumber, c, this);
        addToGridBag(0, 5, 1, lblCheckType, c, this);
        addToGridBag(1, 5, 1, txtCheckType, c, this);
        addToGridBag(0, 6, 1, lblCountryCode, c, this);
        addToGridBag(1, 6, 1, txtCountryCode, c, this);
        addToGridBag(0, 7, 1, lblEpc, c, this);
        addToGridBag(1, 7, 1, txtEpc, c, this);
        addToGridBag(0, 8, 1, lblRawData, c, this);
        addToGridBag(1, 8, 1, txtRawData, c, this);
        addToGridBag(0, 9, 1, lblSerialNumber, c, this);
        addToGridBag(1, 9, 1, txtSerialNumber, c, this);
        addToGridBag(0, 10, 1, lblTransitNumber, c, this);
        addToGridBag(1, 10, 1, txtTransitNumber, c, this);
        addToGridBag(0, 11, 2, button1, c, this);
    }

    public void loadItems() {
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        String xmlFile = "/org/jumpmind/pos/javapos/sim/SimulatedMICRService.xml";

        try {
            doc = builder.build(new InputStreamReader(SimulatedMSRService.class
                    .getResourceAsStream(xmlFile)));
            Element micr = doc.getRootElement();
            for (Object itemObj : micr.getChildren()) {
                Element itemXML = (Element) itemObj;
                MICRBean item = new MICRBean();

                item.setLabel(readElement(itemXML, "label"));
                item.setAccountNumber(readElement(itemXML, "accountNumber"));
                item.setAmount(readElement(itemXML, "amount"));
                item.setBankNumber(readElement(itemXML, "bankNumber"));
                item.setCheckType(readElementInt(itemXML, "checkType"));
                item.setCountryCode(readElementInt(itemXML, "countryCode"));
                item.setEpc(readElement(itemXML, "epc"));
                item.setRawData(readElement(itemXML, "rawData"));
                item.setSerialNumber(readElement(itemXML, "serialNumber"));
                item.setTransitNumber(readElement(itemXML, "transitNumber"));

                items.put(item.getLabel(), item);
            }
        } catch (Exception e) {
            logger.error("Unable to preload items from " + xmlFile);
            e.printStackTrace();
        }

    }

    public Object[] loadItemMICRBeans() {
        Object[] val = null;
        if (items != null) {
            val = new TreeSet<String>(items.keySet()).toArray();
        }
        return val;
    }

    public SimulatedMICRService getDeviceCallback() {
        return deviceCallback;
    }

    public void setDeviceCallback(SimulatedMICRService deviceCallback) {
        this.deviceCallback = deviceCallback;
    }

}
