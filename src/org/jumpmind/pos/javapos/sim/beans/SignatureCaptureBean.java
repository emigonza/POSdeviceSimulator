package org.jumpmind.pos.javapos.sim.beans;

import java.awt.Point;

public class SignatureCaptureBean {
    private byte[] rawData;
    private Point[] pointArray;

    public byte[] getRawData() {
        return rawData;
    }

    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }

    public Point[] getPointArray() {
        return pointArray;
    }

    public void setPointArray(Point[] pointArray) {
        this.pointArray = pointArray;
    }

}
