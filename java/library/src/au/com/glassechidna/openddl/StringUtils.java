package au.com.glassechidna.openddl;

import java.util.Iterator;

public class StringUtils
{
	public static String join(final Iterable<String> iterable, final String separator)
	{
		final StringBuilder stringBuilder = new StringBuilder();
		final Iterator<String> iterator = iterable.iterator();

		if (iterator.hasNext())
		{
			stringBuilder.append(iterator.next());

			while (iterator.hasNext())
			{
				stringBuilder.append(separator);
				stringBuilder.append(iterator.next());
			}
		}

		return stringBuilder.toString();
	}
}
