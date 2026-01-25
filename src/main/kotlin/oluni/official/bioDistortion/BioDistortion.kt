package oluni.official.bioDistortion

import oluni.official.bioDistortion.extensions.flowers
import oluni.official.bioDistortion.extensions.grassesBlocks
import oluni.official.bioDistortion.extensions.loadConfigList
import oluni.official.bioDistortion.extensions.tallFlowers
import oluni.official.bioDistortion.zones.listeners.FlowerPlace
import oluni.official.bioDistortion.zones.listeners.ModelsListener
import oluni.official.bioDistortion.zones.mechanic.AbnormalitySpread
import oluni.official.bioDistortion.zones.mechanic.Particles
import oluni.official.bioDistortion.zones.spawn.ZoneRunnable
import oluni.official.bioDistortion.zones.spawn.ZoneSpawning
import org.bukkit.plugin.java.JavaPlugin

class BioDistortion : JavaPlugin() {

    override fun onEnable() {
        saveDefaultConfig()

        val particle = Particles(this)
        val zoneSpawning = ZoneSpawning(particle)
        val zoneRunnable = ZoneRunnable(zoneSpawning)
        val abnormalitySpread = AbnormalitySpread()

        server.pluginManager.registerEvents(ModelsListener(), this)
        server.pluginManager.registerEvents(FlowerPlace(), this)

        val spreadConfigInterval = config.getInt("timer.spread", 500) * 20L

        zoneRunnable.runTaskTimer(
            this,
            0L,
            config.getInt("timer.zone-spawn", 60) * 20L
        )

        abnormalitySpread.runTaskTimer(
            this,
            spreadConfigInterval,
            spreadConfigInterval
        )

        grassesBlocks.loadConfigList(this, "block.grasses")
        flowers.loadConfigList(this, "block.flowers")
        tallFlowers.loadConfigList(this, "block.tall-flowers")
    }
}
