package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.*;

public final class StringStructure extends PrimitiveStructure<String>
{
	public static final String IDENTIFIER = "string";


	@Override
	protected String decodeDataElement(final String token) throws OpenDDLException
	{
		return Decoder.decodeString(token);
	}

	@Override
	protected void encodeDataElement(final StringBuilder stringBuilder, final String dataElement)
	{
		LiteralEncoding.encodeString(stringBuilder, dataElement);
	}

	public StringStructure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier, decoder);
	}

	public StringStructure(final String name, final int arrayLength)
	{
		super(StringStructure.IDENTIFIER, name, arrayLength);
	}

	public StringStructure(final String name)
	{
		super(StringStructure.IDENTIFIER, name);
	}

	public StringStructure(final int arrayLength)
	{
		super(StringStructure.IDENTIFIER, arrayLength);
	}

	public StringStructure()
	{
		super(StringStructure.IDENTIFIER);
	}
}
