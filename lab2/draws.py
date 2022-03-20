import numpy as np
import matplotlib.pyplot as plt
import sympy as sp


def plot_function(func, min_x, max_x, min_y, max_y, step):
    x = np.linspace(min_x, max_x, 10000)

    fig = plt.figure()
    ax = fig.add_subplot(1, 1, 1)
    ax.spines['left'].set_position('center')
    ax.spines['bottom'].set_position('center')
    ax.spines['right'].set_color('none')
    ax.spines['top'].set_color('none')
    ax.xaxis.set_ticks_position('bottom')
    ax.yaxis.set_ticks_position('left')

    ax.plot(x, func(x), "g", linewidth=2.0)

    ax.set(xlim=(min_x, max_x), xticks=np.arange(min_x, max_x, step),
           ylim=(min_y, max_y), yticks=np.arange(min_y, max_y, step))

    plt.show()


def plot_system(fun1, fun2):
    x, y = sp.symbols("x y")
    sp.plot_implicit(sp.Or(sp.Eq(fun1(x, y), 0), sp.Eq(
        fun2(x, y), 0)))
