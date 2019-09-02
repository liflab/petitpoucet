package ca.uqac.lif.petitpoucet.functions.ltl;

public class Globally extends UnaryOperator
{
	public Globally()
	{
		super(true);
	}
	
	@Override
	public String toString()
	{
		return "Globally";
	}
}
