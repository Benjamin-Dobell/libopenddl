package au.com.glassechidna.openddl;

public class GenericDecoder extends Decoder
{
	public GenericDecoder(final String openddlString)
	{
		super(openddlString);

		addBuilder(Decoder.WILDCARD_IDENTIFIER, new GenericStructure.Builder());
	}
}
