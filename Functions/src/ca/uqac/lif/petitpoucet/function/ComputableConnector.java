package ca.uqac.lif.petitpoucet.function;

import ca.uqac.lif.petitpoucet.circuit.Connectable;
import ca.uqac.lif.petitpoucet.circuit.ConnectableConnector;

public class ComputableConnector extends ConnectableConnector
{
	public static final ComputableConnector instance = new ComputableConnector();
	
	public static void connect(Connectable c1, int i1, Connectable c2, int i2)
	{
		ConnectableConnector.connect(c1, i1, c2, i2);
	}
	
	private ComputableConnector()
	{
		super();
	}
}
