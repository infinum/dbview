package im.dino.dbinspector.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

public class TablePageFormatters {

    private TablePageFormatters() { } // the class serves as a namespace for the interfaces and default helpers

    private static ITablesFormatter nooOpTablesFormatter = new ITablesFormatter() {
        @Nullable
        @Override
        public IRowFormatter getRowFormatterOfTable(String tableName) {
            return null;
        }

        @Nullable
        @Override
        public ICellViewFormatter getDefaultCellViewFormatter() {
            return null;
        }
    };

    private static ITablesFormatterFactory tablesFormatterFactory = null;

    /**
     * Used by apps that integrated with DbInspector to customize content table output.
     */
    public static void setTablesFormatterFactory(ITablesFormatterFactory tablesFormatterFactory) {
        TablePageFormatters.tablesFormatterFactory = tablesFormatterFactory;
    }

    /**
     * Used by {@link TablePageAdapter} to get table-specific formatters.
     */
    @NonNull
    static ITablesFormatter createTablesFormatter(Context context) {
        if (tablesFormatterFactory != null) {
            return tablesFormatterFactory.create(context);
        } else {
            return nooOpTablesFormatter;
        }
    }

    /**
     * Provide formatters that can style / format the output of a collection of tables.
     */
    public interface ITablesFormatter {
        @Nullable
        IRowFormatter getRowFormatterOfTable(String tableName);

        @Nullable
        ICellViewFormatter getDefaultCellViewFormatter();
    }

    public interface ITablesFormatterFactory {
        @NonNull
        ITablesFormatter create(@NonNull Context context);
    }

    /**
     * Format the values of a table row.
     */
    public interface IRowFormatter {
        @Nullable
        ICellValueFormatter getCellValueFormatter(String columnName);
        ICellViewFormatter getHeaderViewFormatter(String columnName);
        ICellViewFormatter getCellViewFormatter(String columnName);
    }

    public interface ICellValueFormatter {
        /**
         *
         * Given the cell value defined by the cursor and column position,
         * return a formatted value, e.g., convert a unix epoch time in <code>long</code>
         * to human-readable string.
         *
         * The caller should use the cursor to access the cell value only, e.g.,
         * <code>cursor.getLong(col)</code>, and MUST not change its state, e.g.,
         * moving to the next row.
         */
        @NonNull
        String formatValue(Cursor cursor, int col);
    }

    // OPEN: Consider to rename it to ICellStyler, to differentiate it from ICellValueFormatter
    public interface ICellViewFormatter {
        /**
         * Styles a given cell, e.g., width, text appearance, etc.
         * It should not change the actual cell value, which is customized
         * with a #ICellValueFormatter
         */
        // The parameter needs to be a TextView, rather than just View, so that
        // TextView-specific styling can be applied.
        // The downside is implementation could break the contract, and modify the value with TextView#setText()
        void formatView(TextView view);
    }

    public static class TablesFormatter implements  ITablesFormatter {
        private final Map<String, IRowFormatter> config = new ArrayMap<String, IRowFormatter>();
        @Nullable
        private ICellViewFormatter defaultCellViewFormatter = null;

        /**
         * Specify the formatters for a collection of tables.
         *
         * @param tableNameFormatterPairs a list of #java.lang.String tableName , #IRowFormatter rowFormatter pairs,
         *                                defining the customization for the tables
         */
        public TablesFormatter(Object... tableNameFormatterPairs) {
            for (int i = 0; i < tableNameFormatterPairs.length; i += 2) {
                try {
                    String tableName = (String) tableNameFormatterPairs[i];
                    IRowFormatter formatter = (IRowFormatter) tableNameFormatterPairs[i + 1];
                    config.put(tableName, formatter);
                } catch (ClassCastException cce) {
                    throw new IllegalArgumentException("Arguments must be in tableName - rowFormatter pairs. Incorrect type at " + i, cce);
                }
            }
        }

        /**
         * Specify an optional #ICellViewFormatter used to style all table cells.
         * It is used only when no specific ones are present in the table / column definition.
         */
        public TablesFormatter setDefaultCellViewFormatter(@Nullable ICellViewFormatter defaultCellViewFormatter) {
            this.defaultCellViewFormatter = defaultCellViewFormatter;
            return this;
        }

        @Nullable
        @Override
        public IRowFormatter getRowFormatterOfTable(String tableName) {
            return config.get(tableName);
        }

        @Nullable
        @Override
        public ICellViewFormatter getDefaultCellViewFormatter() {
            return defaultCellViewFormatter;
        }
    }

    public static class RowFormatter implements IRowFormatter {
        private final Map<String, ICellValueFormatter> valueConfig = new ArrayMap<String, ICellValueFormatter>();
        private final Map<String, ICellViewFormatter> headerViewConfig = new ArrayMap<String, ICellViewFormatter>();
        private final Map<String, ICellViewFormatter> viewConfig = new ArrayMap<String, ICellViewFormatter>();

        public RowFormatter() { }

        /**
         *
         * @param columnNameFormatterPairs a list of {@link String} columnName - #ICellValueFormatter valueFormatter pairs.
         */
        public RowFormatter setValueFormatters(Object... columnNameFormatterPairs) {
            valueConfig.clear();
            for (int i = 0; i < columnNameFormatterPairs.length; i += 2) {
                try {
                    String columnName = (String) columnNameFormatterPairs[i];
                    ICellValueFormatter cellFormatter = (ICellValueFormatter) columnNameFormatterPairs[i + 1];
                    valueConfig.put(columnName, cellFormatter);
                } catch (ClassCastException cce) {
                    throw new IllegalArgumentException("Arguments must be in columnName - cellFormatter pairs. Incorrect type at " + i,
                            cce);
                }
            }
            return this;
        }

        /**
         *
         * @param columnNameFormatterPairs a list of {@link String} columnName - #ICellViewFormatter headerViewFormatter
         *                                 - #ICellViewFormatter cellViewFormatter triples.
         *                                 The view formatters can be null if no specific styling is to be applied.
         */
        @SuppressWarnings("checkstyle:MagicNumber")
        public RowFormatter setViewFormatters(Object... columnNameFormatterPairs) {
            headerViewConfig.clear();
            viewConfig.clear();
            for (int i = 0; i < columnNameFormatterPairs.length; i += 3) {
                try {
                    String columnName = (String) columnNameFormatterPairs[i];
                    ICellViewFormatter headerFormatter = (ICellViewFormatter) columnNameFormatterPairs[i + 1];
                    ICellViewFormatter cellFormatter = (ICellViewFormatter) columnNameFormatterPairs[i + 2];

                    headerViewConfig.put(columnName, headerFormatter);
                    viewConfig.put(columnName, cellFormatter);
                } catch (ClassCastException cce) {
                    String errMsg = "Arguments must be in columnName - headerViewFormatter - cellViewFormatter triples. "
                            + "Incorrect type at " + i;
                    throw new IllegalArgumentException(errMsg, cce);
                }
            }
            return this;
        }

        @Nullable
        @Override
        public ICellValueFormatter getCellValueFormatter(String columnName) {
            return valueConfig.get(columnName);
        }

        @Override
        public ICellViewFormatter getHeaderViewFormatter(String columnName) {
            return headerViewConfig.get(columnName);
        }

        @Override
        public ICellViewFormatter getCellViewFormatter(String columnName) {
            return viewConfig.get(columnName);
        }
    }

    //
    // Formatter classes / instances for conveniences
    //

    public static class DateTimeFormatter implements ICellValueFormatter {
        private final DateFormat formatter;


        @SuppressLint("SimpleDateFormat")
        // SuppressLint reason: the API allows callers to specify the format to present to end users.
        // Hence locale is the system default, without the need of specifying it.
        public DateTimeFormatter(String formatPattern) {
            formatter = new SimpleDateFormat(formatPattern);
        }

        @NonNull
        @Override
        public final String formatValue(Cursor cursor, int col) {
            long value = cursor.getLong(col);
            return value > 0 ? formatter.format(value) : "";
        }
    };

    /**
     * Helper to format a View that is expected to contain long text, by setting
     * max with and truncation.
     */
    public static class LongTextViewFormatter implements ICellViewFormatter {
        private final int maxWidthInPx;
        @Nullable
        private final TextUtils.TruncateAt ellipsizeWhere;

        public LongTextViewFormatter(int maxWidthInPx, @Nullable TextUtils.TruncateAt ellipsizeWhere) {
            this.maxWidthInPx = maxWidthInPx;
            this.ellipsizeWhere = ellipsizeWhere;
        }

        @Override
        public void formatView(TextView view) {
            view.setMaxWidth(maxWidthInPx);
            view.setMaxLines(1);
            view.setEllipsize(ellipsizeWhere);
        }
    }

    /**
     * Use cases: express max width of a view in the unit of dp.
     * @return the pixels of the given amount of dp
     */
    public static int dpToPx(int dp, @NonNull Context context) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    /**
     * Convert a <code>long</code> timestamp to a short date in YY-MM-dd.
     */
    public static final ICellValueFormatter shortDate = new DateTimeFormatter("yy-MM-dd");

    /**
     * Convert a <code>long</code> timestamp to a short timestamp string (up to seconds).
     */
    public static final ICellValueFormatter shortTimeStamp = new DateTimeFormatter("yy-MM-dd HH:mm:ss");

}
