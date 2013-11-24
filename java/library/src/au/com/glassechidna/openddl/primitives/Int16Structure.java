package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.LiteralEncoding;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;

public final class Int16Structure extends PrimitiveStructure<Short> implements MultipleLiteralEncodings
{
	private int literalEncoding;

	@Override
	protected Short decodeDataElement(final String token) throws OpenDDLException
	{
		return Short.valueOf(Decoder.decodeInt16(token));
	}

	@Override
	protected void encodeDataElement(final StringBuilder stringBuilder, final Short dataElement)
	{
		LiteralEncoding.encodeShort(stringBuilder, dataElement.shortValue(), literalEncoding);
	}

	public Int16Structure(final String identifier, final Decoder decoder) throws OpenDDLException
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
