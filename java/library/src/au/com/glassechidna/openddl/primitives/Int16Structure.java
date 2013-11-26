package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.LiteralEncoding;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;

public final class Int16Structure extends PrimitiveStructure<Short> implements MultipleLiteralEncodings
{
	public static final String IDENTIFIER = "int16";


	private int literalEncoding = LiteralEncoding.DECIMAL;

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
	}

	public Int16Structure(final String name, final int arrayLength)
	{
		super(Int16Structure.IDENTIFIER, name, arrayLength);
	}

	public Int16Structure(final String name)
	{
		super(Int16Structure.IDENTIFIER, name);
	}

	public Int16Structure(final int arrayLength)
	{
		super(Int16Structure.IDENTIFIER, arrayLength);
	}

	public Int16Structure()
	{
		super(Int16Structure.IDENTIFIER);
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
