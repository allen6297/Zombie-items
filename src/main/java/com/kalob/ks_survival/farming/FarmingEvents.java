package com.kalob.ks_survival.farming;

import com.kalob.ks_survival.init.ModAttachments;
import com.kalob.ks_survival.init.SurvivalConfig;
import com.kalob.ks_survival.init.SurvivalItems;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.ArrayList;

public class FarmingEvents {

    @SubscribeEvent
    public static void onAnimalTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof Animal animal)) return;
        if (animal.level().isClientSide()) return;
        if (animal.tickCount % FarmAnimalData.tickInterval() != 0) return;

        FarmAnimalData data = animal.getData(ModAttachments.FARM_ANIMAL.get());
        data.tick();
        animal.setData(ModAttachments.FARM_ANIMAL.get(), data);
    }

    @SubscribeEvent
    public static void onAnimalDrops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof Animal animal)) return;

        FarmAnimalData data = animal.getData(ModAttachments.FARM_ANIMAL.get());
        boolean butcherKill = event.getSource().getEntity() instanceof Player player
            && player.getMainHandItem().is(SurvivalItems.BUTCHER_KNIFE.get());

        if (data.isStressed()) {
            event.getDrops().removeIf(drop -> animal.getRandom().nextBoolean());
        } else if (data.isWellFed() || butcherKill) {
            var bonus = new ArrayList<ItemEntity>();
            for (ItemEntity drop : event.getDrops()) {
                if (animal.getRandom().nextFloat() < SurvivalConfig.BONUS_DROP_CHANCE.get().floatValue()) {
                    bonus.add(new ItemEntity(
                        animal.level(),
                        drop.getX(), drop.getY(), drop.getZ(),
                        drop.getItem().copy()
                    ));
                }
            }
            event.getDrops().addAll(bonus);
        }
    }
}
