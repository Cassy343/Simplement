/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   splio.h
 * Author: Ian
 *
 * Created on January 12, 2019, 2:41 PM
 */

#ifndef SPLIO_H
#define SPLIO_H

#ifdef __cplusplus
extern "C" {
#endif

#include "spldefs.h"
#include "libexport.h"
#include "mem.h"

#define MIN_HEADER_LEN 34

#define EXE_HEADER_LEN 4
#define EXE_HEADER_B0 0x53
#define EXE_HEADER_B1 0x50
#define EXE_HEADER_B2 0x4C
#define EXE_HEADER_B3 0x58

#define TAG_START 6
#define TAG_DATA_START_IDX 22
#define TAG_DATA_END_IDX 26
#define NUM_CPOOL_SIZE_IDX 30
#define CPOOL_START 34

const extern uint8_t SPLTYPE_SIZE[TYPE_COUNT];

#define read8(buffer,cursor) buffer[(*cursor)++]
DLL_EXPORT uint16_t read16(const uint8_t *buffer, sfa_t *cursor);
DLL_EXPORT uint32_t read32(const uint8_t *buffer, sfa_t *cursor);
DLL_EXPORT uint64_t read64(const uint8_t *buffer, sfa_t *cursor);
DLL_EXPORT float readf32(const uint8_t *buffer, sfa_t *cursor);
DLL_EXPORT double readf64(const uint8_t *buffer, sfa_t *cursor);
#define move_cursor(buffer,cursor) *cursor = ((uint32_t)buffer[*cursor] << 24) | \
            ((uint32_t)buffer[*cursor + 1] << 16) | ((uint32_t)buffer[*cursor + 2] << 8) | \
            buffer[*cursor + 3]
#define read_fa read32
#define read_ia read16
#define read_id read16
#define read_mod read8
#define read_type_ignore_meta(dest,buffer,cursor) dest = buffer[*cursor]; \
            *cursor += (dest == sobject_type || dest == sarray_type) ? 1 + sizeof(spl_type_meta_t) : 1
#define skip_type(buffer,cursor) *cursor += (buffer[*cursor] == sobject_type || \
            buffer[*cursor] == sarray_type) ? 1 + sizeof(spl_type_meta_t) : 1

DLL_EXPORT void create_file_tag(const uint8_t *buffer, uint8_t *h);
DLL_EXPORT file_data_rt_t* read_file_data_rt(uint8_t *buffer, sfa_t *cursor);
DLL_EXPORT runtime_t* create_runtime(FILE *file, uint64_t memory);


#ifdef __cplusplus
}
#endif

#endif /* SPLIO_H */

