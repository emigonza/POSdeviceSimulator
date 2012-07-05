package org.jumpmind.pos.javapos.sim.util;

public abstract class AbstractEscapeSequence implements IEscapeSequence {
	private String prefix = Character.toString('\u001B') + "|";

	public String getPrefix() {
		return prefix;
	}
	
}
