package im.dino.dbinspector.ui.pragma.indexes

import im.dino.dbinspector.domain.UseCases
import im.dino.dbinspector.domain.shared.models.Query
import im.dino.dbinspector.ui.shared.base.BaseDataSource
import im.dino.dbinspector.ui.shared.base.BaseViewModel

internal class IndexDataSource(
    databasePath: String,
    statement: String,
    private val getPragma: UseCases.GetIndexes
) : BaseDataSource() {

    override var query: Query = Query(
        databasePath = databasePath,
        statement = statement,
        pageSize = BaseViewModel.PAGE_SIZE
    )

    override fun refresh() {
        query = query.copy(page = 1)
        invalidate()
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
        val page = getPragma(query)
        query = query.copy(page = page.nextPage)
        return LoadResult.Page(
            data = page.fields,
            prevKey = null,
            nextKey = page.nextPage
        )
    }
}
