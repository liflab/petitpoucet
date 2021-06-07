package ca.uqac.lif.petitpoucet.function.vector;

import java.util.List;

import ca.uqac.lif.petitpoucet.NodeFactory;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.PartNode;
import ca.uqac.lif.petitpoucet.function.AtomicFunction;
import ca.uqac.lif.petitpoucet.function.InvalidArgumentTypeException;
import ca.uqac.lif.petitpoucet.function.NthOutput;

/**
 * An atomic function taking as its input a single vector, and producing as its
 * output an arbitrary value (not necessarily a vector).
 * <p>
 * The class extends {@link AtomicFunction} by keeping in memory the last
 * of input list involved in a function call. It also overrides the
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
	 * The last vector given as an input to the function.
	 */
	protected List<?> m_lastInputs;

	/**
	 * Creates a new instance of input vector function.
	 */
	public VectorFunction()
	{
		super(1, 1);
	}

	@Override
	protected final Object[] getValue(Object ... inputs) throws InvalidArgumentTypeException
	{
		if (!(inputs[0] instanceof List))
		{
			throw new InvalidArgumentTypeException("Expected a list");
		}
		m_lastInputs = (List<?>) inputs[0];
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
		m_lastInputs = null;
	}

	protected abstract Object getOutputValue(List<?> inputs);

}
