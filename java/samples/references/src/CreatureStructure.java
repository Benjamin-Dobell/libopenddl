import au.com.glassechidna.openddl.*;
import au.com.glassechidna.openddl.primitives.StringStructure;

public class CreatureStructure extends NodeStructure
{
	public static final class Builder implements NodeStructure.Builder
	{
		@Override
		public NodeStructure build(final String identifier, final Decoder decoder) throws OpenDDLException
		{
			return new CreatureStructure(identifier, decoder);
		}
	}


	@Override
	protected void validate(final RootStructure rootStructure) throws OpenDDLException
	{
		super.validate(rootStructure);

		final Structure nameStructure = getFirstStructure("Name");

		if (nameStructure == null || nameStructure != getLastStructure("Name"))
		{
			throw new OpenDDLException("CreatureStructure must contain one NameStructure");
		}

		final Structure friendsStructure = getFirstStructure("Friends");

		if (friendsStructure != getLastStructure("Friends"))
		{
			throw new OpenDDLException("CreatureStructure must contain only one FriendsStructure");
		}

		final Structure favoriteFood = getStructure("%FavoriteFood");

		if (favoriteFood != null)
		{
			if (!(favoriteFood instanceof StringStructure))
			{
				throw new OpenDDLException("CreatureStructure's %FavoriteFood must be a StringStructure");
			}

			if (((StringStructure) favoriteFood).getDataElementCount() != 1)
			{
				throw new OpenDDLException("When specified %FavoriteFood contain only one data element");
			}
		}
	}

	public CreatureStructure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier, decoder);
	}

	public String getFullName()
	{
		final NameStructure nameStructure = (NameStructure) getFirstStructure("Name");
		return nameStructure.getFullName();
	}

	public String getFavoriteFood()
	{
		final StringStructure favoriteFood = (StringStructure) getStructure("%FavoriteFood");

		if (favoriteFood != null)
		{
			return favoriteFood.getDataElement(0);
		}
		else
		{
			return null;
		}
	}

	@Override
	public NodeStructure.Builder getBuilder(final String identifier)
	{
		if (identifier.equals("Name"))
		{
			return new NameStructure.Builder();
		}
		else if (identifier.equals("Friends"))
		{
			return new FriendsStructure.Builder();
		}
		else
		{
			return super.getBuilder(identifier);
		}
	}
}
