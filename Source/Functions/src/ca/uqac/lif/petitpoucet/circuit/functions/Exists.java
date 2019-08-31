package ca.uqac.lif.petitpoucet.circuit.functions;

public class Exists extends Quantifier
{
  
  public Exists(Function phi)
  {
    super(phi);
  }

  @Override
  protected boolean getStartValue()
  {
    return false;
  }

  @Override
  protected boolean update(boolean b1, boolean b2)
  {
    return b1 || b2;
  }
  
  @Override
  public String toString()
  {
    return "exists x : " + m_function;
  }
}
