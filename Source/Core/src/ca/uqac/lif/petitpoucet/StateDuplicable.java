package ca.uqac.lif.petitpoucet;

public interface StateDuplicable<T> extends Duplicable<T>
{
	public T duplicate(boolean with_state);
}
