package org.jumpmind.pos.javapos.sim.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class SimulatedDeviceWindow extends JFrame {

    private static final long serialVersionUID = -7481827273134300406L;
    private static SimulatedDeviceWindow me;
    public static int TAB_SCANNER = 0;
    public static int TAB_MSR = 1;
    public static int TAB_PRINTER = 2;
    public static int TAB_CASH_DRAWER = 3;
    public static int TAB_SIG_CAP = 4;
    public static int TAB_MICR = 5;
    public static int TAB_LINE_DISPLAY = 6;
    public static int TAB_PIN_PAD = 7;

    private boolean initialized;
    private DeviceLegendMetaData legendMSR = new DeviceLegendMetaData("MSR");
    private DeviceLegendMetaData legendPrinter = new DeviceLegendMetaData(
            "Printer");
    private DeviceLegendMetaData legendCashDrawer = new DeviceLegendMetaData(
            "Cash Drawer");
    private DeviceLegendMetaData legendPinPad = new DeviceLegendMetaData("PIN Pad");
    private JTabbedPane tabbedPane = new JTabbedPane();

    private SimulatedDeviceWindow() {
    }

    public static SimulatedDeviceWindow getInstance() {
        if (me == null) {
            me = new SimulatedDeviceWindow();
        }
        return me;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public JTabbedPane findTabbedPane(Component[] children) {
        JTabbedPane p = null;
        for (Component child : children) {
            if (child != null && child instanceof Container) {
                if (child instanceof JTabbedPane) {
                    p = (JTabbedPane) child;
                } else if (((Container) child).getComponents() != null) {
                    p = findTabbedPane(((Container) child).getComponents());
                } else {
                    return p;
                }
            } else {
                return p;
            }
        }
        return p;
    }

    public void init() {
        setInitialized(true);
        me.setTitle("Simulated Devices");
        me.setName("SimulatedDevices");
        me.setFocusable(false);

        JPanel header = buildHeader();
        header.setSize(400, 100);

        tabbedPane.setSize(400, 600);
        tabbedPane.setName("DeviceTabs");

        tabbedPane.addTab("Scanner", SimulatedScannerPanel.getInstance());
        tabbedPane.addTab("MSR", SimulatedMSRPanel.getInstance());
        tabbedPane
                .addTab("POS Printer", SimulatedPOSPrinterPanel.getInstance());
        tabbedPane
                .addTab("Cash Drawer", SimulatedCashDrawerPanel.getInstance());
        tabbedPane.addTab("Sig Cap", SimulatedSignatureCapturePanel
                .getInstance());
        tabbedPane.addTab("MICR", SimulatedMICRPanel.getInstance());
        tabbedPane.addTab("Line Display", SimulatedLineDisplayPanel
                .getInstance());
        tabbedPane.addTab("Fiscal Printer", SimulatedFiscalPrinterPanel
                .getInstance());
        tabbedPane.addTab("PIN Pad", SimulatedPINPadPanel.getInstance());

        me.setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        setLocation(801, 1);
        me.setSize(400, 600);

        setVisible(true);
    }

    public JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(Color.LIGHT_GRAY);
        setLayout(new GridLayout(2, 4));

        header.add(new JLabel("Device"));
        header.add(new JLabel("Opened"));
        header.add(new JLabel("Claimed"));
        header.add(new JLabel("Enabled"));

        header.add(legendMSR.getLblDevice());
        header.add(legendMSR.getLblOpened());
        header.add(legendMSR.getLblClaimed());
        header.add(legendMSR.getLblEnabled());

        return header;
    }

    public void addToGridBag(int x, int y, int colspan, int rowspan, int fill,
            Component component, Container container) {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = colspan;
        c.gridheight = rowspan;
        c.fill = fill;
        container.add(component, c);
    }

    public void addTab(String name, JPanel panel) {
        tabbedPane.addTab(name, panel);
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public DeviceLegendMetaData getLegendMSR() {
        return legendMSR;
    }

    public DeviceLegendMetaData getLegendPrinter() {
        return legendPrinter;
    }

    public DeviceLegendMetaData getLegendCashDrawer() {
        return legendCashDrawer;
    }
    
    public DeviceLegendMetaData getLegendPINPad() {
    	return legendPinPad;
    }

    private class DeviceLegendMetaData extends JPanel {

        private static final long serialVersionUID = 3317726550575860483L;
        JLabel lblDevice;
        JLabel lblOpened;
        JLabel lblClaimed;
        JLabel lblEnabled;

        public DeviceLegendMetaData(String device) {
            lblDevice = new JLabel(device);
            lblOpened = new JLabel("x");
            lblOpened.setBackground(Color.RED);
            lblClaimed = new JLabel("x");
            lblClaimed.setBackground(Color.RED);
            lblEnabled = new JLabel("x");
            lblEnabled.setBackground(Color.RED);
        }
        
        public JLabel getLblDevice() {
            return lblDevice;
        }

        public JLabel getLblOpened() {
            return lblOpened;
        }

        public JLabel getLblClaimed() {
            return lblClaimed;
        }

        public JLabel getLblEnabled() {
            return lblEnabled;
        }
    }
}
