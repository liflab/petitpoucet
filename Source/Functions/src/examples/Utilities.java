package examples;

import java.util.ArrayList;
import java.util.List;

public class Utilities
{
	public static List<Object> createList(Object... objects)
	{
		List<Object> out_list = new ArrayList<Object>(objects.length);
		for (Object o : objects)
		{
			out_list.add(o);
		}
		return out_list;
	}
}
