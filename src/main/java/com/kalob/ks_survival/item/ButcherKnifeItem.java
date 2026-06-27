package com.kalob.ks_survival.item;

import com.kalob.ks_survival.KsSurvival;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class ButcherKnifeItem extends Item {

    public ButcherKnifeItem(Properties properties) {
        super(properties.attributes(
            ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(KsSurvival.MODID, "butcher_knife_damage"),
                        5.0, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                    new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(KsSurvival.MODID, "butcher_knife_speed"),
                        -2.4, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND)
                .build()
        ));
    }
}
