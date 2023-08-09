//
// Created by Lim on 2023/8/9.
//

#ifndef ANDROIDTEST_A_H
#define ANDROIDTEST_A_H

class A {
public:
    A();
    virtual void test();
private:
    int x;
    int y;
};

class AChild: public A {
    void test() override;
};


#endif //ANDROIDTEST_A_H
