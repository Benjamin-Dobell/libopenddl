package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;

public class ReferenceStructure extends PrimitiveStructure<Reference>
{
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
}
