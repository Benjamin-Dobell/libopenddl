import au.com.glassechidna.openddl.*;
import au.com.glassechidna.openddl.primitives.Reference;
import au.com.glassechidna.openddl.primitives.UnsignedInt64Structure;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PropertiesSample
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
		final String inputFilename = "data/properties.oddl";
		final String outputFilename = "data/out/properties.oddl";

		GenericDecoder genericDecoder;

		try
		{
			genericDecoder = new GenericDecoder(readFile(inputFilename));

			final RootStructure rootStructure = genericDecoder.parse();
			System.out.println("Original:");
			System.out.println(rootStructure);

			final NodeStructure propTestStructure = (NodeStructure) rootStructure.getStructure(new Reference("$propTest"));

			propTestStructure.addInt8Property("TestInt8", (byte) 23, LiteralEncoding.DECIMAL);
			propTestStructure.addInt16Property("TestInt16", (short) -46, LiteralEncoding.DECIMAL);
			propTestStructure.addInt32Property("TestInt32", 1234567890, LiteralEncoding.DECIMAL);
			propTestStructure.addInt64Property("TestInt64", Long.MAX_VALUE, LiteralEncoding.DECIMAL);

			propTestStructure.addUnsignedInt8Property("UnsignedTestInt8", (byte) 23, LiteralEncoding.DECIMAL);
			propTestStructure.addUnsignedInt16Property("UnsignedTestInt16", 46, LiteralEncoding.DECIMAL);
			propTestStructure.addUnsignedInt32Property("UnsignedTestInt32", 1234567890, LiteralEncoding.DECIMAL);
			propTestStructure.addUnsignedInt64Property("UnsignedTestInt64", UnsignedInt64Structure.MAX, LiteralEncoding.DECIMAL);

			propTestStructure.addFloatProperty("FloatProperty", 1.2345f, LiteralEncoding.FLOATING_POINT);
			propTestStructure.addDoubleProperty("DoubleProperty", 6.1232143243242344, LiteralEncoding.FLOATING_POINT);

			propTestStructure.addStringProperty("StringProperty", "A string of some sort");
			propTestStructure.addReferenceProperty("ReferenceProperty", new Reference("$propTest"));
			propTestStructure.addTypeProperty("TypeProperty", PrimitiveType.INT64);

			rootStructure.validate();
			writeFile(outputFilename, rootStructure.toString());

			genericDecoder = new GenericDecoder(readFile(outputFilename));
			final RootStructure readOutputRootStructure = genericDecoder.parse();

			System.out.println("\nOutput (read from file):");
			System.out.println(readOutputRootStructure.toString());
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
