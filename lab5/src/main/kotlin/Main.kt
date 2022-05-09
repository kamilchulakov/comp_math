import ExecutionManager.execute
import IOManager.progressBar
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*

// 1, 3 => многочлен Лагранжа и Гаусса
fun main(args: Array<String>) {
    val st = ProgramState(
        fileInput = args.isNotEmpty(),
        scanner = when (args.isNotEmpty()) {
            false -> Scanner(System.`in`)
            else -> {
                Scanner(File(args[0]))
            }
        }
    )
    try {
        runBlocking {
            launch {
                progressBar(st)
            }
            launch {
                execute(st)
            }
        }
    } catch (ex: IllegalStateException) {
        println()
        println(ex.message)
    }
}