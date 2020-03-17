int add3(int a, int b, int c)
{
  return a + b + c;
}

int main()
{
  int i, j, k;

  i = 1;
  j = 3;
  k = 5;

  i = i * add3(i,j,k);

  return i;
}
