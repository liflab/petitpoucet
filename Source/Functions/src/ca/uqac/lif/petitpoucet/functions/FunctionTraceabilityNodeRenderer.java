package ca.uqac.lif.petitpoucet.functions;

import ca.uqac.lif.petitpoucet.DesignatedObject;
import ca.uqac.lif.petitpoucet.Designator;
import ca.uqac.lif.petitpoucet.graph.render.TraceabilityNodeDotRenderer;

public class FunctionTraceabilityNodeRenderer extends TraceabilityNodeDotRenderer
{
	@Override
	protected String formatColor(DesignatedObject dob)
	{
		String color = super.formatColor(dob);
		Designator d = dob.getDesignator().peek();
		if (d instanceof Constant.HardValue)
		{
			color = "DimGray";
		}
		return color;
	}

	@Override
	protected String formatCaption(DesignatedObject dob, CaptionStyle style)
	{
		String caption = super.formatCaption(dob, style);
		if (style == CaptionStyle.SHORT)
		{
			Designator d = dob.getDesignator().peek();
			if (d instanceof Constant.HardValue)
			{
				caption = dob.getObject().toString();
			}
		}
		return caption;
	}
}
