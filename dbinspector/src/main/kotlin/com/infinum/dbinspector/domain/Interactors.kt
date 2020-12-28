package com.infinum.dbinspector.domain

import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import com.infinum.dbinspector.data.models.local.cursor.BlobPreviewType
import com.infinum.dbinspector.data.models.local.cursor.QueryResult
import com.infinum.dbinspector.data.models.local.proto.SettingsEntity
import com.infinum.dbinspector.domain.database.models.Operation
import com.infinum.dbinspector.domain.shared.base.BaseInteractor
import com.infinum.dbinspector.data.models.local.cursor.Query
import com.infinum.dbinspector.data.models.local.cursor.TruncateType
import java.io.File

internal interface Interactors {

    // region Database
    interface GetDatabases : BaseInteractor<Operation, List<File>>

    interface ImportDatabases : BaseInteractor<Operation, List<File>>

    interface RemoveDatabase : BaseInteractor<Operation, List<File>>

    interface RenameDatabase : BaseInteractor<Operation, List<File>>

    interface CopyDatabase : BaseInteractor<Operation, List<File>>
    // endregion

    // region Connection
    interface OpenConnection : BaseInteractor<String, SQLiteDatabase>

    interface CloseConnection : BaseInteractor<String, Unit>
    // endregion

    // region Settings
    interface GetSettings : BaseInteractor<Unit, SettingsEntity?>

    interface SaveLinesLimit : BaseInteractor<Boolean, Unit>

    interface SaveLinesCount : BaseInteractor<Int, Unit>

    interface SaveTruncateMode : BaseInteractor<TruncateType, Unit>

    interface SaveBlobPreviewMode : BaseInteractor<BlobPreviewType, Unit>
    // endregion

    // region Schema
    interface GetTables : BaseInteractor<Query, QueryResult>

    interface GetTableByName : BaseInteractor<Query, QueryResult>

    interface DropTableContentByName : BaseInteractor<Query, QueryResult>

    interface GetTriggers : BaseInteractor<Query, QueryResult>

    interface GetTriggerByName : BaseInteractor<Query, QueryResult>

    interface DropTriggerByName : BaseInteractor<Query, QueryResult>

    interface GetViews : BaseInteractor<Query, QueryResult>

    interface GetViewByName : BaseInteractor<Query, QueryResult>

    interface DropViewByName : BaseInteractor<Query, QueryResult>
    // endregion

    // region Pragma
    interface GetUserVersion : BaseInteractor<Query, QueryResult>

    interface GetTableInfo : BaseInteractor<Query, QueryResult>

    interface GetForeignKeys : BaseInteractor<Query, QueryResult>

    interface GetIndexes : BaseInteractor<Query, QueryResult>
    // endregion
}
