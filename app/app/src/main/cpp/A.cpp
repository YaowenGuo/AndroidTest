#include "a.h"
#include "util/debug_util.h"

A::A() {
    x = 1;
    y = 2;
}

void A::test() {
    LOGE("Call A::test");
}

void AChild::test() {
    LOGE("Call AChild:test");
}