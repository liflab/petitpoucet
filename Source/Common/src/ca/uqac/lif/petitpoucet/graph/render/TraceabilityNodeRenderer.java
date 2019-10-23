package ca.uqac.lif.petitpoucet.graph.render;

import ca.uqac.lif.petitpoucet.graph.ConcreteTraceabilityNode;

public interface TraceabilityNodeRenderer<T>
{
	public enum CaptionStyle {NONE, SHORT, FULL}
	
	public T render(ConcreteTraceabilityNode root);
	
	public void setFlatten(boolean b);
	
	public void setShowCaptions(CaptionStyle s);
}
