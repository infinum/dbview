package im.dino.dbinspector.ui.tables.content

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import im.dino.dbinspector.R
import im.dino.dbinspector.databinding.DbinspectorItemCellBinding

class ContentViewHolder(
    private val viewBinding: DbinspectorItemCellBinding
) : RecyclerView.ViewHolder(viewBinding.root) {

    fun bind(item: String?, row: Int) {
        with(viewBinding) {
            this.valueView.text = item
            this.root.setBackgroundColor(
                if (row % 2 == 0) {
                    ContextCompat.getColor(this.root.context, R.color.dbinspector_alternate_row_background)
                } else {
                    ContextCompat.getColor(this.root.context, android.R.color.transparent)

                }
            )
//            this.content.setOnClickListener { onClick(it) }
        }

    }

    fun unbind() {
        with(viewBinding) {
            this.root.setBackgroundColor(ContextCompat.getColor(this.root.context, android.R.color.transparent))
//            this.content.setOnClickListener(null)
        }
    }
}