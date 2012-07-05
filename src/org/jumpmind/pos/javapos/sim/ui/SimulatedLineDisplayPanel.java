package org.jumpmind.pos.javapos.sim.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import jpos.LineDisplay;
import jpos.events.DirectIOEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jumpmind.pos.javapos.sim.SimulatedLineDisplayService;

public class SimulatedLineDisplayPanel extends BaseSimulatedPanel {
    static final Log logger = LogFactory
            .getLog(SimulatedLineDisplayPanel.class);
    private static final long serialVersionUID = 1L;
    private static SimulatedLineDisplayPanel me;
    private JTextPane textArea;

    private SimulatedLineDisplayService deviceCallback;

    private SimulatedLineDisplayPanel() {
    }

    public static SimulatedLineDisplayPanel getInstance() {
        if (me == null) {
            me = new SimulatedLineDisplayPanel();
        }
        return me;
    }

    public void init() {
        setInitialized(true);

        this.setFocusable(false);
        this.setName("SimulatedLineDisplay");
        textArea = new JTextPane();
        textArea.setName("LineDisplayOutput");
        textArea.setEditable(false);

        StyledDocument doc = textArea.getStyledDocument();
        Style def = StyleContext.getDefaultStyleContext().getStyle(
                StyleContext.DEFAULT_STYLE);
        Style s = doc.addStyle("text", def);
        StyleConstants.setFontFamily(s, "Monospaced");

        JScrollPane scrollPane = new JScrollPane(textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel,BoxLayout.Y_AXIS));
        JButton btnYes = new JButton();
        btnYes.setText("Yes");
        btnYes.setName("Yes");

        JButton btnNo = new JButton();
        btnNo.setText("No");
        btnNo.setName("No");

        JLabel lblCreditDebitConfirm = new JLabel();
        lblCreditDebitConfirm.setText("Confirm credit/debit");
        lblCreditDebitConfirm.setName("ConfirmCreditDebitLabel");

        btnYes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (getCallbacks() != null) {
                    String confirmMessage = "XEVT<FS>2<FS>1<FS>0<FS>CONFIRM";

                    DirectIOEvent evt = new DirectIOEvent(new LineDisplay(), 1,
                            1, confirmMessage.getBytes());

                    getCallbacks().fireDirectIOEvent(evt);
                }
            }
        });

        btnNo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (getCallbacks() != null) {
                    String confirmMessage = "XEVT<FS>2<FS>2<FS>0<FS>CONFIRM";

                    DirectIOEvent evt = new DirectIOEvent(new LineDisplay(), 1,
                            1, confirmMessage.getBytes());

                    getCallbacks().fireDirectIOEvent(evt);
                }
            }
        });

        JPanel creditConfirmPanel = new JPanel();
        creditConfirmPanel.add(lblCreditDebitConfirm);
        creditConfirmPanel.add(btnYes);
        creditConfirmPanel.add(btnNo);
        
        formPanel.add(creditConfirmPanel);
        formPanel.add(createDCCForm());
        

        setLayout(new BorderLayout());
        this.add(formPanel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        //this.add(createDCCForm(), BorderLayout.SOUTH);
    }

    protected JPanel createDCCForm() {
        JPanel formPanel = new JPanel();
        JButton btnYes = new JButton();
        btnYes.setText("Store Currency");
        btnYes.setName("StoreCurrency");

        JButton btnNo = new JButton();
        btnNo.setText("Foreign Currency");
        btnNo.setName("ForeignCurrency");
        
        JLabel lblCreditDebitConfirm = new JLabel();
        lblCreditDebitConfirm.setText("Choose a currency");
        lblCreditDebitConfirm.setName("ChooseCurrencyLabel");

        btnYes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (getCallbacks() != null) {
                    String confirmMessage = "XEVT<FS>2<FS>1<FS>0<FS>DCC";

                    DirectIOEvent evt = new DirectIOEvent(new LineDisplay(), 1,
                            1, confirmMessage.getBytes());

                    getCallbacks().fireDirectIOEvent(evt);
                }
            }
        });

        btnNo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (getCallbacks() != null) {
                    String confirmMessage = "XEVT<FS>2<FS>2<FS>0<FS>DCC";

                    DirectIOEvent evt = new DirectIOEvent(new LineDisplay(), 1,
                            1, confirmMessage.getBytes());

                    getCallbacks().fireDirectIOEvent(evt);
                }
            }
        });

        formPanel.add(lblCreditDebitConfirm);
        formPanel.add(btnYes);
        formPanel.add(btnNo);
        
        return formPanel;
    }
    
    
    public JTextPane getTextArea() {
        return textArea;
    }

    public void setTextArea(JTextPane textArea) {
        this.textArea = textArea;
    }

    public SimulatedLineDisplayService getDeviceCallback() {
        return deviceCallback;
    }

    public void setDeviceCallback(SimulatedLineDisplayService deviceCallback) {
        this.deviceCallback = deviceCallback;
    }
}
