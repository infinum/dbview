package im.dino.dbinspector.ui.content.shared

import androidx.recyclerview.widget.DiffUtil

internal class ContentDiffUtil : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}