package au.com.glassechidna.openddl;

import au.com.glassechidna.openddl.primitives.Reference;

import java.util.HashMap;
import java.util.Iterator;

public final class RootStructure extends NodeStructure
{
	private HashMap<String, Structure> globals;

	RootStructure(final Decoder decoder) throws OpenDDLException
	{
		super(null, decoder);
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

	void setGlobals(final HashMap<String, Structure> globals) throws OpenDDLException
	{
		this.globals = globals;
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
