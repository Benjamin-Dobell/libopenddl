import au.com.glassechidna.openddl.*;
import au.com.glassechidna.openddl.primitives.Reference;
import au.com.glassechidna.openddl.primitives.StringStructure;

public class PersonStructure extends NodeStructure
{
	public static final String IDENTIFIER = "Person";

	public static final class Builder implements NodeStructure.Builder
	{
		@Override
		public NodeStructure build(final String identifier, final Decoder decoder) throws OpenDDLException
		{
			return new PersonStructure(identifier, decoder);
		}
	}

	private static final String FAVORITE_FOOD = "%FavoriteFood";


	@Override
	protected void validate(final RootStructure rootStructure) throws OpenDDLException
	{
		super.validate(rootStructure);

		final Structure nameStructure = getFirstStructure(NameStructure.IDENTIFIER);

		if (nameStructure == null || nameStructure != getLastStructure(NameStructure.IDENTIFIER))
		{
			throw new OpenDDLException("PersonStructure must contain one NameStructure");
		}

		final Structure friendsStructure = getFirstStructure(FriendsStructure.IDENTIFIER);

		if (friendsStructure != getLastStructure(FriendsStructure.IDENTIFIER))
		{
			throw new OpenDDLException("PersonStructure must contain only one FriendsStructure");
		}

		final Structure favoriteFood = getStructure(PersonStructure.FAVORITE_FOOD);

		if (favoriteFood != null)
		{
			if (!(favoriteFood instanceof StringStructure))
			{
				throw new OpenDDLException("PersonStructure's " + PersonStructure.FAVORITE_FOOD + " must be a StringStructure");
			}

			if (((StringStructure) favoriteFood).getDataElementCount() != 1)
			{
				throw new OpenDDLException("When specified " + PersonStructure.FAVORITE_FOOD + " contain only one data element");
			}
		}
	}

	public PersonStructure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier, decoder);
	}

	public PersonStructure()
	{
		super(PersonStructure.IDENTIFIER);
	}

	public String getFullName()
	{
		final NameStructure nameStructure = (NameStructure) getFirstStructure("Name");
		return nameStructure.getFullName();
	}

	public void addName(final String name)
	{
		NameStructure nameStructure = (NameStructure) getFirstStructure("Name");

		if (nameStructure == null)
		{
			nameStructure = new NameStructure();
			addStructure(nameStructure);
		}

		nameStructure.appendName(name);
	}

	public String getFavoriteFood()
	{
		final StringStructure favoriteFood = (StringStructure) getStructure(PersonStructure.FAVORITE_FOOD);

		if (favoriteFood != null)
		{
			return favoriteFood.getDataElement(0);
		}
		else
		{
			return null;
		}
	}

	public void setFavoriteFood(final String favoriteFood)
	{
		StringStructure favoriteFoodStructure = (StringStructure) getStructure(PersonStructure.FAVORITE_FOOD);

		if (favoriteFoodStructure == null)
		{
			favoriteFoodStructure = new StringStructure(PersonStructure.FAVORITE_FOOD);
			addStructure(favoriteFoodStructure);
		}

		favoriteFoodStructure.set(favoriteFood);
	}

	public void addFriend(final Reference friend)
	{
		FriendsStructure friendsReferenceStructure = (FriendsStructure) getFirstStructure(FriendsStructure.IDENTIFIER);

		if (friendsReferenceStructure == null)
		{
			friendsReferenceStructure = new FriendsStructure();
			addStructure(friendsReferenceStructure);
		}

		friendsReferenceStructure.addFriend(friend);
	}

	@Override
	public NodeStructure.Builder getBuilder(final String identifier)
	{
		if (identifier.equals(NameStructure.IDENTIFIER))
		{
			return new NameStructure.Builder();
		}
		else if (identifier.equals(FriendsStructure.IDENTIFIER))
		{
			return new FriendsStructure.Builder();
		}
		else
		{
			return super.getBuilder(identifier);
		}
	}
}
