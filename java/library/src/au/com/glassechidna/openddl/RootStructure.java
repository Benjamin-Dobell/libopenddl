package au.com.glassechidna.openddl;

import au.com.glassechidna.openddl.primitives.Reference;

import java.util.HashMap;
import java.util.Iterator;

public final class RootStructure extends NodeStructure
{
	private HashMap<String, Structure> globals = new HashMap<String, Structure>();

	RootStructure(final Decoder decoder) throws OpenDDLException
	{
		super(null, decoder);

		attach(this, null);
	}

	public RootStructure()
	{
		super(null);

		try
		{
			attach(this, null);
		}
		catch (final OpenDDLException e)
		{
			throw new IllegalStateException(e);
		}
	}

	@Override
	String getInternalIdentifier()
	{
		return "OpenDDL Root";
	}

	@Override
	Structure getStructure(final Reference reference, final int componentIndex)
	{
		if (reference.isGlobal())
		{
			if (componentIndex != 0)
			{
				throw new IllegalStateException("RootStructures should not be nested");
			}

			final Structure structure = globals.get(reference.getComponent(0));

			if (reference.getComponentCount() == 1)
			{
				return structure;
			}
			else if (structure instanceof NodeStructure)
			{
				return ((NodeStructure) structure).getStructure(reference, 1);
			}
			else
			{
				return null;
			}
		}
		else
		{
			return super.getStructure(reference, componentIndex);
		}
	}

	void addGlobal(final Structure structure) throws OpenDDLException
	{
		final String structureName = structure.getStructureName();

		if (globals.containsKey(structureName))
		{
			throw new OpenDDLException("Encountered duplicate global name '" + structureName + "'");
		}

		globals.put(structure.getStructureName(), structure);
	}

	void removeGlobal(final Structure structure)
	{
		globals.remove(structure.getStructureName());
	}

	@Override
	void encode(final StringBuilder stringBuilder, final int depth)
	{
		final Iterator<Structure> structureIterator = iterator();

		if (structureIterator.hasNext())
		{
			for (;;)
			{
				structureIterator.next().encode(stringBuilder, depth);

				if (structureIterator.hasNext())
				{
					stringBuilder.append('\n');
					Structure.encodeNewLine(stringBuilder, depth);
				}
				else
				{
					break;
				}
			}
		}
	}

	public void validate() throws OpenDDLException
	{
		for (final Structure structure : this)
		{
			structure.validate(this);
		}
	}
}
