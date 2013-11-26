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


	private final String structureIdentifier;

	private String structureName;

	private RootStructure rootStructure;
	private NodeStructure parentStructure;

	protected abstract void validate(final RootStructure rootStructure) throws OpenDDLException;

	Structure(final String structureIdentifier, final String structureName)
	{
		if (structureIdentifier != null && !Decoder.isIdentifier(structureIdentifier, 0))
		{
			throw new IllegalStateException("'" + structureIdentifier + "' is not a valid identifier");
		}

		this.structureIdentifier = structureIdentifier;
		this.structureName = structureName;
	}

	Structure(final String structureIdentifier)
	{
		this(structureIdentifier, null);
	}

	abstract void encode(final StringBuilder stringBuilder, final int depth);

	String getInternalIdentifier()
	{
		return structureIdentifier;
	}

	void attach(final RootStructure rootStructure, final NodeStructure parentStructure) throws OpenDDLException
	{
		if (this.rootStructure != rootStructure)
		{
			detach();
		}

		this.rootStructure = rootStructure;
		this.parentStructure = parentStructure;

		if (this.rootStructure != null && isStructureNameGlobal())
		{
			rootStructure.addGlobal(this);
		}
	}

	void detach()
	{
		if (rootStructure != null && isStructureNameGlobal())
		{
			rootStructure.removeGlobal(this);
		}

		rootStructure = null;
		parentStructure = null;
	}

	public final String getStructureIdentifier()
	{
		return getInternalIdentifier();
	}

	public final RootStructure getRootStructure()
	{
		return rootStructure;
	}

	public final NodeStructure getParentStructure()
	{
		return parentStructure;
	}

	public final String getStructureName()
	{
		return structureName;
	}

	public final boolean isStructureNameGlobal()
	{
		return structureName != null && structureName.length() > 0 && structureName.charAt(0) == '$';
	}

	public final void setStructureName(final String structureName)
	{
		this.structureName = structureName;
	}

	public final String toString()
	{
		final StringBuilder stringBuilder = new StringBuilder();
		encode(stringBuilder, 0);
		return stringBuilder.toString();
	}
}
