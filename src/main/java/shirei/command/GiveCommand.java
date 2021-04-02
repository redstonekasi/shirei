package shirei.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;

public class GiveCommand {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final SimpleCommandExceptionType NOT_IN_CREATIVE = new SimpleCommandExceptionType(new TranslatableText("command.shirei.not_in_creative"));

    public static LiteralArgumentBuilder<FabricClientCommandSource> literal() {
        return ClientCommandManager.literal("give").then(
                argument("item", ItemStackArgumentType.itemStack())
                    .executes(ctx -> give(ctx.getSource(), ItemStackArgumentType.getItemStackArgument(ctx, "item"), 1))
                    .then(
                            argument("count", IntegerArgumentType.integer(1, 64)).executes(ctx -> give(ctx.getSource(), ItemStackArgumentType.getItemStackArgument(ctx, "item"), IntegerArgumentType.getInteger(ctx, "count")))
                    )
        );
    }

    private static int give(FabricClientCommandSource source, ItemStackArgument item, int count) throws CommandSyntaxException {
        if(!mc.player.isCreative()) throw NOT_IN_CREATIVE.create();

        ItemStack stack = item.createStack(count, true);

        if(mc.player.getMainHandStack().isEmpty())
            mc.interactionManager.clickCreativeStack(stack, 36 + mc.player.inventory.selectedSlot);
        else {
            int nextEmptySlot = mc.player.inventory.getEmptySlot();

            if(nextEmptySlot < 9) {
                mc.interactionManager.clickCreativeStack(stack, 36 + nextEmptySlot);
            }
            else
                mc.interactionManager.clickCreativeStack(stack, 36 + mc.player.inventory.selectedSlot);
        }

        source.sendFeedback(new TranslatableText("command.shirei.give.success", count, stack.toHoverableText()));
        return 1;
    }

}
