package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.OpenDDLException;

import java.util.ArrayList;
import java.util.Collections;

public final class Reference
{
	public static final String NULL = "null";


	private static ArrayList<String> decodeComponents(final String reference) throws OpenDDLException
	{
		if (reference.equals(NULL))
		{
			return new ArrayList<String>(0);
		}
		else
		{
			final String[] components = reference.split("\\.");

			if (!Decoder.isName(components[0], 0))
			{
				throw new OpenDDLException("Encountered invalid name '" + components[0] + "' in reference");
			}

			for (int i = 1; i < components.length; i++)
			{
				if (!Decoder.isLocalName(components[i], 0))
				{
					throw new OpenDDLException("Encountered invalid local name '" + components[i] + "' in reference");
				}
			}

			final ArrayList<String> componentsList = new ArrayList<String>(components.length);
			Collections.addAll(componentsList, components);
			return componentsList;
		}
	}


	private final String reference;
	private final ArrayList<String> components;

	public Reference(final String reference) throws OpenDDLException
	{
		components = decodeComponents(reference);

		if (components.size() > 0)
		{
			this.reference = reference;
		}
		else
		{
			this.reference = Reference.NULL;
		}
	}

	public int getComponentCount()
	{
		return components.size();
	}

	public String getComponent(final int index)
	{
		return components.get(index);
	}

	public Iterable<String> getComponents()
	{
		return components;
	}

	public boolean isGlobal()
	{
		return components.size() > 0 && components.get(0).charAt(0) == '$';
	}

	public boolean isNull()
	{
		return components.size() == 0;
	}

	@Override
	public String toString()
	{
		return reference;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj instanceof Reference)
		{
			return reference.equals(((Reference) obj).reference);
		}
		else
		{
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		return reference.hashCode();
	}
}
