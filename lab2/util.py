import re


def pretty_round(num):
    try:
        parts = re.search(r'\.(\d+)e.(\d+)', str(num))
        res = len(parts.group(1))
        exp = int(parts.group(2)) + res
        return "{:.{exp}f}".format(float(num), exp=exp - res + 2)
    except Exception:
        return str(round(num, 3))