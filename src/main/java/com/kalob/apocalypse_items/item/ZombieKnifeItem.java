package com.kalob.apocalypse_items.item;

import com.kalob.apocalypse_items.ApocalypseItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class ZombieKnifeItem extends Item {

    public ZombieKnifeItem(Properties properties) {
        super(properties.attributes(
            ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(ApocalypseItems.MODID, "zombie_knife_damage"),
                        6.0,
                        AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                    new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(ApocalypseItems.MODID, "zombie_knife_speed"),
                        -2.0,
                        AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND)
                .build()
        ));
    }
}
