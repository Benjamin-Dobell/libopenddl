package au.com.glassechidna.openddl.primitives;

import au.com.glassechidna.openddl.*;

import java.nio.charset.StandardCharsets;

// TODO: Java 1.7 changed substring() from O(1) to O(N), in order to avoid derps leaking memory. As such replace use of String with a custom CharSequence
//       subclass which offers O(1) substring().
public final class StringStructure extends PrimitiveStructure<String>
{
	private static int readEscapeCharacter(final StringBuilder stringBuilder, final String token, final int startIndex) throws OpenDDLException
	{
		switch (token.charAt(startIndex))
		{
			case '"':
				stringBuilder.append('"');
				return 1;

			case '\'':
				stringBuilder.append('\'');
				return 1;

			case '?':
				stringBuilder.append('?');
				return 1;

			case '\\':
				stringBuilder.append('\\');
				return 1;

			case 'a':
				stringBuilder.append('\7');
				return 1;

			case 'b':
				stringBuilder.append('\b');
				return 1;

			case 'f':
				stringBuilder.append('\f');
				return 1;

			case 'n':
				stringBuilder.append('n');
				return 1;

			case 'r':
				stringBuilder.append('\r');
				return 1;

			case 't':
				stringBuilder.append('\t');
				return 1;

			case 'v':
				stringBuilder.append('\13');
				return 1;

			case 'u':
			{
				if (startIndex + 4 > token.length())
				{
					throw new OpenDDLException("Encountered invalid unicode escape character in token '" + token + "' whilst parsing string structure");
				}

				stringBuilder.append((char) HexEncoding.unsignedShortValue(token.substring(startIndex + 1, startIndex + 5), 0));
				return 5;
			}

			case 'U':
			{
				if (startIndex + 6 > token.length())
				{
					throw new OpenDDLException("Encountered invalid unicode escape character in token '" + token + "' whilst parsing string structure");
				}

				final int codePoint = (int) HexEncoding.unsignedIntValue(token.substring(startIndex + 1, startIndex + 7), 0) - 0x10000;
				stringBuilder.append((char) (((codePoint >> 10) & 0x03FF) + 0xD800));
				stringBuilder.append((char) ((codePoint & 0x03FF) + 0xDC00));

				return 7;
			}

			default:
				throw new OpenDDLException("Encountered invalid escape sequence in token '" + token + "' whilst parsing string structure");
		}
	}


	@Override
	protected String decodeDataElement(final String token) throws OpenDDLException
	{
		final int tokenLength = token.length();

		if (tokenLength > 1 && token.charAt(0) == '"' && token.charAt(tokenLength - 1) == '"')
		{
			final String string = token.substring(1, tokenLength - 1);

			final int length = string.length();
			int index = 0;
			int bufferedIndex = 0;

			StringBuilder builder = null;

			for (; index < length; ++index)
			{
				char character = string.charAt(index);

				if (character == '\\')
				{
					if (index + 1 == length)
					{
						throw new OpenDDLException("Encountered invalid escape character in token " + token + " whilst parsing string structure");
					}

					if (builder == null)
					{
						builder = new StringBuilder(length);
					}

					builder.append(string, bufferedIndex, index);
					index += StringStructure.readEscapeCharacter(builder, string, index + 1);

					bufferedIndex = index + 1;
				}
				else if (character < 0x20 || (character > 0x7E && character < 0xA0))
				{
					throw new OpenDDLException("Encountered illegal character '" + character + "' in token " + token + " whilst parsing string structure");
				}
			}

			if (builder == null)
			{
				// token is a substring() of a String passed to a Decoder. Prior to Java 1.7 JVMs implement substring() via returning a String which is a
				// reference to the original string data, with an offset. However, we don't want to leak the larger String. So we copy...
				// Since Java 1.7 this isn't an issue... but substring() is sloooooooow.
				return new String(string);
			}
			else
			{
				builder.append(string, bufferedIndex, length);
				return builder.toString();
			}
		}
		else
		{
			throw new OpenDDLException("'" + token + "' is not a valid string");
		}
	}

	@Override
	protected void encodeDataElement(final StringBuilder stringBuilder, final String dataElement)
	{
		stringBuilder.append('"');

		final int length = dataElement.length();
		int index = 0;
		int bufferedIndex = 0;

		for (; index < length; ++index)
		{
			final char character = dataElement.charAt(index);

			switch (character)
			{
				case '"':
				case '\\':
					stringBuilder.append(dataElement, bufferedIndex, index);
					stringBuilder.append("\\");
					stringBuilder.append(character);

					bufferedIndex = index + 1;
					break;
			}
		}

		if (bufferedIndex != length)
		{
			stringBuilder.append(dataElement, bufferedIndex, length);
		}

		stringBuilder.append('"');
	}

	public StringStructure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier, decoder);
	}
}
