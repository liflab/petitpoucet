package ca.uqac.lif.petitpoucet.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.DesignatorLink;
import ca.uqac.lif.petitpoucet.DesignatorLink.Quality;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Trackable;
import ca.uqac.lif.petitpoucet.graph.TraceabilityNode.QualityLink;

public class Tracer
{
	/**
	 * Gets the traceability tree for a given designator and object
	 * @param q The type of traceability query
	 * @param d The designator indicating the part of the object this query is about
	 * @param o The target object
	 * @return A node representing the root of the traceability tree
	 */
	/*@ non_null @*/ public TraceabilityNode getTree(TraceabilityQuery q, /*@ non_null @*/ Designator d, /*@ non_null @*/ Object o)
	{
		Map<DesignatedObject,TraceabilityNode> map = new HashMap<DesignatedObject,TraceabilityNode>();
		DesignatedObject dob = new ConcreteDesignatedObject(d, o);
		TraceabilityNode tn = new TraceabilityNode(dob);
		List<QualityLink> children = getChildren(q, tn, map);
		tn.addChildren(children);
		return tn;
	}

	/*@ non_null @*/ protected List<QualityLink> getChildren(TraceabilityQuery q, /*@ non_null @*/ TraceabilityNode tn, /*@ non_null @*/ Map<DesignatedObject,TraceabilityNode> nodes)
	{
		List<QualityLink> out_list = new ArrayList<QualityLink>();
		DesignatedObject dob = tn.m_object;
		if (nodes.containsKey(dob))
		{
			// This node has already been expanded
			return out_list;
		}
		Object o = dob.getObject();
		Designator d = dob.getDesignator();
		if (d instanceof Designator.Identity || d instanceof Designator.Nothing 
				|| d instanceof Designator.Unknown)
		{
			// Trivial designator: nothing to expand
			return out_list;
		}
		if (o instanceof Trackable)
		{
			// Object is trackable: send the query and create nodes from its result
			Trackable to = (Trackable) o;
			List<DesignatorLink> links = to.query(q, d);
			for (DesignatorLink dl : links)
			{
				DesignatedObject l_dob = dl.getDesignatedObject();
				Quality l_quality = dl.getQuality();
				TraceabilityNode tn_child = null;
				if (nodes.containsKey(l_dob))
				{
					tn_child = nodes.get(l_dob);
				}
				else
				{
					tn_child = new TraceabilityNode(l_dob);
					nodes.put(l_dob, tn_child);
					List<QualityLink> l_ql = getChildren(q, tn_child, nodes);
					tn_child.addChildren(l_ql);
				}
				QualityLink ql = new QualityLink(tn_child, l_quality);
				out_list.add(ql);
			}
		}
		else
		{
			// Query is non-trivial, and object is not trackable: nothing to do
			ConcreteDesignatedObject cdo = new ConcreteDesignatedObject(Designator.unknown, o);
			TraceabilityNode tn_child = getNode(cdo, nodes);
			QualityLink ql = new QualityLink(tn_child, Quality.EXACT);
			out_list.add(ql);
		}
		return out_list;
	}

	/*@ non_null @*/ protected static TraceabilityNode getNode(DesignatedObject dob, /*@ non_null @*/ Map<DesignatedObject,TraceabilityNode> nodes)
	{
		TraceabilityNode tn_child = null;
		if (nodes.containsKey(dob))
		{
			tn_child = nodes.get(dob);

		}
		else
		{
			tn_child = new TraceabilityNode(dob);
		}
		return tn_child;
	}
}
