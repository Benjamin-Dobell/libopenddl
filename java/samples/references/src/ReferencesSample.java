import au.com.glassechidna.openddl.*;
import au.com.glassechidna.openddl.primitives.Reference;
import au.com.glassechidna.openddl.primitives.ReferenceStructure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ReferencesSample
{
	private static String readFile(final String path) throws IOException
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, StandardCharsets.UTF_8);
	}

	private static void findRelations(final HashMap<String, CreatureStructure> relations, final RootStructure rootStructure, final CreatureStructure creatureStructure)
	{
		final FriendsStructure friendsStructure = (FriendsStructure) creatureStructure.getFirstStructure("Friends");
		final ReferenceStructure references = (friendsStructure != null && friendsStructure.getStructureCount() > 0) ? (ReferenceStructure) friendsStructure.getStructure(0) : null;

		if (references != null)
		{
			for (final Reference friendReference : references)
			{
				final CreatureStructure friend;

				if (friendReference.isGlobal())
				{
					friend = (CreatureStructure) rootStructure.getStructure(friendReference);
				}
				else
				{
					friend = (CreatureStructure) creatureStructure.getStructure(friendReference);
				}

				if (!relations.containsKey(friend.getStructureName()))
				{
					relations.put(friend.getStructureName(), friend);

					findRelations(relations, rootStructure, friend);
				}
			}
		}
	}

	private static String buildFriendString(final RootStructure rootStructure, final CreatureStructure creatureStructure)
	{
		String friendString = "";

		final FriendsStructure friendsStructure = (FriendsStructure) creatureStructure.getFirstStructure("Friends");
		final ReferenceStructure references = (friendsStructure != null && friendsStructure.getStructureCount() > 0) ? (ReferenceStructure) friendsStructure.getStructure(0) : null;

		if (references != null)
		{
			int i = 0;

			for (; i < references.getDataElementCount() - 1; ++i)
			{
				final Reference friendReference = references.getDataElement(i);
				final CreatureStructure friend;

				if (friendReference.isGlobal())
				{
					friend = (CreatureStructure) rootStructure.getStructure(friendReference);
				}
				else
				{
					friend = (CreatureStructure) creatureStructure.getStructure(friendReference);
				}

				friendString += friend.getFullName() + ", ";
			}

			final Reference lastFriendReference = references.getDataElement(references.getDataElementCount() - 1);
			final CreatureStructure lastFriend;

			if (lastFriendReference.isGlobal())
			{
				lastFriend = (CreatureStructure) rootStructure.getStructure(lastFriendReference);
			}
			else
			{
				lastFriend = (CreatureStructure) creatureStructure.getStructure(lastFriendReference);
			}

			if (references.getDataElementCount() > 1)
			{
				friendString += "and " + lastFriend.getFullName();
			}
			else
			{
				friendString += lastFriend.getFullName();
			}
		}

		if (friendString.length() == 0)
		{
			return " has no friends";
		}
		else
		{
			return " is friends with " + friendString;
		}
	}

	public static void main(final String[] args)
	{
		final GenericDecoder genericDecoder;

		try
		{
			genericDecoder = new GenericDecoder(readFile("data/references.oddl"));
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			return;
		}

		final CreatureStructure.Builder creatureBuilder = new CreatureStructure.Builder();
		genericDecoder.addBuilder("Person", creatureBuilder);
		genericDecoder.addBuilder("Zombie", creatureBuilder);

		final HashMap<String, CreatureStructure> relations = new HashMap<String, CreatureStructure>();
		final RootStructure rootStructure;

		try
		{
			rootStructure = genericDecoder.parse();

			final CreatureStructure chuck = (CreatureStructure) rootStructure.getStructure("$chuck");
			findRelations(relations, rootStructure, chuck);
		}
		catch (final OpenDDLException e)
		{
			e.printStackTrace();
			return;
		}

		for (final CreatureStructure creatureStructure : relations.values())
		{
			final String favoriteFood = creatureStructure.getFavoriteFood();

			if (favoriteFood != null)
			{
				System.out.println(creatureStructure.getFullName() + buildFriendString(rootStructure, creatureStructure) + ". " + creatureStructure.getFullName() + " likes " + favoriteFood + ".");
			}
			else
			{
				System.out.println(creatureStructure.getFullName() + buildFriendString(rootStructure, creatureStructure) + ". " + creatureStructure.getFullName() + " does not have a favorite food.");
			}
		}
	}
}
