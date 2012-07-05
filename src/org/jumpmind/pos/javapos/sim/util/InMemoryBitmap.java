package org.jumpmind.pos.javapos.sim.util;

public class InMemoryBitmap {
	private int station;
	private String fileName;
	private int width;
	private int alignment;
	
	public InMemoryBitmap(int station, String fileName, int width, int alignment) {
		this.station = station;
		this.fileName = fileName;
		this.width = width;
		this.alignment = alignment;    		
	}
	public int getStation() {
		return station;
	}
	public void setStation(int station) {
		this.station = station;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getAlignment() {
		return alignment;
	}
	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}
}
