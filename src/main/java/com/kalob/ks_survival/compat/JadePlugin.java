package com.kalob.ks_survival.compat;

import com.kalob.ks_survival.block.FoodTroughBlock;
import com.kalob.ks_survival.block.FoodTroughBlockEntity;
import com.kalob.ks_survival.block.WaterTroughBlock;
import com.kalob.ks_survival.block.WaterTroughBlockEntity;
import net.minecraft.world.entity.animal.Animal;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin("ks_survival")
public class JadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(AnimalDataProvider.INSTANCE, Animal.class);
        registration.registerBlockDataProvider(WaterTroughDataProvider.INSTANCE, WaterTroughBlockEntity.class);
        registration.registerBlockDataProvider(FoodTroughDataProvider.INSTANCE, FoodTroughBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(AnimalDataProvider.INSTANCE, Animal.class);
        registration.registerBlockComponent(WaterTroughDataProvider.INSTANCE, WaterTroughBlock.class);
        registration.registerBlockComponent(FoodTroughDataProvider.INSTANCE, FoodTroughBlock.class);
    }
}
