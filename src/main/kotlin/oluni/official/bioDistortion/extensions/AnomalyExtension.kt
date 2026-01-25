package oluni.official.bioDistortion.extensions

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.entity.ItemDisplay
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

internal val entityIdKey = NamespacedKey(MYSTICISM, "block_entity_id")

internal val flowers = mutableSetOf<Material>()
internal val tallFlowers = mutableSetOf<Material>()
internal val grassesBlocks = mutableSetOf<Material>()


fun Block.isReplaceableBlock(): Boolean = type in grassesBlocks
fun Block.isFlower(): Boolean = type in flowers
fun Block.isTallFlower(): Boolean = type in tallFlowers
fun Block.isGrassBlock(): Boolean = type in grassesBlocks
fun Block.isGrass(): Boolean = type == Material.SHORT_GRASS
fun Block.isTallGrass(): Boolean = type == Material.TALL_GRASS

fun Block.isAnomaly(): Boolean {
    val center = this.location.clone().add(0.5, 0.5, 0.5)
    return world.getNearbyEntities(center, 0.1, 0.1, 0.1)
        .filterIsInstance<ItemDisplay>()
        .any { it.persistentDataContainer.has(entityIdKey, PersistentDataType.STRING) }
}
fun MutableCollection<Material>.loadConfigList(plugin: JavaPlugin, path: String) {
    this.clear()
    plugin.config.getStringList(path).forEach { name ->
        Material.getMaterial(name.uppercase())
            ?.let { this.add(it) }
            ?: plugin.logger.warning("Ошибка в конфиге ($path): Материал '$name' не существует.")
    }
}

