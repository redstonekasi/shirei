package shirei.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.ItemEnchantmentArgumentType;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.argument.NbtCompoundTagArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.TranslatableText;
import shirei.util.ItemUtil;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;

public class DataCommand {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static final SimpleCommandExceptionType NOT_IN_CREATIVE = new SimpleCommandExceptionType(new TranslatableText("command.shirei.not_in_creative"));
    private static final SimpleCommandExceptionType EMPTY_STACK = new SimpleCommandExceptionType(new TranslatableText("command.shirei.empty_stack"));
    private static final DynamicCommandExceptionType NO_DATA = new DynamicCommandExceptionType(i -> new TranslatableText("command.shirei.data.get.no_data", i));

    public static LiteralArgumentBuilder<FabricClientCommandSource> literal() {
        return ClientCommandManager.literal("data").then(
                ClientCommandManager.literal("get").executes(ctx -> get(ctx.getSource()))
        ).then(
                ClientCommandManager.literal("set").then(
                        argument("nbt", NbtCompoundTagArgumentType.nbtCompound()).executes(ctx -> set(ctx.getSource(), NbtCompoundTagArgumentType.getCompoundTag(ctx, "nbt")))
                )
        );
    }

    private static int get(FabricClientCommandSource source) throws CommandSyntaxException {
        if (mc.player.getMainHandStack().isEmpty()) throw EMPTY_STACK.create();
        if (!mc.player.getMainHandStack().hasTag()) throw NO_DATA.create(mc.player.getMainHandStack().getName());

        ItemStack item = mc.player.getMainHandStack();
        source.sendFeedback(new TranslatableText("command.shirei.data.get.success", item.toHoverableText(), item.getTag().toText()).styled(style -> {
            style = style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, item.getTag().asString()));
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.copy")));
            return style;
        }));

        return 1;
    }

    private static int set(FabricClientCommandSource source, CompoundTag nbt) throws CommandSyntaxException {
        if (!mc.player.isCreative()) throw NOT_IN_CREATIVE.create();
        if (mc.player.getMainHandStack().isEmpty()) throw EMPTY_STACK.create();

        ItemStack item = mc.player.getMainHandStack();
        item.setTag(item.getOrCreateTag().copyFrom(nbt));

        ItemUtil.setItemInHand(item);
        source.sendFeedback(new TranslatableText("command.shirei.data.set.success", item.toHoverableText()));
        return 1;
    }

}
