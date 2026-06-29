package com.kalob.ks_survival.health;

import com.kalob.ks_survival.init.ModAttachments;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HealthHudOverlay {

    private static final BodyPart[] PARTS = {
            BodyPart.HEAD, BodyPart.TORSO,
            BodyPart.LEFT_ARM, BodyPart.RIGHT_ARM,
            BodyPart.LEFT_LEG, BodyPart.RIGHT_LEG
    };
    private static final String[] LABELS = {
            "Head", "Torso", "L.Arm", "R.Arm", "L.Leg", "R.Leg"
    };

    // Wound icons — all confirmed to render in Minecraft's font
    private static final String ICON_BLEED   = "§c❤";  // red heart  → bleeding
    private static final String ICON_SEVERE_BLEED = "§4❤"; // dark red heart → severe bleeding
    private static final String ICON_FRACTURE = "§6✖"; // orange ×   → broken bone
    private static final String ICON_INFECT  = "§2☣";  // dark green biohazard → infection

    private static final int ROW_H   = 13;
    private static final int PANEL_W = 70;
    private static final int PANEL_H = ROW_H * PARTS.length + 4;
    private static final int MARGIN  = 5;
    private static final int BAR_H   = 2;
    private static final int BAR_W   = 62;

    // Panel background: dark with a subtle left accent stripe
    private static final int BG_COLOR     = 0xAA0A0A0A;
    private static final int ACCENT_COLOR = 0xFF1A1A1A;

    public static void onRenderHud(GuiGraphics gfx, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        Player player = mc.player;
        BodyPartData data = player.getData(ModAttachments.BODY_PARTS.get());

        int px = MARGIN;
        int py = MARGIN;

        // Background + left accent stripe
        gfx.fill(px,     py,     px + 2,       py + PANEL_H, ACCENT_COLOR);
        gfx.fill(px + 2, py,     px + PANEL_W, py + PANEL_H, BG_COLOR);

        for (int i = 0; i < PARTS.length; i++) {
            drawRow(gfx, mc, data, PARTS[i], LABELS[i], px + 4, py + 2 + i * ROW_H);
        }

        if (data.isMovementLocked()) {
            drawMovementLockCountdown(gfx, mc, data);
        }
    }

    private static void drawMovementLockCountdown(GuiGraphics gfx, Minecraft mc, BodyPartData data) {
        int seconds = Math.max(1, (data.getMovementLockTicks() + 19) / 20);
        String text = "FALL STUN  -- " + seconds + " --";
        int x = (mc.getWindow().getGuiScaledWidth() - mc.font.width(text)) / 2;
        int y = mc.getWindow().getGuiScaledHeight() / 2 + 28;

        gfx.fill(x - 6, y - 4, x + mc.font.width(text) + 6, y + 12, 0xAA000000);
        gfx.drawString(mc.font, text, x, y, 0xFFE53935, false);
    }

    private static void drawRow(GuiGraphics gfx, Minecraft mc, BodyPartData data,
                                BodyPart part, String label, int x, int y) {
        int   hp    = data.getHp(part);
        int   maxHp = data.getMaxHp(part);
        float frac  = maxHp > 0 ? Math.min(1f, Math.max(0f, (float) hp / maxHp)) : 0f;
        boolean flashing = data.getDamageFlashTicks() > 0 && data.getLastDamagedPart() == part;

        // Label colour: white when healthy, dim when crippled
        int labelColor = hp <= 0 ? 0xFF666666 : flashing ? 0xFFFFD6D6 : 0xFFCCCCCC;

        // Wound icons after label
        StringBuilder lbl = new StringBuilder(label);
        if (data.hasWound(part, Wound.BLEEDING))  lbl.append(" ").append(ICON_BLEED);
        if (data.hasWound(part, Wound.SEVERE_BLEEDING)) lbl.append(" ").append(ICON_SEVERE_BLEED);
        if (data.hasWound(part, Wound.FRACTURE))  lbl.append(" ").append(ICON_FRACTURE);
        if (data.hasWound(part, Wound.INFECTION)) lbl.append(" ").append(ICON_INFECT);

        if (flashing) {
            gfx.fill(x - 2, y - 1, x + BAR_W + 2, y + ROW_H - 1, 0x55E53935);
        }

        gfx.drawString(mc.font, lbl.toString(), x, y, labelColor, false);

        // HP bar — sits 1px below the 8px font
        int barY = y + 9;
        int barColor;
        if (hp <= 0) {
            barColor = 0xFF3A3A3A;
        } else if (frac > 0.6f) {
            barColor = 0xFF4CAF50; // muted green
        } else if (frac > 0.3f) {
            barColor = 0xFFFF9800; // amber
        } else {
            barColor = 0xFFE53935; // red
        }

        int filled = (int) (frac * BAR_W);
        gfx.fill(x, barY, x + BAR_W, barY + BAR_H, 0xFF1E1E1E);      // track
        if (filled > 0) gfx.fill(x, barY, x + filled, barY + BAR_H, barColor); // fill
    }
}
