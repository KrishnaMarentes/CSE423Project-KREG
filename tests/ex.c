#include <stdio.h>

int __func(int a, int b)
{
    return a + b;
}

int main() {
        //this is a comment test
        /*
            another comment
            test
            just checking
            probably won't work
        */
        int arr_ex[3];
        arr_ex[0] = 10;
        arr_ex[1] = 0x1;
        arr_ex[2] = 0x5;
        int i = 0; //checking more comments
        i += 1;
        i<<=3;
        i|=2;
        /*check this also, multiline comment on one line*/
        printf("Hello, World! %d\n", i++);
        return 0;
}
