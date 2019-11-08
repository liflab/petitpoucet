package examples.ltl;

import ca.uqac.lif.petitpoucet.functions.CircuitFunction;
import ca.uqac.lif.petitpoucet.functions.Fork;
import ca.uqac.lif.petitpoucet.functions.GroupFunction;
import ca.uqac.lif.petitpoucet.functions.ltl.Ltl;

public class HasNext 
{

	public static void main(String[] args) 
	{
		GroupFunction gf = new GroupFunction(1, 1);
		{
			CircuitFunction f = new CircuitFunction(new Fork(String.class, 2));
			
			CircuitFunction g = new CircuitFunction(Ltl.globally);
			CircuitFunction imp = new CircuitFunction(Ltl.implies);
		}

	}

}
