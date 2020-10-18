package im.dino.dbinspector.ui.pragma.indexes

import im.dino.dbinspector.domain.UseCases
import im.dino.dbinspector.domain.shared.models.Statements
import im.dino.dbinspector.ui.pragma.shared.PragmaSourceViewModel

internal class IndexViewModel(
    private val getPragma: UseCases.GetIndexes
) : PragmaSourceViewModel() {

    override fun pragmaStatement(name: String) =
        Statements.Pragma.indexes(name)

    override fun dataSource(databasePath: String, statement: String) =
        IndexDataSource(databasePath, statement, getPragma)
}