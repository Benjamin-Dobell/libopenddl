package au.com.glassechidna.openddl;

import java.math.BigInteger;

public class HexEncoding
{
	private static char hexChar(final byte halfByte)
	{
		if (halfByte < 10)
		{
			return (char) ('0' + halfByte);
		}
		else // halfByte < 16
		{
			return (char) ('A' + halfByte - 10);
		}
	}

	public static byte charValue(final char hexChar) throws NumberFormatException
	{
		switch (hexChar)
		{
			case '0':
				return 0;

			case '1':
				return 1;

			case '2':
				return 2;

			case '3':
				return 3;

			case '4':
				return 4;

			case '5':
				return 5;

			case '6':
				return 6;

			case '7':
				return 7;

			case '8':
				return 8;

			case '9':
				return 9;

			case 'A':
			case 'a':
				return 10;

			case 'B':
			case 'b':
				return 11;

			case 'C':
			case 'c':
				return 12;

			case 'D':
			case 'd':
				return 13;

			case 'E':
			case 'e':
				return 14;

			case 'F':
			case 'f':
				return 15;
		}

		throw new NumberFormatException();
	}

	public static short unsignedByteValue(final String token, final int offset) throws NumberFormatException
	{
		final int hexLength = token.length() - offset;

		if (hexLength == 1)
		{
			return charValue(token.charAt(offset));
		}
		else if (hexLength == 2)
		{
			return (short) (16 * charValue(token.charAt(offset)) + charValue(token.charAt(offset + 1)));
		}
		else
		{
			throw new NumberFormatException();
		}
	}

	public static int unsignedShortValue(final String token, final int offset) throws NumberFormatException
	{
		final int hexLength = token.length() - offset;

		if (hexLength > 0 && hexLength < 5)
		{
			int value = 0;
			int multiplier = 1;

			for (int i = hexLength - 1; i >= 0; --i)
			{
				value += charValue(token.charAt(offset + i)) * multiplier;
				multiplier *= 16;
			}

			return value;
		}
		else
		{
			throw new NumberFormatException();
		}
	}

	public static long unsignedIntValue(final String token, final int offset) throws NumberFormatException
	{
		final int hexLength = token.length() - offset;

		if (hexLength > 0 && hexLength < 9)
		{
			long value = 0;
			long multiplier = 1;

			for (int i = hexLength - 1; i >= 0; --i)
			{
				value += charValue(token.charAt(offset + i)) * multiplier;
				multiplier *= 16;
			}

			return value;
		}
		else
		{
			throw new NumberFormatException();
		}
	}

	public static BigInteger unsignedLongValue(final String token, final int offset) throws NumberFormatException
	{
		final int hexLength = token.length() - offset;

		if (hexLength > 0 && hexLength < 17)
		{
			final char firstCharacter = token.charAt(offset);

			if (firstCharacter == '-' || firstCharacter == '+')
			{
				throw new NumberFormatException();
			}

			if (hexLength > 1)
			{
				final char secondCharacter = token.charAt(offset + 1);

				if (secondCharacter == 'x' || secondCharacter == 'X')
				{
					throw new NumberFormatException();
				}
			}

			return new BigInteger(token.substring(offset), 16);
		}
		else
		{
			throw new NumberFormatException();
		}
	}

	public static void hexString(final StringBuilder stringBuilder, final int value, final int bytes)
	{
		for (int i = bytes - 1; i >= 0; --i)
		{
			stringBuilder.append(hexChar((byte) ((value >> (4 * i)) & 0xF)));
		}
	}

	// TODO: These are unsigned encodings... account for signed numbers!

	public static void byteHexString(final StringBuilder stringBuilder, final byte value)
	{
		final byte upper = (byte) (value >>> 4);
		final byte lower = (byte) (value & 0xF);

		if (upper != 0)
		{
			stringBuilder.append(hexChar(upper));
		}

		stringBuilder.append(hexChar(lower));
	}

	public static void shortHexString(final StringBuilder stringBuilder, final short value)
	{
		int i = 3;

		for (; i > 0; --i)
		{
			if (((value >> (4 * i)) & 0xF) != 0)
			{
				break;
			}
		}

		for (; i >= 0; --i)
		{
			stringBuilder.append(hexChar((byte) ((value >> (4 * i)) & 0xF)));
		}
	}

	public static void intHexString(final StringBuilder stringBuilder, final int value)
	{
		int i = 7;

		for (; i > 0; --i)
		{
			if (((value >> (4 * i)) & 0xF) != 0)
			{
				break;
			}
		}

		for (; i >= 0; --i)
		{
			stringBuilder.append(hexChar((byte) ((value >> (4 * i)) & 0xF)));
		}
	}

	public static void longHexString(final StringBuilder stringBuilder, final long value)
	{
		int i = 15;

		for (; i > 0; --i)
		{
			if (((value >> (4 * i)) & 0xF) != 0)
			{
				break;
			}
		}

		for (; i >= 0; --i)
		{
			stringBuilder.append(hexChar((byte) ((value >> (4 * i)) & 0xF)));
		}
	}
}
