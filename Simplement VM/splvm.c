#define SPL_DLL_MEMBER

#include <stdio.h>
#include <string.h>
#include "splvm.h"
#include "splnative.h"
#include "math.h"

#define __fdata thread->fdata
#define __buff thread->fdata->buffer
#define __cur thread->cursor
#define __stack thread->stack

#define __ldc(code,push,type) case code: push(&__stack, memgetoff(__fdata->num_pool, read_ia(__buff, &__cur), type)); break
#define __op(code,push,pop,op) case code: push(&__stack, pop(&__stack) op pop(&__stack)); break
#define __opfn(code,push,pop,fn) case code: push(&__stack, fn(pop(&__stack), pop(&__stack))); break

DLL_EXPORT void execute_thread(runtime_t *rt, splthread_t *thread) {
    while(TRUE) {
#if DEBUG
        printf("Executing Code: %hhu, Cursor: %u\n", __buff[__cur], __cur);
#endif
        switch(__buff[(__cur)++]) {
            case halt_: goto end;
            
            case call_:
            {
                func_spec_rt_t *fspec = &__fdata->funcs[read_id(__buff, &__cur)];
#if DEBUG
                printf("Calling function %s\n", fspec->name);
#endif
                // Stack overflow
                if(__stack.top + 16 * sizeof(uintptr_t) + 8 * sizeof(sfa_t) + fspec->stack_size +
                        fspec->local_var_size > __stack.values + __stack.capacity)
                    goto end;
                uintptr_t nrbtm = __stack.top;
                push_intptr(&__stack, __stack.rbtm);
                __stack.rbtm = nrbtm;
                push_fa(&__stack, __cur);
                push_ptr(&__stack, __fdata);
                __cur = fspec->code_pointer;
                break;
            }
            
            case callnative_:
                call_native(read_id(__buff, &__cur), rt, thread);
                break;
            
            case returnvoid_:
            {
                file_data_rt_t *fdata = (file_data_rt_t *)memgetoff(__stack.rbtm, sizeof(uintptr_t) + sizeof(sfa_t), uintptr_t);
                if(fdata)
                    __fdata = fdata;
                __cur = memgetoff(__stack.rbtm, sizeof(uintptr_t), sfa_t);
                uintptr_t rbtm = memget(__stack.rbtm, uintptr_t);
                if(rbtm == 0)
                    goto end;
                __stack.rbtm = rbtm;
                break;
            }
            
            __ldc(loadconst8_,push8,uint8_t);
            __ldc(loadconst16_,push16,uint16_t);
            __ldc(loadconst32_,push32,uint32_t);
            __ldc(loadconst64_,push64,uint64_t);
            
            __op(add8_,push8,pop8,+);
            __op(uadd8_,push8,pop8u,+);
            __op(add16_,push16,pop16,+);
            __op(uadd16_,push16,pop16u,+);
            __op(add32_,push32,pop32,+);
            __op(uadd32_,push32,pop32u,+);
            __op(add64_,push64,pop64,+);
            __op(uadd64_,push64,pop64u,+);
            __op(fadd32_,push32f,pop32f,+);
            __op(fadd64_,push64f,pop64f,+);
            
            __op(sub8_,push8,pop8,-);
            __op(usub8_,push8,pop8u,-);
            __op(sub16_,push16,pop16,-);
            __op(usub16_,push16,pop16u,-);
            __op(sub32_,push32,pop32,-);
            __op(usub32_,push32,pop32u,-);
            __op(sub64_,push64,pop64,-);
            __op(usub64_,push64,pop64u,-);
            __op(fsub32_,push32f,pop32f,-);
            __op(fsub64_,push64f,pop64f,-);
            
            __op(mul8_,push8,pop8,*);
            __op(umul8_,push8,pop8u,*);
            __op(mul16_,push16,pop16,*);
            __op(umul16_,push16,pop16u,*);
            __op(mul32_,push32,pop32,*);
            __op(umul32_,push32,pop32u,*);
            __op(mul64_,push64,pop64,*);
            __op(umul64_,push64,pop64u,*);
            __op(fmul32_,push32f,pop32f,*);
            __op(fmul64_,push64f,pop64f,*);
            
            __op(div8_,push8,pop8,/);
            __op(udiv8_,push8,pop8u,/);
            __op(div16_,push16,pop16,/);
            __op(udiv16_,push16,pop16u,/);
            __op(div32_,push32,pop32,/);
            __op(udiv32_,push32,pop32u,/);
            __op(div64_,push64,pop64,/);
            __op(udiv64_,push64,pop64u,/);
            __op(fdiv32_,push32f,pop32f,/);
            __op(fdiv64_,push64f,pop64f,/);
            
            __op(mod8_,push8,pop8,%);
            __op(umod8_,push8,pop8u,%);
            __op(mod16_,push16,pop16,%);
            __op(umod16_,push16,pop16u,%);
            __op(mod32_,push32,pop32,%);
            __op(umod32_,push32,pop32u,%);
            __op(mod64_,push64,pop64,%);
            __op(umod64_,push64,pop64u,%);
            __opfn(fmod32_,push32f,pop32f,fmodf);
            __opfn(fmod64_,push64f,pop64f,fmod);
            
            default:
                goto end;
        }
    }
    end:
#if DEBUG
    printf("Finished executing thread %p\n", thread);
#endif
    return;
}