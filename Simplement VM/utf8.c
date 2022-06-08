#define SPL_DLL_MEMBER

#include "utf8.h"

DLL_EXPORT int64_t decode_utf8(const uint8_t *src, uint32_t src_pos, utf8char_t *dest,
        uint32_t dest_pos, uint32_t utf8len) {
    uint8_t byte;
    int64_t osrc_pos = (int64_t)src_pos;
    utf8char_t ch;
    while(utf8len > 0) {
        byte = src[src_pos++];
        if(byte & UTF8_MASK_L1) {
            if((byte & UTF8_MASK_L2) == UTF8_MASK_CBYTE) {
                ch = (byte & UTF8_CMASK_L2) << 6;
                byte = src[src_pos++];
                if((byte & UTF8_MASK_CBYTE) != UTF8_MASK_L1)
                    return -1L;
                ch |= byte & UTF8_CMASK_CBYTE;
                dest[dest_pos++] = ch;
            }else if((byte & UTF8_MASK_L3) == UTF8_MASK_L2) {
                ch = (byte & UTF8_CMASK_L3) << 12;
                byte = src[src_pos++];
                if((byte & UTF8_MASK_CBYTE) != UTF8_MASK_L1)
                    return -1L;
                ch |= (byte & UTF8_CMASK_CBYTE) << 6;
                byte = src[src_pos++];
                if((byte & UTF8_MASK_CBYTE) != UTF8_MASK_L1)
                    return -1L;
                ch |= byte & UTF8_CMASK_CBYTE;
                dest[dest_pos++] = ch;
            }else if((byte & UTF8_MASK_L4) == UTF8_MASK_L3) {
#if SUPPORTS_4CP_CODE
                ch = (byte & UTF8_CMASK_L4) << 18;
                byte = src[src_pos++];
                if((byte & UTF8_MASK_CBYTE) != UTF8_MASK_L1)
                    return -1L;
                ch |= (byte & UTF8_CMASK_CBYTE) << 12;
                byte = src[src_pos++];
                if((byte & UTF8_MASK_CBYTE) != UTF8_MASK_L1)
                    return -1L;
                ch |= (byte & UTF8_CMASK_CBYTE) << 6;
                byte = src[src_pos++];
                if((byte & UTF8_MASK_CBYTE) != UTF8_MASK_L1)
                    return -1L;
                ch |= byte & UTF8_CMASK_CBYTE;
                dest[dest_pos++] = ch;
#else
                return -1L;
#endif
            }
        }else
            dest[dest_pos++] = (utf8char_t)byte;
        -- utf8len;
    }
    return (int64_t)src_pos - osrc_pos;
}

DLL_EXPORT int32_t utf8_encoded_len(utf8char_t *decoded, uint32_t pos, uint32_t len) {
    int32_t encoded_len = 0, ch;
    while(len > 0) {
        ch = decoded[pos++];
        if(ch < UTF8_MAXVAL_L1)
            ++ encoded_len;
        else if(ch < UTF8_MAXVAL_L2)
            encoded_len += 2;
        else if(ch < UTF8_MAXVAL_L3)
            encoded_len += 3;
        else
#if SUPPORTS_4CP_CODE
            encoded_len += 4;
#else
            return -1;
#endif
        -- len;
    }
    return encoded_len;
}

DLL_EXPORT bool_t encode_utf8(const utf8char_t *src, uint32_t src_pos, uint8_t *dest,
        uint32_t dest_pos, uint32_t utf8len) {
    utf8char_t ch;
    while(utf8len > 0) {
        ch = src[src_pos++];
        if(ch <= UTF8_MAXVAL_L1)
            dest[dest_pos++] = (uint8_t)ch;
        else if(ch <= UTF8_MAXVAL_L2) {
            dest[dest_pos++] = UTF8_MASK_CBYTE | ((ch >> 6) & UTF8_CMASK_L2);
            dest[dest_pos++] = UTF8_MASK_L1 | (ch & UTF8_CMASK_CBYTE);
        }else if(ch <= UTF8_MAXVAL_L3) {
            dest[dest_pos++] = UTF8_MASK_L2 | ((ch >> 12) & UTF8_CMASK_L3);
            dest[dest_pos++] = UTF8_MASK_L1 | ((ch >> 6) & UTF8_CMASK_CBYTE);
            dest[dest_pos++] = UTF8_MASK_L1 | (ch & UTF8_CMASK_CBYTE);
        }else{
#if SUPPORTS_4CP_CODE
            dest[dest_pos++] = UTF8_MASK_L3 | ((ch >> 18) & UTF8_CMASK_L4);
            dest[dest_pos++] = UTF8_MASK_L1 | ((ch >> 12) & UTF8_CMASK_CBYTE);
            dest[dest_pos++] = UTF8_MASK_L1 | ((ch >> 6) & UTF8_CMASK_CBYTE);
            dest[dest_pos++] = UTF8_MASK_L1 | (ch & UTF8_CMASK_CBYTE);
#else
            return FALSE;
#endif
        }
        -- utf8len;
    }
    return TRUE;
}