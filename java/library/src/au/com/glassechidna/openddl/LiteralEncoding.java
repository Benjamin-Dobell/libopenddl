package au.com.glassechidna.openddl;

import java.math.BigInteger;

public class LiteralEncoding
{
	public static final int DECIMAL = 0;
	public static final int HEX = 1;
	public static final int BINARY = 2;
	public static final int CHAR = 3;
	public static final int FLOATING_POINT = 4;

	private static void encodeCharLiteral(final StringBuilder stringBuilder, final byte value)
	{
		switch (value)
		{
			case '\'':
				stringBuilder.append("'\\\''");
				break;

			case '\\':
				stringBuilder.append("'\\\\'");
				break;

			case 0x7:
				stringBuilder.append("'\\a'");
				break;

			case '\b':
				stringBuilder.append("'\\b'");
				break;

			case '\f':
				stringBuilder.append("'\\f'");
				break;

			case '\n':
				stringBuilder.append("'\\n'");
				break;

			case '\r':
				stringBuilder.append("'\\r'");
				break;

			case '\t':
				stringBuilder.append("'\\t'");
				break;

			case 0x11:
				stringBuilder.append("'\\v'");
				break;
		}

		if (value >= 0x20 && value <= 0x7E)
		{
			stringBuilder.append("'");
			stringBuilder.append((char) value);
			stringBuilder.append("'");
		}
		else
		{
			stringBuilder.append("'\\x");
			HexEncoding.byteHexString(stringBuilder, value);
			stringBuilder.append("'");
		}
	}

	public static void encodeBoolean(final StringBuilder stringBuilder, final boolean value)
	{
		stringBuilder.append(value ? "true" : "false");
	}

	public static void encodeByte(final StringBuilder stringBuilder, final byte value, final int literalEncoding)
	{
		switch (literalEncoding)
		{
			case DECIMAL:
				stringBuilder.append(Byte.toString(value));
				return;

			case HEX:
				stringBuilder.append("0x");
				HexEncoding.byteHexString(stringBuilder, value);
				return;

			case BINARY:
				stringBuilder.append("0b");
				BinaryEncoding.byteBinaryString(stringBuilder, value);
				return;

			case CHAR:
				encodeCharLiteral(stringBuilder, value);
				return;
		}

		throw new IllegalStateException("Literal encoding '" + literalEncoding + "' is not valid for 8-bit integers");
	}

	public static void encodeShort(final StringBuilder stringBuilder, final short value, final int literalEncoding)
	{
		switch (literalEncoding)
		{
			case DECIMAL:
				stringBuilder.append(Short.toString(value));
				return;

			case HEX:
				stringBuilder.append("0x");
				HexEncoding.shortHexString(stringBuilder, value);
				return;

			case BINARY:
				stringBuilder.append("0b");
				BinaryEncoding.shortBinaryString(stringBuilder, value);
				return;

			case CHAR:
				encodeCharLiteral(stringBuilder, (byte) value);
				return;
		}

		throw new IllegalStateException("Literal encoding '" + literalEncoding + "' is not valid for 16-bit integers");
	}

	public static void encodeInt(final StringBuilder stringBuilder, final int value, final int literalEncoding)
	{
		switch (literalEncoding)
		{
			case DECIMAL:
				stringBuilder.append(Integer.toString(value));
				return;

			case HEX:
				stringBuilder.append("0x");
				HexEncoding.intHexString(stringBuilder, value);
				return;

			case BINARY:
				stringBuilder.append("0b");
				BinaryEncoding.intBinaryString(stringBuilder, value);
				return;

			case CHAR:
				encodeCharLiteral(stringBuilder, (byte) value);
				return;
		}

		throw new IllegalStateException("Literal encoding '" + literalEncoding + "' is not valid for 32-bit integers");
	}

	public static void encodeLong(final StringBuilder stringBuilder, final long value, final int literalEncoding)
	{
		switch (literalEncoding)
		{
			case DECIMAL:
				stringBuilder.append(Long.toString(value));
				return;

			case HEX:
				stringBuilder.append("0x");
				HexEncoding.longHexString(stringBuilder, value);
				return;

			case BINARY:
				stringBuilder.append("0b");
				BinaryEncoding.longBinaryString(stringBuilder, value);
				return;

			case CHAR:
				encodeCharLiteral(stringBuilder, (byte) value);
				return;
		}

		throw new IllegalStateException("Literal encoding '" + literalEncoding + "' is not valid for 64-bit integers");
	}

	public static final void encodeBigInteger(final StringBuilder stringBuilder, final BigInteger value, final int literalEncoding)
	{
		switch (literalEncoding)
		{
			case DECIMAL:
				stringBuilder.append(value.toString());
				return;

			case HEX:
				stringBuilder.append("0x");
				stringBuilder.append(value.toString(16).toUpperCase());
				return;

			case BINARY:
				stringBuilder.append("0b");
				stringBuilder.append(value.toString(2));
				return;

			case CHAR:
				encodeCharLiteral(stringBuilder, value.byteValue());
				return;
		}

		throw new IllegalStateException("Literal encoding '" + literalEncoding + "' is not valid for big integers");
	}

	public static void encodeFloat(final StringBuilder stringBuilder, final float value, final int literalEncoding)
	{
		switch (literalEncoding)
		{
			case HEX:
				stringBuilder.append("0x");
				HexEncoding.intHexString(stringBuilder, Float.floatToIntBits(value));
				return;

			case BINARY:
				stringBuilder.append("0b");
				BinaryEncoding.intBinaryString(stringBuilder, Float.floatToIntBits(value));
				return;

			case FLOATING_POINT:
				stringBuilder.append(Float.toString(value));
				return;
		}

		throw new IllegalStateException("Literal encoding '" + literalEncoding + "' is not valid for floats");
	}

	public static void encodeDouble(final StringBuilder stringBuilder, final double value, final int literalEncoding)
	{
		switch (literalEncoding)
		{
			case HEX:
				stringBuilder.append("0x");
				HexEncoding.longHexString(stringBuilder, Double.doubleToLongBits(value));
				return;

			case BINARY:
				stringBuilder.append("0b");
				BinaryEncoding.longBinaryString(stringBuilder, Double.doubleToLongBits(value));
				return;

			case FLOATING_POINT:
				stringBuilder.append(Double.toString(value));
				return;
		}

		throw new IllegalStateException("Literal encoding '" + literalEncoding + "' is not valid for floats");
	}
}
