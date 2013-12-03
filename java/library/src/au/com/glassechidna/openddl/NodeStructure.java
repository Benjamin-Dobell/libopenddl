package au.com.glassechidna.openddl;

import au.com.glassechidna.openddl.primitives.*;

import java.math.BigInteger;
import java.util.*;

public abstract class NodeStructure extends Structure implements Iterable<Structure>
{
	public interface Builder
	{
		NodeStructure build(String identifier, Decoder decoder) throws OpenDDLException;
	}


	private class StructureIterator implements Iterator<Structure>
	{
		private int index;

		@Override
		public boolean hasNext()
		{
			return index < structures.size();
		}

		@Override
		public Structure next()
		{
			return structures.get(index++);
		}

		@Override
		public void remove()
		{
			if (index > 0)
			{
				structures.remove(index - 1);
			}
			else
			{
				throw new IllegalStateException();
			}
		}
	}

	private class StructureIdentifierIterator implements Iterator<Structure>
	{
		private final String identifier;

		private Structure current;
		private Structure next;

		private Structure findNext()
		{
			for (int i = 0; i < structures.size(); ++i)
			{
				final Structure structure = structures.get(i);

				if (structure.getStructureIdentifier().equals(identifier))
				{
					return structure;
				}
			}

			return null;
		}

		public StructureIdentifierIterator(final String identifier)
		{
			this.identifier = identifier;

			next = findNext();
		}

		@Override
		public boolean hasNext()
		{
			return next != null;
		}

		@Override
		public Structure next()
		{
			current = this.next;
			next = findNext();
			return current;
		}

		@Override
		public void remove()
		{
			if (current != null)
			{
				structures.remove(current);
			}
			else
			{
				throw new IllegalStateException();
			}
		}
	}


	private final LinkedHashMap<String, String> properties = new LinkedHashMap<String, String>();

	private final ArrayList<Structure> structures = new ArrayList<Structure>();
	private final HashMap<String, Structure> namedStructures = new HashMap<String, Structure>();

	private void decodeProperties(final Decoder decoder) throws OpenDDLException
	{
		String token = decoder.nextToken();

		if (token == null)
		{
			throw new OpenDDLException("Encountered unexpected EOF whilst parsing property list in a '" + getStructureIdentifier() + "' structure");
		}

		if (!token.equals(")"))
		{
			for (;;)
			{
				if (!Decoder.isIdentifier(token, 0))
				{
					throw new OpenDDLException("Encountered invalid property name '" + token + "'");
				}

				final String propertyName = token;
				final String equalsToken = decoder.nextToken();

				if (equalsToken == null)
				{
					throw new OpenDDLException("Encountered unexpected EOF whilst parsing property list in a '" + getStructureIdentifier() + "' structure");
				}
				else if (!equalsToken.equals("="))
				{
					throw new OpenDDLException("Encountered unexpected token '" + equalsToken + "' where '=' was expected");
				}

				final String propertyValue = decoder.nextToken();

				if (propertyValue == null)
				{
					throw new OpenDDLException("Encountered unexpected EOF whilst parsing property list in a '" + getStructureIdentifier() + "' structure");
				}

				properties.put(propertyName, propertyValue);

				token = decoder.nextToken();

				if (token == null)
				{
					throw new OpenDDLException("Encountered unexpected EOF whilst parsing property list in a '" + getStructureIdentifier() + "' structure");
				}

				if (token.equals(","))
				{
					token = decoder.nextToken();
				}
				else if (token.equals(")"))
				{
					break;
				}
			}
		}
	}

	private void decodeSubstructures(final Decoder decoder) throws OpenDDLException
	{
		final int startLine = decoder.getLine();

		String token = decoder.nextToken();

		while (token != null && !token.equals("}"))
		{
			final Structure structure = decoder.parseStructure(this, token);
			final String name = structure.getStructureName();

			if (name != null)
			{
				final Structure duplicateNamedStructure = namedStructures.get(name);

				if (duplicateNamedStructure != null)
				{
					throw new OpenDDLException("Encountered duplicate local name '" + name + "'");
				}
			}

			structures.add(structure);

			structure.attach(getRootStructure(), this);

			final String structureName = structure.getStructureName();

			if (structureName != null)
			{
				final Structure duplicateNamedStructure = namedStructures.put(structureName, structure);

				if (duplicateNamedStructure != null)
				{
					throw new OpenDDLException("Attempted to add add a structure with name '" + structureName + "' when a structure with that name already exists");
				}
			}

			token = decoder.nextToken();
		}

		if (token == null)
		{
			throw new OpenDDLException("Encountered unexpected EOF whilst parsing '" + getStructureIdentifier() + "' structure's substructures which started on line " + startLine);
		}
	}

	protected NodeStructure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier);

		String token = decoder.nextToken();

		if (token == null)
		{
			throw new OpenDDLException("Encountered unexpected EOF whilst parsing a '" + getStructureIdentifier() + "' structure");
		}

		if (!token.equals("{") && !token.equals("("))
		{
			if (Decoder.isName(token, 0))
			{
				setStructureName(token);
			}
			else
			{
				throw new OpenDDLException("Encountered invalid name '" + token + "' whilst parsing a '" + getStructureIdentifier() + "' structure");
			}

			token = decoder.nextToken();

			if (token == null)
			{
				throw new OpenDDLException("Encountered unexpected EOF whilst parsing a '" + getStructureIdentifier() + "' structure");
			}
		}

		if (token.equals("("))
		{
			decodeProperties(decoder);

			token = decoder.nextToken();

			if (token == null)
			{
				throw new OpenDDLException("Encountered unexpected EOF whilst parsing a '" + getStructureIdentifier() + "' structure");
			}
		}

		if (token.equals("{"))
		{
			decodeSubstructures(decoder);
		}
		else
		{
			throw new OpenDDLException("Encountered invalid token '" + token + "' whilst parsing a '" + getStructureIdentifier() + "' structure");
		}
	}

	protected NodeStructure(final String identifier, final String name)
	{
		super(identifier, name);
	}

	protected NodeStructure(final String identifier)
	{
		super(identifier);
	}

	@Override
	protected void validate(final RootStructure rootStructure) throws OpenDDLException
	{
		for (final Structure structure : structures)
		{
			structure.validate(rootStructure);
		}

		for (final Map.Entry<String, String> property : properties.entrySet())
		{
			final String value = property.getValue();

			if (value.charAt(0) == '$' || value.charAt(0) == '%')
			{
				final Reference reference = new Reference(value);

				if (reference.isGlobal())
				{
					if (rootStructure.getStructure(reference) == null)
					{
						throw new OpenDDLException("Unable to resolve global reference " + reference);
					}
				}
				else
				{
					if (getParentStructure().getStructure(reference) == null)
					{
						throw new OpenDDLException("Unable to resolve local reference " + reference);
					}
				}
			}
		}
	}

	Structure getStructure(final Reference reference, final int componentIndex)
	{
		if (reference.isNull())
		{
			return null;
		}

		final Structure structure = namedStructures.get(reference.getComponent(componentIndex));

		if (componentIndex == reference.getComponentCount() - 1)
		{
			return structure;
		}
		else if (structure instanceof NodeStructure)
		{
			return ((NodeStructure) structure).getStructure(reference, componentIndex + 1);
		}
		else
		{
			return null;
		}
	}

	@Override
	void encode(final StringBuilder stringBuilder, final int depth)
	{
		stringBuilder.append(getStructureIdentifier());

		if (getStructureName() != null)
		{
			stringBuilder.append(' ');
			stringBuilder.append(getStructureName());
		}

		if (properties.size() > 0)
		{
			stringBuilder.append(" ( ");

			final Iterator<Map.Entry<String, String>> propertyIterator = properties.entrySet().iterator();

			for (;;)
			{
				final Map.Entry<String, String> entry = propertyIterator.next();

				stringBuilder.append(entry.getKey());
				stringBuilder.append(" = ");
				stringBuilder.append(entry.getValue());

				if (propertyIterator.hasNext())
				{
					stringBuilder.append(", ");
				}
				else
				{
					break;
				}
			}

			stringBuilder.append(" )");
		}

		final boolean compact;

		if (structures.size() == 1)
		{
			if (structures.get(0) instanceof PrimitiveStructure<?>)
			{
				final PrimitiveStructure<?> primitiveStructure = (PrimitiveStructure<?>) structures.get(0);
				compact = primitiveStructure.getArrayLength() == PrimitiveStructure.NOT_AN_ARRAY;
			}
			else
			{
				compact = false;
			}
		}
		else
		{
			compact = false;
		}

		if (compact)
		{
			stringBuilder.append(" { ");

			if (structures.size() > 0)
			{
				final Structure structure = structures.get(0);
				structure.encode(stringBuilder, depth);
			}

			stringBuilder.append(" }");
		}
		else
		{
			Structure.encodeNewLine(stringBuilder, depth);
			stringBuilder.append('{');

			if (structures.size() > 0)
			{
				final int structureDepth = depth + 1;
				final Iterator<Structure> structureIterator = iterator();

				for (;;)
				{
					final Structure structure = structureIterator.next();

					Structure.encodeNewLine(stringBuilder, structureDepth);
					structure.encode(stringBuilder, structureDepth);

					if (structureIterator.hasNext())
					{
						stringBuilder.append('\n');
					}
					else
					{
						break;
					}
				}
			}

			Structure.encodeNewLine(stringBuilder, depth);
			stringBuilder.append('}');
		}
	}

	public final Structure getStructure(final int index)
	{
		return structures.get(index);
	}

	public final int getStructureCount()
	{
		return structures.size();
	}

	public final Structure getStructure(final String name)
	{
		return namedStructures.get(name);
	}

	public final Structure getStructure(final Reference reference)
	{
		return getStructure(reference, 0);
	}

	public final Structure getFirstStructure(final String identifier)
	{
		for (final Structure structure : structures)
		{
			if (structure.getStructureIdentifier().equals(identifier))
			{
				return structure;
			}
		}

		return null;
	}

	public final Structure getLastStructure(final String identifier)
	{
		for (int i = structures.size() - 1; i >= 0; --i)
		{
			final Structure structure = structures.get(i);

			if (structure.getStructureIdentifier().equals(identifier))
			{
				return structure;
			}
		}

		return null;
	}

	public final Iterator<Structure> getStructures(final String identifier)
	{
		return new StructureIdentifierIterator(identifier);
	}

	public final void addStructure(final int index, final Structure structure)
	{
		structures.add(index, structure);

		try
		{
			structure.attach(getRootStructure(), this);
		}
		catch (final OpenDDLException e)
		{
			throw new IllegalStateException(e);
		}

		final String structureName = structure.getStructureName();

		if (structureName != null)
		{
			final Structure duplicateNamedStructure = namedStructures.put(structureName, structure);

			if (duplicateNamedStructure != null)
			{
				throw new IllegalStateException("Attempted to add add a structure with name '" + structureName + "' when a structure with that name already exists");
			}
		}
	}

	public final void addStructure(final Structure structure)
	{
		addStructure(structures.size(), structure);
	}

	public final Structure removeStructure(final int index)
	{
		final Structure structure = structures.remove(index);

		if (structure != null)
		{
			namedStructures.remove(structure.getStructureName());

			structure.detach();
		}

		return structure;
	}

	public final boolean removeStructure(final Structure structure)
	{
		final int index = structures.indexOf(structure);

		if (index > -1)
		{
			removeStructure(index);
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	void attach(final RootStructure rootStructure, final NodeStructure parentStructure) throws OpenDDLException
	{
		super.attach(rootStructure, parentStructure);

		for (final Structure structure : structures)
		{
			structure.attach(rootStructure, this);
		}
	}

	@Override
	void detach()
	{
		for (final Structure structure : structures)
		{
			structure.detach();
		}

		super.detach();
	}

	@Override
	public final Iterator<Structure> iterator()
	{
		return new StructureIterator();
	}

	public final Iterator<Map.Entry<String, String>> properties()
	{
		return properties.entrySet().iterator();
	}

	public final byte getInt8Property(final String key) throws OpenDDLException
	{
		return Decoder.decodeInt8(properties.get(key));
	}

	public final short getInt16Property(final String key) throws OpenDDLException
	{
		return Decoder.decodeInt16(properties.get(key));
	}

	public final int getInt32Property(final String key) throws OpenDDLException
	{
		return Decoder.decodeInt32(properties.get(key));
	}

	public final long getInt64Property(final String key) throws OpenDDLException
	{
		return Decoder.decodeInt64(properties.get(key));
	}

	public final short getUnsignedInt8Property(final String key) throws OpenDDLException
	{
		return Decoder.decodeUnsignedInt8(properties.get(key));
	}

	public final int getUnsignedInt16Property(final String key) throws OpenDDLException
	{
		return Decoder.decodeUnsignedInt16(properties.get(key));
	}

	public final long getUnsignedInt32Property(final String key) throws OpenDDLException
	{
		return Decoder.decodeUnsignedInt32(properties.get(key));
	}

	public final BigInteger getUnsignedInt64Property(final String key) throws OpenDDLException
	{
		return Decoder.decodeUnsignedInt64(properties.get(key));
	}

	public final float getFloatProperty(final String key) throws OpenDDLException
	{
		return Decoder.decodeFloat(properties.get(key));
	}

	public final double getDoubleProperty(final String key) throws OpenDDLException
	{
		return Decoder.decodeDouble(properties.get(key));
	}

	public final String getStringProperty(final String key) throws OpenDDLException
	{
		return Decoder.decodeString(properties.get(key));
	}

	public final Reference getReferenceProperty(final String key) throws OpenDDLException
	{
		return new Reference(properties.get(key));
	}

	public final int getTypeProperty(final String key) throws OpenDDLException
	{
		return Decoder.parseType(properties.get(key));
	}

	public Builder getBuilder(final String identifier)
	{
		return null;
	}

	public final void addInt8Property(final String key, final byte value, final int encoding)
	{
		final StringBuilder stringBuilder = new StringBuilder();
		LiteralEncoding.encodeByte(stringBuilder, value, encoding);
		properties.put(key, stringBuilder.toString());
	}

	public final void addInt16Property(final String key, final short value, final int encoding)
	{
		final StringBuilder stringBuilder = new StringBuilder();
		LiteralEncoding.encodeShort(stringBuilder, value, encoding);
		properties.put(key, stringBuilder.toString());
	}

	public final void addInt32Property(final String key, final int value, final int encoding)
	{
		final StringBuilder stringBuilder = new StringBuilder();
		LiteralEncoding.encodeInt(stringBuilder, value, encoding);
		properties.put(key, stringBuilder.toString());
	}

	public final void addInt64Property(final String key, final long value, final int encoding)
	{
		final StringBuilder stringBuilder = new StringBuilder();
		LiteralEncoding.encodeLong(stringBuilder, value, encoding);
		properties.put(key, stringBuilder.toString());
	}

	public final void addUnsignedInt8Property(final String key, final short value, final int encoding)
	{
		if (value < 0 || value > UnsignedInt8Structure.MAX)
		{
			throw new IllegalArgumentException("value must be in the range [0, 255]");
		}

		final StringBuilder stringBuilder = new StringBuilder();
		LiteralEncoding.encodeShort(stringBuilder, value, encoding);
		properties.put(key, stringBuilder.toString());
	}

	public final void addUnsignedInt16Property(final String key, final int value, final int encoding)
	{
		if (value < 0 || value > UnsignedInt16Structure.MAX)
		{
			throw new IllegalArgumentException("value must be in the range [0, 65536]");
		}

		final StringBuilder stringBuilder = new StringBuilder();
		LiteralEncoding.encodeInt(stringBuilder, value, encoding);
		properties.put(key, stringBuilder.toString());
	}

	public final void addUnsignedInt32Property(final String key, final long value, final int encoding)
	{
		if (value < 0 || value > UnsignedInt32Structure.MAX)
		{
			throw new IllegalArgumentException("value must be in the range [0, 4294967296]");
		}

		final StringBuilder stringBuilder = new StringBuilder();
		LiteralEncoding.encodeLong(stringBuilder, value, encoding);
		properties.put(key, stringBuilder.toString());
	}

	public final void addUnsignedInt64Property(final String key, final BigInteger value, final int encoding)
	{
		if (value.compareTo(BigInteger.ZERO) < 0 || value.compareTo(UnsignedInt64Structure.MAX) > 0)
		{
			throw new IllegalArgumentException("value must be in the range [0, 18446744073709551615]");
		}

		final StringBuilder stringBuilder = new StringBuilder();
		LiteralEncoding.encodeBigInteger(stringBuilder, value, encoding);
		properties.put(key, stringBuilder.toString());
	}

	public final void addFloatProperty(final String key, final float value, final int encoding)
	{
		final StringBuilder stringBuilder = new StringBuilder();
		LiteralEncoding.encodeFloat(stringBuilder, value, encoding);
		properties.put(key, stringBuilder.toString());
	}

	public final void addDoubleProperty(final String key, final double value, final int encoding)
	{
		final StringBuilder stringBuilder = new StringBuilder();
		LiteralEncoding.encodeDouble(stringBuilder, value, encoding);
		properties.put(key, stringBuilder.toString());
	}

	public final void addStringProperty(final String key, final String value)
	{
		final StringBuilder stringBuilder = new StringBuilder();
		LiteralEncoding.encodeString(stringBuilder, value);
		properties.put(key, stringBuilder.toString());
	}

	public final void addReferenceProperty(final String key, final Reference value)
	{
		properties.put(key, value.toString());
	}

	public final void addTypeProperty(final String key, final int type)
	{
		properties.put(key, PrimitiveType.IDENTIFIERS[type]);
	}

	public final void removeProperty(final String key)
	{
		properties.remove(key);
	}
}
