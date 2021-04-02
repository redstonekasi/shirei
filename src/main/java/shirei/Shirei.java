package shirei;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.text.LiteralText;
import shirei.command.GiveCommand;

public class Shirei implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("shirei")
				.then(GiveCommand.literal()));
	}
}
