package net.ryzenmc.shieldstun;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
public class GappleTimeReducerClient implements ClientModInitializer {
    private static final int RELEASE_TICKS = 2;
    private static boolean eatingGapple = false;
    private static int startCount = -1;
    private static Hand eatingHand = Hand.MAIN_HAND;
    private static int releaseTicksLeft = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                if (releaseTicksLeft > 0) {
                    releaseTicksLeft--;
                    MinecraftClient.getInstance().options.useKey.setPressed(false);
                } else {
                    if (client.player.isUsingItem()) {
                        ItemStack active = client.player.getActiveItem();
                        if (active.getItem() == Items.GOLDEN_APPLE) {
                            if (!eatingGapple) {
                                eatingGapple = true;
                                startCount = active.getCount();
                                eatingHand = ItemStack.areEqual(client.player.getMainHandStack(), active) ? Hand.MAIN_HAND : Hand.OFF_HAND;
                            } else {
                                int nowCount = client.player.getStackInHand(eatingHand).getCount();
                                if (startCount != -1 && nowCount < startCount) {
                                    releaseTicksLeft = RELEASE_TICKS;
                                    MinecraftClient.getInstance().options.useKey.setPressed(false);
                                    client.player.sendMessage(Text.literal("§aGap Finished Early"), true);
                                    eatingGapple = false;
                                    startCount = -1;
                                }
                            }
                            return;
                        }
                    }

                    eatingGapple = false;
                    startCount = -1;
                }
            }
        });
    }
}
