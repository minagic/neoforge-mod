package com.minagic.testing;

import com.minagic.minagic.capabilities.hudAlerts.HudAlert;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertInstance;
import com.minagic.minagic.capabilities.hudAlerts.HudAlertManager;
import com.minagic.minagic.registries.ModAttachments;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class TestingUtils {
    public static List<String> getUserWarnings(LivingEntity entity){
        HudAlertManager hud = entity.getData(ModAttachments.HUD_ALERTS);
        List<String> result = new ArrayList<>();
        for (HudAlertInstance instance: hud.getActiveAlerts()){
            result.add(instance.getAlert().message());
        }
        return result;
    }
}
