package net.gobbob.mobends.standard;

import net.gobbob.mobends.core.animatedentity.AlterEntry;
import net.gobbob.mobends.core.data.LivingEntityData;
import net.gobbob.mobends.standard.previewer.PlayerPreviewer;
import net.minecraft.client.entity.AbstractClientPlayer;

public class PlayerAlterEntry extends AlterEntry<AbstractClientPlayer>
{

	public PlayerAlterEntry(PlayerPreviewer previewer)
	{
		super(previewer);
	}
	
	@Override
	public LivingEntityData<AbstractClientPlayer> getDataForPreview()
	{
		return PlayerPreviewer.getPreviewData();
	}
	
}
