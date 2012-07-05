package org.jumpmind.pos.javapos.sim.ui;

/*
 * Copyright (c) 2000 David Flanagan.  All rights reserved.
 * This code is from the book Java Examples in a Nutshell, 2nd Edition.
 * It is provided AS-IS, WITHOUT ANY WARRANTY either expressed or implied.
 * You may study, use, and modify it for any non-commercial purpose.
 * You may distribute it non-commercially as long as you retain this notice.
 * For a commercial use license, or to purchase the book (recommended),
 * visit http://www.davidflanagan.com/javaexamples2.
 */

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * This component can operate in two modes. In "draw mode", it allows the user
 * to scribble with the mouse. In "drag mode", it allows the user to drag
 * scribbles with the mouse. Regardless of the mode, it always allows scribbles
 * to be dropped on it from other applications.
 */
public class ScribbleDragAndDrop extends JComponent implements
        DragGestureListener, // For recognizing the start of drags
        DragSourceListener, // For processing drag source events
        DropTargetListener, // For processing drop target events
        MouseListener, // For processing mouse clicks
        MouseMotionListener // For processing mouse drags
{

    private static final long serialVersionUID = 1L;

    ArrayList<Scribble> scribbles = new ArrayList<Scribble>(); // A list of Scribbles to draw

    Scribble currentScribble; // The scribble in progress

    Scribble beingDragged; // The scribble being dragged

    DragSource dragSource; // A central DnD object

    boolean dragMode = false; // Are we dragging or scribbling?

    // These are some constants we use
    static final int LINEWIDTH = 3;

    static final BasicStroke linestyle = new BasicStroke(LINEWIDTH);

    static final Border normalBorder = new BevelBorder(BevelBorder.LOWERED);

    static final Border dropBorder = new BevelBorder(BevelBorder.RAISED);

    /** The constructor: set up drag-and-drop stuff */
    public ScribbleDragAndDrop() {
        // Give ourselves a nice default border.
        // We'll change this border during drag-and-drop.
        setBorder(normalBorder);

        // Register listeners to handle drawing
        addMouseListener(this);
        addMouseMotionListener(this);
        
        setName("Scribble");

        // Create a DragSource and DragGestureRecognizer to listen for drags
        // The DragGestureRecognizer will notify the DragGestureListener
        // when the user tries to drag an object
        dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(this, // What component
                DnDConstants.ACTION_COPY_OR_MOVE, // What drag types?
                this);// the listener

        // Create and set up a DropTarget that will listen for drags and
        // drops over this component, and will notify the DropTargetListener
        DropTarget dropTarget = new DropTarget(this, // component to monitor
                this); // listener to notify
        this.setDropTarget(dropTarget); // Tell the component about it.
    }

    /**
     * The component draws itself by drawing each of the Scribble objects.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(linestyle); // Specify wide lines

        int numScribbles = scribbles.size();
        for (int i = 0; i < numScribbles; i++) {
            Scribble s = scribbles.get(i);
            g2.draw(s); // Draw the scribble
        }
    }

    public void setDragMode(boolean dragMode) {
        this.dragMode = dragMode;
    }

    public boolean getDragMode() {
        return dragMode;
    }

    /**
     * This method, and the following four methods are from the MouseListener
     * interface. If we're in drawing mode, this method handles mouse down
     * events and starts a new scribble.
     */
    public void mousePressed(MouseEvent e) {
        if (dragMode)
            return;
        currentScribble = new Scribble();
        scribbles.add(currentScribble);
        currentScribble.moveto(e.getX(), e.getY());
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    /**
     * This method and mouseMoved() below are from the MouseMotionListener
     * interface. If we're in drawing mode, this method adds a new point to the
     * current scribble and requests a redraw
     */
    public void mouseDragged(MouseEvent e) {
        if (dragMode)
            return;
        currentScribble.lineto(e.getX(), e.getY());
        repaint();
    }

    public void mouseMoved(MouseEvent e) {
    }

    /**
     * This method implements the DragGestureListener interface. It will be
     * invoked when the DragGestureRecognizer thinks that the user has initiated
     * a drag. If we're not in drawing mode, then this method will try to figure
     * out which Scribble object is being dragged, and will initiate a drag on
     * that object.
     */
    public void dragGestureRecognized(DragGestureEvent e) {
        // Don't drag if we're not in drag mode
        if (!dragMode)
            return;

        // Figure out where the drag started
        MouseEvent inputEvent = (MouseEvent) e.getTriggerEvent();
        int x = inputEvent.getX();
        int y = inputEvent.getY();

        // Figure out which scribble was clicked on, if any by creating a
        // small rectangle around the point and testing for intersection.
        Rectangle r = new Rectangle(x - LINEWIDTH, y - LINEWIDTH,
                LINEWIDTH * 2, LINEWIDTH * 2);
        int numScribbles = scribbles.size();
        for (int i = 0; i < numScribbles; i++) { // Loop through the scribbles
            Scribble s = (Scribble) scribbles.get(i);
            if (s.intersects(r)) {
                // The user started the drag on top of this scribble, so
                // start to drag it.

                // First, remember which scribble is being dragged, so we can
                // delete it later (if this is a move rather than a copy)
                beingDragged = s;

                // Next, create a copy that will be the one dragged
                Scribble dragScribble = (Scribble) s.clone();
                // Adjust the origin to the point the user clicked on.
                dragScribble.translate(-x, -y);

                // Choose a cursor based on the type of drag the user initiated
                Cursor cursor;
                switch (e.getDragAction()) {
                case DnDConstants.ACTION_COPY:
                    cursor = DragSource.DefaultCopyDrop;
                    break;
                case DnDConstants.ACTION_MOVE:
                    cursor = DragSource.DefaultMoveDrop;
                    break;
                default:
                    return; // We only support move and copys
                }

                // Some systems allow us to drag an image along with the
                // cursor. If so, create an image of the scribble to drag
                if (DragSource.isDragImageSupported()) {
                    Rectangle scribbleBox = dragScribble.getBounds();
                    Image dragImage = this.createImage(scribbleBox.width,
                            scribbleBox.height);
                    Graphics2D g = (Graphics2D) dragImage.getGraphics();
                    g.setColor(new Color(0, 0, 0, 0)); // transparent background
                    g.fillRect(0, 0, scribbleBox.width, scribbleBox.height);
                    g.setColor(Color.black);
                    g.setStroke(linestyle);
                    g.translate(-scribbleBox.x, -scribbleBox.y);
                    g.draw(dragScribble);
                    Point hotspot = new Point(-scribbleBox.x, -scribbleBox.y);

                    // Now start dragging, using the image.
                    e.startDrag(cursor, dragImage, hotspot, dragScribble, this);
                } else {
                    // Or start the drag without an image
                    e.startDrag(cursor, dragScribble, this);
                }
                // After we've started dragging one scribble, stop looking
                return;
            }
        }
    }

    /**
     * This method, and the four unused methods that follow it implement the
     * DragSourceListener interface. dragDropEnd() is invoked when the user
     * drops the scribble she was dragging. If the drop was successful, and if
     * the user did a "move" rather than a "copy", then we delete the dragged
     * scribble from the list of scribbles to draw.
     */
    public void dragDropEnd(DragSourceDropEvent e) {
        if (!e.getDropSuccess())
            return;
        int action = e.getDropAction();
        if (action == DnDConstants.ACTION_MOVE) {
            scribbles.remove(beingDragged);
            beingDragged = null;
            repaint();
        }
    }

    // These methods are also part of DragSourceListener.
    // They are invoked at interesting points during the drag, and can be
    // used to perform "drag over" effects, such as changing the drag cursor
    // or drag image.
    public void dragEnter(DragSourceDragEvent e) {
    }

    public void dragExit(DragSourceEvent e) {
    }

    public void dropActionChanged(DragSourceDragEvent e) {
    }

    public void dragOver(DragSourceDragEvent e) {
    }

    // The next five methods implement DropTargetListener

    /**
     * This method is invoked when the user first drags something over us. If we
     * understand the data type being dragged, then call acceptDrag() to tell
     * the system that we're receptive. Also, we change our border as a "drag
     * under" effect to signal that we can accept the drop.
     */
    public void dragEnter(DropTargetDragEvent e) {
        if (e.isDataFlavorSupported(Scribble.scribbleDataFlavor)
                || e.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
            this.setBorder(dropBorder);
        }
    }

    /** The user is no longer dragging over us, so restore the border */
    public void dragExit(DropTargetEvent e) {
        this.setBorder(normalBorder);
    }

    /**
     * This is the key method of DropTargetListener. It is invoked when the user
     * drops something on us.
     */
    public void drop(DropTargetDropEvent e) {
        this.setBorder(normalBorder); // Restore the default border

        // First, check whether we understand the data that was dropped.
        // If we supports our data flavors, accept the drop, otherwise reject.
        if (e.isDataFlavorSupported(Scribble.scribbleDataFlavor)
                || e.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        } else {
            e.rejectDrop();
            return;
        }

        // We've accepted the drop, so now we attempt to get the dropped data
        // from the Transferable object.
        Transferable t = e.getTransferable(); // Holds the dropped data
        Scribble droppedScribble; // This will hold the Scribble object

        // First, try to get the data directly as a scribble object
        try {
            droppedScribble = (Scribble) t
                    .getTransferData(Scribble.scribbleDataFlavor);
        } catch (Exception ex) { // unsupported flavor, IO exception, etc.
            // If that doesn't work, try to get it as a String and parse it
            try {
                String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                droppedScribble = Scribble.parse(s);
            } catch (Exception ex2) {
                // If we still couldn't get the data, tell the system we failed
                e.dropComplete(false);
                return;
            }
        }

        // If we get here, we've got the Scribble object
        Point p = e.getLocation(); // Where did the drop happen?
        droppedScribble.translate(p.getX(), p.getY()); // Move it there
        scribbles.add(droppedScribble); // add to display list
        repaint(); // ask for redraw
        e.dropComplete(true); // signal success!
    }

    // These are unused DropTargetListener methods
    public void dragOver(DropTargetDragEvent e) {
    }

    public void dropActionChanged(DropTargetDragEvent e) {
    }

    /**
     * The main method. Creates a simple application using this class. Note the
     * buttons for switching between draw mode and drag mode.
     */
    public static void main(String[] args) {
        // Create a frame and put a scribble pane in it
        JFrame frame = new JFrame("ScribbleDragAndDrop");
        final ScribbleDragAndDrop scribblePane = new ScribbleDragAndDrop();
        frame.getContentPane().add(scribblePane, BorderLayout.CENTER);

        // Create two buttons for switching modes
        JToolBar toolbar = new JToolBar();
        ButtonGroup group = new ButtonGroup();
        JToggleButton draw = new JToggleButton("Draw");
        JToggleButton drag = new JToggleButton("Drag");
        draw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scribblePane.setDragMode(false);
            }
        });
        drag.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scribblePane.setDragMode(true);
            }
        });
        group.add(draw);
        group.add(drag);
        toolbar.add(draw);
        toolbar.add(drag);
        frame.getContentPane().add(toolbar, BorderLayout.NORTH);

        // Start off in drawing mode
        draw.setSelected(true);
        scribblePane.setDragMode(false);

        // Pop up the window
        frame.setSize(400, 400);
        frame.setVisible(true);
    }
}

class Scribble implements Shape, Transferable, Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    protected double[] points = new double[64]; // The scribble data

    protected int numPoints = 0; // The current number of points

    double maxX = Double.NEGATIVE_INFINITY; // The bounding box

    double maxY = Double.NEGATIVE_INFINITY;

    double minX = Double.POSITIVE_INFINITY;

    double minY = Double.POSITIVE_INFINITY;

    /**
     * Begin a new polyline at (x,y). Note the use of Double.NaN in the points
     * array to mark the beginning of a new polyline
     */
    public void moveto(double x, double y) {
        if (numPoints + 3 > points.length)
            reallocate();
        // Mark this as the beginning of a new line
        points[numPoints++] = Double.NaN;
        // The rest of this method is just like lineto();
        lineto(x, y);
    }

    /**
     * Add the point (x,y) to the end of the current polyline
     */
    public void lineto(double x, double y) {
        if (numPoints + 2 > points.length)
            reallocate();
        points[numPoints++] = x;
        points[numPoints++] = y;

        // See if the point enlarges our bounding box
        if (x > maxX)
            maxX = x;
        if (x < minX)
            minX = x;
        if (y > maxY)
            maxY = y;
        if (y < minY)
            minY = y;
    }

    /**
     * Append the Scribble s to this Scribble
     */
    public void append(Scribble s) {
        int n = numPoints + s.numPoints;
        double[] newpoints = new double[n];
        System.arraycopy(points, 0, newpoints, 0, numPoints);
        System.arraycopy(s.points, 0, newpoints, numPoints, s.numPoints);
        points = newpoints;
        numPoints = n;
        minX = Math.min(minX, s.minX);
        maxX = Math.max(maxX, s.maxX);
        minY = Math.min(minY, s.minY);
        maxY = Math.max(maxY, s.maxY);
    }

    /**
     * Translate the coordinates of all points in the Scribble by x,y
     */
    public void translate(double x, double y) {
        for (int i = 0; i < numPoints; i++) {
            if (Double.isNaN(points[i]))
                continue;
            points[i++] += x;
            points[i] += y;
        }
        minX += x;
        maxX += x;
        minY += y;
        maxY += y;
    }

    /** An internal method to make more room in the data array */
    protected void reallocate() {
        double[] newpoints = new double[points.length * 2];
        System.arraycopy(points, 0, newpoints, 0, numPoints);
        points = newpoints;
    }

    /** Clone a Scribble object and its internal array of data */
    @Override
    public Object clone() {
        try {
            Scribble s = (Scribble) super.clone(); // make a copy of all fields
            s.points = points.clone(); // copy the entire array
            return s;
        } catch (CloneNotSupportedException e) { // This should never happen
            return this;
        }
    }

    /** Convert the scribble data to a textual format */
    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < numPoints; i++) {
            if (Double.isNaN(points[i])) {
                b.append("m ");
            } else {
                b.append(points[i]);
                b.append(' ');
            }
        }
        return b.toString();
    }

    /**
     * Create a new Scribble object and initialize it by parsing a string of
     * coordinate data in the format produced by toString()
     */
    public static Scribble parse(String s) throws NumberFormatException {
        StringTokenizer st = new StringTokenizer(s);
        Scribble scribble = new Scribble();
        while (st.hasMoreTokens()) {
            String t = st.nextToken();
            if (t.charAt(0) == 'm') {
                scribble.moveto(Double.parseDouble(st.nextToken()), Double
                        .parseDouble(st.nextToken()));
            } else {
                scribble.lineto(Double.parseDouble(t), Double.parseDouble(st
                        .nextToken()));
            }
        }
        return scribble;
    }

    // ========= The following methods implement the Shape interface ========

    /** Return the bounding box of the Shape */
    public Rectangle getBounds() {
        return new Rectangle((int) (minX - 0.5f), (int) (minY - 0.5f),
                (int) (maxX - minX + 0.5f), (int) (maxY - minY + 0.5f));
    }

    /** Return the bounding box of the Shape */
    public Rectangle2D getBounds2D() {
        return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
    }

    /** Our shape is an open curve, so it never contains anything */
    public boolean contains(Point2D p) {
        return false;
    }

    public boolean contains(Rectangle2D r) {
        return false;
    }

    public boolean contains(double x, double y) {
        return false;
    }

    public boolean contains(double x, double y, double w, double h) {
        return false;
    }

    /**
     * Determine if the scribble intersects the specified rectangle by testing
     * each line segment individually
     */
    public boolean intersects(Rectangle2D r) {
        if (numPoints < 4)
            return false;
        int i = 0;
        double x1, y1, x2 = 0.0, y2 = 0.0;
        while (i < numPoints) {
            if (Double.isNaN(points[i])) { // If we're beginning a new line
                i++; // Skip the NaN
                x2 = points[i++];
                y2 = points[i++];
            } else {
                x1 = x2;
                y1 = y2;
                x2 = points[i++];
                y2 = points[i++];
                if (r.intersectsLine(x1, y1, x2, y2))
                    return true;
            }
        }

        return false;
    }

    /** Test for intersection by invoking the method above */
    public boolean intersects(double x, double y, double w, double h) {
        return intersects(new Rectangle2D.Double(x, y, w, h));
    }

    /**
     * Return a PathIterator object that tells Java2D how to draw this scribble
     */
    public PathIterator getPathIterator(AffineTransform at) {
        return new ScribbleIterator(at);
    }

    /**
     * Return a PathIterator that doesn't include curves. Ours never does.
     */
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return getPathIterator(at);
    }

    /**
     * This inner class implements the PathIterator interface to describe the
     * shape of a scribble. Since a Scribble is composed of arbitrary movetos
     * and linetos, we simply return their coordinates
     */
    public class ScribbleIterator implements PathIterator {
        protected int i = 0; // Position in array

        protected AffineTransform transform;

        public ScribbleIterator(AffineTransform transform) {
            this.transform = transform;
        }

        /** How to determine insideness and outsideness for this shape */
        public int getWindingRule() {
            return PathIterator.WIND_NON_ZERO;
        }

        /** Have we reached the end of the scribble path yet? */
        public boolean isDone() {
            return i >= numPoints;
        }

        /** Move on to the next segment of the path */
        public void next() {
            if (Double.isNaN(points[i]))
                i += 3;
            else
                i += 2;
        }

        /**
         * Get the coordinates of the current moveto or lineto as floats
         */
        public int currentSegment(float[] coords) {
            int retval;
            if (Double.isNaN(points[i])) { // If its a moveto
                coords[0] = (float) points[i + 1];
                coords[1] = (float) points[i + 2];
                retval = SEG_MOVETO;
            } else {
                coords[0] = (float) points[i];
                coords[1] = (float) points[i + 1];
                retval = SEG_LINETO;
            }

            // If a transform was specified, use it on the coordinates
            if (transform != null)
                transform.transform(coords, 0, coords, 0, 1);

            return retval;
        }

        /**
         * Get the coordinates of the current moveto or lineto as doubles
         */
        public int currentSegment(double[] coords) {
            int retval;
            if (Double.isNaN(points[i])) {
                coords[0] = points[i + 1];
                coords[1] = points[i + 2];
                retval = SEG_MOVETO;
            } else {
                coords[0] = points[i];
                coords[1] = points[i + 1];
                retval = SEG_LINETO;
            }
            if (transform != null)
                transform.transform(coords, 0, coords, 0, 1);
            return retval;
        }
    }

    // ====== The following methods implement the Transferable interface =====

    // This is the custom DataFlavor for Scribble objects
    public static DataFlavor scribbleDataFlavor = new DataFlavor(
            Scribble.class, "Scribble");

    // This is a list of the flavors we know how to work with
    public static DataFlavor[] supportedFlavors = { scribbleDataFlavor,
            DataFlavor.stringFlavor };

    /** Return the data formats or "flavors" we know how to transfer */
    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors.clone();
    }

    /** Check whether we support a given flavor */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavor.equals(scribbleDataFlavor) || flavor
                .equals(DataFlavor.stringFlavor));
    }

    /**
     * Return the scribble data in the requested format, or throw an exception
     * if we don't support the requested format
     */
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException {
        if (flavor.equals(scribbleDataFlavor)) {
            return this;
        } else if (flavor.equals(DataFlavor.stringFlavor)) {
            return toString();
        } else
            throw new UnsupportedFlavorException(flavor);
    }
}