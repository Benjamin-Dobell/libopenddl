package au.com.glassechidna.openddl;

import au.com.glassechidna.openddl.primitives.*;

public final class PrimitiveType
{
	public static final int BOOL = 0; // A boolean type that can have the value true or false.
	public static final int INT8 = 1; // An 8-bit signed integer that can have values in the range [−128, 127].
	public static final int INT16 = 2; // A 16-bit signed integer that can have values in the range [−32768, 32767].
	public static final int INT32 = 3; // A 32-bit signed integer that can have values in the range [−2147483648, 2147483647].
	public static final int INT64 = 4; // A 64-bit signed integer that can have values in the range [−9223372036854775808, 9223372036854775807].
	public static final int UNSIGNED_INT8 = 5; // An 8-bit unsigned integer that can have values in the range [0, 255].
	public static final int UNSIGNED_INT16 = 6; // A 16-bit unsigned integer that can have values in the range [0, 65535].
	public static final int UNSIGNED_INT32 = 7; // A 32-bit unsigned integer that can have values in the range [0, 4294967295].
	public static final int UNSIGNED_INT64 = 8; // A 64-bit unsigned integer that can have values in the range [0, 18446744073709551615].
	public static final int FLOAT = 9; // A 32-bit floating-point type conforming to the standard S1E8M23 format.
	public static final int DOUBLE = 10; // A 64-bit floating-point type conforming to the standard S1E11M52 format.
	public static final int STRING = 11; // A double-quoted character string with contents encoded in UTF-8.
	public static final int REF = 12; // A sequence of structure names, or the keyword null.
	public static final int TYPE = 13; // A type whose values are identifiers naming types as listed in IDENTIFIERS

	public static final String[] IDENTIFIERS = {
		BoolStructure.IDENTIFIER,
		Int8Structure.IDENTIFIER,
		Int16Structure.IDENTIFIER,
		Int32Structure.IDENTIFIER,
		Int64Structure.IDENTIFIER,
		UnsignedInt8Structure.IDENTIFIER,
		UnsignedInt16Structure.IDENTIFIER,
		UnsignedInt32Structure.IDENTIFIER,
		UnsignedInt64Structure.IDENTIFIER,
		FloatStructure.IDENTIFIER,
		DoubleStructure.IDENTIFIER,
		StringStructure.IDENTIFIER,
		ReferenceStructure.IDENTIFIER,
		TypeStructure.IDENTIFIER
	};
}
