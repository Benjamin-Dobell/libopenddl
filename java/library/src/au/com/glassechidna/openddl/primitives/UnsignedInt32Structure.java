package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.LiteralEncoding;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;

public final class UnsignedInt32Structure extends PrimitiveStructure<Long> implements MultipleLiteralEncodings
{
	public static final long MAX = 4294967296L;


	private int literalEncoding;

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

		literalEncoding = LiteralEncoding.DECIMAL;
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
