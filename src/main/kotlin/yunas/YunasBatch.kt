package yunas

import yunas.batch.BatchManager
import yunas.batch.DefaultBatchManager
import yunas.configuration.Configuration

/**
 * Entry Point of Yunas Framework For DefaultBatchManager.
 *
 */
object YunasBatch  {

    private val manager: BatchManager = DefaultBatchManager()

    private val instance = App.initBatch()

    val configuration: Configuration?
            get() = instance.configuration

    fun run(args: Array<String>) {
        manager.run(args)
    }

    fun add(name:String,executable: Batch) {
        manager.add(name,executable)
    }

}