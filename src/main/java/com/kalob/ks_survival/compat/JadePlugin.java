package com.kalob.ks_survival.compat;

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
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(AnimalDataProvider.INSTANCE, Animal.class);
    }
}
