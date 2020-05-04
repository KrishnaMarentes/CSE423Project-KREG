int get_curvedgrade(int grade) {
    int t = grade + 10;
    return t;
}

int main()
{
    int grade = 66;
    int num;

    if(grade == 65) {
        num = 90;
    } else if(grade == 66) {
        num = 80;
    } else if(grade == 67) {
        num = 70;
    } else {
        num = 60;
    }

    int curved_grade = get_curvedgrade(grade);

    while(grade < curved_grade) {
        ++grade;
    }


    return grade;
}
