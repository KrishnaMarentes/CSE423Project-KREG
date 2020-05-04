int foo(int t, int g)
{
    int f = 2;
    int k = 1;
    int m = 3;
    int i = m;
    return f * t + i - g;
}

int main()
{
    int i = foo(5, 22);
    return i;
}
