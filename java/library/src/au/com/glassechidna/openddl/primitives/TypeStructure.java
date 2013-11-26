package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;
import au.com.glassechidna.openddl.PrimitiveType;

public final class TypeStructure extends PrimitiveStructure<Integer>
{
	public static final String IDENTIFIER = "type";


	@Override
	protected Integer decodeDataElement(final String token) throws OpenDDLException
	{
		final int type = Decoder.parseType(token);
		return Integer.valueOf(type);
	}

	@Override
	protected void encodeDataElement(final StringBuilder stringBuilder, final Integer dataElement)
	{
		stringBuilder.append(PrimitiveType.IDENTIFIERS[dataElement.intValue()]);
	}

	public TypeStructure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier, decoder);
	}

	public TypeStructure(final String name, final int arrayLength)
	{
		super(TypeStructure.IDENTIFIER, name, arrayLength);
	}

	public TypeStructure(final String name)
	{
		super(TypeStructure.IDENTIFIER, name);
	}

	public TypeStructure(final int arrayLength)
	{
		super(TypeStructure.IDENTIFIER, arrayLength);
	}

	public TypeStructure()
	{
		super(TypeStructure.IDENTIFIER);
	}
}
