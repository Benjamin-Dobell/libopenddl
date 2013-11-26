package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.Decoder;
import au.com.glassechidna.openddl.LiteralEncoding;
import au.com.glassechidna.openddl.OpenDDLException;
import au.com.glassechidna.openddl.PrimitiveStructure;

public final class Int64Structure extends PrimitiveStructure<Long> implements MultipleLiteralEncodings
{
	public static final String IDENTIFIER = "int64";


	private int literalEncoding = LiteralEncoding.DECIMAL;

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
	}

	public Int64Structure(final String name, final int arrayLength)
	{
		super(Int64Structure.IDENTIFIER, name, arrayLength);
	}

	public Int64Structure(final String name)
	{
		super(Int64Structure.IDENTIFIER, name);
	}

	public Int64Structure(final int arrayLength)
	{
		super(Int64Structure.IDENTIFIER, arrayLength);
	}

	public Int64Structure()
	{
		super(Int64Structure.IDENTIFIER);
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
