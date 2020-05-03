int main() {
    int i = 12;
    int j = (i/2) * 15;
    int k = i/j;
    k = k + j;
    i = j - k;

    if (i < 10) {
        i = 10;
    } else {
        i = 5;
    }

    while (i < 10) {
        i = 5 * 9;
    }

    return i;
}

