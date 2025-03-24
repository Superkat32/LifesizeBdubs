package net.superkat.lifesizebdubs.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public class TooltipItem extends Item {
    public Text tooltip;
    public TooltipItem(Text tooltip) {
        super(new Item.Settings());
        this.tooltip = tooltip;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(this.tooltip);
    }
}
