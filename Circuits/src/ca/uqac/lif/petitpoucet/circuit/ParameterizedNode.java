package ca.uqac.lif.petitpoucet.circuit;

import static ca.uqac.lif.petitpoucet.CompositePart.compose;
import static ca.uqac.lif.petitpoucet.CompositePart.head;
import static ca.uqac.lif.petitpoucet.CompositePart.tail;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.AbstractVertex;
import ca.uqac.lif.petitpoucet.LazyVertex;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.Subgraph;
import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.VertexFactory;
import ca.uqac.lif.petitpoucet.Vertex.PartVertex;
import ca.uqac.lif.petitpoucet.circuit.Lists.NthElement;

/**
 * A node that takes another one as a parameter.
 * @author Sylvain Hallé
 */
public abstract class ParameterizedNode extends Node
{
	/*@ non_null @*/ protected final Node m_f;
	
	/**
	 * The explanations for each invocation of the node.
	 */
	/*@ non_null @*/ protected final List<AbstractVertex> m_explanations;
	
	public ParameterizedNode(int in_arity, int out_arity, Node parameter)
	{
		super(in_arity, out_arity);
		m_f = parameter;
		m_explanations = new ArrayList<>();
	}
	
	public abstract class ParameterLazyVertex extends LazyVertex
	{
		public ParameterLazyVertex(VertexFactory f, Part p, int options)
		{
			super(f, p, options);
		}
		
		protected abstract Node getInstance();

		/**
		 * Computes the explanation for a specific element of the output list.
		 * @param index The position of the element in the list
		 * @param tail The tail part of the explanation
		 * @param f The factory used to create vertices for this explanation
		 * @return The root vertex of the explanation graph
		 * @throws ExplanationException Thrown if an error occurred during the
		 * calculation of the explanation
		 */
		/*@ non_null @*/ protected Vertex explainElement(int index, Part new_p) throws ExplanationException
		{
			Vertex inner;
			AbstractVertex in_e = m_explanations.get(index);
			if (in_e instanceof LazyVertex)
			{
				((LazyVertex) in_e).concretize(new_p);
				inner = ((LazyVertex) in_e).subgraph();
			}
			else
			{
				inner = (Vertex) in_e;
			}
			List<Vertex> children;
			Subgraph add_to = null;
			if (inner instanceof Subgraph)
			{
				children = ((Subgraph) inner).findLeaves();
				add_to = (Subgraph) inner;
			}
			else
			{
				children = inner.getChildren();
			}
			for (int i = 0; i < children.size(); i++)
			{
				Vertex child = children.get(i);
				if (!(child instanceof PartVertex))
				{
					continue;
				}
				PartVertex pv = (PartVertex) child;
				Part p = pv.getPart();
				Part p_head = head(p);
				if (!(p_head instanceof InputPart))
				{
					continue;
				}
				InputPart op = (InputPart) p_head;
				if (op.getIndex() != 0)
				{
					throw new ExplanationException("Expected input 0");
				}
				Part new_part = compose(tail(p), new NthElement(index), new InputPart(0));
				if (add_to == null)
				{
					child.addChild(m_factory.getPart(new_part, getInstance()));
				}
				else
				{
					add_to.addChild(m_factory.getPart(new_part, getInstance()), i);
				}
			}
			Vertex root = m_factory.getPart(compose(new_p, new OutputPart(0)), m_f);
			if (inner instanceof Subgraph)
			{
				((Subgraph) inner).pushRoot(root);
				return inner;
			}
			root.addChild(inner);
			return root;
		}
	}
}