import javafx.application.Platform
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.vis.swing.jfx.DefaultPlotPanelJfx
import jetbrains.letsPlot.geom.geomPoint
import jetbrains.letsPlot.intern.Plot
import jetbrains.letsPlot.intern.toSpec
import jetbrains.letsPlot.letsPlot
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import javax.swing.JFrame.EXIT_ON_CLOSE

//object UIManager {
//    private const val n = 2000
//    private const val eps = 10.0 / n
//
//    private fun getRandomColor(): Color {
//        return when (getRandomIntInRange(0, 5)) {
//            0 -> Color.RED
//            1 -> Color.GREEN
//            2 -> Color.ORANGE
//            3 -> Color.DARK_MAGENTA
//            else -> Color.BLACK
//        }
//    }
//    fun draw(lmd: (Double, Double)->Double) {
//        val plots = HashMap<String, Plot>()
//        val a = -5
//        val b = 5
//        val n = 10000
//        val h = (b-a)/n
//        val progression = IntRange(a, b).toList()
//        var i = 1
//        val wanted = mapOf<String, List<*>>(
//            "xvar" to List(n) { j:Int-> progression[j] },
//            "yvar" to List(n) { j:Int->
//                lmd(progression[j])
//            }
//        )
//        val wanted2 = mapOf<String, List<*>>(
//            "xvar" to List(n) { j:Int-> st.x[0]+h*j } + List(st.n) { j:Int-> st.x[j] },
//            "yvar" to List(n) { j:Int->
//                lagrangeInterpolationGraph(st, st.x[0]+h*j)
//            } + List(st.n) { j:Int-> st.y[j] }
//        )
//        plots["Гаусс"] = letsPlot(wanted) { x = "xvar"; y = "yvar" } + geomPoint(shape = 1, color = getRandomColor(), size = 5)
//        plots["Лаграндж"] = letsPlot(wanted2) { x = "xvar"; y = "yvar" } + geomPoint(shape = 1, color = getRandomColor(), size = 5)
//
//
//        val selectedPlotKey = plots.keys.first()
//        val controller = Controller(
//            plots,
//            selectedPlotKey,
//            false
//        )
//
//        val window = JFrame("Lets plot Kotlin")
//        window.defaultCloseOperation = EXIT_ON_CLOSE
//        window.contentPane.layout = BoxLayout(window.contentPane, BoxLayout.Y_AXIS)
//
//        // Add controls
//        val controlsPanel = Box.createHorizontalBox().apply {
//            // Plot selector
//            val plotButtonGroup = ButtonGroup()
//            for (key in plots.keys) {
//                plotButtonGroup.add(
//                    JRadioButton(key, key == selectedPlotKey).apply {
//                        addActionListener {
//                            controller.plotKey = this.text
//                        }
//                    }
//                )
//            }
//
//            this.add(Box.createHorizontalBox().apply {
//                border = BorderFactory.createTitledBorder("Plot")
//                for (elem in plotButtonGroup.elements) {
//                    add(elem)
//                }
//            })
//
//            // Preserve aspect ratio selector
//            val aspectRadioButtonGroup = ButtonGroup()
//            aspectRadioButtonGroup.add(JRadioButton("Стандарт", false).apply {
//                addActionListener {
//                    controller.preserveAspectRadio = true
//                }
//            })
//            aspectRadioButtonGroup.add(JRadioButton("Широко", true).apply {
//                addActionListener {
//                    controller.preserveAspectRadio = false
//                }
//            })
//
//            this.add(Box.createHorizontalBox().apply {
//                border = BorderFactory.createTitledBorder("Ширина")
//                for (elem in aspectRadioButtonGroup.elements) {
//                    add(elem)
//                }
//            })
//        }
//        window.contentPane.add(controlsPanel)
//
//        // Add plot panel
//        val plotContainerPanel = JPanel(GridLayout())
//        window.contentPane.add(plotContainerPanel)
//
//        controller.plotContainerPanel = plotContainerPanel
//        controller.rebuildPlotComponent()
//
//        SwingUtilities.invokeLater {
//            window.pack()
//            window.size = Dimension(850, 400)
//            window.setLocationRelativeTo(null)
//            window.isVisible = true
//        }
//    }
//}

private class Controller(
    private val plots: Map<String, Plot>,
    initialPlotKey: String,
    initialPreserveAspectRadio: Boolean
) {
    var plotContainerPanel: JPanel? = null
    var plotKey: String = initialPlotKey
        set(value) {
            field = value
            rebuildPlotComponent()
        }
    var preserveAspectRadio: Boolean = initialPreserveAspectRadio
        set(value) {
            field = value
            rebuildPlotComponent()
        }

    fun rebuildPlotComponent() {
        plotContainerPanel?.let {
            val container = plotContainerPanel!!
            // cleanup
            for (component in container.components) {
                if (component is Disposable) {
                    component.dispose()
                }
            }
            container.removeAll()

            // build
            container.add(createPlotPanel())
            container.parent?.revalidate()
        }
    }

    fun createPlotPanel(): JPanel {
        // Make sure JavaFX event thread won't get killed after JFXPanel is destroyed.
        Platform.setImplicitExit(false)

        val rawSpec = plots[plotKey]!!.toSpec()
        val processedSpec = MonolithicCommon.processRawSpecs(rawSpec, frontendOnly = false)

        return DefaultPlotPanelJfx(
            processedSpec = processedSpec,
            preserveAspectRatio = preserveAspectRadio,
            preferredSizeFromPlot = false,
            repaintDelay = 10,
        ) { messages ->
            for (message in messages) {
                println("[Example App] $message")
            }
        }
    }
}