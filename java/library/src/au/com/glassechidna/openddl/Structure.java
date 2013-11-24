package au.com.glassechidna.openddl;

public abstract class Structure
{
	static void encodeNewLine(final StringBuilder stringBuilder, final int depth)
	{
		stringBuilder.append('\n');

		for (int i = 0; i < depth; i++)
		{
			stringBuilder.append('\t');
		}
	}


	private final String identifier;

	protected abstract void validate(final RootStructure rootStructure) throws OpenDDLException;

	Structure(final String identifier) throws OpenDDLException
	{
		if (identifier != null && !Decoder.isIdentifier(identifier, 0))
		{
			throw new OpenDDLException("Encountered invalid identifier '" + identifier + "' whilst parsing a structure");
		}

		this.identifier = identifier;
	}

	abstract void encode(final StringBuilder stringBuilder, final int depth);

	String getInternalIdentifier()
	{
		return identifier;
	}

	public final String getIdentifier()
	{
		return getInternalIdentifier();
	}

	public abstract String getName();

	public String toString()
	{
		final StringBuilder stringBuilder = new StringBuilder();
		encode(stringBuilder, 0);
		return stringBuilder.toString();
	}
}
