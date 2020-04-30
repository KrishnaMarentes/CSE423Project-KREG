int foo(int t, int g)
{
    int f = t + g;
    return f;
}

int main()
{
    int i, j = 3 << 4;
    i = 16 | j;
    int k = 4 + ~i;
    i += foo(j, k);
    return i;
}
