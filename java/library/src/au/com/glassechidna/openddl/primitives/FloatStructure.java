package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.LiteralEncoding;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;

public final class FloatStructure extends PrimitiveStructure<Float> implements MultipleLiteralEncodings
{
	private int literalEncoding;

	@Override
	protected Float decodeDataElement(final String token) throws OpenDDLException
	{
		return Float.valueOf(Decoder.decodeFloat(token));
	}

	@Override
	protected void encodeDataElement(final StringBuilder stringBuilder, final Float dataElement)
	{
		float value = dataElement.floatValue();

		if (literalEncoding == LiteralEncoding.FLOATING_POINT && (Float.isInfinite(value) || Float.isNaN(value)))
		{
			LiteralEncoding.encodeFloat(stringBuilder, dataElement, LiteralEncoding.HEX);
		}
		else
		{
			LiteralEncoding.encodeFloat(stringBuilder, dataElement, literalEncoding);
		}
	}

	public FloatStructure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier, decoder);

		literalEncoding = LiteralEncoding.FLOATING_POINT;
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
