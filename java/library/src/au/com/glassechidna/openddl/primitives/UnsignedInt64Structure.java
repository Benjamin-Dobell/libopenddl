package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.LiteralEncoding;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;

import java.math.BigInteger;

public final class UnsignedInt64Structure extends PrimitiveStructure<BigInteger> implements MultipleLiteralEncodings
{
	public static final BigInteger MAX = new BigInteger("18446744073709551615");


	private int literalEncoding;

	@Override
	protected BigInteger decodeDataElement(final String token) throws OpenDDLException
	{
		return Decoder.decodeUnsignedInt64(token);
	}

	@Override
	protected void encodeDataElement(final StringBuilder stringBuilder, final BigInteger dataElement)
	{
		LiteralEncoding.encodeBigInteger(stringBuilder, dataElement, literalEncoding);
	}

	public UnsignedInt64Structure(final String identifier, final Decoder decoder) throws OpenDDLException
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
