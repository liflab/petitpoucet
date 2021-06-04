package ca.uqac.lif.dag;

import java.io.PrintStream;

public interface Renderer
{
	/**
	 * Renders an object to a print stream.
	 * @param ps The print stream
	 */
	public void render(PrintStream ps);
}
