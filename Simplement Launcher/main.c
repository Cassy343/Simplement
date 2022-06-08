#include <stdio.h>
#include <stdlib.h>
#include "mem.h"
#include "splio.h"
#include "splnative.h"
#include "splvm.h"

int main(int argc, char** argv) {
    FILE *file = fopen("E:\\projects\\Simplement\\tests\\test_0.splx", "rb");
    runtime_t *rt = create_runtime(file, 0);
    
    execute_thread(rt, &(rt->threads[0]));
    
    free_runtime(rt);
    fclose(file);
    return EXIT_SUCCESS;
}