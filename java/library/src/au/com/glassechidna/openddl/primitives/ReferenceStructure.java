package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;

public class ReferenceStructure extends PrimitiveStructure<Reference>
{
	public static final String IDENTIFIER = "ref";


	@Override
	protected Reference decodeDataElement(final String token) throws OpenDDLException
	{
		return new Reference(token);
	}

	@Override
	protected void encodeDataElement(final StringBuilder stringBuilder, final Reference dataElement)
	{
		stringBuilder.append(dataElement.toString());
	}

	public ReferenceStructure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier, decoder);
	}

	public ReferenceStructure(final String name, final int arrayLength)
	{
		super(ReferenceStructure.IDENTIFIER, name, arrayLength);
	}

	public ReferenceStructure(final String name)
	{
		super(ReferenceStructure.IDENTIFIER, name);
	}

	public ReferenceStructure(final int arrayLength)
	{
		super(ReferenceStructure.IDENTIFIER, arrayLength);
	}

	public ReferenceStructure()
	{
		super(ReferenceStructure.IDENTIFIER);
	}
}
