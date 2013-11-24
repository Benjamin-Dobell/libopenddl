package au.com.glassechidna.openddl;

import java.math.BigInteger;

public class BinaryEncoding
{
	public static short unsignedByteValue(final String token, final int offset) throws NumberFormatException
	{
		final int binaryLength = token.length() - offset;

		if (binaryLength > 0 && binaryLength < 9)
		{
			short value = 0;
			short multiplier = 1;

			for (int i = binaryLength - 1; i >= 0; --i)
			{
				final char binChar = token.charAt(offset + i);

				if (binChar == '1')
				{
					value += multiplier;
				}
				else if (binChar != '0')
				{
					throw new NumberFormatException();
				}

				multiplier <<= 1;
			}

			return value;
		}
		else
		{
			throw new NumberFormatException();
		}
	}

	public static int unsignedShortValue(final String token, final int offset) throws NumberFormatException
	{
		final int binaryLength = token.length() - offset;

		if (binaryLength > 0 && binaryLength < 17)
		{
			int value = 0;
			int multiplier = 1;

			for (int i = binaryLength - 1; i >= 0; --i)
			{
				final char binChar = token.charAt(offset + i);

				if (binChar == '1')
				{
					value += multiplier;
				}
				else if (binChar != '0')
				{
					throw new NumberFormatException();
				}

				multiplier <<= 1;
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
		final int binaryLength = token.length() - offset;

		if (binaryLength > 0 && binaryLength < 33)
		{
			long value = 0;
			long multiplier = 1;

			for (int i = binaryLength - 1; i >= 0; --i)
			{
				final char binChar = token.charAt(offset + i);

				if (binChar == '1')
				{
					value += multiplier;
				}
				else if (binChar != '0')
				{
					throw new NumberFormatException();
				}

				multiplier <<= 1;
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
		final int binaryLength = token.length() - offset;

		if (binaryLength > 0 && binaryLength < 65)
		{
			final char firstCharacter = token.charAt(0);

			if (firstCharacter == '-' || firstCharacter == '+')
			{
				throw new NumberFormatException();
			}

			if (binaryLength > 1)
			{
				final char secondCharacter = token.charAt(1);

				if (secondCharacter == 'b' || secondCharacter == 'B')
				{
					throw new NumberFormatException();
				}
			}

			return new BigInteger(token.substring(offset), 2);
		}
		else
		{
			throw new NumberFormatException();
		}
	}

	public static void byteBinaryString(final StringBuilder stringBuilder, final byte value)
	{
		int i = 7;

		for (; i > 0; --i)
		{
			if (((value >> i) & 0x1) != 0)
			{
				break;
			}
		}

		for (; i >= 0; --i)
		{
			stringBuilder.append((char) ('0' + ((value >> i) & 0x1)));
		}
	}

	public static void shortBinaryString(final StringBuilder stringBuilder, final short value)
	{
		int i = 15;

		for (; i > 0; --i)
		{
			if (((value >> i) & 0x1) != 0)
			{
				break;
			}
		}

		for (; i >= 0; --i)
		{
			stringBuilder.append((char) ('0' + ((value >> i) & 0x1)));
		}
	}

	public static void intBinaryString(final StringBuilder stringBuilder, final int value)
	{
		int i = 31;

		for (; i > 0; --i)
		{
			if (((value >> i) & 0x1) != 0)
			{
				break;
			}
		}

		for (; i >= 0; --i)
		{
			stringBuilder.append((char) ('0' + ((value >> i) & 0x1)));
		}
	}

	public static void longBinaryString(final StringBuilder stringBuilder, final long value)
	{
		int i = 63;

		for (; i > 0; --i)
		{
			if (((value >> i) & 0x1) != 0)
			{
				break;
			}
		}

		for (; i >= 0; --i)
		{
			stringBuilder.append((char) ('0' + ((value >> i) & 0x1)));
		}
	}
}
