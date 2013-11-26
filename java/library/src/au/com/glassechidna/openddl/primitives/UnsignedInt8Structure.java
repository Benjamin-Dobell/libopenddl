package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.LiteralEncoding;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;

public final class UnsignedInt8Structure extends PrimitiveStructure<Short> implements MultipleLiteralEncodings
{
	public static final String IDENTIFIER = "unsigned_int8";

	public static final short MAX = 255;


	private int literalEncoding = LiteralEncoding.DECIMAL;

	@Override
	protected Short decodeDataElement(final String token) throws OpenDDLException
	{
		return Short.valueOf(Decoder.decodeUnsignedInt8(token));
	}

	@Override
	protected void encodeDataElement(final StringBuilder stringBuilder, final Short dataElement)
	{
		LiteralEncoding.encodeShort(stringBuilder, dataElement.shortValue(), literalEncoding);
	}

	public UnsignedInt8Structure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier, decoder);
	}

	public UnsignedInt8Structure(final String name, final int arrayLength)
	{
		super(UnsignedInt8Structure.IDENTIFIER, name, arrayLength);
	}

	public UnsignedInt8Structure(final String name)
	{
		super(UnsignedInt8Structure.IDENTIFIER, name);
	}

	public UnsignedInt8Structure(final int arrayLength)
	{
		super(UnsignedInt8Structure.IDENTIFIER, arrayLength);
	}

	public UnsignedInt8Structure()
	{
		super(UnsignedInt8Structure.IDENTIFIER);
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
