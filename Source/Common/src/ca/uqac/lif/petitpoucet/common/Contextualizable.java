package ca.uqac.lif.petitpoucet.common;

public interface Contextualizable
{
	public Object getContext(String key);
	
	public void setContext(String key, Object value);

}
