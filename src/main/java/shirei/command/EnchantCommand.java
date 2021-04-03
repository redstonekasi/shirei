package shirei.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.ItemEnchantmentArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import shirei.util.ItemUtil;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;

public class EnchantCommand {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static final SimpleCommandExceptionType NOT_IN_CREATIVE = new SimpleCommandExceptionType(new TranslatableText("command.shirei.not_in_creative"));
    private static final SimpleCommandExceptionType EMPTY_STACK = new SimpleCommandExceptionType(new TranslatableText("command.shirei.empty_stack"));

    public static LiteralArgumentBuilder<FabricClientCommandSource> literal() {
        return ClientCommandManager.literal("enchant").then(
                argument("enchantment", ItemEnchantmentArgumentType.itemEnchantment())
                        .executes(ctx -> enchant(ctx.getSource(), ctx.getArgument("enchantment", Enchantment.class), 1))
                        .then(
                                argument("level", IntegerArgumentType.integer(1)).executes(ctx -> enchant(ctx.getSource(), ctx.getArgument("enchantment", Enchantment.class), IntegerArgumentType.getInteger(ctx, "level")))
                        )
        ).then(
                ClientCommandManager.literal("clear").executes(ctx -> clear(ctx.getSource()))
        );
    }

    private static int enchant(FabricClientCommandSource source, Enchantment enchantment, int level) throws CommandSyntaxException {
        if(!mc.player.isCreative()) throw NOT_IN_CREATIVE.create();
        if (mc.player.getMainHandStack().isEmpty()) throw EMPTY_STACK.create();

        ItemStack item = mc.player.getMainHandStack();
        item.addEnchantment(enchantment, level);

        ItemUtil.setItemInHand(item);
        source.sendFeedback(new TranslatableText("command.shirei.enchant.success", enchantment.getName(level), item.toHoverableText()));
        return 1;
    }

    private static int clear(FabricClientCommandSource source) throws CommandSyntaxException {
        if(!mc.player.isCreative()) throw NOT_IN_CREATIVE.create();
        if (mc.player.getMainHandStack().isEmpty()) throw EMPTY_STACK.create();

        ItemStack item = mc.player.getMainHandStack();
        item.getEnchantments().clear();

        ItemUtil.setItemInHand(item);
        source.sendFeedback(new TranslatableText("command.shirei.enchant.clear", item.toHoverableText()));
        return 1;
    }

}
