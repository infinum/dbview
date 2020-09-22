package im.dino.dbinspector.ui.tables

import androidx.recyclerview.widget.RecyclerView
import im.dino.dbinspector.databinding.DbinspectorItemTableBinding
import im.dino.dbinspector.domain.table.models.Table

class TableViewHolder(
    private val viewBinding: DbinspectorItemTableBinding
) : RecyclerView.ViewHolder(viewBinding.root) {

    fun bind(item: Table?, onClick: (Table) -> Unit, onLongClick: (Table) -> Boolean) {
        item?.let { table ->
            with(viewBinding) {
                this.name.text = table.name
                this.root.setOnClickListener { onClick(table) }
                this.root.setOnLongClickListener { onLongClick(table) }
            }
        }
    }

    fun unbind() {
        with(viewBinding) {
            this.root.setOnClickListener(null)
            this.root.setOnLongClickListener(null)
        }
    }
}