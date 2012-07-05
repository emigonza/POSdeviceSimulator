package org.jumpmind.pos.javapos.sim.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import jpos.CashDrawer;
import jpos.events.DataEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jumpmind.pos.javapos.sim.SimulatedCashDrawerService;

public class SimulatedCashDrawerPanel extends BaseSimulatedPanel {
    static final Log logger = LogFactory.getLog(SimulatedCashDrawerPanel.class);
    private static final long serialVersionUID = 1L;
    private static SimulatedCashDrawerPanel me;

    private SimulatedCashDrawerService deviceCallback;

    private JButton btnClose = new JButton("Close Drawer");
    private JLabel lblStatus = new JLabel("Cash drawer is closed");

    private SimulatedCashDrawerPanel() {
    }

    public static SimulatedCashDrawerPanel getInstance() {
        if (me == null) {
            me = new SimulatedCashDrawerPanel();
        }
        return me;
    }

    public void init() {
        setInitialized(true);

        this.setFocusable(false);
        this.setBackground(Color.LIGHT_GRAY);
        this.setName("SimulatedCashDrawer");

        btnClose.setSize(200, 20);
        btnClose.setEnabled(false);
        btnClose.setName("CloseDrawer");
        
        lblStatus.setSize(200, 20);
        lblStatus.setName("DrawerStatusLabel");
        
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getDeviceCallback().toggleDrawer(false);

                if (getCallbacks() != null) {
                    CashDrawer cd = new CashDrawer();
                    DataEvent evt = new DataEvent(cd, 1);

                    getCallbacks().fireDataEvent(evt);
                }
            }
        });

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        addToGridBag(0, 0, 1, lblStatus, c, this);
        addToGridBag(0, 1, 1, btnClose, c, this);
    }

    public JButton getBtnClose() {
        return btnClose;
    }

    public void updateStatusLabel(boolean cashDrawerOpened) {
        lblStatus.setText("Cash drawer is "
                + (cashDrawerOpened ? "opened" : "closed"));
    }

    public SimulatedCashDrawerService getDeviceCallback() {
        return deviceCallback;
    }

    public void setDeviceCallback(SimulatedCashDrawerService deviceCallback) {
        this.deviceCallback = deviceCallback;
    }
}
