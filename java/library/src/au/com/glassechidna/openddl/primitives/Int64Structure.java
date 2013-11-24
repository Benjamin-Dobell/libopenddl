package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.LiteralEncoding;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;

public final class Int64Structure extends PrimitiveStructure<Long> implements MultipleLiteralEncodings
{
	private int literalEncoding;

	@Override
	protected Long decodeDataElement(final String token) throws OpenDDLException
	{
		return Long.valueOf(Decoder.decodeInt64(token));
	}

	@Override
	protected void encodeDataElement(final StringBuilder stringBuilder, final Long dataElement)
	{
		LiteralEncoding.encodeLong(stringBuilder, dataElement.longValue(), literalEncoding);
	}

	public Int64Structure(final String identifier, final Decoder decoder) throws OpenDDLException
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
