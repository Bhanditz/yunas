package yunas.db.manager

import org.slf4j.LoggerFactory
import yunas.db.DBName
import yunas.db.Databases
import yunas.db.model.ResultSetRow
import yunas.util.BaseUtil
import java.io.Closeable
import java.sql.*

/**
 * Custom Methods For executing sql.
 * DBManager Must be called with 'use'
 *
 * see yunas.db.Databases
 */
class DBManager
/**
 * Constructor.
 *
 * @param dbName DBName
 * @throws SQLException
 */
@Throws(SQLException::class)
constructor(dbName: DBName) : AutoCloseable, Closeable {

    private val connection: Connection?

    private var error: Throwable? = null

    fun setError(error: Throwable) {
        this.error = error
    }

    fun hasError(): Boolean {
        return error != null
    }


    init {

        connection = Databases.getConnection(dbName)
    }

    /**
     * Execute Select SQL.
     *
     * @param sql String
     * @param params List
     * @return ResultSetRow is always not null
     */
    fun select(sql: String, params: List<Any>): ResultSetRow? {

        var st: PreparedStatement? = null
        try {

            st = connection!!.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)

            setParameters(st, params)

            val rs = st!!.executeQuery()

            return ResultSetRow.convert(rs)

        } catch (e: Exception) {
            LOG.debug(e.message)
            this.error = e
            return ResultSetRow(emptyMap())
        } finally {
            BaseUtil.closeAC(st)
        }

    }

    /**
     * Execute Select SQL And Return List.
     *
     * @param sql String
     * @param params List
     * @return List is always not null
     */
    fun selectList(sql: String, params: List<Any>): List<ResultSetRow?> {

        var st: PreparedStatement? = null
        try {

            st = connection!!.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)

            setParameters(st, params)

            val rs = st!!.executeQuery()

            return ResultSetRow.convertToList(rs)

        } catch (e: Exception) {
            LOG.debug(e.message)
            this.error = e
            return emptyList()
        } finally {
            BaseUtil.closeAC(st)
        }

    }


    /**
     * Execute Update Query.
     * @param sql String
     * @param params List<Object>
     * @return update rows.  -1: error.
    </Object> *
     */
    fun update(sql: String, params: List<Any>): Int {


        var st: PreparedStatement? = null
        try {

            st = connection!!.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)

            setParameters(st, params)

            return st!!.executeUpdate()

        } catch (e: Exception) {
            LOG.debug(e.message)
            this.error = e
            return -1
        } finally {
            BaseUtil.closeAC(st)
        }

    }

    /**
     * Execute Insert Query.
     * @param sql String
     * @param params List<Object>
     * @return insert rows.  -1: error.
    </Object> *
     */
    fun insert(sql: String, params: List<Any>): Long {

        var st: PreparedStatement? = null
        var rs: ResultSet? = null
        try {

            st = connection!!.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)

            setParameters(st, params)

            val cnt = st!!.executeUpdate()
            if (cnt > 0) {

                rs = st.generatedKeys

                return if (rs == null || !rs.next()) {

                    cnt.toLong()

                } else {
                    rs.getLong(1)
                }

            }

            return cnt.toLong()

        } catch (e: Exception) {
            LOG.debug(e.message)
            this.error = e
            return -1
        } finally {
            BaseUtil.closeAC(rs, st)
        }

    }

    /**
     * Execute Delete Query.
     * @param sql String
     * @param params List<Object>
     * @return delete rows.  -1: error.
    </Object> *
     */
    fun delete(sql: String, params: List<Any>): Int {

        return update(sql, params)
    }

    @Throws(Exception::class)
    private fun setParameters(st: PreparedStatement?, params: List<*>?) {

        if (params == null || params.isEmpty()) {
            return
        }

        for (i in params.indices) {

            val param = params[i]

            val index = i + 1

            st!!.setObject(index, param)

        }

    }

    fun beginTransaction(): Boolean {

        if (connection == null) {
            return false
        }

        try {

            connection.autoCommit = false

            return true

        } catch (e: Exception) {

            LOG.debug(e.message)
            this.error = e
            return false

        }

    }

    fun rollback() {

        if (connection == null) {
            return
        }

        try {

            if (!connection.autoCommit) {
                connection.rollback()
            }

        } catch (e: Exception) {
            LOG.debug(e.message)
            this.error = e
        }

    }

    @Throws(SQLException::class)
    fun setAutoCommit(autoCommit: Boolean) {
        if (connection != null) {
            connection.autoCommit = autoCommit
        }
    }

    fun commit(): Boolean {

        if (connection == null) {
            return false
        }

        try {

            if (!connection.autoCommit) {
                connection.commit()
            }

            return true

        } catch (e: Exception) {

            LOG.debug(e.message)
            this.error = e

            return false

        }

    }


    override fun close() {

        try {

            connection!!.close()
        } catch (e: Exception) {
            LOG.debug(e.message)
            this.error = e
        }

    }

    companion object {

        private val LOG = LoggerFactory.getLogger(DBManager::class.java)
    }


}


