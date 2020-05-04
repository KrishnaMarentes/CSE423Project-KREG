int main() {
        int a = 5;
        int b = 20;
        int c;

        if (a == b) {
                c = a + b;
        }
        if (a <= b) {
                c = a * b;
        }

        a = 0;
        b = 10;

        if (a > b) {
                c++;
        } else {
                c = a - b;
        }

        if (a == b) {
                c++;
        }
}
