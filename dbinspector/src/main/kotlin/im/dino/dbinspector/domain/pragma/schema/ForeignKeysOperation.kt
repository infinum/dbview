package im.dino.dbinspector.domain.pragma.schema

import im.dino.dbinspector.data.models.Row
import im.dino.dbinspector.domain.shared.AbstractSchemaOperation

internal class ForeignKeysOperation(
    private val name: String,
    pageSize: Int
) : AbstractSchemaOperation<List<Row>>(pageSize) {

    override fun query(): String = String.format(FORMAT_PRAGMA_FOREIGN_KEYS, name)
}