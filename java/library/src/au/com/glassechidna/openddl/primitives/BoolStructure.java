package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.LiteralEncoding;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;

public final class BoolStructure extends PrimitiveStructure<Boolean>
{
	@Override
	protected Boolean decodeDataElement(final String token) throws OpenDDLException
	{
		return Boolean.valueOf(Decoder.decodeBoolean(token));
	}

	@Override
	protected void encodeDataElement(final StringBuilder stringBuilder, final Boolean dataElement)
	{
		LiteralEncoding.encodeBoolean(stringBuilder, dataElement.booleanValue());
	}

	public BoolStructure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier, decoder);
	}
}
