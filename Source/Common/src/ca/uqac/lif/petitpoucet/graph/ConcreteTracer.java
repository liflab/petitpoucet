/*
    Petit Poucet, a library for tracking links between objects.
    Copyright (C) 2016-2019 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.petitpoucet.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.LabeledEdge.Quality;
import ca.uqac.lif.petitpoucet.Tracer;
import ca.uqac.lif.petitpoucet.TraceabilityNode;
import ca.uqac.lif.petitpoucet.TraceabilityQuery;
import ca.uqac.lif.petitpoucet.Trackable;

public class ConcreteTracer implements Tracer
{
  /**
   * Determines whether degenerate and/or nodes are removed from the tree
   * (default: true)
   */
  protected boolean m_simplify = true;
  
  /**
   * A map keeping track of which designated objects already have nodes
   */
  protected Map<DesignatedObject,ConcreteTraceabilityNode> m_nodes;
  
  /**
   * Creates a new tracer with default settings
   */
  public ConcreteTracer()
  {
    super();
    m_nodes = new HashMap<DesignatedObject,ConcreteTraceabilityNode>();
  }
  
  /**
   * Sets whether degenerate and/or nodes are removed from the tree
   * @param b <tt>true</tt> to simplify (default), <tt>false</tt> otherwise
   * @return This tracer
   */
  public ConcreteTracer setSimplify(boolean b)
  {
    m_simplify = b;
    return this;
  }

  /**
   * Gets the traceability tree for a given designator and object
   * @param q The type of traceability query
   * @param d The designator indicating the part of the object this query is about
   * @param o The target object
   * @return A node representing the root of the traceability tree
   */
  /*@ non_null @*/ public ConcreteObjectNode getTree(TraceabilityQuery q, /*@ non_null @*/ Designator d, /*@ non_null @*/ Object o)
  {
    Set<ConcreteTraceabilityNode> visited = new HashSet<ConcreteTraceabilityNode>();
    DesignatedObject dob = new ConcreteDesignatedObject(d, o);
    ConcreteObjectNode tn = getObjectNode(dob);
    getChildren(q, tn, visited);
    return tn;
  }

  /*@ non_null @*/ protected void getChildren(TraceabilityQuery q, /*@ non_null @*/ ConcreteTraceabilityNode root, /*@ non_null @*/ Set<ConcreteTraceabilityNode> visited)
  {
    if (visited.contains(root))
    {
      // This node has already been expanded
      return;
    }
    visited.add(root);
    if (!(root instanceof ConcreteObjectNode))
    {
      // Nothing to expand
      return;
    }
    DesignatedObject dob = ((ConcreteObjectNode) root).m_object;
    Object o = dob.getObject();
    Designator d = dob.getDesignator();
    if (d instanceof Designator.Identity || d instanceof Designator.Nothing 
        || d instanceof Designator.Unknown)
    {
      // Trivial designator: nothing to expand
      return;
    }
    if (o instanceof Trackable)
    {
      // Object is trackable: send the query and create nodes from its result
      Trackable to = (Trackable) o;
      List<TraceabilityNode> leaves = to.query(q, d, root, this);
      for (TraceabilityNode leaf : leaves)
      {
        getChildren(q, (ConcreteTraceabilityNode) leaf, visited);
      }
    }
    else
    {
      // Query is non-trivial, and object is not trackable: nothing to do
      ConcreteTraceabilityNode n = (ConcreteTraceabilityNode) getObjectNode(Designator.unknown, o);
      root.addChild(n, Quality.EXACT);
    }
    return;
  }

  @Override
  public ConcreteObjectNode getObjectNode(DesignatedObject dob)
  {
    if (m_nodes.containsKey(dob))
    {
      return (ConcreteObjectNode) m_nodes.get(dob);
    }
    ConcreteObjectNode on = new ConcreteObjectNode(dob);
    m_nodes.put(dob, on);
    return on;
  }
  
  @Override
  public ConcreteObjectNode getObjectNode(Designator d, Object o)
  {
    ConcreteDesignatedObject cdo = new ConcreteDesignatedObject(d, o);
    return getObjectNode(cdo);
  }

  @Override
  public AndNode getAndNode()
  {
    return new AndNode();
  }

  @Override
  public OrNode getOrNode()
  {
    return new OrNode();
  }

  @Override
  public UnknownNode getUnknownNode()
  {
    return new UnknownNode();
  }

  @Override
  public ConcreteTracer getSubTracer()
  {
    return new ConcreteTracer();
  }

  @Override
  public ConcreteTraceabilityTree trace(TraceabilityQuery q, Designator d, Object o)
  {
    TraceabilityNode root = getTree(q, d, o);
    ConcreteTraceabilityTree ctt = new ConcreteTraceabilityTree(root);
    return ctt;
  }
}
