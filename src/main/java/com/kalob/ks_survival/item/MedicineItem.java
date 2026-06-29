package com.kalob.ks_survival.item;

import com.kalob.ks_survival.farming.FarmAnimalData;
import com.kalob.ks_survival.init.ModAttachments;
import com.kalob.ks_survival.init.SurvivalConfig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class MedicineItem extends Item {

    public MedicineItem(Properties properties) {
        super(properties);
    }

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

        if (!event.getEntity().getAbilities().instabuild) {
            held.shrink(1);
        }

        event.setCanceled(true);
    }
}
