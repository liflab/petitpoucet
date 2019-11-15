package ca.uqac.lif.petitpoucet.functions.trees;

import ca.uqac.lif.azrael.ObjectPrinter;
import ca.uqac.lif.azrael.ObjectReader;
import ca.uqac.lif.azrael.PrintException;
import ca.uqac.lif.azrael.ReadException;
import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator.NthInput;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator.NthElement;
import ca.uqac.lif.petitpoucet.common.Context;
import ca.uqac.lif.petitpoucet.functions.Function;
import ca.uqac.lif.petitpoucet.functions.FunctionQueryable;
import ca.uqac.lif.petitpoucet.functions.UnaryFunction;
import ca.uqac.lif.petitpoucet.graph.ConcreteDesignatedObject;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class PathFetch extends UnaryFunction<TreeNode,List> implements Designator
{
	protected List<PathElement> m_path;

	public PathFetch(PathElement ... elements)
	{
		super(TreeNode.class, List.class);
		m_path = new ArrayList<PathElement>(elements.length);
		for (PathElement pe : elements)
		{
			m_path.add(pe);
		}
	}

	public PathFetch(List<PathElement> elements)
	{
		super(TreeNode.class, List.class);
		m_path = new ArrayList<PathElement>(elements.size());
		m_path.addAll(elements);
	}

	@Override
	public boolean appliesTo(Object o) 
	{
		return o instanceof TreeNode;
	}

	@Override
	public Designator peek()
	{
		return this;
	}

	@Override
	public Designator tail()
	{
		return null;
	}

	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < m_path.size(); i++)
		{
			if (i > 0)
			{
				out.append("/");
			}
			out.append(m_path.get(i));
		}
		return out.toString();
	}

	@Override
	public Object print(ObjectPrinter<?> printer) throws PrintException 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object read(ObjectReader<?> reader, Object o) throws ReadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Function duplicate(boolean with_state)
	{
		return this;
	}

	@Override
	public FunctionQueryable evaluate(Object[] inputs, Object[] outputs, Context c, boolean track) 
	{
		TreeNode root = (TreeNode) inputs[0];
		List<PathElement> designated_path = new ArrayList<PathElement>();
		List<DesignatedObject> l_dob = evaluateRecursive(root, root, m_path, 0, designated_path, c, track);
		List<TreeNode> out_nodes = new ArrayList<TreeNode>(l_dob.size());
		for (int i = 0; i < l_dob.size(); i++)
		{
			DesignatedObject dob = l_dob.get(i);
			out_nodes.add((TreeNode) dob.getObject());
		}
		outputs[0] = out_nodes;
		if (track)
		{
			return new PathFetchQueryable(toString(), l_dob);
		}
		return null;
	}

	protected List<DesignatedObject> evaluateRecursive(TreeNode top_root, TreeNode root, List<PathElement> query_path, int path_index, List<PathElement> designated_path, Context c, boolean track)
	{
		List<DesignatedObject> out_list = new ArrayList<DesignatedObject>();
		PathElement p_head = query_path.get(path_index);
		if (path_index == query_path.size() - 1)
		{
			int i = 0;
			for (TreeNode child : root.getChildren())
			{
				if (p_head.fulfills(child, i))
				{
					List<PathElement> child_list = new ArrayList<PathElement>(designated_path.size() + 1);
					child_list.addAll(designated_path);
					child_list.add(PathElement.get(PathElement.WILDCARD, NthChild.get(i)));
					ConcreteDesignatedObject dob = new ConcreteDesignatedObject(new PathFetch(child_list), child);
					out_list.add(dob);
				}
				if (p_head.isDescendent())
				{
					List<PathElement> child_list = new ArrayList<PathElement>(designated_path.size() + 1);
					child_list.addAll(designated_path);
					child_list.add(PathElement.get(PathElement.WILDCARD, NthChild.get(i)));
					List<DesignatedObject> sub_list = evaluateRecursive(top_root, child, query_path, path_index, child_list, c, track);
					out_list.addAll(sub_list);
				}
				i++;
			}
		}
		else
		{
			int i = 0;
			for (TreeNode child : root.getChildren())
			{
				if (p_head.fulfills(child, i))
				{
					List<PathElement> child_list = new ArrayList<PathElement>(designated_path.size() + 1);
					child_list.addAll(designated_path);
					child_list.add(PathElement.get(PathElement.WILDCARD, NthChild.get(i)));
					List<DesignatedObject> sub_list = evaluateRecursive(top_root, child, query_path, path_index + 1, child_list, c, track);
					out_list.addAll(sub_list);
				}
				if (p_head.isDescendent())
				{
					List<PathElement> child_list = new ArrayList<PathElement>(designated_path.size() + 1);
					child_list.addAll(designated_path);
					child_list.add(PathElement.get(PathElement.WILDCARD, NthChild.get(i)));
					List<DesignatedObject> sub_list = evaluateRecursive(top_root, child, query_path, path_index, child_list, c, track);
					out_list.addAll(sub_list);
				}
				i++;
			}
		}
		return out_list;
	}

	protected static class PathFetchQueryable extends FunctionQueryable
	{
		protected List<DesignatedObject> m_nodes;

		public PathFetchQueryable(String expression, List<DesignatedObject> l_dob)
		{
			super(expression, 1, 1);
			m_nodes = l_dob;
		}

		@Override
		protected List<TraceabilityNode> queryOutput(TraceabilityQuery q, int out_index, 
				Designator tail, TraceabilityNode root, Tracer factory)
		{
			List<TraceabilityNode> leaves = new ArrayList<TraceabilityNode>(1);
			Designator t_head = tail.peek();
			Designator t_tail = tail.tail();
			if (!(t_head instanceof NthElement))
			{
				return super.queryOutput(q, out_index, t_tail, root, factory);
			}
			int elem_index = ((NthElement) t_head).getIndex();
			if (elem_index < 0 || elem_index >= m_nodes.size())
			{
				TraceabilityNode n = factory.getUnknownNode();
				root.addChild(n, Quality.NONE);
				leaves.add(n);
				return leaves;
			}
			DesignatedObject dob = m_nodes.get(elem_index);
			TraceabilityNode n = factory.getObjectNode(new ComposedDesignator(t_tail, dob.getDesignator(), NthInput.get(0)), this);
			root.addChild(n, Quality.EXACT);
			leaves.add(n);
			return leaves;
		}
	}

	public static class PathElement
	{
		protected String m_name;

		protected List<PathPredicate> m_predicates;
		
		protected boolean m_descendent = false;

		public static final transient String WILDCARD = "*";

		protected PathElement(String name, PathPredicate ... predicates)
		{
			super();
			m_name = name;
			m_predicates = new ArrayList<PathPredicate>(predicates.length);
			for(PathPredicate p : predicates)
			{
				m_predicates.add(p);
			}
		}
		
		protected void setDescendent(boolean b)
		{
			m_descendent = b;
		}
		
		public boolean isDescendent()
		{
			return m_descendent;
		}

		public static PathElement get(String name, PathPredicate ... predicates)
		{
			return get(name, false, predicates);
		}
		
		public static PathElement get(String name, boolean descendent, PathPredicate ... predicates)
		{
			PathElement pe = new PathElement(name, predicates);
			pe.setDescendent(descendent);
			return pe;
		}

		@Override
		public String toString()
		{
			StringBuilder out = new StringBuilder();
			if (m_descendent)
			{
				out.append(WILDCARD);
			}
			out.append(m_name);
			for (PathPredicate p : m_predicates)
			{
				out.append(p);
			}
			return out.toString();
		}

		public boolean fulfills(TreeNode node, int index)
		{
			if (m_name.compareTo(WILDCARD) != 0 && node.getName().compareTo(m_name) != 0)
			{
				return false;
			}
			for (PathPredicate p : m_predicates)
			{
				if (!p.fulfills(node, index))
				{
					return false;
				}
			}
			return true;
		}
	}

	public static abstract class PathPredicate
	{
		public abstract boolean fulfills(TreeNode node, int index);
	}

	public static class NthChild extends PathPredicate
	{
		protected int m_index;

		protected NthChild(int index)
		{
			super();
			m_index = index;
		}

		public static NthChild get(int index)
		{
			return new NthChild(index);
		}

		@Override
		public boolean fulfills(TreeNode node, int index)
		{
			return m_index == index;
		}

		@Override
		public String toString()
		{
			return "[" + m_index + "]";
		}
	}
}
