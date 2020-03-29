package goblinbob.mobends.standard.animation.controller;

import goblinbob.mobends.core.animation.bit.AnimationBit;
import goblinbob.mobends.core.animation.bit.KeyframeAnimationBit;
import goblinbob.mobends.core.animation.controller.IAnimationController;
import goblinbob.mobends.core.animation.keyframe.ArmatureMask;
import goblinbob.mobends.core.animation.layer.HardAnimationLayer;
import goblinbob.mobends.core.animation.layer.KeyframeAnimationLayer;
import goblinbob.mobends.standard.animation.bit.biped.*;
import goblinbob.mobends.standard.animation.bit.player.*;
import goblinbob.mobends.standard.animation.bit.player.SprintAnimationBit;
import goblinbob.mobends.standard.animation.bit.player.WalkAnimationBit;
import goblinbob.mobends.standard.data.BipedEntityData;
import goblinbob.mobends.standard.data.PlayerData;
import goblinbob.mobends.standard.animation.bit.biped.BowAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.EatingAnimationBit;
import goblinbob.mobends.standard.animation.bit.biped.FallingAnimationBit;
import goblinbob.mobends.standard.animation.bit.player.FlyingAnimationBit;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.*;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This is an animation controller for a player instance.
 * It's a part of the EntityData structure.
 */
public class PlayerController implements IAnimationController<PlayerData>
{
	
	protected HardAnimationLayer<BipedEntityData<?>> layerBase = new HardAnimationLayer<>();
	protected HardAnimationLayer<BipedEntityData<?>> layerTorch = new HardAnimationLayer<>();
	protected HardAnimationLayer<BipedEntityData<?>> layerSneak = new HardAnimationLayer<>();
	protected HardAnimationLayer<BipedEntityData<?>> layerAction = new HardAnimationLayer<>();
	protected HardAnimationLayer<BipedEntityData<?>> layerCape = new HardAnimationLayer<>();
	protected KeyframeAnimationLayer<PlayerData> layerKeyframe = new KeyframeAnimationLayer<>();

	protected AnimationBit<BipedEntityData<?>> bitStand = new StandAnimationBit<>();
	protected AnimationBit<BipedEntityData<?>> bitJump = new JumpAnimationBit<>();
	protected AnimationBit<BipedEntityData<?>> bitSneak = new SneakAnimationBit();
	protected AnimationBit<BipedEntityData<?>> bitLadderClimb = new LadderClimbAnimationBit();
	protected AnimationBit<BipedEntityData<?>> bitSwimming = new SwimmingAnimationBit();
	protected AnimationBit<BipedEntityData<?>> bitRiding = new RidingAnimationBit();
	protected AnimationBit<BipedEntityData<?>> bitSitting = new SittingAnimationBit();
	protected AnimationBit<BipedEntityData<?>> bitFalling = new FallingAnimationBit();
	protected AnimationBit<PlayerData> bitWalk = new WalkAnimationBit();
	protected AnimationBit<PlayerData> bitSprint = new SprintAnimationBit();
	protected AnimationBit<PlayerData> bitSprintJump = new SprintJumpAnimationBit();
	protected AnimationBit<BipedEntityData<?>> bitTorchHolding = new TorchHoldingAnimationBit();
	protected AnimationBit<PlayerData> bitAttack = new AttackAnimationBit();
	protected FlyingAnimationBit bitFlying = new FlyingAnimationBit();
	protected BowAnimationBit bitBow = new BowAnimationBit();
	protected EatingAnimationBit bitEating = new EatingAnimationBit();
	protected KeyframeAnimationBit<BipedEntityData<?>> bitBreaking = new BreakingAnimationBit(1.2F);
	protected CapeAnimationBit bitCape = new CapeAnimationBit();

	protected ArmatureMask upperBodyOnlyMask;

	public PlayerController()
	{
		this.upperBodyOnlyMask = new ArmatureMask(ArmatureMask.Mode.EXCLUDE_ONLY);
		this.upperBodyOnlyMask.exclude("root");
		this.upperBodyOnlyMask.exclude("head");
		this.upperBodyOnlyMask.exclude("leftLeg");
		this.upperBodyOnlyMask.exclude("leftForeLeg");
		this.upperBodyOnlyMask.exclude("rightLeg");
		this.upperBodyOnlyMask.exclude("rightForeLeg");
	}

	public static boolean shouldPerformBreaking(PlayerData data)
	{
		final Item item = data.getEntity().getHeldItemMainhand().getItem();

		return item instanceof ItemPickaxe || item instanceof ItemAxe;
	}

	@Override
	public Collection<String> perform(PlayerData data)
	{
		final AbstractClientPlayer player = data.getEntity();
		final EnumHandSide primaryHand = player.getPrimaryHand();
		final EnumHandSide offHand = primaryHand == EnumHandSide.RIGHT ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
		final ItemStack heldItemMainhand = player.getHeldItemMainhand();
		final ItemStack heldItemOffhand = player.getHeldItemOffhand();
		final ItemStack activeStack = player.getActiveItemStack();
		ModelBiped.ArmPose armPoseMain = getAction(player, heldItemMainhand);
		ModelBiped.ArmPose armPoseOff = getAction(player, heldItemOffhand);
		final EnumHand activeHand = player.getActiveHand();
		final EnumHandSide activeHandSide = activeHand == EnumHand.MAIN_HAND ? primaryHand : offHand;

		layerCape.playOrContinueBit(bitCape, data);

		if (player.isRiding())
		{
			if (player.getRidingEntity() instanceof EntityLivingBase)
			{
				layerBase.playOrContinueBit(bitRiding, data);
			}
			else
			{
				layerBase.playOrContinueBit(bitSitting, data);
			}
			layerSneak.clearAnimation();
			bitBreaking.setMask(upperBodyOnlyMask);
		}
		else
		{
			if (data.isClimbing())
			{
				layerBase.playOrContinueBit(bitLadderClimb, data);
				layerSneak.clearAnimation();
				layerTorch.clearAnimation();
				bitBreaking.setMask(upperBodyOnlyMask);
			}
			else if (player.isInWater())
			{
				layerBase.playOrContinueBit(bitSwimming, data);
				layerSneak.clearAnimation();
				layerTorch.clearAnimation();
				bitBreaking.setMask(upperBodyOnlyMask);
			}
			else if (!data.isOnGround() || data.getTicksAfterTouchdown() < 1)
			{
				// Airborne
				if (data.isFlying())
				{
					// Flying
					layerBase.playOrContinueBit(bitFlying, data);
				}
				else
				{
					if (data.getTicksFalling() > FallingAnimationBit.TICKS_BEFORE_FALLING)
					{
						layerBase.playOrContinueBit(bitFalling, data);
					}
					else
					{
						if (player.isSprinting())
							layerBase.playOrContinueBit(bitSprintJump, data);
						else
							layerBase.playOrContinueBit(bitJump, data);
					}
				}
				layerSneak.clearAnimation();
				layerTorch.clearAnimation();
				bitBreaking.setMask(upperBodyOnlyMask);
			}
			else
			{
				if (data.isStillHorizontally())
				{
					layerBase.playOrContinueBit(bitStand, data);
					layerTorch.playOrContinueBit(bitTorchHolding, data);
					bitBreaking.setMask(null);
				}
				else
				{
					if (player.isSprinting())
					{
						layerBase.playOrContinueBit(bitSprint, data);
						layerTorch.clearAnimation();
					}
					else
					{
						layerBase.playOrContinueBit(bitWalk, data);
						layerTorch.playOrContinueBit(bitTorchHolding, data);
					}
					bitBreaking.setMask(upperBodyOnlyMask);
				}

				if (player.isSneaking())
					layerSneak.playOrContinueBit(bitSneak, data);
				else
					layerSneak.clearAnimation();
			}
		}

		/*
		 * ACTIONS
		 */
		if (activeStack.getItem() instanceof ItemFood)
		{
			bitEating.setActionHand(activeHandSide);
			layerAction.playOrContinueBit(bitEating, data);
		}
		else
		{
			if (armPoseMain == ArmPose.BOW_AND_ARROW || armPoseOff == ArmPose.BOW_AND_ARROW)
			{
				bitBow.setActionHand(armPoseMain == ArmPose.BOW_AND_ARROW ? primaryHand : offHand);
				layerAction.playOrContinueBit(bitBow, data);
			}
			else if (heldItemMainhand.getItem() instanceof ItemPickaxe || heldItemMainhand.getItem() instanceof ItemAxe)
			{
				if (player.isSwingInProgress)
					layerAction.playOrContinueBit(bitBreaking, data);
				else
					layerAction.clearAnimation();
			}
			else
			{
				layerAction.playOrContinueBit(bitAttack, data);
			}
		}

		final List<String> actions = new ArrayList<>();
		layerBase.perform(data, actions);
		layerSneak.perform(data, actions);
		layerTorch.perform(data, actions);
		layerAction.perform(data, actions);
		layerCape.perform(data, actions);
		layerKeyframe.perform(data, actions);
		return actions;
	}

	private ArmPose getAction(AbstractClientPlayer player, ItemStack heldItem)
	{
		if (!heldItem.isEmpty())
		{
			if (player.getItemInUseCount() > 0)
			{
				EnumAction enumaction = heldItem.getItemUseAction();

				if (enumaction == EnumAction.BLOCK)
					return ArmPose.BLOCK;
				else if (enumaction == EnumAction.BOW)
					return ArmPose.BOW_AND_ARROW;
			}

			return ArmPose.ITEM;
		}

		return ArmPose.EMPTY;
	}
	
}