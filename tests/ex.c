#include <stdio.h>
#include <stdlib.h>

struct TestStruct {
    int i;
    int j;
    int k;
};

int __func(int a, int b)
{
    return a + b;
}

int main() {
        struct TestStruct* ts = malloc(sizeof(struct TestStruct));
        //this is a comment test
        /*
            another comment
            test
            just checking
            probably won't work
        */

        float f = 4455.21342;
        int arr_ex[3];
        arr_ex[0] = 10;
        arr_ex[1] = 0x1;
        arr_ex[2] = 0x5;
        int i = 0; //checking more comments
        i += 1;
        i<<=3;
        i|=2;
        i >>= 1;

        ts -> i = 3;

        /*check this also, multiline comment on one line*/
        printf("Hello, World!            %d\n", ts->i);
        free(ts);
        return 0;
}
