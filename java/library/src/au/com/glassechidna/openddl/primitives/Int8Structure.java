package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.LiteralEncoding;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;

public final class Int8Structure extends PrimitiveStructure<Byte> implements MultipleLiteralEncodings
{
	private int literalEncoding;

	@Override
	protected Byte decodeDataElement(final String token) throws OpenDDLException
	{
		return Byte.valueOf(Decoder.decodeInt8(token));
	}

	@Override
	protected void encodeDataElement(final StringBuilder stringBuilder, final Byte dataElement)
	{
		LiteralEncoding.encodeByte(stringBuilder, dataElement.byteValue(), literalEncoding);
	}

	public Int8Structure(final String identifier, final Decoder decoder) throws OpenDDLException
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
