import au.com.glassechidna.openddl.*;
import au.com.glassechidna.openddl.primitives.Reference;
import au.com.glassechidna.openddl.primitives.ReferenceStructure;

public class FriendsStructure extends NodeStructure
{
	public static final class Builder implements NodeStructure.Builder
	{
		@Override
		public NodeStructure build(final String identifier, final Decoder decoder) throws OpenDDLException
		{
			return new FriendsStructure(identifier, decoder);
		}
	}


	@Override
	protected void validate(final RootStructure rootStructure) throws OpenDDLException
	{
		super.validate(rootStructure);

		final int structureCount = getStructureCount();

		if (structureCount > 1 || (structureCount == 1 && !(getStructure(0) instanceof ReferenceStructure)))
		{
			throw new OpenDDLException("FriendsStructure must contain at most a single ReferenceStructure");
		}

		if (structureCount == 1)
		{
			final ReferenceStructure referenceStructure = (ReferenceStructure) getStructure(0);

			if (referenceStructure.getArrayLength() != PrimitiveStructure.NOT_AN_ARRAY)
			{
				throw new OpenDDLException("FriendStructure's child ReferenceStructure should not be an array structure");
			}

			for (final Reference reference : referenceStructure)
			{
				final Structure structure;

				if (reference.isGlobal())
				{
					structure = rootStructure.getStructure(reference);
				}
				else
				{
					structure = getStructure(reference);
				}

				if (!(structure instanceof CreatureStructure))
				{
					throw new OpenDDLException("All FriendStructure's child ReferenceStructure's references must resolve to a CreatureStructure");
				}
			}
		}
	}

	public FriendsStructure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier, decoder);
	}
}
