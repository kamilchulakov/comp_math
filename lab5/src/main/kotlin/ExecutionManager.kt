import GUIManager.draw
import IOManager.readInputFromCLI
import IOManager.readInputFromFile
import LabConfiguration.sleepTime
import MathSolver.gaussInterpolation
import MathSolver.lagrangeInterpolation
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

    private fun readInput(st: ProgramState) {
        when (st.fileInput) {
            true -> {
                readInputFromFile(st)
                st.interpolationParam = st.scanner.nextLine().toDouble()
            }
            else -> readInputFromCLI(st)
        }
    }

    private suspend fun peekCalculatingAndUpdate(st: ProgramState) {
        when ((st.stateType as Calculating).numOfFunc) {
            0 -> {
                lagrangeInterpolation(st)
                updateState(st, Calculating(1))
            }
            1 -> {
                gaussInterpolation(st)
                updateState(st, Finished)
            }
        }
    }


    private suspend fun executeByState(st: ProgramState) {
        when (st.stateType) {
            is New -> updateState(st, Started)
            is Started -> updateState(st, ReadingInput)
            is ReadingInput -> {
                readInput(st)
                updateState(st, Calculating())
            }
            is Calculating -> {
                peekCalculatingAndUpdate(st)
            }
            else -> throw IllegalStateException("Invalid program state.")
        }
    }

    suspend fun execute(st: ProgramState) {
        while (st.stateType !is Finished) {
            executeByState(st)
        }
        st.stateChannel.send(st.stateType)
        println("\nДля x = ${st.interpolationParam}")
        st.interpolationValues.forEach { println("${it.method} - ${it.result}") }
        draw(st)
    }
}