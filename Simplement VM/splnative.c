#define SPL_DLL_MEMBER

#include "splnative.h"
#include "mem.h"

#define __printfunc(name,type,pop,fmt) static void name(runtime_t *rt, splthread_t *thread) \
        {                                           \
            printf(fmt, (type)pop(&thread->stack)); \
        }

__printfunc(println_i8,int8_t,pop8,"%hhd\n")
__printfunc(println_ui8,uint8_t,pop8u,"%hhu\n")
__printfunc(println_i16,int16_t,pop16,"%hd\n")
__printfunc(println_ui16,uint16_t,pop16u,"%hu\n")
__printfunc(println_i32,int32_t,pop32,"%d\n")
__printfunc(println_ui32,uint32_t,pop32u,"%u\n")
__printfunc(println_i64,int64_t,pop64,"%ld\n")
__printfunc(println_ui64,uint64_t,pop64u,"%lu\n")
__printfunc(println_f32,float,pop32f,"%f\n")
__printfunc(println_f64,double,pop64f,"%f\n")
__printfunc(print_i8,int8_t,pop8,"%hhd")
__printfunc(print_ui8,uint8_t,pop8u,"%hhu")
__printfunc(print_i16,int16_t,pop16,"%hd")
__printfunc(print_ui16,uint16_t,pop16u,"%hu")
__printfunc(print_i32,int32_t,pop32,"%d")
__printfunc(print_ui32,uint32_t,pop32u,"%u")
__printfunc(print_i64,int64_t,pop64,"%ld")
__printfunc(print_ui64,uint64_t,pop64u,"%lu")
__printfunc(print_f32,float,pop32f,"%f")
__printfunc(print_f64,double,pop64f,"%f")

static void (*SPLNATIVE_FUNCS[])(runtime_t *, splthread_t *) = {
    // !AST_MARKER_START_NATIVES
    println_i8, println_ui8, println_i16, println_ui16, println_i32, println_ui32,
    println_i64, println_ui64, println_f32, println_f64, print_i8, print_ui8,
    print_i16, print_ui16, print_i32, print_ui32, print_i64, print_ui64, print_f32,
    print_f64
    // !AST_MARKER_END
};

DLL_EXPORT void call_native(sid_t id, runtime_t *rt, splthread_t *thread) {
    SPLNATIVE_FUNCS[id](rt, thread);
}