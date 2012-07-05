package org.jumpmind.pos.javapos.sim.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import jpos.SignatureCapture;
import jpos.events.DataEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jumpmind.pos.javapos.sim.SimulatedSignatureCaptureService;
import org.jumpmind.pos.javapos.sim.beans.SignatureCaptureBean;

public class SimulatedSignatureCapturePanel extends BaseSimulatedPanel {
    static final Log logger = LogFactory.getLog(SimulatedSignatureCapturePanel.class);
    private static final long serialVersionUID = 1L;
    private static SimulatedSignatureCapturePanel me;
    private SignatureCaptureBean signature;
    private SimulatedSignatureCaptureService deviceCallback;
    private JButton clearButton;
    private JButton sendButton;
    private Point points[] = new Point[2];
    ScribbleDragAndDrop s = new ScribbleDragAndDrop();

    private boolean p0Exists = false;
    private boolean p1Exists = false;
    private int r = 8;

    private SimulatedSignatureCapturePanel() {
    }

    public static SimulatedSignatureCapturePanel getInstance() {
        if (me == null) {
            me = new SimulatedSignatureCapturePanel();
        }
        return me;
    }

    public void clear() {
        s.scribbles = new ArrayList<Scribble>();
        s.repaint();
    }

    public void init() {
        setInitialized(true);

        this.setFocusable(false);
        this.setBackground(Color.LIGHT_GRAY);
        this.setName("SimulatedSignatureCapture");

        sendButton = new JButton("Send Signature");
        sendButton.setName("SendSignature");
        sendButton.setSize(200, 20);
        sendButton.setEnabled(false);

        clearButton = new JButton("Clear");
        clearButton.setName("Clear");
        clearButton.setSize(200, 20);
        clearButton.setEnabled(false);

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (getCallbacks() != null) {
                    SignatureCapture sigCap = new SignatureCapture();
                    DataEvent evt = new DataEvent(sigCap, 1);
                    evt.getSource();

                    s.repaint();
                    signature = new SignatureCaptureBean();

                    List<Point> pointList = new ArrayList<Point>();
                    for (Object obj : s.scribbles) {
                        Scribble scribble = (Scribble) obj;

                        for (int i = 1; i + 1 < scribble.points.length; i = i + 2) {
                            int x = new Double(scribble.points[i]).intValue();
                            int y = new Double(scribble.points[i + 1]).intValue();

                            if (x > 0 && y > 0) {
                                Point p = new Point(x, y);
                                pointList.add(p);
                            }
                        }
                    }

                    signature.setPointArray((Point[]) pointList
                            .toArray(new Point[pointList.size()]));
                    signature.setRawData(null);

                    SimulatedSignatureCaptureService service = getDeviceCallback();
                    service.setSignature(signature);                    
                    getCallbacks().fireDataEvent(new DataEvent(evt, 1));
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear();
                signature = new SignatureCaptureBean();
            }
        });

        setLayout(new BorderLayout());
        s.setSize(300, 300);
        s.setVisible(true);

        add(sendButton, BorderLayout.NORTH);
        add(s, BorderLayout.CENTER);
        add(clearButton, BorderLayout.SOUTH);

    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        sendButton.setEnabled(enabled);
        clearButton.setEnabled(enabled);
        clear();
    }

    public class PaintPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        // set up GUI and register mouse event handler
        public PaintPanel() {
            super();
            this.setBackground(Color.LIGHT_GRAY);

            // handle frame mouse event
            addMouseListener(

            new MouseAdapter() // anonymous inner class
            {

                // define a point and repaint
                @Override
                public void mousePressed(MouseEvent event) {
                    if (!p0Exists) {
                        // create point0
                        points[0] = new Point(event.getX(), event.getY());
                        System.out.println("Point 0 created: " + points[0]);
                        p0Exists = true;
                        repaint();
                    } else if (!p1Exists) {
                        // create point1
                        points[1] = new Point(event.getX(), event.getY());
                        System.out.println("Point 1 created: " + points[1]);
                        p1Exists = true;
                        repaint();
                    }

                } // end method mousePressed

            } // end anonymous inner class
            ); // end call to addMouseMotionListener
        } // end PaintPanel constructor

        @Override
        public void paint(Graphics g) {
            super.paint(g); // clears drawing area
            if (p0Exists) {
                g.drawOval(points[0].x - r / 2, points[0].y - r / 2, r, r);
                g.drawString("Point0", points[0].x, points[0].y - 3 * r);
                g
                        .drawString("x=" + points[0].x + " y=" + points[0].y, points[0].x,
                                points[0].y - r);
                // g.drawLine(points[ 0 ].x - r/2, points[ 0 ].y - r/2, r, r);
            }
            if (p1Exists) {
                g.drawOval(points[1].x - r / 2, points[1].y - r / 2, r, r);
                g.drawString("Point1", points[1].x, points[1].y - 3 * r);
                g
                        .drawString("x=" + points[1].x + " y=" + points[1].y, points[1].x,
                                points[1].y - r);
            }
            if (p0Exists && p1Exists) {
                g.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);
            }
        } // end method paint
    }

    public SimulatedSignatureCaptureService getDeviceCallback() {
        return deviceCallback;
    }

    public void setDeviceCallback(SimulatedSignatureCaptureService deviceCallback) {
        this.deviceCallback = deviceCallback;
    }

}
