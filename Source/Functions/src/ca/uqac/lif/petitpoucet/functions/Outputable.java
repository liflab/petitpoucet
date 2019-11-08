package ca.uqac.lif.petitpoucet.functions;

import ca.uqac.lif.petitpoucet.common.Context;

public interface Outputable
{
	public Object getOutput(Context c);
	
	public Object getOutput(Context c, boolean track);

	public Object getOutput(int index, Context c);
	
	public Object getOutput(int index, Context c, boolean track);
}