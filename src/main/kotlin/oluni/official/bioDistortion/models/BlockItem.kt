package oluni.official.bioDistortion.models

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

data class BlockItem(
    val id: String,
    val displayName: String,
    val lore: List<Component>,
    val modelPath: String,
    val hitboxMaterial: Material,
    val isTall: Boolean = false
) {
    fun getItem(amount: Int): ItemStack {
        val item = ItemStack(Material.BARRIER, amount)
        val parts = modelPath.split(":")
        item.editMeta {
            it.displayName(Component.text(displayName))
            it.lore(lore)
            it.persistentDataContainer.set(
                NamespacedKey("mysticism", "block_id"),
                PersistentDataType.STRING,
                id
            )
            if (parts.size == 2) it.itemModel = NamespacedKey(parts[0], parts[1])
        }
        return item
    }
}