import org.junit.jupiter.api.Test
import kotlin.math.pow

internal class MethodsKtTest {
    @Test
    fun testSolveRectangleLeft() {
        val state = ProgramState
        state.method = 1
        state.n = 5.0
        state.function = {
                x: Double -> x.pow(2.0)
        }
        state.a = 1.0
        state.b = 2.0
        solveRectangleLeft(state)

        assert(state.result == 2.04)
    }

    @Test
    fun testSolveRectangleRight() {
        val state = ProgramState
        state.method = 1
        state.n = 5.0
        state.function = {
                x: Double -> x.pow(2.0)
        }
        state.a = 1.0
        state.b = 2.0
        solveRectangleRight(state)

        assert(state.result == 2.64)
    }

    @Test
    fun testSolveRectangleMid() {
        val state = ProgramState
        state.method = 1
        state.n = 5.0
        state.function = {
                x: Double -> x.pow(2.0)
        }
        state.a = 1.0
        state.b = 2.0
        solveRectangleMid(state)

        assert(String.format("%.2f",state.result) == "2.33")
    }
}