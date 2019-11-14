package examples;

import java.util.List;

import ca.uqac.lif.petitpoucet.functions.CircuitFunction;
import ca.uqac.lif.petitpoucet.functions.Constant;
import ca.uqac.lif.petitpoucet.functions.Equals;
import ca.uqac.lif.petitpoucet.functions.Fork;
import ca.uqac.lif.petitpoucet.functions.GroupFunction;
import ca.uqac.lif.petitpoucet.functions.lists.GetElement;
import ca.uqac.lif.petitpoucet.functions.lists.GetSize;
import ca.uqac.lif.petitpoucet.functions.logic.Booleans;
import ca.uqac.lif.petitpoucet.functions.logic.IfThenElse;
import ca.uqac.lif.petitpoucet.functions.numbers.Numbers;
import ca.uqac.lif.petitpoucet.functions.reflect.InstanceOf;

public class Triangle
{
	public static void main(String[] args)
	{
		IsATriangle is_a_triangle = new IsATriangle();
		List<Object> list = Utilities.createList(3, 8);
		Object[] outputs = new Object[1];
		is_a_triangle.evaluate(new Object[] {list}, outputs);
		System.out.println(outputs[0]);
	}
	
	protected static class IsATriangle extends GroupFunction
	{
		public IsATriangle()
		{
			super(1, 1);
			setName("Is triangle");
			CircuitFunction f1 = new CircuitFunction(new Fork(List.class, 3));
			associateInput(0, f1, 0);
			CircuitFunction f2 = new CircuitFunction(new Fork(List.class, 2));
			connect(f1, 0, f2, 0);
			CircuitFunction size = new CircuitFunction(GetSize.instance);
			connect(f2, 0, size, 0);
			CircuitFunction eq_3 = new CircuitFunction(Equals.instance);
			CircuitFunction three = new CircuitFunction(new Constant(3));
			connect(three, 0, eq_3, 0);
			connect(size, 0, eq_3, 1);
			CircuitFunction not_1 = new CircuitFunction(Booleans.not);
			connect(eq_3, 0, not_1, 0);
			CircuitFunction ite_1 = new CircuitFunction(IfThenElse.instance);
			connect(not_1, 0, ite_1, 0);
			CircuitFunction false_1 = new CircuitFunction(new Constant(false));
			connect(false_1, 0, ite_1, 1);
			CircuitFunction numbers = new CircuitFunction(new AllNumbers());
			connect(f1, 1, numbers, 0);
			CircuitFunction not_2 = new CircuitFunction(Booleans.not);
			connect(numbers, 0, not_2, 0);
			CircuitFunction ite_2 = new CircuitFunction(IfThenElse.instance);
			connect(not_2, 0, ite_2, 0);
			CircuitFunction false_2 = new CircuitFunction(new Constant(false));
			connect(false_2, 0, ite_2, 1);
			connect(ite_2, 0, ite_1, 2);
			CircuitFunction positive = new CircuitFunction(new AllPositive());
			connect(f1, 2, positive, 0);
			CircuitFunction not_3 = new CircuitFunction(Booleans.not);
			connect(positive, 0, not_3, 0);
			CircuitFunction ite_3 = new CircuitFunction(IfThenElse.instance);
			connect(not_3, 0, ite_3, 0);
			CircuitFunction false_3 = new CircuitFunction(new Constant(false));
			connect(false_3, 0, ite_3, 1);
			connect(ite_3, 0, ite_2, 2);
			CircuitFunction f3 = new CircuitFunction(new Fork(List.class, 3));
			connect(f2, 1, f3, 0);
			CircuitFunction te_1 = new CircuitFunction(new TriangleInequality(0, 1, 2));
			CircuitFunction te_2 = new CircuitFunction(new TriangleInequality(0, 2, 1));
			CircuitFunction te_3 = new CircuitFunction(new TriangleInequality(1, 2, 0));
			connect(f3, 0, te_1, 0);
			connect(f3, 1, te_2, 0);
			connect(f3, 2, te_3, 0);
			associateOutput(0, ite_1, 0);
			CircuitFunction and_1 = new CircuitFunction(Booleans.and);
			connect(te_1, 0, and_1, 0);
			connect(te_2, 0, and_1, 1);
			CircuitFunction and_2 = new CircuitFunction(Booleans.and);
			connect(and_1, 0, and_2, 0);
			connect(te_3, 0, and_2, 1);
			connect(and_2, 0, ite_3, 2);
		}
	}
	
	protected static class AllNumbers extends GroupFunction
	{
		public AllNumbers()
		{
			super(1, 1);
			setName("All numbers");
			CircuitFunction f = new CircuitFunction(new Fork(Object.class, 3));
			associateInput(0, f, 0);
			CircuitFunction get_1 = new CircuitFunction(new GetElement(0));
			CircuitFunction get_2 = new CircuitFunction(new GetElement(1));
			CircuitFunction get_3 = new CircuitFunction(new GetElement(2));
			connect(f, 0, get_1, 0);
			connect(f, 1, get_2, 0);
			connect(f, 2, get_3, 0);
			CircuitFunction isnum_1 = new CircuitFunction(new InstanceOf(Number.class));
			CircuitFunction isnum_2 = new CircuitFunction(new InstanceOf(Number.class));
			CircuitFunction isnum_3 = new CircuitFunction(new InstanceOf(Number.class));
			connect(get_1, 0, isnum_1, 0);
			connect(get_2, 0, isnum_2, 0);
			connect(get_3, 0, isnum_3, 0);
			CircuitFunction and_1 = new CircuitFunction(Booleans.and);
			CircuitFunction and_2 = new CircuitFunction(Booleans.and);
			connect(isnum_1, 0, and_2, 0);
			connect(isnum_2, 0, and_2, 1);
			connect(isnum_3, 0, and_1, 0);
			connect(and_2, 0, and_1, 1);
			associateOutput(0, and_2, 0);
		}
		
		@Override
		public AllNumbers duplicate(boolean with_state)
		{
			return new AllNumbers();
		}
	}
	
	protected static class AllPositive extends GroupFunction
	{
		public AllPositive()
		{
			super(1, 1);
			setName("All positive");
			CircuitFunction f = new CircuitFunction(new Fork(Object.class, 3));
			associateInput(0, f, 0);
			CircuitFunction get_1 = new CircuitFunction(new GetElement(0));
			CircuitFunction get_2 = new CircuitFunction(new GetElement(1));
			CircuitFunction get_3 = new CircuitFunction(new GetElement(2));
			connect(f, 0, get_1, 0);
			connect(f, 1, get_2, 0);
			connect(f, 2, get_3, 0);
			CircuitFunction isnum_1 = new CircuitFunction(new IsPositive());
			CircuitFunction isnum_2 = new CircuitFunction(new IsPositive());
			CircuitFunction isnum_3 = new CircuitFunction(new IsPositive());
			connect(get_1, 0, isnum_1, 0);
			connect(get_2, 0, isnum_2, 0);
			connect(get_3, 0, isnum_3, 0);
			CircuitFunction and_1 = new CircuitFunction(Booleans.and);
			CircuitFunction and_2 = new CircuitFunction(Booleans.and);
			connect(isnum_1, 0, and_2, 0);
			connect(isnum_2, 0, and_2, 1);
			connect(isnum_3, 0, and_1, 0);
			connect(and_2, 0, and_1, 1);
			associateOutput(0, and_2, 0);
		}
		
		@Override
		public AllNumbers duplicate(boolean with_state)
		{
			return new AllNumbers();
		}
	}
	
	protected static class IsPositive extends GroupFunction
	{
		public IsPositive()
		{
			super(1, 1);
			CircuitFunction gt = new CircuitFunction(Numbers.isGreaterThan);
			associateInput(0, gt, 0);
			CircuitFunction zero = new CircuitFunction(new Constant(0));
			connect(zero, 0, gt, 1);
			associateOutput(0, gt, 0);
		}
		
		@Override
		public IsPositive duplicate(boolean with_state)
		{
			return new IsPositive();
		}
	}
	
	protected static class TriangleInequality extends GroupFunction
	{
		protected int m_x;
		protected int m_y;
		protected int m_z;
		
		public TriangleInequality(int x, int y, int z)
		{
			super(1, 1);
			setName("Triangle inequality");
			m_x = x;
			m_y = y;
			m_z = z;
			CircuitFunction f = new CircuitFunction(new Fork(Object.class, 3));
			associateInput(0, f, 0);
			CircuitFunction get_1 = new CircuitFunction(new GetElement(x));
			CircuitFunction get_2 = new CircuitFunction(new GetElement(y));
			CircuitFunction get_3 = new CircuitFunction(new GetElement(z));
			connect(f, 0, get_1, 0);
			connect(f, 1, get_2, 0);
			connect(f, 2, get_3, 0);
			CircuitFunction plus = new CircuitFunction(Numbers.addition);
			connect(get_1, 0, plus, 0);
			connect(get_2, 0, plus, 1);
			CircuitFunction gt = new CircuitFunction(Numbers.isGreaterThan);
			connect(plus, 0, gt, 0);
			connect(get_3, 0, gt, 1);
			associateOutput(0, gt, 0);
		}
		
		@Override
		public TriangleInequality duplicate(boolean with_state)
		{
			return new TriangleInequality(m_x, m_y, m_z);
		}
	}
	
}
