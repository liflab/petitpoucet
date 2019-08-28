package examples.functions;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.DesignatorLink;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthOutput;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator;

/**
 * Applies a function to all the elements of input lists,
 * producing output lists.
 */
public class ApplyToAll extends CircuitFunction
{
	/**
	 * The function to apply
	 */
	protected CircuitFunction m_function;
	
	public ApplyToAll(CircuitFunction f)
	{
		super(f.getInputArity(), f.getOutputArity());
		m_function = f;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getValue(Object[] inputs, Object[] outputs)
	{
		List[] in_lists = new List[m_inArity];
		int num_el = Integer.MAX_VALUE;
		for (int i = 0; i < m_inArity; i++)
		{
			in_lists[i] = (List<?>) inputs[i];
			num_el = Math.min(num_el, in_lists[i].size());
		}
		for (int i = 0; i < m_outArity; i++)
		{
			outputs[i] = new ArrayList<Object>();
		}
		for (int i = 0; i < num_el; i++)
		{
			Object[] outs = evaluateInnerFunctionAt(i, in_lists);
			for (int j = 0; j < m_outArity; j++)
			{
				((List<Object>) outputs[j]).add(outs[j]);
			}
		}		
	}
	
	protected Object[] evaluateInnerFunctionAt(int pos, List<?> ... lists)
	{
		Object[] ins = new Object[m_inArity];
		Object[] outs = new Object[m_outArity];
		for (int j = 0; j < m_inArity; j++)
		{
			ins[j] = lists[j].get(pos);
		}
		m_function.getValue(ins, outs);
		return outs;
	}

	@Override
	public void provenanceQuery(NthOutput d, List<DesignatorLink> links)
	{
		if (d instanceof CollectionDesignator.NthElement)
		{
			
		}
		
	}

	@Override
	public void causalityQuery(NthOutput d, List<DesignatorLink> links) {
		// TODO Auto-generated method stub
		
	}

}
