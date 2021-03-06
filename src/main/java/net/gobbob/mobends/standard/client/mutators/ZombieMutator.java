package net.gobbob.mobends.standard.client.mutators;

import net.gobbob.mobends.core.data.IEntityDataFactory;
import net.gobbob.mobends.standard.data.ZombieData;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.entity.monster.EntityZombie;

public class ZombieMutator extends ZombieMutatorBase<ZombieData, EntityZombie, ModelZombie>
{

	public ZombieMutator(IEntityDataFactory<EntityZombie> dataFactory)
	{
		super(dataFactory);
	}
	
	@Override
	public void storeVanillaModel(ModelZombie model)
	{
		ModelZombie vanillaModel = new ModelZombie(0.0F, this.halfTexture);
		this.vanillaModel = vanillaModel;
		
		// Calling the super method here, since it
		// requires the vanillaModel property to be
		// set.
		super.storeVanillaModel(model);
	}
	
	@Override
	public boolean isModelEligible(ModelBase model)
	{
		return model instanceof ModelZombie;
	}
}
