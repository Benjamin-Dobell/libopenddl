package au.com.glassechidna.openddl.sample;

import au.com.glassechidna.openddl.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class InputOutputSample
{
	private static String readFile(final String path) throws IOException
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, StandardCharsets.UTF_8);
	}

	private static void writeFile(final String pathName, final String string) throws IOException
	{
		final Path filePath = Paths.get(pathName);

		final File dir = new File(filePath.getParent().toString());
		dir.mkdirs();

		Files.write(filePath, string.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	public static void main(final String[] args)
	{
		if (args.length != 2)
		{
			System.out.println("Usage: sample <input filename> <output filename>");
			return;
		}

		final String inputFilename = args[0];
		final String outputFilename = args[1];

		final GenericDecoder genericDecoder;

		try
		{
			genericDecoder = new GenericDecoder(readFile(inputFilename));
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			return;
		}

		try
		{
			final RootStructure rootStructure = genericDecoder.parse();
			System.out.println(rootStructure);
			writeFile(outputFilename, rootStructure.toString());
		}
		catch (final OpenDDLException e)
		{
			e.printStackTrace();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}
}
