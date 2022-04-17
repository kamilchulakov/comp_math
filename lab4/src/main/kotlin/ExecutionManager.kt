import LabConfiguration.sleepTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ExecutionManager {
    private suspend fun updateState(st: ProgramState, stateType: StateType) {
        withContext(Dispatchers.IO) {
            Thread.sleep(sleepTime)
        }
        st.stateType = stateType
        st.stateChannel.send(stateType)
    }

    private suspend fun executeByState(st: ProgramState) {
        when (st.stateType) {
            New -> updateState(st, Started)
            Started -> updateState(st, ReadingInput)
            else -> throw IllegalStateException("Invalid program state.")
        }
    }

    suspend fun execute(st: ProgramState) {
        while (!(st.stateType is Finished || st.stateType is Terminated)) {
            executeByState(st)
        }
    }
}