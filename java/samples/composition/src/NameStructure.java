import au.com.glassechidna.openddl.*;
import au.com.glassechidna.openddl.primitives.StringStructure;
import com.sun.deploy.util.StringUtils;

public class NameStructure extends NodeStructure
{
	public static final String IDENTIFIER = "Name";

	public static final class Builder implements NodeStructure.Builder
	{
		@Override
		public NodeStructure build(final String identifier, final Decoder decoder) throws OpenDDLException
		{
			return new NameStructure(identifier, decoder);
		}
	}


	@Override
	protected void validate(final RootStructure rootStructure) throws OpenDDLException
	{
		super.validate(rootStructure);

		if (getStructureCount() > 1)
		{
			throw new OpenDDLException("NameStructure must only have one child structure");
		}

		final Structure nameStructure = getStructure(0);

		if (!(nameStructure instanceof StringStructure))
		{
			throw new OpenDDLException("NameStructure must contain a StringStructure");
		}

		final StringStructure nameStringStructure = (StringStructure) nameStructure;

		if (nameStringStructure.getArrayLength() != PrimitiveStructure.NOT_AN_ARRAY || nameStringStructure.getDataElementCount() == 0)
		{
			throw new OpenDDLException("NameStructure's StringStructure child must not be an array structure and must contain at least on data element");
		}
	}

	public NameStructure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier, decoder);
	}

	public NameStructure()
	{
		super(NameStructure.IDENTIFIER);

		addStructure(new StringStructure());
	}

	public String getFullName()
	{
		final StringStructure nameStructure = (StringStructure) getStructure(0);
		return StringUtils.join(nameStructure, " ");
	}

	public void appendName(final String name)
	{
		final StringStructure stringStructure = (StringStructure) getStructure(0);
		stringStructure.add(name);
	}
}
