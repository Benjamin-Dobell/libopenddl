package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.LiteralEncoding;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;

public final class UnsignedInt16Structure extends PrimitiveStructure<Integer> implements MultipleLiteralEncodings
{
	public static final String IDENTIFIER = "unsigned_int16";

	public static final int MAX = 65536;


	private int literalEncoding = LiteralEncoding.DECIMAL;

	@Override
	protected Integer decodeDataElement(final String token) throws OpenDDLException
	{
		return Integer.valueOf(Decoder.decodeUnsignedInt16(token));
	}

	@Override
	protected void encodeDataElement(final StringBuilder stringBuilder, final Integer dataElement)
	{
		LiteralEncoding.encodeInt(stringBuilder, dataElement.intValue(), literalEncoding);
	}

	public UnsignedInt16Structure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier, decoder);
	}

	public UnsignedInt16Structure(final String name, final int arrayLength)
	{
		super(UnsignedInt16Structure.IDENTIFIER, name, arrayLength);
	}

	public UnsignedInt16Structure(final String name)
	{
		super(UnsignedInt16Structure.IDENTIFIER, name);
	}

	public UnsignedInt16Structure(final int arrayLength)
	{
		super(UnsignedInt16Structure.IDENTIFIER, arrayLength);
	}

	public UnsignedInt16Structure()
	{
		super(UnsignedInt16Structure.IDENTIFIER);
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
