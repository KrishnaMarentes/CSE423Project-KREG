int foo(int i) {
    int m = 6 + i;
    int j;
    return m;
}

int main()
{
    int i, b;
    i = 12 * 18;
    b = 2 + 4;
    i = foo();
    while(i < 3) {
        if(i > 1) {
            i++;
        } else if(b == 40) {
            --i;
        }
        if(b < 0) {
            b = 4;
        }
    }
    return b;
}
