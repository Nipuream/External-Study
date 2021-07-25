//
// Created by yanghui4 on 2021/4/30.
//

#include "utils.h"

void matrixSetIdentityM(float *m)
{
    memset((void*)m, 0, 16*sizeof(float));
    m[0] = m[5] = m[10] = m[15] = 1.0f;
}

