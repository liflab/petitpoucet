package examples;

import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.CausalityQuery;
import ca.uqac.lif.petitpoucet.TraceabilityQuery.ProvenanceQuery;
import ca.uqac.lif.petitpoucet.circuit.CircuitDesignator;
import ca.uqac.lif.petitpoucet.circuit.functions.Add;
import ca.uqac.lif.petitpoucet.circuit.functions.Connector;
import ca.uqac.lif.petitpoucet.circuit.functions.Constant;
import ca.uqac.lif.petitpoucet.circuit.functions.Multiply;
import ca.uqac.lif.petitpoucet.graph.ConcreteObjectNode;
import ca.uqac.lif.petitpoucet.graph.Tracer;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeDotRenderer;

@SuppressWarnings("unused")
public class Example1
{
  public static void main(String[] args)
  {
    int x = 1, y = -1, z = 0;
    Multiply mul = new Multiply();
    Add add = new Add();
    Constant c_x = new Constant(x);
    Constant c_y = new Constant(y);
    Constant c_z = new Constant(z);
    Connector.connect(c_x, 0, add, 0);
    Connector.connect(c_y, 0, add, 1);
    Connector.connect(add,  0, mul, 0);
    Connector.connect(c_z, 0, mul, 1);
    Object[] output = mul.evaluate();
    System.out.println(output[0]);
    Designator d = new CircuitDesignator.NthOutput(0);
    Tracer t = new Tracer();
    ConcreteObjectNode root = t.getTree(ProvenanceQuery.instance, d, mul);
    TraceabilityNodeDotRenderer rend = new TraceabilityNodeDotRenderer();
    String s = rend.render(root);
    System.out.println(s);
  }
}
