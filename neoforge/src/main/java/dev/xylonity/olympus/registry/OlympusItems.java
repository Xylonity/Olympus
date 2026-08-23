package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.item.BracersOfZeusItem;
import dev.xylonity.olympus.common.item.HelmetOfHadesItem;
import dev.xylonity.olympus.common.item.PersephoneCupItem;
import dev.xylonity.olympus.common.item.PoseidonTridentItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.Weapon;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public class OlympusItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Olympus.MOD_ID);

    public static final DeferredItem<BracersOfZeusItem> BRACERS_OF_ZEUS = ITEMS.registerItem("bracers_of_zeus", BracersOfZeusItem::new, properties -> properties.stacksTo(1).rarity(Rarity.EPIC));
    public static final DeferredItem<HelmetOfHadesItem> HELMET_OF_HADES = ITEMS.registerItem("helmet_of_hades", HelmetOfHadesItem::new, properties -> properties.stacksTo(1).rarity(Rarity.EPIC));
    public static final DeferredItem<PersephoneCupItem> PERSEPHONE_CUP = ITEMS.registerItem("persephone_cup", PersephoneCupItem::new, properties -> properties.stacksTo(1).rarity(Rarity.EPIC));
    public static final DeferredItem<BlockItem> POPPY_OF_DEMETER = ITEMS.registerSimpleBlockItem(OlympusBlocks.POPPY_OF_DEMETER, properties -> properties.rarity(Rarity.EPIC));
    public static final DeferredItem<PoseidonTridentItem> POSEIDON_TRIDENT = ITEMS.registerItem("poseidon_trident", PoseidonTridentItem::new,
            properties -> properties
                .rarity(Rarity.EPIC)
                .durability(2000)
                .attributes(PoseidonTridentItem.createAttributes())
                .component(DataComponents.TOOL, TridentItem.createToolProperties())
                .enchantable(1)
                .component(DataComponents.WEAPON, new Weapon(1))
    );

}
