package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.LiteralEncoding;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;

public final class DoubleStructure extends PrimitiveStructure<Double> implements MultipleLiteralEncodings
{
	public static final String IDENTIFIER = "double";


	private int literalEncoding = LiteralEncoding.FLOATING_POINT;;

	@Override
	protected Double decodeDataElement(final String token) throws OpenDDLException
	{
		return Double.valueOf(Decoder.decodeDouble(token));
	}

	@Override
	protected void encodeDataElement(final StringBuilder stringBuilder, final Double dataElement)
	{
		double value = dataElement.doubleValue();

		if (literalEncoding == LiteralEncoding.FLOATING_POINT && (Double.isInfinite(value) || Double.isNaN(value)))
		{
			LiteralEncoding.encodeDouble(stringBuilder, dataElement, LiteralEncoding.HEX);
		}
		else
		{
			LiteralEncoding.encodeDouble(stringBuilder, dataElement, literalEncoding);
		}
	}

	public DoubleStructure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier, decoder);
	}

	public DoubleStructure(final String name, final int arrayLength)
	{
		super(DoubleStructure.IDENTIFIER, name, arrayLength);
	}

	public DoubleStructure(final String name)
	{
		super(DoubleStructure.IDENTIFIER, name);
	}

	public DoubleStructure(final int arrayLength)
	{
		super(DoubleStructure.IDENTIFIER, arrayLength);
	}

	public DoubleStructure()
	{
		super(DoubleStructure.IDENTIFIER);
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
