package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.RootStructure;
import au.com.glassechidna.openddl.Structure;
import com.sun.deploy.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

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

	public static Reference globalReference(Structure structure) throws OpenDDLException
	{
		final LinkedList<String> components = new LinkedList<String>();

		while (structure != null && structure.getStructureName() != null)
		{
			final String name = structure.getStructureName();
			components.addFirst(name);

			if (name.startsWith("$") || structure instanceof RootStructure)
			{
				return new Reference(components);
			}

			structure = structure.getParentStructure();
		}

		throw new OpenDDLException("Can not generate global references to a structure who has an ancestor that does not have a name");
	}

	public static Reference relativeReference(final Structure sourceStructure, Structure structure) throws OpenDDLException
	{
		final LinkedList<String> components = new LinkedList<String>();

		while (structure != null && structure.getStructureName() != null)
		{
			if (structure == sourceStructure)
			{
				return new Reference(components);
			}

			final String name = structure.getStructureName();
			components.addFirst(name);

			structure = structure.getParentStructure();
		}

		if (structure == null)
		{
			throw new OpenDDLException("Can not generate a relative references to a structure who is not a decendent of the specified source structure");
		}
		else
		{
			throw new OpenDDLException("Can not generate a relative references to a structure who has an ancestor that does not have a name");
		}
	}

	public static Reference shortestReference(final Structure sourceStructure, Structure structure) throws OpenDDLException
	{
		final LinkedList<String> components = new LinkedList<String>();

		while (structure != null && structure.getStructureName() != null)
		{
			if (structure == sourceStructure)
			{
				return new Reference(components);
			}

			final String name = structure.getStructureName();
			components.addFirst(name);

			if (name.startsWith("$") || structure instanceof RootStructure)
			{
				return new Reference(components);
			}

			structure = structure.getParentStructure();
		}

		throw new OpenDDLException("Can not generate reference to a structure who has an ancestor that does not have a name");
	}


	private final String reference;
	private final ArrayList<String> components;

	private Reference(final Collection<String> components)
	{
		this.components = new ArrayList<String>(components);

		reference = StringUtils.join(components, ".");
	}

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
