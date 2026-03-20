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

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.Vertex;
import ca.uqac.lif.petitpoucet.CompositePart;
import ca.uqac.lif.petitpoucet.Connectable;
import ca.uqac.lif.petitpoucet.LazyVertex;
import ca.uqac.lif.petitpoucet.Part;
import ca.uqac.lif.petitpoucet.Subgraph;
import ca.uqac.lif.petitpoucet.VertexFactory;
import ca.uqac.lif.petitpoucet.ConcreteVertex;

/**
 * A node that takes another one as a parameter.
 * @author Sylvain Hallé
 */
public abstract class ParameterizedNode extends Node
{
	/*@ non_null @*/ protected final Circuit m_f;

	/**
	 * The explanations for each invocation of the node.
	 */
	/*@ non_null @*/ protected final List<Vertex> m_explanations;

	/**
	 * Creates a new instance of the node.
	 * @param in_arity The input arity of the node
	 * @param out_arity The output arity of the node
	 * @param parameter The parameter {@link Node}
	 */
	public ParameterizedNode(int in_arity, int out_arity, Circuit parameter)
	{
		super(in_arity, out_arity);
		m_f = parameter;
		m_explanations = new ArrayList<>();
	}

	protected void register(Object[] outputs, Object[] inputs)
	{
		m_f.reset();
		for (int i = 0; i < inputs.length; i++)
		{
			UpstreamConnection uc = new UpstreamConnection(new Constant(inputs[i]), 0);
			DownstreamConnection dc = new DownstreamConnection(m_f, i);
			Connectable.connect(uc, 0, dc, i);
		}
		m_f.evaluate(inputs, outputs);
		try
		{
			m_explanations.add(m_f.explain(OutputPart.FIRST));
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
		public ParameterLazyVertex(VertexFactory f, Part p)
		{
			super(f, p);
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
		/*@ non_null @*/ protected ConcreteVertex explainElement(int index, Part new_p, VertexFactory m_factory) throws ExplanationException
		{
			ConcreteVertex exp;
			Vertex in_e = m_explanations.get(index);
			if (in_e instanceof LazyVertex)
			{
				exp = ((LazyVertex) in_e).concretize(new_p, m_factory);

			}
			else
			{
				exp = (ConcreteVertex) in_e;
			}
			if (!(exp instanceof Subgraph))
			{
				extendLeaves(new_p, index, exp.findLeaves(), null, m_factory);
				return exp;
			}
			else
			{
				Subgraph inner = (Subgraph) exp;
				extendLeaves(new_p, index, inner.innerLeaves(), inner, m_factory);
				ConcreteVertex root = m_factory.getPart(CompositePart.compose(new_p, OutputPart.FIRST), m_f);
				root.addChild(inner);
				return root;
			}
		}

		protected abstract ConcreteVertex extendLeaves(Part new_p, int index, List<Vertex> children, Subgraph inner, VertexFactory m_factory);
	}
}