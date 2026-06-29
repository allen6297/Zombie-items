package com.kalob.ks_survival.compat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

public class SereneSeasonsCompat {

    private static Boolean loaded = null;

    private static boolean isLoaded() {
        if (loaded == null) loaded = ModList.get().isLoaded("sereneseasons");
        return loaded;
    }

    /** Extra hunger drain per tick interval (0 = normal). */
    public static int extraHungerDrain(Level level) {
        if (!isLoaded()) return 0;
        Season season = SeasonHelper.getSeasonState(level).getSeason();
        return season == Season.WINTER ? 1 : 0;
    }

    /**
     * Returns true if the entity is in its configured breeding season.
     * Always true if Serene Seasons is not loaded, or if the animal has no configured seasons.
     * Domestic animals always return true.
     */
    public static boolean isBreedingSeason(Level level, Entity entity,
            java.util.Set<String> seasons, boolean isDomestic) {
        if (isDomestic || !isLoaded() || seasons.isEmpty()) return true;
        Season season = SeasonHelper.getSeasonState(level).getSeason();
        return seasons.contains(season.name());
    }

    /** Extra thirst drain per tick interval (0 = normal). */
    public static int extraThirstDrain(Level level) {
        if (!isLoaded()) return 0;
        Season season = SeasonHelper.getSeasonState(level).getSeason();
        return season == Season.SUMMER ? 1 : 0;
    }
}
