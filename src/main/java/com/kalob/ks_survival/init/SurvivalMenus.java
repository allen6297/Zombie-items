package com.kalob.ks_survival.init;

import com.kalob.ks_survival.KsSurvival;
import com.kalob.ks_survival.block.menu.FoodTroughMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SurvivalMenus {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, KsSurvival.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<FoodTroughMenu>> FOOD_TROUGH =
            MENU_TYPES.register("food_trough",
                    () -> IMenuTypeExtension.create(FoodTroughMenu::fromNetwork));
}
