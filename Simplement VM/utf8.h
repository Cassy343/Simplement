#ifndef UTF8_H
#define UTF8_H

#ifdef __cplusplus
extern "C" {
#endif

#include "spldefs.h"
#include "libexport.h"

typedef uint16_t utf8char_t;
// True if sizeof(utf8char_t) is large enough to accomodate a 21 bit code
#define SUPPORTS_4CP_CODE FALSE

#define UTF8_MASK_L1 0x80
#define UTF8_CMASK_L1 0x7F
#define UTF8_MASK_L2 0xE0
#define UTF8_CMASK_L2 0x1F
#define UTF8_MASK_L3 0xF0
#define UTF8_CMASK_L3 0xF
#define UTF8_MASK_L4 0xF8
#define UTF8_CMASK_L4 0x7
#define UTF8_MASK_CBYTE 0xC0
#define UTF8_CMASK_CBYTE 0x3F

#define UTF8_MAXVAL_L1 0x7F
#define UTF8_MAXVAL_L2 0x7FF
#define UTF8_MAXVAL_L3 0xFFFF

DLL_EXPORT int64_t decode_utf8(const uint8_t *src, uint32_t src_pos, utf8char_t *dest,
        uint32_t dest_pos, uint32_t utf8len);
DLL_EXPORT int32_t utf8_encoded_len(utf8char_t *decoded, uint32_t pos, uint32_t len);
DLL_EXPORT bool_t encode_utf8(const utf8char_t *src, uint32_t src_pos, uint8_t *dest,
        uint32_t dest_pos, uint32_t utf8len);

#ifdef __cplusplus
}
#endif

#endif /* UTF8_H */

