import time
import numpy as np
from PIL import Image

import calculations
import functions
import draws
import util


def error():
    print("Вы ввелили что-то плохое, не делайте так, пожалуйста!\n")
    im = Image.open('again.png')
    im.show()
    exit(-1)


def show_david():
    im = Image.open('david.png')
    im.show()


def accuracy():
    return float(input("Введите точность, например, 0.01\n"))


def solve_one_preset():
    print("Доступные функции:\n")
    print("1 - ", functions.get_function_str(1))
    print("2 - ", functions.get_function_str(2))
    print("3 - ", functions.get_function_str(3))
    choice = int(input("Введите номер функции:\n"))
    if choice < 1 | choice > 3:
        error()

    func, lmd = functions.get_function_str(choice), functions.get_function_lmbd(choice)
    draws.plot_function(lmd, -9, 9, -9, 9, 1)
    a, b = calculations.find_interval(lmd, 0.5)
    if a is None:
        error()

    calculations.print_methods()
    choice = int(input("Введите номер метода:\n"))

    acc = accuracy()

    print("Решаю нелинейное уравнение...\n")
    ans, iterations = 0, 0
    match choice:
        case 1:
            ans, iterations = calculations.half_div_method(lmd, a, b, acc)
        case 2:
            ans, iterations = calculations.iterrations_method(lmd, a, b, acc)
    print("Корень: ", ans)
    print("\nИтераций: ", iterations)
    draws.plot_function(lmd,  a - 0.5, b + 0.5, lmd(a) - 0.5, lmd(b) + 0.5, 0.2)


def solve_one_manual():
    error()
    # print("Решаю нелинейное уравнение...\n")


def solve_one_choice():
    choice = int(input("Введите 1, если хотите выбрать доступные функции.\nВведите 2, если не хотите.\n"))
    match choice:
        case 1:
            solve_one_preset()
        case 2:
            solve_one_manual()


def solve_system():
    print("Доступные функции:\n")
    print("1 - ", functions.get_system_function_str(1))
    print("2 - ", functions.get_system_function_str(2))
    print("3 - ", functions.get_system_function_str(3))
    print("4 - ", functions.get_system_function_str(4))
    choice = int(input("Введите номер первой функции:\n"))
    if choice < 1 | choice >= 5:
        error()
    choice2 = int(input("Введите номер второй функции:\n"))
    if choice2 < 1 | choice2 > 4 | choice == choice2:
        error()

    draws.plot_system(functions.get_system_function_lmd(choice), functions.get_system_function_lmd(choice2))
    a, b = map(float, input("Введите начальное приближение через пробел: ").split())
    acc = accuracy()

    print("Решаю систему нелинейных уравнений...\n")
    x, y, err_x, err_y, it = calculations.calc_newton_system(functions.get_system_function_lmd(choice),
                                                             functions.get_system_function_lmd(choice2), a, b,
                                                             acc)
    print("x = " + util.pretty_round(x) + ", y = " + util.pretty_round(y) + ", найден за " + util.pretty_round(it) + " итераций, вектор погрешностей: [" + util.pretty_round(
        err_x) + ", " + util.pretty_round(err_y) + "]")


def greet():
    slow = 1
    fast = 0.1
    mid = 0.2
    report = "> Good morning!\nIt's March 20 2022 and it's sunday.\nHere in LA: a clear morning, quite a strong breeze " \
             "blowing right now.\n55 degrees fahrenheit, around 13 celcius.\nDay 2 of weekend projects. Day 2 of riding " \
             "the fun work train.\nAfter the weather report i'm headed for the dining car for a hot\n" \
             "cup of coffee and a cookie. And I noticed in the observation car:\nI looked" \
             " at the clock and it said time for peace.\nThis afternoon it'll be going " \
             "up to 75 degrees fahrenheit around 24 celsius\nand it looks like we're going to be enjoying once again wall-to-wall\n" \
             "beautiful blue skies and golden sunshine all along the way.\nEveryone, have a great day!\n\n\- Weather report by David Lynch\n"
    for chr in report:
        print(chr, end="")
        if chr == '\n':
            time.sleep(fast)
    print("\n")


def main_choice():
    choice = int(input("Введите 1, если хотите решить нелинейное уравнение.\nВведите 2, если хотите решить систему "
                       "нелинейных уравнений.\n"))
    match choice:
        case 1:
            solve_one_choice()
        case 2:
            solve_system()
        case _:
            error()


if __name__ == "__main__":
    greet()
    main_choice()
