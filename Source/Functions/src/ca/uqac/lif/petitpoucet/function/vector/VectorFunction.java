package ca.uqac.lif.petitpoucet.function.vector;

import java.util.List;

import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
import ca.uqac.lif.petitpoucet.function.NthOutput;

/**
 * An atomic function taking as its input m vectors, and producing as its
 * output an arbitrary value (not necessarily a vector).
 * <p>
 * The class extends {@link AtomicFunction} by keeping in memory the last
 * input lists involved in a function call. It also overrides the
 * explanation provided by its parent {@link AtomicFunction}, and provides a
 * boilerplate explanation where the whole output is explained by the whole
 * input. Only functions that explain their output differently need to
 * override this method.
 * 
 * @author Sylvain Hallé
 */
public abstract class VectorFunction extends AtomicFunction
{
	/**
	 * The last vectors given as an input to the function.
	 */
	/*@ non_null @*/ protected List<?>[] m_lastInputs;

	/**
	 * Creates a new instance of input vector function.
	 * @param in_arity The input arity of the function
	 */
	public VectorFunction(int in_arity)
	{
		super(in_arity, 1);
		m_lastInputs = new List[in_arity];
	}

	@Override
	protected final Object[] getValue(Object ... inputs) throws InvalidArgumentTypeException
	{
		if (!(inputs[0] instanceof List))
		{
			throw new InvalidArgumentTypeException("Expected a list");
		}
		for (int i = 0; i < m_lastInputs.length; i++)
		{
			m_lastInputs[i] = (List<?>) inputs[i];
		}
		return new Object[] {getOutputValue(m_lastInputs)};
	}

	@Override
	/*@ non_null @*/ public PartNode getExplanation(Part part, NodeFactory factory)
	{
		PartNode root = factory.getPartNode(part, this);
		int index = NthOutput.mentionedOutput(part);
		if (index == 0) // Only one output pin possible
		{
			root.addChild(factory.getPartNode(NthOutput.replaceOutByIn(part, 0), this));
		}
		return root;
	}

	@Override
	public void reset()
	{
		super.reset();
		for (int i = 0; i < m_lastInputs.length; i++)
		{
			m_lastInputs[i] = null;			
		}
	}
	
	/**
	 * Returns the length of the shortest of the input lists given to the
	 * function the last time it was called.
	 * @return The length
	 */
	protected int getMinLength()
	{
		int len = -1;
		for (List<?> list : m_lastInputs)
		{
			if (list == null)
			{
				len = 0;
				break;
			}
			if (len < 0 || list.size() < len)
			{
				len = list.size();
			}
		}
		return len;
	}

	protected abstract Object getOutputValue(List<?> ... inputs);

}
