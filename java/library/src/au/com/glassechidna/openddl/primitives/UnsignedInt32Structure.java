package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.LiteralEncoding;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;

public final class UnsignedInt32Structure extends PrimitiveStructure<Long> implements MultipleLiteralEncodings
{
	public static final String IDENTIFIER = "unsigned_int32";

	public static final long MAX = 4294967296L;


	private int literalEncoding = LiteralEncoding.DECIMAL;

	@Override
	protected Long decodeDataElement(final String token) throws OpenDDLException
	{
		return Long.valueOf(Decoder.decodeUnsignedInt32(token));
	}

	@Override
	protected void encodeDataElement(final StringBuilder stringBuilder, final Long dataElement)
	{
		LiteralEncoding.encodeLong(stringBuilder, dataElement.longValue(), literalEncoding);
	}

	public UnsignedInt32Structure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier, decoder);
	}

	public UnsignedInt32Structure(final String name, final int arrayLength)
	{
		super(UnsignedInt32Structure.IDENTIFIER, name, arrayLength);
	}

	public UnsignedInt32Structure(final String name)
	{
		super(UnsignedInt32Structure.IDENTIFIER, name);
	}

	public UnsignedInt32Structure(final int arrayLength)
	{
		super(UnsignedInt32Structure.IDENTIFIER, arrayLength);
	}

	public UnsignedInt32Structure()
	{
		super(UnsignedInt32Structure.IDENTIFIER);
	}

	@Override
	public int getLiteralEncoding()
	{
		return literalEncoding;
	}

	@Override
	public void setLiteralEncoding(final int literalEncoding)
	{
		this.literalEncoding = literalEncoding;
	}
}
