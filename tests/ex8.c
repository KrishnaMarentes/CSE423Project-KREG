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

    if (j == 0) {
        j = 0;
    } else if (j == 9) {
        j = 9;
        i = 200;
        if (i < 200) {
            i = 0;
        } else {
            i = 500;
        }
    } else if (j == 8) {
        j = 8;
    } else {
        j = 0;
    }

    return 0;
}