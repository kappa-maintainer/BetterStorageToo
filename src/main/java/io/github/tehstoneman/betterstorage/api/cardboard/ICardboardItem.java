package io.github.tehstoneman.betterstorage.api.cardboard;

import io.github.tehstoneman.betterstorage.api.IDyeableItem;

/**
 * Interface to describe a cardboard item
 *
 * @author TehStoneMan
 */
public interface ICardboardItem extends IDyeableItem
{
	@Override
	default int getDefaultColor()
	{
		return 0xA08060;
	}
}
