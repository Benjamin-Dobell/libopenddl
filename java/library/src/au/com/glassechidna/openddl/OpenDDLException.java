package au.com.glassechidna.openddl;

public class OpenDDLException extends Exception
{
	public OpenDDLException(final String s)
	{
		super(s);
	}
	public OpenDDLException(final Throwable throwable)
	{
		super(throwable);
	}

	public OpenDDLException(final String s, final Throwable throwable)
	{
		super(s, throwable);
	}
}
