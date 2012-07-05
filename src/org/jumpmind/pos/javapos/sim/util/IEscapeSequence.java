package org.jumpmind.pos.javapos.sim.util;

import jpos.JposException;

import org.jumpmind.pos.javapos.sim.SimulatedPOSPrinterService;

public interface IEscapeSequence {
	public void print(SimulatedPOSPrinterService service, int station) throws JposException;
	
	public int findNext(String data, int startPosition);
	
	public int getSequenceLength();
	
	public String getPrefix();
	
	public String getSuffix();
}
