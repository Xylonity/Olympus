package dev.xylonity.olympus.registry;

import dev.xylonity.olympus.Olympus;
import dev.xylonity.olympus.common.item.BracersOfZeusItem;
import dev.xylonity.olympus.common.item.HelmetOfHadesItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public class OlympusItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Olympus.MOD_ID);

    public static final DeferredItem<BracersOfZeusItem> BRACERS_OF_ZEUS = ITEMS.registerItem("bracers_of_zeus", BracersOfZeusItem::new, properties -> properties.stacksTo(1));
    public static final DeferredItem<HelmetOfHadesItem> HELMET_OF_HADES = ITEMS.registerItem("helmet_of_hades", HelmetOfHadesItem::new, properties -> properties.stacksTo(1));

}