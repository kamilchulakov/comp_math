import numpy as np


def get_function_str(num):
    match num:
        case 1:
            return "x**3-4.5*x**2-9.21*x-0.383"
        case 2:
            return "x**2 - x - 4 + e**2"
        case 3:
            return "np.sin(x)" + " - " + "5*np.cos(x)" + "+ 0.1*x"


def get_function_lmbd(num):
    match num:
        case 1:
            return lambda x: x**3-4.5*x**2-9.21*x-0.383
        case 2:
            return lambda x: x**2 - x - 4 + np.e**2
        case 3:
            return lambda x: np.sin(x) - 5*np.cos(x) + + 0.1*x


def get_system_function_str(num):
    match num:
        case 1:
            return "x**3 - 5*y**2  + 2"
        case 2:
            return "x**2 + y**2 - 4"
        case 3:
            return "x - y**3 + 2"
        case 4:
            return "−3*x**2 + y"


def get_system_function_lmd(num):
    match num:
        case 1:
            return lambda x, y: x**3 - 5*y**2 + 2
        case 2:
            return lambda x, y: x**2 + y**2 - 4
        case 3:
            return lambda x, y: x - y**3 + 2
        case 4:
            return lambda x, y: 3*x**2 - y
