package oluni.official.kLifeAbnormality.zones.spawn

import oluni.official.kLifeAbnormality.extensions.isFlower
import oluni.official.kLifeAbnormality.extensions.isGrass
import oluni.official.kLifeAbnormality.extensions.isTallFlower
import oluni.official.kLifeAbnormality.models.BlockEntity
import oluni.official.kLifeAbnormality.models.list.CustomBlocks
import oluni.official.kLifeAbnormality.zones.mechanic.Particles
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import kotlin.math.min
import kotlin.math.sqrt

class ZoneSpawning(val particles: Particles) {
    private val circleOffsets = (-2..2).flatMap { x ->
        (-2..2).map { z -> x to z }
    }.filter { (x, z) -> sqrt((x * x + z * z).toDouble()) <= 2.5 }

    fun findLocationNearPlayer(player: Player, attempts: Int = 0) {
        if (attempts > 5) return
        val location: Location = player.location
        val world = location.world ?: return
        if (world.environment != World.Environment.NORMAL) return
        val x = location.blockX + (-50..50).random()
        val z = location.blockZ + (-50..50).random()
        val y = world.getHighestBlockYAt(x, z)
        createZone(Location(world, x.toDouble(), y.toDouble(), z.toDouble()), player, attempts)
    }

    fun createZone(location: Location, player: Player, attempts: Int) {
        if (attempts >= 5) return
        val world = location.world ?: return
        var badBlockCount = 0
        for ((xOffset, zOffset) in circleOffsets) {
            val checkBlock = world.getBlockAt(location.blockX + xOffset, location.blockY, location.blockZ + zOffset)
            val material = checkBlock.type
            if (material == Material.AIR || material == Material.WATER || material == Material.LAVA) {
                badBlockCount++
                if (badBlockCount > 3) {
                    findLocationNearPlayer(player, attempts + 1)
                    return
                }
            }
            for (checkY in 1..2) {
                val anotherMaterial = checkBlock.getRelative(0, checkY, 0)
                if (anotherMaterial.isSolid && !anotherMaterial.isFlower() && !anotherMaterial.isTallFlower()) {
                    findLocationNearPlayer(player, attempts + 1)
                    return
                }
            }
        }
        val validLocation = mutableListOf<Location>()
        for ((xOffset, zOffset) in circleOffsets) {
            val floorBlock = location.block.getRelative(xOffset, 0, zOffset)
            if (floorBlock.isGrass()) {
                val plantBlock = floorBlock.getRelative(0, 1, 0)
                when {
                    plantBlock.isFlower() -> {
                        plantBlock.type = Material.WITHER_ROSE
                    }
                    plantBlock.isTallFlower() -> {
                        plantBlock.type = Material.AIR
                        plantBlock.getRelative(0, 1, 0).type = Material.AIR
                        BlockEntity(plantBlock.location, CustomBlocks.ANOMALY_KOREN)
                    }
                    plantBlock.type == Material.SNOW -> plantBlock.type = Material.AIR
                }
                BlockEntity(floorBlock.location, CustomBlocks.ANOMALY_DIRT)
                validLocation += floorBlock.location
            }
        }
        if (validLocation.isNotEmpty()) {
            val raysCount = min((3..5).random(), validLocation.size)
            validLocation.shuffled().take(raysCount).forEach { rayLoc ->
                particles.greenParticlesRunnable(rayLoc.clone().add(0.5, 1.0, 0.5))
            }
            world.getNearbyEntities(location, 3.0, 3.0, 3.0).forEach { entity ->
                if (entity is LivingEntity) {
                    entity.damage(16.0)
                }
            }
        }
    }
}