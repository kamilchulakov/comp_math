import java.util.function.Consumer
import kotlin.math.pow

fun progressBar() {
    var i = 0
    while (i < 21) {
        print("[")
        for (j in 0 until i) {
            print("#")
        }
        for (j in 0 until 20 - i) {
            print(" ")
        }
        print("] " + i * 5 + "%")
        if (i < 20) {
            print("\r")
            Thread.sleep(300)
        }
        i++
    }
    println()
}

fun main(args: Array<String>) {
    val log: Consumer<String> = if (args.isNotEmpty() && args[0] == "debug") {
        Consumer { msg: String ->  println(msg) }
    } else Consumer {  }
    val start = ProceedProgram {
        log.accept("Started in debug mode.")
        it
    }
    val peekFunction = ProceedProgram {
        log.accept("Peeking function.")
        println("1: x^2")
        println("2: x^3+x^2+5")
        println("3: 3x^3-4x^2+5x-16")
        println("Peek a number from 1 to 3")
        when (readln().toInt()) {
            1 -> it.function = {
                    x: Double -> x.pow(2.0)
            }
            2 -> it.function = {
                    x: Double -> x.pow(3.0) + x.pow(2.0) + 5.0
            }
            3 -> it.function = {
                    x: Double -> 3*x.pow(3.0) - 4*x.pow(2.0) + 5*x-16
            }
            else -> error("Bad function number")
        }
        it
    }
    val peekRestrictions = ProceedProgram {
        log.accept("Peeking restrictions.")
        println("Type a")
        it.a = readln().toDouble()
        println("Type b")
        it.b = readln().toDouble()
        it
    }
    val peekInitialPartition = ProceedProgram {
        log.accept("Peeking partition.")
        log.accept("Default partition: ${it.n}")
        it
    }
    val peekMethod = ProceedProgram {
        log.accept("Peeking method.")
        println("1: Метод левых прямоугольников")
        println("2: Метод средних прямоугольников")
        println("3: Метод правых прямоугольников")
        println("4: Метод Симпсона")
        println("Peek a method number from 1 to 4")
        it.method = readln().toInt()
        if ((it.method > 4) or (it.method < 0)) {
            error("Bad method number.")
        }
        it
    }
    val peekEps = ProceedProgram {
        log.accept("Peeking eps.")
        println("Peek a number from 0.0 to 1.0")
        it.eps = readln().toDouble()
        if ((it.eps > 1.0) or (it.eps < 0.0)) {
            error("Bad method number.")
        }
        it
    }
    val solve = ProceedProgram {
        log.accept("Solving.")
        when(it.method) {
            1->solveRectangleLeft(it)
            2->solveRectangleMid(it)
            3->solveRectangleRight(it)
            4->solveSimpson(it)
            else->error("Not implemented yet")
        }
        progressBar()
        it.solved = true
        it
    }
    val finish: (ProgramState)->Unit =  {
        if (!it.solved) error("No solution found...")
        log.accept("Solved.")
        println("Result: ${it.result}")
        println("Operations: ${it.operations}")
    }
    finish(solve(peekEps(peekMethod(peekInitialPartition(peekRestrictions(peekFunction(start(ProgramState))))))))
}