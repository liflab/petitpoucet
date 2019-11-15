package examples;

import java.util.List;

import ca.uqac.lif.petitpoucet.functions.CircuitFunction;
import ca.uqac.lif.petitpoucet.functions.Constant;
import ca.uqac.lif.petitpoucet.functions.Fork;
import ca.uqac.lif.petitpoucet.functions.GroupFunction;
import ca.uqac.lif.petitpoucet.functions.lists.ApplyToAll;
import ca.uqac.lif.petitpoucet.functions.lists.Filter;
import ca.uqac.lif.petitpoucet.functions.lists.GetElement;
import ca.uqac.lif.petitpoucet.functions.numbers.Numbers;

public class TriangleAreas 
{
	public static void main(String[] args)
	{
		Object[] outputs = new Object[1];
		SumOfAreas soa = new SumOfAreas();
		soa.evaluate(new Object[] {Utilities.createList(
				Utilities.createList(3, 4, 5),
				Utilities.createList(3, "foo", 5),
				Utilities.createList(3, 5, 8),
				Utilities.createList(5, 10, 7)
				)}, outputs);
		/*
		HeronFormula hf = new HeronFormula();
		hf.evaluate(new Object[] {3, 4, 5}, outputs);
		*/
		System.out.println(outputs[0]);
	}
	
	public static class SumOfAreas extends GroupFunction
	{
		public SumOfAreas()
		{
			super(1, 1);
			setName("Sum areas");
			CircuitFunction fork = new CircuitFunction(new Fork(List.class, 2));
			associateInput(0, fork, 0);
			CircuitFunction valid = new CircuitFunction(new ApplyToAll(new Triangle.IsATriangle()));
			connect(fork, 1, valid, 0);
			CircuitFunction filter = new CircuitFunction(new Filter());
			connect(fork, 0, filter, 0);
			connect(valid, 0, filter, 1);
			CircuitFunction areas = new CircuitFunction(new ApplyToAll(new HeronFormula()));
			connect(filter, 0, areas, 0);
			CircuitFunction sum = new CircuitFunction(Numbers.avg);
			connect(areas, 0, sum, 0);
			associateOutput(0, sum, 0);
		}
	}
	
	public static class HeronFormula extends GroupFunction
	{
		public HeronFormula()
		{
			super(1, 1);
			setName("Heron");
			CircuitFunction h_f = new CircuitFunction(new Fork(List.class, 3));
			associateInput(0, h_f, 0);
			CircuitFunction h_a = new CircuitFunction(new GetElement(0));
			CircuitFunction h_b = new CircuitFunction(new GetElement(1));
			CircuitFunction h_c = new CircuitFunction(new GetElement(2));
			connect(h_f, 0, h_a, 0);
			connect(h_f, 1, h_b, 0);
			connect(h_f, 2, h_c, 0);
			CircuitFunction f_a = new CircuitFunction(new Fork(Number.class, 2));
			CircuitFunction f_b = new CircuitFunction(new Fork(Number.class, 2));
			CircuitFunction f_c = new CircuitFunction(new Fork(Number.class, 2));
			connect(h_a, 0, f_a, 0);
			connect(h_b, 0, f_b, 0);
			connect(h_c, 0, f_c, 0);
			CircuitFunction plus_1 = new CircuitFunction(Numbers.addition);
			connect(f_a, 0, plus_1, 0);
			connect(f_b, 0, plus_1, 1);
			CircuitFunction plus_2 = new CircuitFunction(Numbers.addition);
			connect(plus_1, 0, plus_2, 0);
			connect(f_c, 0, plus_2, 1);
			CircuitFunction div = new CircuitFunction(Numbers.division);
			connect(plus_2, 0, div, 0);
			CircuitFunction two = new CircuitFunction(new Constant(2));
			connect(two, 0, div, 1);
			CircuitFunction f_s = new CircuitFunction(new Fork(Number.class, 4));
			connect(div, 0, f_s, 0);
			CircuitFunction minus_1 = new CircuitFunction(Numbers.subtraction);
			connect(f_s, 1, minus_1, 0);
			connect(f_a, 1, minus_1, 1);
			CircuitFunction minus_2 = new CircuitFunction(Numbers.subtraction);
			connect(f_s, 0, minus_2, 0);
			connect(f_b, 1, minus_2, 1);
			CircuitFunction minus_3 = new CircuitFunction(Numbers.subtraction);
			connect(f_s, 2, minus_3, 0);
			connect(f_c, 1, minus_3, 1);
			CircuitFunction times_1 = new CircuitFunction(Numbers.multiplication);
			connect(f_s, 3, times_1, 0);
			connect(minus_1, 0, times_1, 1);
			CircuitFunction times_2 = new CircuitFunction(Numbers.multiplication);
			connect(times_1, 0, times_2, 0);
			connect(minus_2, 0, times_2, 1);
			CircuitFunction times_3 = new CircuitFunction(Numbers.multiplication);
			connect(times_2, 0, times_3, 0);
			connect(minus_3, 0, times_3, 1);
			CircuitFunction sqrt = new CircuitFunction(Numbers.sqrt);
			connect(times_3, 0, sqrt, 0);
			associateOutput(0, sqrt, 0);
		}
		
		@Override
		public HeronFormula duplicate(boolean with_state)
		{
			return this;
		}
	}
}
