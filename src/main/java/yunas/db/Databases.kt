package yunas.db

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import yunas.configuration.Configuration
import yunas.db.factory.ConnectionPoolFactory
import yunas.db.strategy.DataSourceStrategy
import yunas.exception.YunasExceptionProvider

import javax.sql.DataSource
import java.sql.Connection
import java.sql.SQLException
import java.util.concurrent.ConcurrentHashMap

/**
 * Store DataSources.
 *
 * @author yosuke kobayashi
 */
object Databases {

    private var dataSourceMap: Map<String, DataSource> = ConcurrentHashMap()

    private val LOG = LoggerFactory.getLogger(Databases::class.java)

    @Synchronized
    fun init(conf: Configuration, sourceStrategy: DataSourceStrategy) {

        if (!dataSourceMap.isEmpty()) {
            LOG.warn("Already Init")
            return
        }
        val factory = sourceStrategy.getFactory(conf)
        dataSourceMap = factory.create(conf)

    }

    fun getDataSource(dbName: String): DataSource {

        if (dataSourceMap.containsKey(dbName)) {
            return dataSourceMap[dbName] as DataSource
        }

        throw YunasExceptionProvider().unknownDBName(dbName)
    }

    val connection: Connection
        @Throws(SQLException::class)
        get() = getConnection(DBName.MASTER.value, true)

    @Throws(SQLException::class)
    fun getConnection(dbName: DBName): Connection {
        return getConnection(dbName.value, true)

    }

    @Throws(SQLException::class)
    fun getConnection(dbName: String, autoCommit: Boolean): Connection {

        val dataSource = getDataSource(dbName)
        val connection = dataSource.connection
        connection.autoCommit = autoCommit
        return connection
    }

}
