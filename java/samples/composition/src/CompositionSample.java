import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.RootStructure;
import au.com.glassechidna.openddl.primitives.Reference;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CompositionSample
{
	private static void writeFile(final String pathName, final String string) throws IOException
	{
		final Path filePath = Paths.get(pathName);

		final File dir = new File(filePath.getParent().toString());
		dir.mkdirs();

		Files.write(filePath, string.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	public static void main(final String[] args)
	{
		final RootStructure rootStructure = new RootStructure();

		try
		{
			final PersonStructure guy = new PersonStructure();
			guy.setStructureName("$guy");
			guy.addName("Guy");
			guy.addFriend(new Reference("$bob")); // Predeclared reference

			rootStructure.addStructure(guy);

			final PersonStructure frank = new PersonStructure();
			frank.addName("Frank");
			frank.addName("en");
			frank.addName("Stein");
			frank.setFavoriteFood("Monkey Brain");

			rootStructure.addStructure(frank);

			final PersonStructure carli = new PersonStructure();
			carli.addName("Carli");
			carli.addFriend(Reference.globalReference(guy));

			rootStructure.addStructure(1, frank);

			final PersonStructure bob = new PersonStructure();
			bob.setStructureName("$bob");
			bob.addName("Bob");
			bob.addName("Hanson");
			bob.setFavoriteFood("Bacon");

			rootStructure.addStructure(bob);

			rootStructure.validate();

			writeFile("out/composed.oddl", rootStructure.toString());

			System.out.println("Wrote OpenDDL to out/composed.oddl");
		}
		catch (final OpenDDLException e)
		{
			System.out.println(e.toString());
		}
		catch (final IOException e)
		{
			System.out.println(e);
		}
	}
}
