package au.com.glassechidna.openddl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class PrimitiveStructure<T> extends Structure implements Collection<T>
{
	public static final int NOT_AN_ARRAY = -1;


	private final ArrayList<T> dataElements = new ArrayList<T>();
	private final int arrayLength;

	private int arrayCount;

	private void decodeDataList(final Decoder decoder) throws OpenDDLException
	{
		String token = decoder.nextToken();

		while (token != null && !token.equals("}"))
		{
			dataElements.add(decodeDataElement(token));

			token = decoder.nextToken();

			if (token != null && token.equals(","))
			{
				token = decoder.nextToken();
			}
		}

		if (token == null)
		{
			throw new OpenDDLException("Encountered unexpected EOF whilst parsing property list in a primitive structure");
		}
	}

	private void decodeArrayList(final Decoder decoder) throws OpenDDLException
	{
		String token = decoder.nextToken();

		if (token == null)
		{
			throw new OpenDDLException("Encountered unexpected EOF whilst parsing a primitive structure");
		}

		while (token != null && !token.equals("}"))
		{
			if (!token.equals("{"))
			{
				throw new OpenDDLException("Expected '{' but encountered '" + token + "' whilst parsing primitive structure");
			}

			for (int i = 0; i < arrayLength - 1; i++)
			{
				token = decoder.nextToken();

				if (token == null)
				{
					throw new OpenDDLException("Encountered unexpected EOF whilst parsing data element arrays in a primitive structure");
				}

				dataElements.add(decodeDataElement(token));

				token = decoder.nextToken();

				if (token == null)
				{
					throw new OpenDDLException("Encountered unexpected EOF whilst parsing data element arrays in a primitive structure");
				}

				if (!token.equals(","))
				{
					throw new OpenDDLException("Expected ',' but encountered '" + token + "' whilst parsing primitive structure");
				}
			}

			token = decoder.nextToken();

			if (token == null)
			{
				throw new OpenDDLException("Encountered unexpected EOF whilst parsing data element arrays in a primitive structure");
			}

			dataElements.add(decodeDataElement(token));

			token = decoder.nextToken();

			if (token == null)
			{
				throw new OpenDDLException("Encountered unexpected EOF whilst parsing a primitive structure");
			}

			if (!token.equals("}"))
			{
				throw new OpenDDLException("Expected '}' but encountered '" + token + "' whilst parsing primitive structure");
			}

			token = decoder.nextToken();

			if (token != null && token.equals(","))
			{
				token = decoder.nextToken();
			}
		}

		if (token == null)
		{
			throw new OpenDDLException("Encountered unexpected EOF whilst parsing data element arrays in a primitive structure");
		}
	}

	protected PrimitiveStructure(final String identifier, final Decoder decoder) throws OpenDDLException
	{
		super(identifier);

		String token = decoder.nextToken();

		if (token == null)
		{
			throw new OpenDDLException("Encountered unexpected EOF whilst parsing a primitive structure");
		}

		if (token.equals("["))
		{
			token = decoder.nextToken();

			arrayLength = Decoder.decodeInt32(token);

			if (arrayLength <= 0)
			{
				throw new OpenDDLException("Encountered invalid array length '" + arrayLength + "' whilst parsing a primitive structure");
			}

			token = decoder.nextToken();

			if (token == null)
			{
				throw new OpenDDLException("Encountered unexpected EOF whilst parsing a primitive structure");
			}

			if (!token.equals("]"))
			{
				throw new OpenDDLException("Expected ']' but encountered '" + token + "' whilst parsing primitive structure");
			}

			token = decoder.nextToken();

			if (token == null)
			{
				throw new OpenDDLException("Encountered unexpected EOF whilst parsing a primitive structure");
			}
		}
		else
		{
			arrayLength = PrimitiveStructure.NOT_AN_ARRAY;
		}

		if (!token.equals("{"))
		{
			if (!Decoder.isName(token, 0))
			{
				throw new OpenDDLException("Encountered invalid name '" + token + "' whilst parsing primitive structure");
			}

			setStructureName(token);

			token = decoder.nextToken();

			if (token == null)
			{
				throw new OpenDDLException("Encountered unexpected EOF whilst parsing a primitive structure");
			}
		}

		if (!token.equals("{"))
		{
			throw new OpenDDLException("Expected '{' but encountered '" + token + "' whilst parsing primitive structure");
		}

		if (arrayLength == PrimitiveStructure.NOT_AN_ARRAY)
		{
			decodeDataList(decoder);

			arrayCount = PrimitiveStructure.NOT_AN_ARRAY;
		}
		else
		{
			decodeArrayList(decoder);

			arrayCount = dataElements.size() / arrayLength;
		}
	}

	protected PrimitiveStructure(final String identifier, final String name, final int arrayLength)
	{
		super(identifier, name);

		this.arrayLength = arrayLength;
	}

	protected PrimitiveStructure(final String identifier, final String name)
	{
		this(identifier, name, PrimitiveStructure.NOT_AN_ARRAY);
	}

	protected PrimitiveStructure(final String identifier, final int arrayLength)
	{
		this(identifier, null, arrayLength);
	}

	protected PrimitiveStructure(final String identifier)
	{
		this(identifier, null, PrimitiveStructure.NOT_AN_ARRAY);
	}

	@Override
	protected void validate(final RootStructure rootStructure) throws OpenDDLException
	{
	}

	protected abstract T decodeDataElement(final String token) throws OpenDDLException;
	protected abstract void encodeDataElement(final StringBuilder stringBuilder, final T dataElement);

	@Override
	void encode(final StringBuilder stringBuilder, final int depth)
	{
		stringBuilder.append(getStructureIdentifier());

		if (arrayLength != PrimitiveStructure.NOT_AN_ARRAY)
		{
			stringBuilder.append('[');
			stringBuilder.append(arrayLength);
			stringBuilder.append(']');
		}

		if (getStructureName() != null)
		{
			stringBuilder.append(' ');
			stringBuilder.append(getStructureName());
		}

		final Iterator<T> dataElementIterator = dataElements.iterator();

		if (arrayLength == PrimitiveStructure.NOT_AN_ARRAY)
		{
			stringBuilder.append(" { ");

			if (dataElementIterator.hasNext())
			{
				for (;;)
				{
					encodeDataElement(stringBuilder, dataElementIterator.next());

					if (dataElementIterator.hasNext())
					{
						stringBuilder.append(", ");
					}
					else
					{
						break;
					}
				}
			}

			stringBuilder.append(" }");
		}
		else
		{
			Structure.encodeNewLine(stringBuilder, depth);
			stringBuilder.append("{");

			if (dataElementIterator.hasNext())
			{
				final int arrayDepth = depth + 1;

				for (;;)
				{
					Structure.encodeNewLine(stringBuilder, arrayDepth);
					stringBuilder.append("{ ");

					final int lastArrayIndex = arrayLength - 1;

					for (int i = 0; i < lastArrayIndex; ++i)
					{
						encodeDataElement(stringBuilder, dataElementIterator.next());
						stringBuilder.append(", ");
					}

					encodeDataElement(stringBuilder, dataElementIterator.next());
					stringBuilder.append(" }");

					if (dataElementIterator.hasNext())
					{
						stringBuilder.append(',');
					}
					else
					{
						break;
					}
				}
			}

			Structure.encodeNewLine(stringBuilder, depth);
			stringBuilder.append("}");
		}
	}

	public final int getDataElementCount()
	{
		return dataElements.size();
	}

	public final T getDataElement(final int index)
	{
		return dataElements.get(index);
	}

	public final int getArrayLength()
	{
		return arrayLength;
	}

	public final int getArrayCount()
	{
		return arrayCount;
	}

	public final List<T> getArray(final int arrayIndex)
	{
		if (arrayLength == PrimitiveStructure.NOT_AN_ARRAY)
		{
			throw new UnsupportedOperationException("Primitive structure is not an array");
		}

		if (arrayIndex > arrayCount)
		{
			throw new IndexOutOfBoundsException("arrayIndex '" + arrayIndex + "' is out of bounds for a primitive structure containing " + arrayCount + " inner arrays");
		}

		return dataElements.subList(arrayIndex * arrayLength, (arrayIndex + 1) * arrayLength);
	}

	public final T getArrayDataElement(final int arrayIndex, final int elementIndex)
	{
		if (arrayLength == PrimitiveStructure.NOT_AN_ARRAY)
		{
			throw new UnsupportedOperationException("Primitive structure is not an array");
		}

		if (arrayIndex > arrayCount)
		{
			throw new IndexOutOfBoundsException("arrayIndex '" + arrayIndex + "' is out of bounds for a primitive structure containing " + arrayCount + " inner arrays");
		}

		if (elementIndex > arrayLength)
		{
			throw new IndexOutOfBoundsException("elementIndex '" + elementIndex + "' is out of bounds for a primitive structure whose inner arrays are of length " + arrayLength);
		}

		return dataElements.get(arrayIndex * arrayLength + elementIndex);
	}

	public boolean addArray(final Collection<T> array)
	{
		if (arrayLength == PrimitiveStructure.NOT_AN_ARRAY)
		{
			throw new UnsupportedOperationException("PrimitiveStructure is not an array structure");
		}

		if (arrayLength != array.size())
		{
			throw new IllegalArgumentException("Cannot add an array whose size does not match the PrimitiveStructure's array length");
		}

		++arrayCount;

		return dataElements.addAll(array);
	}

	public boolean addArray(final int index, final Collection<T> array)
	{
		if (arrayLength == PrimitiveStructure.NOT_AN_ARRAY)
		{
			throw new UnsupportedOperationException("PrimitiveStructure is not an array structure");
		}

		if (arrayLength != array.size())
		{
			throw new IllegalArgumentException("Cannot add an array whose size does not match the PrimitiveStructure's array length");
		}

		++arrayCount;

		return dataElements.addAll(index * arrayLength, array);
	}

	public void removeArray(final int arrayIndex)
	{
		if (arrayLength == PrimitiveStructure.NOT_AN_ARRAY)
		{
			throw new UnsupportedOperationException("PrimitiveStructure is not an array structure");
		}

		if (arrayIndex > arrayCount)
		{
			throw new IndexOutOfBoundsException("arrayIndex '" + arrayIndex + "' is out of bounds for a primitive structure containing " + arrayCount + " inner arrays");
		}

		final int index = arrayIndex * arrayLength;

		for (int i = 0; i < arrayLength; ++i)
		{
			dataElements.remove(index);
		}

		--arrayCount;
	}

	public void addArrays(final Collection<Collection<? extends T>> arrays)
	{
		if (arrayLength != PrimitiveStructure.NOT_AN_ARRAY)
		{
			throw new UnsupportedOperationException("PrimitiveStructure is an array structure, you cannot add individual data elements");
		}

		for (final Collection<? extends T> array : arrays)
		{
			if (arrayLength != array.size())
			{
				throw new IllegalArgumentException("Cannot add an array whose does size not match the PrimitiveStructure's array length");
			}
		}

		for (final Collection<? extends T> array : arrays)
		{
			dataElements.addAll(array);
		}

		arrayCount += arrays.size();
	}

	public void set(final T value)
	{
		if (arrayLength != PrimitiveStructure.NOT_AN_ARRAY)
		{
			throw new UnsupportedOperationException("PrimitiveStructure is an array structure, you cannot add individual data elements");
		}

		dataElements.clear();
		dataElements.add(value);
	}

	// Collection<T>

	@Override
	public int size()
	{
		return dataElements.size();
	}

	@Override
	public boolean isEmpty()
	{
		return dataElements.isEmpty();
	}

	@Override
	public boolean contains(final Object o)
	{
		return dataElements.contains(o);
	}

	@Override
	public Iterator<T> iterator()
	{
		return dataElements.iterator();
	}

	@Override
	public Object[] toArray()
	{
		return dataElements.toArray();
	}

	@Override
	public <T1> T1[] toArray(final T1[] a)
	{
		return dataElements.toArray(a);
	}

	@Override
	public boolean add(final T t)
	{
		if (arrayLength != PrimitiveStructure.NOT_AN_ARRAY)
		{
			throw new UnsupportedOperationException("PrimitiveStructure is not an array structure");
		}

		return dataElements.add(t);
	}

	@Override
	public boolean remove(final Object o)
	{
		if (arrayLength != PrimitiveStructure.NOT_AN_ARRAY)
		{
			throw new UnsupportedOperationException("PrimitiveStructure is an array structure");
		}

		return dataElements.remove(o);
	}

	@Override
	public boolean containsAll(final Collection<?> c)
	{
		return dataElements.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends T> c)
	{
		if (arrayLength != PrimitiveStructure.NOT_AN_ARRAY)
		{
			throw new UnsupportedOperationException("PrimitiveStructure is an array structure");
		}

		return dataElements.addAll(c);
	}

	@Override
	public boolean removeAll(final Collection<?> c)
	{
		if (arrayLength != PrimitiveStructure.NOT_AN_ARRAY)
		{
			throw new UnsupportedOperationException("PrimitiveStructure is an array structure");
		}

		return dataElements.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c)
	{
		if (arrayLength != PrimitiveStructure.NOT_AN_ARRAY)
		{
			throw new UnsupportedOperationException("PrimitiveStructure is an array structure");
		}

		return dataElements.retainAll(c);
	}

	@Override
	public void clear()
	{
		dataElements.clear();
	}
}
