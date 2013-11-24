package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.LiteralEncoding;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;

public final class Int32Structure extends PrimitiveStructure<Integer> implements MultipleLiteralEncodings
{
	private int literalEncoding;

	@Override
	protected Integer decodeDataElement(final String token) throws OpenDDLException
	{
		return Integer.valueOf(Decoder.decodeInt32(token));
	}

	@Override
	protected void encodeDataElement(final StringBuilder stringBuilder, final Integer dataElement)
	{
		LiteralEncoding.encodeInt(stringBuilder, dataElement.intValue(), literalEncoding);
	}

	public Int32Structure(final String identifier, final Decoder decoder) throws OpenDDLException
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
