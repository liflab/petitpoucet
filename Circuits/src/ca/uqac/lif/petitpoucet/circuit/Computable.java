package ca.uqac.lif.petitpoucet.circuit;

public interface Computable
{
	public Object compute(int index);
	
	public default Object compute()
	{
		return compute(0);
	}
	
	public void reset();
}
