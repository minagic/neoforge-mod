package com.minagic.minagic.entity.sorcerer.voidbourne;

import com.minagic.minagic.Minagic;
import com.minagic.minagic.api.SpellcastingItem;
import com.minagic.minagic.capabilities.PlayerClass;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerClassEnum;
import com.minagic.minagic.capabilities.MagicClassEnums.PlayerSubClassEnum;
import com.minagic.minagic.capabilities.SimulacraAttachment;
import com.minagic.minagic.registries.ModAttachments;
import com.minagic.minagic.registries.ModItems;
import com.minagic.minagic.sorcerer.StaffData;
import com.minagic.minagic.sorcerer.sorcererStaff;
import com.minagic.minagic.sorcerer.spells.VoidBlast;
import com.minagic.minagic.sorcerer.voidbourne.spells.KineticNullificationField;
import com.minagic.minagic.spellCasting.SpellCastContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class VoidborneSorcererEntity extends Monster implements ItemSupplier {
    private static final ResourceLocation KNF_ID = ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "kinetic_nullification_field");
    private static final ResourceLocation VOIDBLAST_ID = ResourceLocation.fromNamespaceAndPath(Minagic.MODID, "void_blast");

    public VoidborneSorcererEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setGlowingTag(true);
        this.xpReward = 20;
        // a class for free!
        PlayerClass pc = this.getData(ModAttachments.PLAYER_CLASS);
        pc.setMainClass(PlayerClassEnum.SORCERER);
        pc.setSubclassLevel(PlayerSubClassEnum.SORCERER_VOIDBOURNE, 10);
        this.setData(ModAttachments.PLAYER_CLASS, pc);

        // standard issue staff

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, 4.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 12.0f));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide()) {
            LivingEntity target = this.getTarget();
            ItemStack stack = this.getMainHandItem();
            Minagic.LOGGER.trace("Voidborne sorcerer target: {}", target);
            Minagic.LOGGER.trace("Voidborne sorcerer main-hand item: {}", stack.getItem());
            if (!(stack.getItem() instanceof sorcererStaff staffItem)) {
                stack = new ItemStack(ModItems.SORCERER_STAFF.asItem());
                ((sorcererStaff) stack.getItem()).writeSpell(stack, this.level(), this, 1, new KineticNullificationField());
                ((sorcererStaff) stack.getItem()).writeSpell(stack, this.level(), this, 0, new VoidBlast());
                this.setItemInHand(InteractionHand.MAIN_HAND, stack);
                Minagic.LOGGER.debug("Voidborne sorcerer equipped default staff: {}", this.getMainHandItem().getItem());
            }

            sorcererStaff staffItem = (sorcererStaff) stack.getItem();
            //ResourceLocation activeId = (staffItem.getData(stack).getActive().getSpellId());
            SimulacraAttachment sim = this.getData(ModAttachments.PLAYER_SIMULACRA);
            boolean knfActive = sim.hasSpell(KNF_ID);

            // === Hostile Projectile Nearby? ===
            boolean hostileProjectile = !level().getEntities(this, getBoundingBox().inflate(5), e ->
                    e instanceof Projectile proj && proj.getOwner() != this
            ).isEmpty();

            if (hostileProjectile && !knfActive) {
                cycleToSpell(staffItem, stack, KNF_ID);
                tryCastSpell(staffItem, stack);
                return;
            }

            // === Player Target Detected ===

            if (target != null && this.hasLineOfSight(target)) {
                cycleToSpell(staffItem, stack, VOIDBLAST_ID);
                tryCastSpell(staffItem, stack);

            }

        }
    }

    private void cycleToSpell(SpellcastingItem<StaffData> item, ItemStack stack, ResourceLocation desired) {

        int maxTries = 5;
        for (int i = 0; i < maxTries; i++) {

            if (Objects.equals(item.getActive(stack).getSpellId(), desired))
                return;
            item.cycleSlotUp(this, stack);
        }
    }

    private void tryCastSpell(sorcererStaff item, ItemStack stack) {
        if (item.getRemainingCooldown(stack, this) <= 0) {
            SpellCastContext context = new SpellCastContext(this);
            ((SpellcastingItem<?>) this.getItemInHand(InteractionHand.MAIN_HAND).getItem()).getData(stack).getActive().onStart(context);
            ((SpellcastingItem<?>) this.getItemInHand(InteractionHand.MAIN_HAND).getItem()).getData(stack).getActive().onStop(context);
        }
    }

    @Override
    public @NotNull ItemStack getItem() {
        return new ItemStack(Items.WITHER_SKELETON_SKULL);
    }


}
