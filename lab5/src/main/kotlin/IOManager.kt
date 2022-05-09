import LabConfiguration.badWaiting
import LabConfiguration.delimiter
import LabConfiguration.progressNum
import LabConfiguration.sleepTime
import LabConfiguration.uiMult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object IOManager {
    suspend fun progressBar(programState: ProgramState) {
        var i = stateProgressNum(programState.stateType)
        while (i <= progressNum) {
            print("[")
            for (j in 0 until i) {
                print("#")
            }
            for (j in 0 until progressNum - i - 1) {
                print(" ")
            }
            print("] " + i * uiMult + "%")
            if (i < progressNum) {
                var proceed = false
                var cnt = 0
                while (!proceed) {
                    try {
                        cnt++
                        i = stateProgressNum(programState.stateChannel.tryReceive().getOrThrow())
                        proceed = true
                        print("\r")
                    } catch (ex: IllegalStateException) {
                        withContext(Dispatchers.IO) {
                            Thread.sleep(sleepTime)
                        }
                    } finally {
                        if (cnt > badWaiting) {
                            throw IllegalStateException("Waited $cnt times for new state.")
                        }
                    }
                }
            }
            if (i == progressNum) break
        }
        println()
    }

    private fun addAndCheck(num: Double, nums: MutableList<Double>) {
        nums.add(num)
        if (nums.last() == 0.0) nums[nums.size-1] = nums.last() + 0.001
    }
    fun readInputFromFile(st: ProgramState) {
        var setN = false
        var i = 0
        while (st.scanner.hasNextLine() && i < st.n) {
            val line = st.scanner.nextLine()
            if (line.isNotBlank()) {
                val args = line.split(delimiter)
                if (args.size > 1 && !setN) {
                    setN = true
                    println("\nUsing default table size: ${st.n}")
                    addAndCheck(args[0].toDouble(), st.x)
                    addAndCheck(args[1].toDouble(), st.y)
                } else if (args.size == 1 && !setN) {
                    setN = true
                    st.n = args[0].toInt()
                    i--
                } else {
                    addAndCheck(args[0].toDouble(), st.x)
                    addAndCheck(args[1].toDouble(), st.y)
                }
            }
            i++
        }
        // TODO: check if contains
        st.interpolationParam = st.scanner.nextDouble()
    }

    fun readInputFromCLI(st: ProgramState) {

    }
}