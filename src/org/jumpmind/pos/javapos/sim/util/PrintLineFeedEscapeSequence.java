package org.jumpmind.pos.javapos.sim.util;

import jpos.JposException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jumpmind.pos.javapos.sim.SimulatedPOSPrinterService;

public class PrintLineFeedEscapeSequence extends AbstractEscapeSequence {
	static final Log logger = LogFactory.getLog(PrintLineFeedEscapeSequence.class);
	private int lineFeedNumber;
	
	public int findNext(String data, int startPosition) {
		int current = data.indexOf(getPrefix(), startPosition);
		if (current >= 0) {
			int beginSeqPosition = current + getPrefix().length();
			int endSeqPosition = data.indexOf(getSuffix(), beginSeqPosition);
			if (endSeqPosition > 0) {
				lineFeedNumber = new Integer(data.substring(beginSeqPosition, endSeqPosition)).intValue();
				current = endSeqPosition + getSuffix().length() - 1;
			} 
			else {
				current = -1;
			}
			
		}
		return current;
	}

	public int getSequenceLength() {
		int length = 0;
		if (lineFeedNumber > 0) {
			length = new String(getPrefix() + (new Integer(lineFeedNumber).toString()) + getSuffix()).length();
		}
		return length;
	}

	public String getSuffix() {
		return "lF";
	}

	public void print(SimulatedPOSPrinterService service, int station) throws JposException {
		for (int i = 0; i < lineFeedNumber; i++) {
			service.printNormal(station, "\n");
		}
	}
	

}
