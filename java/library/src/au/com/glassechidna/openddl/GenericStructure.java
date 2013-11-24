package au.com.glassechidna.openddl;

public class GenericStructure extends NodeStructure
{
	public static class Builder implements NodeStructure.Builder
	{
		@Override
		public NodeStructure build(final String identifier, final Decoder decoder) throws OpenDDLException
		{
			return new GenericStructure(identifier, decoder);
		}
	}


	public GenericStructure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier, decoder);
	}
}
