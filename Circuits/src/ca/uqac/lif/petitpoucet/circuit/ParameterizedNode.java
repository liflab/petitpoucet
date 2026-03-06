/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2026 Laboratoire d'informatique formelle
    Université du Québec à Chicoutimi, Canada

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet.circuit;

import static ca.uqac.lif.petitpoucet.CompositePart.compose;
import static ca.uqac.lif.petitpoucet.CompositePart.head;
import static ca.uqac.lif.petitpoucet.CompositePart.tail;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.AbstractVertex;
import ca.uqac.lif.petitpoucet.Connectable;
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
	
	/**
	 * Creates a new instance of the node.
	 * @param in_arity The input arity of the node
	 * @param out_arity The output arity of the node
	 * @param parameter The parameter {@link Node}
	 */
	public ParameterizedNode(int in_arity, int out_arity, Node parameter)
	{
		super(in_arity, out_arity);
		m_f = parameter;
		m_explanations = new ArrayList<>();
	}
	
	protected void register(Object[] outputs, Object ... inputs)
	{
		m_f.reset();
		for (int i = 0; i < inputs.length; i++)
		{
			Connectable.connect(new Constant(inputs[i]), 0, m_f, i);
		}
		m_f.evaluate(inputs, outputs);
		try
		{
			m_explanations.add(m_f.explain(new OutputPart(0)));
		}
		catch (ExplanationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * A {@link LazyVertex} that is linked to one specific evaluation of the
	 * node's parameter.
	 */
	public abstract class ParameterLazyVertex extends LazyVertex
	{
		/**
		 * Creates a new instance of the lazy vertex.
		 * @param f The factory used to generate new nodes in the lineage graph
		 * @param p The part to explain
		 * @param options The options to be passed when generating the graph
		 */
		public ParameterLazyVertex(VertexFactory f, Part p, int options)
		{
			super(f, p, options);
		}
				
		/**
		 * Gets the node to which this vertex is associated.
		 * @return The node
		 */
		/*@ non_null @*/ protected abstract Node getInstance();

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