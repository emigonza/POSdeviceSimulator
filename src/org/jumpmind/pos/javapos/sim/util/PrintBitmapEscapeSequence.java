package org.jumpmind.pos.javapos.sim.util;

import jpos.JposException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jumpmind.pos.javapos.sim.SimulatedPOSPrinterService;

public class PrintBitmapEscapeSequence extends AbstractEscapeSequence {
	static final Log logger = LogFactory.getLog(PrintBitmapEscapeSequence.class);
	private int bitmapNumber;
		
	public void print(SimulatedPOSPrinterService printerService, int station) throws JposException {
		if (printerService.getInMemoryBitmaps() == null || 
				!printerService.getInMemoryBitmaps().containsKey(new Integer(bitmapNumber))) {
			
			logger.error("Unable to print bitmap from memory, it was not found.  " +
					"Ensure setBitmap() was called to load it.");
		}
		else {
			InMemoryBitmap memoryBMap = printerService.getInMemoryBitmaps().get(new Integer(bitmapNumber));
			printerService.printBitmap(memoryBMap.getStation(), memoryBMap.getFileName(), 
					memoryBMap.getWidth(), memoryBMap.getAlignment());
		}
	}

	public int findNext(String data, int startPosition) {
		int current = data.indexOf(getPrefix(), startPosition);
		if (current >= 0) {
			int beginSeqPosition = current + getPrefix().length();
			int endSeqPosition = data.indexOf(getSuffix(), beginSeqPosition);
			if (endSeqPosition > 0) {
				bitmapNumber = new Integer(data.substring(beginSeqPosition, endSeqPosition)).intValue();
			} 
			else {
				current = -1;
			}
			current = endSeqPosition;
		}
		return current;
	}	
	
	public int getSequenceLength() {
		int length = 0;
		if (bitmapNumber > 0) {
			length = new String(getPrefix() + (new Integer(bitmapNumber).toString()) + getSuffix()).length();
		}
		return length;
	}
	
	public String getSuffix() {
		return "B";
	}
}
