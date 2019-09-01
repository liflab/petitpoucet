package ca.uqac.lif.petitpoucet.functions;

public class Connector
{
	public static void connect(Function f1, int output_index, Function f2, int input_index)
	{
		FunctionConnection cc1 = new FunctionConnection(input_index, f2);
		f1.setToOutput(output_index, cc1);
		FunctionConnection cc2 = new FunctionConnection(output_index, f1);
		f2.setToInput(input_index, cc2);
	}

	public static void connect(Function f1, Function f2)
	{
		connect(f1, 0, f2, 0);
	}
}
