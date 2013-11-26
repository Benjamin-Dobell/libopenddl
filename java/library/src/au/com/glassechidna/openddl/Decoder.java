package au.com.glassechidna.openddl;

import au.com.glassechidna.openddl.primitives.*;

import java.math.BigInteger;
import java.util.HashMap;

// TODO: Java 1.7 changed substring() from O(1) to O(N), in order to avoid derps leaking memory. As such replace use of String with a custom CharSequence
//       subclass which offers O(1) substring().

public class Decoder
{
	public static final String WILDCARD_IDENTIFIER = "*";

	private static final BigInteger LONG_MAXIMUM_NEGATIVE_MAGNITUDE = new BigInteger("9223372036854775808");
	private static final BigInteger LONG_MAXIMUM_POSITIVE_MAGNITUDE = BigInteger.valueOf(9223372036854775807L);


	private static short parseCharacterLiteral(final String token, final int startIndex) throws NumberFormatException
	{
		final int length = token.length();

		if (length > startIndex + 2 && token.charAt(startIndex) == '\'' && token.charAt(length - 1) == '\'')
		{
			final char firstEnclosedChar = token.charAt(startIndex + 1);

			if (firstEnclosedChar == '\\')
			{
				if (length == startIndex + 4)
				{
					switch (token.charAt(startIndex + 2))
					{
						case '"':
							return '"';

						case '\'':
							return '\'';

						case '?':
							return '?';

						case '\\':
							return '\\';

						case 'a':
							return 0x7;

						case 'b':
							return '\b';

						case 'f':
							return '\f';

						case 'n':
							return '\n';

						case 'r':
							return '\r';

						case 't':
							return '\t';

						case 'v':
							return 0x11;
					}
				}
				else if ((length == startIndex + 6) && (token.charAt(startIndex + 2) == 'x'))
				{
					return HexEncoding.unsignedByteValue(token.substring(startIndex + 3, startIndex + 5), 0);
				}
			}
			else if (length == startIndex + 3 && firstEnclosedChar <= 0x7F)
			{
				return (short) firstEnclosedChar;
			}
		}
		else if (length == startIndex + 1)
		{
			final char character = token.charAt(startIndex);

			if ((character >= 0x20 && character < '\'') || (character > '\'' && character < 0x7F))
			{
				return (short) character;
			}
		}

		throw new NumberFormatException("Not a valid character literal");
	}

	public static boolean isIdentifier(final String token, final int startIndex)
	{
		final int length = token.length();

		if (startIndex >= length)
		{
			return false;
		}

		char character = token.charAt(startIndex);

		if ((character < 'A' || character > 'Z') && (character < 'a' || character > 'z') && character != '_')
		{
			return false;
		}

		for (int i = startIndex + 1; i < token.length(); i++)
		{
			character = token.charAt(i);

			if ((character < 'A' || character > 'Z') && (character < 'a' || character > 'z') && (character < '0' || character > '9') && character != '_')
			{
				return false;
			}
		}

		return true;
	}

	public static boolean isName(final String token, final int startIndex)
	{
		final int length = token.length();

		if (startIndex + 1 >= length)
		{
			return false;
		}

		final char firstCharacter = token.charAt(startIndex);

		if (firstCharacter != '$' && firstCharacter != '%')
		{
			return false;
		}

		return isIdentifier(token, startIndex + 1);
	}

	public static boolean isLocalName(final String token, final int startIndex)
	{
		final int length = token.length();

		if (startIndex + 1 >= length)
		{
			return false;
		}

		final char firstCharacter = token.charAt(startIndex);

		if (firstCharacter != '%')
		{
			return false;
		}

		return isIdentifier(token, startIndex + 1);
	}

	public static int parseType(final String token)
	{
		for (int dataType = 0; dataType < PrimitiveType.IDENTIFIERS.length; dataType++)
		{
			if (PrimitiveType.IDENTIFIERS[dataType].equals(token))
				return dataType;
		}

		return -1;
	}

	public static boolean decodeBoolean(final String token) throws OpenDDLException
	{
		if (token.equals("true"))
		{
			return true;
		}
		else if (token.equals("false"))
		{
			return false;
		}
		else
		{
			throw new OpenDDLException("'" + token + "' is not a valid boolean literal");
		}
	}

	public static short decodeUnsignedInt8(final String token) throws OpenDDLException
	{
		try
		{
			final int length = token.length();

			if (length > 2)
			{
				final char firstCharacter = token.charAt(0);
				final int magnitudeOffset;

				if (firstCharacter == '-')
				{
					throw new NumberFormatException();
				}
				else if (firstCharacter == '+')
				{
					magnitudeOffset = 1;
				}
				else
				{
					magnitudeOffset = 0;
				}

				char encodingChar = token.charAt(magnitudeOffset);

				if (encodingChar == '\'')
				{
					return Decoder.parseCharacterLiteral(token, magnitudeOffset);
				}
				else if (encodingChar == '0')
				{
					encodingChar = token.charAt(magnitudeOffset + 1);

					if (encodingChar == 'x' || encodingChar == 'X')
					{
						return HexEncoding.unsignedByteValue(token, magnitudeOffset + 2);
					}
					else if (encodingChar == 'b' || encodingChar == 'B')
					{
						return BinaryEncoding.unsignedByteValue(token.substring(magnitudeOffset + 2), 2);
					}
					else
					{
						throw new NumberFormatException("Invalid encoding");
					}
				}
			}

			final short value = Short.parseShort(token);

			if (value < 0 || value > UnsignedInt8Structure.MAX)
			{
				throw new NumberFormatException();
			}

			return value;
		}
		catch (final NumberFormatException e)
		{
			throw new OpenDDLException("'" + token + "' is not a valid 8-bit unsigned integer literal", e);
		}
	}

	public static byte decodeInt8(final String token) throws OpenDDLException
	{
		try
		{
			final int length = token.length();

			if (length > 2)
			{
				final char firstCharacter = token.charAt(0);

				final boolean negate;
				final int magnitudeOffset;

				if (firstCharacter == '-')
				{
					negate = true;
					magnitudeOffset = 1;
				}
				else
				{
					if (firstCharacter == '+')
					{
						magnitudeOffset = 1;
					}
					else
					{
						magnitudeOffset = 0;
					}

					negate = false;
				}

				final short magnitudeValue;
				char encodingChar = token.charAt(magnitudeOffset);

				if (encodingChar == '\'')
				{
					magnitudeValue = Decoder.parseCharacterLiteral(token, magnitudeOffset);
				}
				else if (encodingChar == '0')
				{
					encodingChar = token.charAt(magnitudeOffset + 1);

					if (encodingChar == 'x' || encodingChar == 'X')
					{
						magnitudeValue = HexEncoding.unsignedByteValue(token, magnitudeOffset + 2);
					}
					else if (encodingChar == 'b' || encodingChar == 'B')
					{
						magnitudeValue = BinaryEncoding.unsignedByteValue(token.substring(magnitudeOffset + 2), 2);
					}
					else
					{
						throw new NumberFormatException("Invalid encoding");
					}
				}
				else
				{
					magnitudeValue = -1;
				}

				if (magnitudeValue > -1)
				{
					if (negate)
					{
						if (magnitudeValue > -Byte.MIN_VALUE)
						{
							throw new NumberFormatException("Overflow");
						}

						return (byte) -magnitudeValue;
					}
					else
					{
						if (magnitudeValue > Byte.MAX_VALUE)
						{
							throw new NumberFormatException("Overflow");
						}

						return (byte) magnitudeValue;
					}
				}
			}

			return Byte.parseByte(token);
		}
		catch (final NumberFormatException e)
		{
			throw new OpenDDLException("'" + token + "' is not a valid 8-bit integer literal", e);
		}
	}

	public static int decodeUnsignedInt16(final String token) throws OpenDDLException
	{
		try
		{
			final int length = token.length();

			if (length > 2)
			{
				final char firstCharacter = token.charAt(0);
				final int magnitudeOffset;

				if (firstCharacter == '-')
				{
					throw new NumberFormatException();
				}
				else if (firstCharacter == '+')
				{
					magnitudeOffset = 1;
				}
				else
				{
					magnitudeOffset = 0;
				}

				char encodingChar = token.charAt(magnitudeOffset);

				if (encodingChar == '\'')
				{
					return Decoder.parseCharacterLiteral(token, magnitudeOffset);
				}
				else if (encodingChar == '0')
				{
					encodingChar = token.charAt(magnitudeOffset + 1);

					if (encodingChar == 'x' || encodingChar == 'X')
					{
						return HexEncoding.unsignedShortValue(token, magnitudeOffset + 2);
					}
					else if (encodingChar == 'b' || encodingChar == 'B')
					{
						return BinaryEncoding.unsignedShortValue(token.substring(magnitudeOffset + 2), 2);
					}
					else
					{
						throw new NumberFormatException("Invalid encoding");
					}
				}
			}

			final int value = Integer.parseInt(token);

			if (value < 0 || value > UnsignedInt16Structure.MAX)
			{
				throw new NumberFormatException();
			}

			return value;
		}
		catch (final NumberFormatException e)
		{
			throw new OpenDDLException("'" + token + "' is not a valid 16-bit unsigned integer literal", e);
		}
	}

	public static short decodeInt16(final String token) throws OpenDDLException
	{
		try
		{
			final int length = token.length();

			if (length > 2)
			{
				final char firstCharacter = token.charAt(0);

				final boolean negate;
				final int magnitudeOffset;

				if (firstCharacter == '-')
				{
					negate = true;
					magnitudeOffset = 1;
				}
				else
				{
					if (firstCharacter == '+')
					{
						magnitudeOffset = 1;
					}
					else
					{
						magnitudeOffset = 0;
					}

					negate = false;
				}

				char encodingChar = token.charAt(magnitudeOffset);

				if (encodingChar == '\'')
				{
					return (negate) ? (short) -Decoder.parseCharacterLiteral(token, magnitudeOffset) : Decoder.parseCharacterLiteral(token, magnitudeOffset);
				}

				final int magnitudeValue;

				if (encodingChar == '0')
				{
					encodingChar = token.charAt(magnitudeOffset + 1);

					if (encodingChar == 'x' || encodingChar == 'X')
					{
						magnitudeValue = HexEncoding.unsignedShortValue(token, magnitudeOffset + 2);
					}
					else if (encodingChar == 'b' || encodingChar == 'B')
					{
						magnitudeValue = BinaryEncoding.unsignedShortValue(token.substring(magnitudeOffset + 2), 2);
					}
					else
					{
						throw new NumberFormatException("'" + token + "' is not a valid integral encoding");
					}
				}
				else
				{
					magnitudeValue = -1;
				}

				if (magnitudeValue > -1)
				{
					if (negate)
					{
						if (magnitudeValue > -Short.MIN_VALUE)
						{
							throw new NumberFormatException("Overflow");
						}

						return (short) -magnitudeValue;
					}
					else
					{
						if (magnitudeValue > Short.MAX_VALUE)
						{
							throw new NumberFormatException("Overflow");
						}

						return (short) magnitudeValue;
					}
				}
			}

			return Short.parseShort(token);
		}
		catch (final NumberFormatException e)
		{
			throw new OpenDDLException("'" + token + "' is not a valid 16-bit integer literal", e);
		}
	}

	public static long decodeUnsignedInt32(final String token) throws OpenDDLException
	{
		try
		{
			final int length = token.length();

			if (length > 2)
			{
				final char firstCharacter = token.charAt(0);
				final int magnitudeOffset;

				if (firstCharacter == '-')
				{
					throw new NumberFormatException();
				}
				else if (firstCharacter == '+')
				{
					magnitudeOffset = 1;
				}
				else
				{
					magnitudeOffset = 0;
				}

				char encodingChar = token.charAt(magnitudeOffset);

				if (encodingChar == '\'')
				{
					return Decoder.parseCharacterLiteral(token, magnitudeOffset);
				}
				else if (encodingChar == '0')
				{
					encodingChar = token.charAt(magnitudeOffset + 1);

					if (encodingChar == 'x' || encodingChar == 'X')
					{
						return HexEncoding.unsignedIntValue(token, magnitudeOffset + 2);
					}
					else if (encodingChar == 'b' || encodingChar == 'B')
					{
						return BinaryEncoding.unsignedIntValue(token.substring(magnitudeOffset + 2), 2);
					}
					else
					{
						throw new NumberFormatException("'" + token + "' is not a valid integral encoding");
					}
				}
			}

			final long value = Long.parseLong(token);

			if (value < 0 || value > UnsignedInt32Structure.MAX)
			{
				throw new NumberFormatException();
			}

			return value;
		}
		catch (final NumberFormatException e)
		{
			throw new OpenDDLException("'" + token + "' is not a valid 32-bit unsigned integer literal", e);
		}
	}

	public static int decodeInt32(final String token) throws OpenDDLException
	{
		try
		{
			final int length = token.length();

			if (length > 2)
			{
				final char firstCharacter = token.charAt(0);

				final boolean negate;
				final int magnitudeOffset;

				if (firstCharacter == '-')
				{
					negate = true;
					magnitudeOffset = 1;
				}
				else
				{
					if (firstCharacter == '+')
					{
						magnitudeOffset = 1;
					}
					else
					{
						magnitudeOffset = 0;
					}

					negate = false;
				}

				char encodingChar = token.charAt(magnitudeOffset);

				if (encodingChar == '\'')
				{
					return (negate) ? -Decoder.parseCharacterLiteral(token, magnitudeOffset) : Decoder.parseCharacterLiteral(token, magnitudeOffset);
				}

				final long magnitudeValue;

				if (encodingChar == '0')
				{
					encodingChar = token.charAt(magnitudeOffset + 1);

					if (encodingChar == 'x' || encodingChar == 'X')
					{
						magnitudeValue = HexEncoding.unsignedIntValue(token, magnitudeOffset + 2);
					}
					else if (encodingChar == 'b' || encodingChar == 'B')
					{
						magnitudeValue = BinaryEncoding.unsignedIntValue(token.substring(magnitudeOffset + 2), 2);
					}
					else
					{
						throw new NumberFormatException("'" + token + "' is not a valid integral encoding");
					}
				}
				else
				{
					magnitudeValue = -1;
				}

				if (magnitudeValue > -1)
				{
					if (negate)
					{
						if (magnitudeValue > -1L * Integer.MIN_VALUE)
						{
							throw new NumberFormatException("Overflow");
						}

						return (int) -magnitudeValue;
					}
					else
					{
						if (magnitudeValue > Integer.MAX_VALUE)
						{
							throw new NumberFormatException("Overflow");
						}

						return (int) magnitudeValue;
					}
				}
			}

			return Integer.parseInt(token);
		}
		catch (final NumberFormatException e)
		{
			throw new OpenDDLException("'" + token + "' is not a valid 32-bit integer literal", e);
		}
	}

	public static BigInteger decodeUnsignedInt64(final String token) throws OpenDDLException
	{
		try
		{
			final int length = token.length();

			if (length > 2)
			{
				final char firstCharacter = token.charAt(0);
				final int magnitudeOffset;

				if (firstCharacter == '-')
				{
					throw new NumberFormatException();
				}
				else if (firstCharacter == '+')
				{
					magnitudeOffset = 1;
				}
				else
				{
					magnitudeOffset = 0;
				}

				char encodingChar = token.charAt(magnitudeOffset);

				if (encodingChar == '\'')
				{
					return BigInteger.valueOf(Decoder.parseCharacterLiteral(token, magnitudeOffset));
				}
				else if (encodingChar == '0')
				{
					encodingChar = token.charAt(magnitudeOffset + 1);

					if (encodingChar == 'x' || encodingChar == 'X')
					{
						return HexEncoding.unsignedLongValue(token, magnitudeOffset + 2);
					}
					else if (encodingChar == 'b' || encodingChar == 'B')
					{
						return BinaryEncoding.unsignedLongValue(token.substring(magnitudeOffset + 2), 2);
					}
					else
					{
						throw new NumberFormatException("'" + token + "' is not a valid integral encoding");
					}
				}
			}

			final BigInteger value = new BigInteger(token);

			if (value.compareTo(BigInteger.ZERO) < 0 || value.compareTo(UnsignedInt64Structure.MAX) > 0)
			{
				throw new NumberFormatException();
			}

			return value;
		}
		catch (final NumberFormatException e)
		{
			throw new OpenDDLException("'" + token + "' is not a valid 64-bit unsigned integer literal", e);
		}
	}


	public static long decodeInt64(final String token) throws OpenDDLException
	{
		try
		{
			final int length = token.length();

			if (length > 2)
			{
				final char firstCharacter = token.charAt(0);

				final boolean negate;
				final int magnitudeOffset;

				if (firstCharacter == '-')
				{
					negate = true;
					magnitudeOffset = 1;
				}
				else
				{
					if (firstCharacter == '+')
					{
						magnitudeOffset = 1;
					}
					else
					{
						magnitudeOffset = 0;
					}

					negate = false;
				}

				char encodingChar = token.charAt(magnitudeOffset);

				if (encodingChar == '\'')
				{
					return (negate) ? -Decoder.parseCharacterLiteral(token, magnitudeOffset) : Decoder.parseCharacterLiteral(token, magnitudeOffset);
				}

				final BigInteger magnitudeValue;

				if (encodingChar == '0')
				{
					encodingChar = token.charAt(magnitudeOffset + 1);

					if (encodingChar == 'x' || encodingChar == 'X')
					{
						magnitudeValue = HexEncoding.unsignedLongValue(token, magnitudeOffset + 2);
					}
					else if (encodingChar == 'b' || encodingChar == 'B')
					{
						magnitudeValue = BinaryEncoding.unsignedLongValue(token.substring(magnitudeOffset + 2), 2);
					}
					else
					{
						throw new NumberFormatException("'" + token + "' is not a valid integral encoding");
					}
				}
				else
				{
					magnitudeValue = null;
				}

				if (magnitudeValue != null)
				{
					if (negate)
					{
						if (magnitudeValue.compareTo(Decoder.LONG_MAXIMUM_NEGATIVE_MAGNITUDE) > 0)
						{
							throw new NumberFormatException("Overflow");
						}

						return magnitudeValue.negate().longValue();
					}
					else
					{
						if (magnitudeValue.compareTo(Decoder.LONG_MAXIMUM_POSITIVE_MAGNITUDE) > 0)
						{
							throw new NumberFormatException("Overflow");
						}

						return magnitudeValue.longValue();
					}
				}
			}

			return Integer.parseInt(token);
		}
		catch (final NumberFormatException e)
		{
			throw new OpenDDLException("'" + token + "' is not a valid 64-bit integer literal", e);
		}
	}

	public static float decodeFloat(final String token) throws OpenDDLException
	{
		try
		{
			final int length = token.length();

			if (length > 2)
			{
				final char secondCharacter = token.charAt(1);
				final char thirdCharacter = token.charAt(2);

				if (secondCharacter == 'x' || secondCharacter == 'X' || thirdCharacter == 'x' || thirdCharacter == 'X')
				{
					return Float.intBitsToFloat((int) HexEncoding.unsignedIntValue(token, 2));
				}
				else if (secondCharacter == 'b' || secondCharacter == 'B' || thirdCharacter == 'b' || thirdCharacter == 'B')
				{
					return Float.intBitsToFloat((int) BinaryEncoding.unsignedIntValue(token, 2));
				}
			}

			return Float.parseFloat(token);
		}
		catch (final NumberFormatException e)
		{
			throw new OpenDDLException("'" + token + "' is not a valid float literal", e);
		}
	}

	public static double decodeDouble(final String token) throws OpenDDLException
	{
		try
		{
			final int length = token.length();

			if (length > 2)
			{
				final char secondCharacter = token.charAt(1);
				final char thirdCharacter = token.charAt(2);

				if (secondCharacter == 'x' || secondCharacter == 'X' || thirdCharacter == 'x' || thirdCharacter == 'X')
				{
					return Double.longBitsToDouble(HexEncoding.unsignedLongValue(token, 2).longValue());
				}
				else if (secondCharacter == 'b' || secondCharacter == 'B' || thirdCharacter == 'b' || thirdCharacter == 'B')
				{
					return Double.longBitsToDouble(BinaryEncoding.unsignedLongValue(token, 2).longValue());
				}
			}

			return Double.parseDouble(token);
		}
		catch (final NumberFormatException e)
		{
			throw new OpenDDLException("'" + token + "' is not a valid double literal", e);
		}
	}


	private final HashMap<String, NodeStructure.Builder> builders = new HashMap<String, NodeStructure.Builder>();
	private final HashMap<String, Structure> globals = new HashMap<String, Structure>();

	private final String input;
	private final int inputLength;

	private int index = -1;
	private int line = 1;

	private void skipLineComment() throws OpenDDLException
	{
		while (index < inputLength)
		{
			if (input.charAt(index++) == '\n')
			{
				++line;
				break;
			}
		}
	}

	private void skipBlockComment() throws OpenDDLException
	{
		while (index < inputLength)
		{
			final char character = input.charAt(index++);

			if (character == '*')
			{
				if (index < inputLength)
				{
					final char nextCharacter = input.charAt(index);

					if (nextCharacter == '/')
					{
						++index;
						return;
					}
					else if (nextCharacter != '*')
					{
						++index;
					}
				}
			}
			else if (character == '\n')
			{
				++line;
			}
		}

		throw new OpenDDLException("Reached EOF prior to termination of a block comment");
	}

	private String nextQuotedString() throws OpenDDLException
	{
		final int startIndex = index - 1;

		while (index < inputLength)
		{
			if (input.charAt(index++) == '"' && input.charAt(index - 2) != '\\')
				return input.substring(startIndex, index);
		}

		throw new OpenDDLException("Unexpected EOF encountered whilst reading double quoted string");
	}

	private String nextTextualToken() throws OpenDDLException
	{
		final int startIndex = index - 1;

		for (; index < inputLength; ++index)
		{
			final char character = input.charAt(index);

			if (character >= 1 && character <= 32) // Whitespace
			{
				return input.substring(startIndex, index);
			}
			else
			{
				switch (character)
				{
					case '{':
					case '}':
					case '(':
					case ')':
					case '[':
					case ']':
					case ',':
						return input.substring(startIndex, index);
				}
			}
		}

		throw new OpenDDLException("Unexpected EOF encountered whilst reading token");
	}

	int getLine()
	{
		return line;
	}

	String nextToken() throws OpenDDLException
	{
		// The follow special bounds tokens ensure RootStructure will parse as a NodeStructure

		if (index == -1)
		{
			++index;
			return "{";
		}

		while (index < inputLength)
		{
			final char character = input.charAt(index++);

			if (character >= 1 && character <= 32) // Whitespace
			{
				if (character == '\n')
				{
					++line;
				}

				continue;
			}
			else
			{
				switch (character)
				{
					case '/':
						if (index < inputLength)
						{
							final char commentTypeChar = input.charAt(index++);

							if (commentTypeChar == '/')
							{
								skipLineComment();
							}
							else if (commentTypeChar == '*')
							{
								skipBlockComment();
							}
							else
							{
								throw new OpenDDLException("Found invalid '/' character at index " + (index - 1));
							}
						}
						else
						{
							throw new OpenDDLException("Unexpected EOF encountered whilst determining comment mode");
						}

						continue;

					case '=':
						return "=";

					case '{':
						return "{";

					case '}':
						return  "}";

					case '(':
						return "(";

					case ')':
						return ")";

					case '[':
						return "[";

					case ']':
						return "]";

					case ',':
						return ",";

					case '"':
						return nextQuotedString();

					default:
						return nextTextualToken();
				}
			}
		}

		if (index == inputLength)
		{
			++index;
			return "}";
		}

		return null;
	}

	Structure parseStructure(final NodeStructure parent, final String identifier) throws OpenDDLException
	{
		final int dataType = Decoder.parseType(identifier);

		final Structure structure;

		switch (dataType)
		{
			case PrimitiveType.BOOL:
				structure = new BoolStructure(identifier, this);
				break;

			case PrimitiveType.INT8:
				structure = new Int8Structure(identifier, this);
				break;

			case PrimitiveType.INT16:
				structure = new Int16Structure(identifier, this);
				break;

			case PrimitiveType.INT32:
				structure = new Int32Structure(identifier, this);
				break;

			case PrimitiveType.INT64:
				structure = new Int64Structure(identifier, this);
				break;

			case PrimitiveType.UNSIGNED_INT8:
				structure = new UnsignedInt8Structure(identifier, this);
				break;

			case PrimitiveType.UNSIGNED_INT16:
				structure = new UnsignedInt16Structure(identifier, this);
				break;

			case PrimitiveType.UNSIGNED_INT32:
				structure = new UnsignedInt32Structure(identifier, this);
				break;

			case PrimitiveType.UNSIGNED_INT64:
				structure = new UnsignedInt64Structure(identifier, this);
				break;

			case PrimitiveType.FLOAT:
				structure = new FloatStructure(identifier, this);
				break;

			case PrimitiveType.DOUBLE:
				structure = new DoubleStructure(identifier, this);
				break;

			case PrimitiveType.STRING:
				structure = new StringStructure(identifier, this);
				break;

			case PrimitiveType.REF:
				structure = new ReferenceStructure(identifier, this);
				break;

			case PrimitiveType.TYPE:
				structure = new TypeStructure(identifier, this);
				break;

			default:
			{
				NodeStructure.Builder builder = parent.getBuilder(identifier);

				if (builder == null)
				{
					builder = builders.get(identifier);
				}

				if (builder == null)
				{
					builder = builders.get(Decoder.WILDCARD_IDENTIFIER);
				}

				if (builder != null)
				{
					structure = builder.build(identifier, this);
				}
				else
				{
					throw new OpenDDLException("Decoder cannot decode structure with unknown identifier '" + identifier + "'");
				}

				break;
			}
		}

		return structure;
	}

	public Decoder(final String openddlString)
	{
		input = openddlString;
		inputLength = openddlString.length();
	}

	public NodeStructure.Builder addBuilder(final String identifier, final NodeStructure.Builder builder)
	{
		return builders.put(identifier, builder);
	}

	public NodeStructure.Builder removeBuilder(final String identifier)
	{
		return builders.remove(identifier);
	}

	public RootStructure parse() throws OpenDDLException
	{
		try
		{
			final RootStructure rootStructure = new RootStructure(this);
			rootStructure.validate();
			return rootStructure;
		}
		catch (final OpenDDLException e)
		{
			throw new OpenDDLException("Encountered parsing exception on line " + line, e);
		}
	}
}
