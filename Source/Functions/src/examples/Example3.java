package examples;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.petitpoucet.ComposedDesignator;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.ProvenanceQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator;
import ca.uqac.lif.petitpoucet.circuit.functions.Add;
import ca.uqac.lif.petitpoucet.circuit.functions.ApplyToAll;
import ca.uqac.lif.petitpoucet.circuit.functions.Connector;
import ca.uqac.lif.petitpoucet.circuit.functions.Constant;
import ca.uqac.lif.petitpoucet.circuit.functions.Exists;
import ca.uqac.lif.petitpoucet.circuit.functions.ForAll;
import ca.uqac.lif.petitpoucet.circuit.functions.IsEven;
import ca.uqac.lif.petitpoucet.circuit.functions.Multiply;
import ca.uqac.lif.petitpoucet.common.CollectionDesignator;
import ca.uqac.lif.petitpoucet.graph.ConcreteObjectNode;
import ca.uqac.lif.petitpoucet.graph.Tracer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeDotRenderer;

@SuppressWarnings("unused")
public class Example3
{
  public static void main(String[] args)
  {
    List<Object> list1 = createList(2, 4, 6);
    Constant c_list1 = new Constant(list1);
    ForAll ex = new ForAll(new IsEven());
    Connector.connect(c_list1, ex);
    Object[] output = ex.evaluate();
    System.out.println(output[0]);
    Designator d = new ComposedDesignator(new CollectionDesignator.NthElement(1), new CircuitDesignator.NthOutput(0));
    Tracer t = new Tracer();
    ConcreteObjectNode root = t.getTree(CausalityQuery.instance, d, ex);
    TraceabilityNodeDotRenderer rend = new TraceabilityNodeDotRenderer();
    String s = rend.render(root);
    System.out.println(s);
  }
  
  protected static List<Object> createList(Object ... objects)
  {
    List<Object> out_list = new ArrayList<Object>(objects.length);
    for (Object o : objects)
    {
      out_list.add(o);
    }
    return out_list;
  }
}
