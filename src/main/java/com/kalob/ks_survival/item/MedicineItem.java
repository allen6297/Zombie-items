package com.kalob.ks_survival.item;

import com.kalob.ks_survival.farming.FarmAnimalData;
import com.kalob.ks_survival.farming.FarmAnimalSyncPacket;
import com.kalob.ks_survival.health.HealingAction;
import com.kalob.ks_survival.health.Wound;
import com.kalob.ks_survival.init.ModAttachments;
import com.kalob.ks_survival.init.SurvivalConfig;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class MedicineItem extends BodyHealingItem {

    public MedicineItem(Properties properties) {
        super(properties);
    }

    @Override
    public HealingAction getAction() {
        // Infection is systemic — clear it from all parts at once and restore a little HP everywhere
        return HealingAction.builder(40)
                .allParts()
                .removes(Wound.INFECTION)
                .restores(3)
                .build();
    }

    /** Also handles right-clicking on farm animals to cure sickness. */
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Animal animal)) return;
        if (!SurvivalConfig.isTrackedAnimal(animal)) return;
        if (animal.level().isClientSide()) return;

        ItemStack held = event.getEntity().getItemInHand(event.getHand());
        if (!(held.getItem() instanceof MedicineItem)) return;

        FarmAnimalData data = animal.getData(ModAttachments.FARM_ANIMAL.get());
        if (!data.isSick()) return;

        data.cure();
        animal.setData(ModAttachments.FARM_ANIMAL.get(), data);
        PacketDistributor.sendToPlayersTrackingEntity(animal, new FarmAnimalSyncPacket(animal.getId(), data));

        if (!event.getEntity().getAbilities().instabuild) held.shrink(1);
        event.setCanceled(true);
    }
}
