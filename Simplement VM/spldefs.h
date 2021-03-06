/* 
 * Created on January 4, 2019, 2:05 AM
 */

#ifndef SPLDEFS_H
#define SPLDEFS_H

#ifdef __cplusplus
extern "C" {
#endif

#include <sys/types.h>
#include <stdlib.h>

#define TRUE 1
#define FALSE 0

#define DEBUG TRUE
#define BYTECODE_VERSION 0
#define MAX_IDENTIFIER_LEN 256
#define FILE_TAG_SIZE 16 // In bytes

#define sbyte_t signed char
#define subyte_t unsigned char
#define sshort_t signed short
#define sushort_t unsigned short
#define sint_t signed int
#define suint_t unsigned int
#define slong_t signed long long
#define sulong_t unsigned long long
#define sfloat_t float
#define sdouble_t double
#define sbool_t sbyte_t

#define bool_t uint8_t

// !AST_MARKER_START_BCP
#define sfa_t uint32_t // File address
#define sia_t uint16_t // Internal address (pointer offset)
#define sid_t uint16_t // ID for an object, function, etc.
#define smodifier_t uint8_t
// !AST_MARKER_END

// !AST_MARKER_START_TYPES
#define sbyte_type 0
#define subyte_type 1
#define sshort_type 2
#define sushort_type 3
#define sint_type 4
#define suint_type 5
#define slong_type 6
#define sulong_type 7
#define sfloat_type 8
#define sdouble_type 9
#define sstring_type 10
#define sbool_type 11
#define sobject_type 12
#define sarray_type 13
#define svoid_type 14
// !AST_MARKER_END

#define TYPE_COUNT 15

// Bytecodes (don't touch)
// <editor-fold defaultstate="collapsed" desc="Computer Generated Bytecodes">
// !BCT_MARKER_START 0
#define halt_ 0x0
#define call_ 0x1 //ID
#define callnative_ 0x2 //ID
#define callextern_ 0x3 //ID,ID
#define thread_ 0x4 //FA
#define goto_ 0x5 //FA
#define jump8_ 0x6 //I8
#define jump16_ 0x7 //I16
#define loadconst8_ 0x8 //IA
#define loadconst16_ 0x9 //IA
#define loadconst32_ 0xa //IA
#define loadconst64_ 0xb //IA
#define loadstrconst_ 0xc //ID
#define cpystrconst_ 0xd //ID
#define dup8_ 0xe
#define dup16_ 0xf
#define dup32_ 0x10
#define dup64_ 0x11
#define store8_ 0x12 //IA
#define load8_ 0x13 //IA
#define store16_ 0x14 //IA
#define load16_ 0x15 //IA
#define store32_ 0x16 //IA
#define load32_ 0x17 //IA
#define store64_ 0x18 //IA
#define load64_ 0x19 //IA
#define return8_ 0x1a
#define return16_ 0x1b
#define return32_ 0x1c
#define return64_ 0x1d
#define returnvoid_ 0x1e
#define newobject_ 0x1f //ID
#define getclass_ 0x20 //ID
#define getfield_ 0x21 //ID
#define putfield_ 0x22 //ID
#define getfieldstatic_ 0x23 //ID,ID
#define putfieldstatic_ 0x24 //ID,ID
#define delobject_ 0x25
#define newarray_ 0x26 //I8
#define aget_ 0x27
#define aset_ 0x28
#define amacroset_ 0x29
#define alen_ 0x2a
#define aresize_ 0x2b
#define delarray_ 0x2c
#define neg8_ 0x2d
#define neg16_ 0x2e
#define neg32_ 0x2f
#define neg64_ 0x30
#define fneg32_ 0x31
#define fneg64_ 0x32
#define iconst_n1_ 0x33
#define iconst_0_ 0x34
#define iconst_1_ 0x35
#define iconst_2_ 0x36
#define iconst_3_ 0x37
#define iconst_4_ 0x38
#define iconst_5_ 0x39
#define fconst_n1_ 0x3a
#define fconst_0_ 0x3b
#define fconst_1_ 0x3c
#define dconst_n1_ 0x3d
#define dconst_0_ 0x3e
#define dconst_1_ 0x3f
#define ztrue_ 0x40
#define zfalse_ 0x41
#define add8_ 0x42
#define uadd8_ 0x43
#define add16_ 0x44
#define uadd16_ 0x45
#define add32_ 0x46
#define uadd32_ 0x47
#define add64_ 0x48
#define uadd64_ 0x49
#define fadd32_ 0x4a
#define fadd64_ 0x4b
#define sub8_ 0x4c
#define usub8_ 0x4d
#define sub16_ 0x4e
#define usub16_ 0x4f
#define sub32_ 0x50
#define usub32_ 0x51
#define sub64_ 0x52
#define usub64_ 0x53
#define fsub32_ 0x54
#define fsub64_ 0x55
#define mul8_ 0x56
#define umul8_ 0x57
#define mul16_ 0x58
#define umul16_ 0x59
#define mul32_ 0x5a
#define umul32_ 0x5b
#define mul64_ 0x5c
#define umul64_ 0x5d
#define fmul32_ 0x5e
#define fmul64_ 0x5f
#define div8_ 0x60
#define udiv8_ 0x61
#define div16_ 0x62
#define udiv16_ 0x63
#define div32_ 0x64
#define udiv32_ 0x65
#define div64_ 0x66
#define udiv64_ 0x67
#define fdiv32_ 0x68
#define fdiv64_ 0x69
#define mod8_ 0x6a
#define umod8_ 0x6b
#define mod16_ 0x6c
#define umod16_ 0x6d
#define mod32_ 0x6e
#define umod32_ 0x6f
#define mod64_ 0x70
#define umod64_ 0x71
#define fmod32_ 0x72
#define fmod64_ 0x73
#define pow8_ 0x74
#define upow8_ 0x75
#define pow16_ 0x76
#define upow16_ 0x77
#define pow32_ 0x78
#define upow32_ 0x79
#define pow64_ 0x7a
#define upow64_ 0x7b
#define fpow32_ 0x7c
#define fpow64_ 0x7d
#define inc8_ 0x7e //IA
#define inc16_ 0x7f //IA
#define inc32_ 0x80 //IA
#define inc64_ 0x81 //IA
#define finc32_ 0x82 //IA
#define finc64_ 0x83 //IA
#define rshift8_ 0x84
#define rshift16_ 0x85
#define rshift32_ 0x86
#define rshift64_ 0x87
#define lshift8_ 0x88
#define lshift16_ 0x89
#define lshift32_ 0x8a
#define lshift64_ 0x8b
#define bflip8_ 0x8c
#define bflip16_ 0x8d
#define bflip32_ 0x8e
#define bflip64_ 0x8f
#define urshift8_ 0x90
#define urshift16_ 0x91
#define urshift32_ 0x92
#define urshift64_ 0x93
#define or8_ 0x94
#define or16_ 0x95
#define or32_ 0x96
#define or64_ 0x97
#define and8_ 0x98
#define and16_ 0x99
#define and32_ 0x9a
#define and64_ 0x9b
#define xor8_ 0x9c
#define xor16_ 0x9d
#define xor32_ 0x9e
#define xor64_ 0x9f
#define cmp8_equ_ 0xa0 //FA
#define cmp16_equ_ 0xa1 //FA
#define cmp32_equ_ 0xa2 //FA
#define cmp64_equ_ 0xa3 //FA
#define fcmp32_equ_ 0xa4 //FA
#define fcmp64_equ_ 0xa5 //FA
#define cmp8_neq_ 0xa6 //FA
#define cmp16_neq_ 0xa7 //FA
#define cmp32_neq_ 0xa8 //FA
#define cmp64_neq_ 0xa9 //FA
#define fcmp32_neq_ 0xaa //FA
#define fcmp64_neq_ 0xab //FA
#define cmp8_gt_ 0xac //FA
#define ucmp8_gt_ 0xad //FA
#define cmp16_gt_ 0xae //FA
#define ucmp16_gt_ 0xaf //FA
#define cmp32_gt_ 0xb0 //FA
#define ucmp32_gt_ 0xb1 //FA
#define cmp64_gt_ 0xb2 //FA
#define ucmp64_gt_ 0xb3 //FA
#define fcmp32_gt_ 0xb4 //FA
#define fcmp64_gt_ 0xb5 //FA
#define cmp8_gte_ 0xb6 //FA
#define ucmp8_gte_ 0xb7 //FA
#define cmp16_gte_ 0xb8 //FA
#define ucmp16_gte_ 0xb9 //FA
#define cmp32_gte_ 0xba //FA
#define ucmp32_gte_ 0xbb //FA
#define cmp64_gte_ 0xbc //FA
#define ucmp64_gte_ 0xbd //FA
#define fcmp32_gte_ 0xbe //FA
#define fcmp64_gte_ 0xbf //FA
#define cmp8_lt_ 0xc0 //FA
#define ucmp8_lt_ 0xc1 //FA
#define cmp16_lt_ 0xc2 //FA
#define ucmp16_lt_ 0xc3 //FA
#define cmp32_lt_ 0xc4 //FA
#define ucmp32_lt_ 0xc5 //FA
#define cmp64_lt_ 0xc6 //FA
#define ucmp64_lt_ 0xc7 //FA
#define fcmp32_lt_ 0xc8 //FA
#define fcmp64_lt_ 0xc9 //FA
#define cmp8_lte_ 0xca //FA
#define ucmp8_lte_ 0xcb //FA
#define cmp16_lte_ 0xcc //FA
#define ucmp16_lte_ 0xcd //FA
#define cmp32_lte_ 0xce //FA
#define ucmp32_lte_ 0xcf //FA
#define cmp64_lte_ 0xd0 //FA
#define ucmp64_lte_ 0xd1 //FA
#define fcmp32_lte_ 0xd2 //FA
#define fcmp64_lte_ 0xd3 //FA
// !BCT_MARKER_END
// </editor-fold>

#ifdef __cplusplus
}
#endif

#endif /* SPLDEFS_H */

