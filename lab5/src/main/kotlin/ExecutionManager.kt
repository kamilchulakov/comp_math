import GUIManager.draw
import LabConfiguration.delimiter
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
        var setN = false
        var i = 0
        while (st.scanner.hasNextLine() && i < st.n) {
            val line = st.scanner.nextLine()
            if (line.isNotBlank()) {
                val args = line.split(delimiter)
                if (args.size > 1 && !setN) {
                    setN = true
                    st.x.add(args[0].toDouble())
                    st.y.add(args[1].toDouble())
                    if (st.x[i] == 0.0) st.x[i] = st.x[i] + 0.001
                    if (st.y[i] == 0.0) st.y[i] = st.y[i] + 0.001
                } else if (args.size == 1 && !setN) {
                    setN = true
                    st.n = args[0].toInt()
                    i--
                } else {
                    st.x.add(args[0].toDouble())
                    st.y.add(args[1].toDouble())
                    if (st.x[i] == 0.0) st.x[i] = st.x[i] + 0.001
                    if (st.y[i] == 0.0) st.y[i] = st.y[i] + 0.001
                }
            }
            i++
        }
        // TODO: check if contains
        st.interpolationParam = st.scanner.nextDouble()
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
        println()
        st.interpolationValues.forEach { println(it) }
        draw(st)
    }
}