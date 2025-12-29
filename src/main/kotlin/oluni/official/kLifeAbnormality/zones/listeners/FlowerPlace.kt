package oluni.official.kLifeAbnormality.zones.listeners

import oluni.official.kLifeAbnormality.extensions.isAnomaly
import oluni.official.kLifeAbnormality.extensions.isFlower
import oluni.official.kLifeAbnormality.extensions.isTallFlower
import oluni.official.kLifeAbnormality.models.BlockEntity
import oluni.official.kLifeAbnormality.models.list.CustomBlocks
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class FlowerPlace: Listener {

    @EventHandler
    fun placeFlowerOnAnomalyDirt(event: BlockPlaceEvent) {
        val block = event.block
        if (block.getRelative(0, -1, 0).isAnomaly()) {
            when {
                block.isFlower() -> {
                    block.type = Material.WITHER_ROSE
                }
                block.isTallFlower() -> {
                    val top = block.getRelative(BlockFace.UP)
                    block.type = Material.AIR
                    if (top.type == Material.TALL_GRASS || top.isTallFlower()) {
                        top.type = Material.AIR
                    }
                    BlockEntity(block.location, CustomBlocks.ANOMALY_KOREN)
                }
            }
        }
    }
}