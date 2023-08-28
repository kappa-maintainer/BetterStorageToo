package io.github.tehstoneman.betterstorage.api;

import net.minecraft.util.StringRepresentable;

/**
 * The type of connection used to connect multi-block containers
 *
 * @author TehStoneMan
 */
public enum ConnectedType implements StringRepresentable
{
	SINGLE( "single", 0 ),
	MASTER( "master", 2 ),
	SLAVE( "slave", 1 ),
	PILE( "pile", 3 );

	public static final ConnectedType[] VALUES = values();
	private final String                name;
	private final int                   opposite;

	ConnectedType( String name, int oppositeIn )
	{
		this.name = name;
		opposite  = oppositeIn;
	}

	public String getName()
	{
		return name;
	}

	public ConnectedType opposite()
	{
		return VALUES[opposite];
	}

	@Override
	public String getSerializedName()
	{
		return getName();
	}
}
