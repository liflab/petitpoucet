package ca.uqac.lif.petitpoucet.function;

import ca.uqac.lif.dag.Connectable;
import ca.uqac.lif.dag.Pin;
import ca.uqac.lif.util.Duplicable;

public abstract class FunctionPin<T extends Connectable> extends Pin<T> implements Duplicable
{
	protected Object m_value;

	protected boolean m_evaluated;
	
	public FunctionPin(T function, int index)
	{
		super(function, index);
		m_evaluated = false;
		m_value = null;
	}
	
	public void copyInto(FunctionPin<?> pin, boolean with_state)
	{
		pin.m_evaluated = m_evaluated;
		pin.m_value = m_value;
	}
	
	public void reset()
	{
		m_evaluated = false;
	}
	
	public void setValue(Object v)
	{
		m_evaluated = true;
		m_value = v;
	}
	
	@Override
	public FunctionPin<T> duplicate()
	{
		return duplicate(false);
	}
	
	@Override
	public abstract FunctionPin<T> duplicate(boolean with_state);
	
	public abstract Object getValue();
}
