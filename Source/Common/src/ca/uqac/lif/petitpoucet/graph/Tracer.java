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
import ca.uqac.lif.petitpoucet.graph.TraceabilityNode.LabeledEdge;

public class Tracer
{
  /**
   * Creates a new tracer with default settings
   */
  public Tracer()
  {
    super();
  }

  /**
   * Gets the traceability tree for a given designator and object
   * @param q The type of traceability query
   * @param d The designator indicating the part of the object this query is about
   * @param o The target object
   * @return A node representing the root of the traceability tree
   */
  /*@ non_null @*/ public ObjectNode getTree(TraceabilityQuery q, /*@ non_null @*/ Designator d, /*@ non_null @*/ Object o)
  {
    Map<DesignatedObject,ObjectNode> map = new HashMap<DesignatedObject,ObjectNode>();
    DesignatedObject dob = new ConcreteDesignatedObject(d, o);
    ObjectNode tn = new ObjectNode(dob);
    List<LabeledEdge> children = getChildren(q, tn, map);
    tn.addChildren(children);
    return tn;
  }

  /*@ non_null @*/ protected List<LabeledEdge> getChildren(TraceabilityQuery q, /*@ non_null @*/ ObjectNode tn, /*@ non_null @*/ Map<DesignatedObject,ObjectNode> nodes)
  {
    List<LabeledEdge> out_list = new ArrayList<LabeledEdge>();
    DesignatedObject dob = tn.m_object;
    if (dob == null || nodes.containsKey(dob))
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
      List<List<DesignatorLink>> o_links = to.query(q, d);
      OrNode or_root = new OrNode();
      for (List<DesignatorLink> links : o_links)
      {
        AndNode and = new AndNode();
        boolean and_added = false;
        for (DesignatorLink dl : links)
        {
          List<DesignatedObject> list_dob = dl.getDesignatedObjects();
          if (list_dob != null)
          {
            for (DesignatedObject l_dob : list_dob)
            {
              Quality l_quality = dl.getQuality();
              ObjectNode tn_child = null;
              if (nodes.containsKey(l_dob))
              {
                tn_child = nodes.get(l_dob);
              }
              else
              {
                tn_child = new ObjectNode(l_dob);
                List<LabeledEdge> l_ql = getChildren(q, tn_child, nodes);
                nodes.put(l_dob, tn_child);
                tn_child.addChildren(l_ql);
              }
              LabeledEdge ql = new LabeledEdge(tn_child, l_quality);
              and.addChildren(ql);
              and_added = true;
            }
          }
        }
        if (and_added)
        {
          or_root.addChildren(new LabeledEdge(and, Quality.EXACT));
        }
      }
      if (or_root.getChildren().size() > 0)
      {
        out_list.add(new LabeledEdge(or_root, Quality.EXACT));
      }
    }
    else
    {
      // Query is non-trivial, and object is not trackable: nothing to do
      ConcreteDesignatedObject cdo = new ConcreteDesignatedObject(Designator.unknown, o);
      ObjectNode tn_child = getNode(cdo, nodes);
      LabeledEdge ql = new LabeledEdge(tn_child, Quality.EXACT);
      out_list.add(ql);
    }
    return out_list;
  }

  /*@ non_null @*/ protected static ObjectNode getNode(DesignatedObject dob, /*@ non_null @*/ Map<DesignatedObject,ObjectNode> nodes)
  {
    ObjectNode tn_child = null;
    if (nodes.containsKey(dob))
    {
      tn_child = nodes.get(dob);

    }
    else
    {
      tn_child = new ObjectNode(dob);
    }
    return tn_child;
  }
}
