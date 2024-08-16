package org.team9432

import kotlinx.coroutines.*
import org.team9432.lib.coroutines.RobotScope
import org.team9432.lib.resource.Action

object RobotController {
    private val coroutineScope = RobotScope

    private var currentJob: Job? = null

    private fun executeRequest(action: Action) {
        val conflictingJob = currentJob
        currentJob = coroutineScope.launch {
            try {
                // Cancel conflicting job
                withContext(NonCancellable) {
                    conflictingJob?.cancelAndJoin()
                }

                // run the action
                coroutineScope { action.invoke(this) }
            } finally {
                currentJob = null
            }
        }
    }

    suspend fun setAction(action: Action) {
        currentJob?.cancelAndJoin()
        executeRequest(action)
    }


    suspend fun resetRequests() {
        currentJob?.cancelAndJoin()
    }
}