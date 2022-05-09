import LabConfiguration.badWaiting
import LabConfiguration.delimiter
import LabConfiguration.functions
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
                    println("\nИспользую количество строк по-умолчанию: ${st.n}")
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
    }

    fun readInputFromCLI(st: ProgramState, time: Int = 0) {
        println()
        when (time) {
            0 -> println("Добрый день!")
            1 -> println("О, снова ты, как приятно видеть знакомое лицо.")
            2 -> println("Ты испытываещь моё терпение.")
            else -> {
                println("Отдохни немного.")
                Thread.sleep(1000)
            }
        }
        println("Введите 1, если хотите ввести табличную функцию.\nВведите 2, если хотите выбрать функцию.")
        when (st.scanner.nextLine().toInt()) {
            1 -> {
                println("Введите число строк.")
                st.n = st.scanner.nextLine().toInt()
                println("Вводите пары координат x и y точек.")
                for (i in 0 until st.n) {
                    val args = st.scanner.nextLine().split(delimiter)
                    addAndCheck(args[0].toDouble(), st.x)
                    addAndCheck(args[1].toDouble(), st.y)
                }
                println("Введите x.")
                st.interpolationParam = st.scanner.nextLine().toDouble()
            }
            2 -> {
                println("Выберете функцию из предложенных:")
                functions.forEachIndexed { index, (str, _) -> println("$index - $str") }
                val ch = st.scanner.nextLine().toInt()
                if (ch in functions.indices) {
                    println("Введите левую границу.")
                    val a = st.scanner.nextLine().toDouble()
                    println("Введите правую границу.")
                    val b = st.scanner.nextLine().toDouble()
                    println("Введите количество узлов.")
                    val m = st.scanner.nextLine().toInt()
                    st.n = m
                    val step = (b - a) / m
                    var i = a
                    val fc = functions[ch].second
                    while (i <= b) {
                        addAndCheck(i, st.x)
                        addAndCheck(fc(i), st.y)
                        i += step
                    }
                    println("Введите x.")
                    st.interpolationParam = st.scanner.nextLine().toDouble()
                } else {
                    println("Упс... такой функции нет, начнём сначала.")
                    readInputFromCLI(st, time+1)
                }
            }
            else -> readInputFromCLI(st, time+1)
        }
    }
}