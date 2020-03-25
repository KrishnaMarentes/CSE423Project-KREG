int main() {

    int i;
    int j = 0;

    goto test;
    i = 5;

test:
    while (i < 10) {
        j *= 2;
        i++;
    }

    while (j > 10) {
        j  = j - 4;
    }

    return 0;
}