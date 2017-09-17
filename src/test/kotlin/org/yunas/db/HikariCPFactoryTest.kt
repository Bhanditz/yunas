package org.yunas.db

import org.yunas.configuration.DefaultConfigurationFactory
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import javax.sql.DataSource

/**
 *
 * HikariCPFactoryTest.
 *
 */
class HikariCPFactoryTest {


    @Test @Ignore fun createTest() {

        val conf = DefaultConfigurationFactory().create()
        val dataSources = HikariCPFactory().create(conf)

        val dataSource : DataSource? = dataSources[DBName.MASTER.value]
        Assert.assertNotNull(dataSource)

    }


}