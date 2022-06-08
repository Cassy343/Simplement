#define SPL_DLL_MEMBER

#include "splio.h"
#include <string.h>

const uint8_t SPLTYPE_SIZE[TYPE_COUNT] = {
    1, 1, 2, 2, 4, 4, 8, 8, 4, 8, 8, 1, 8, 8, 0
};

DLL_EXPORT uint16_t read16(const uint8_t *buffer, sfa_t *cursor) {
    uint16_t val = ((uint16_t)(buffer[*cursor]) << 8) | buffer[*cursor + 1];
    *cursor += 2;
    return val;
}

DLL_EXPORT uint32_t read32(const uint8_t *buffer, sfa_t *cursor) {
    uint32_t val = ((uint32_t)buffer[*cursor] << 24) | ((uint32_t)buffer[*cursor + 1] << 16) |
            ((uint32_t)buffer[*cursor + 2] << 8) | buffer[*cursor + 3];
    *cursor += 4;
    return val;
}

DLL_EXPORT uint64_t read64(const uint8_t *buffer, sfa_t *cursor) {
    uint64_t val = ((uint64_t)buffer[*cursor] << 56) | ((uint64_t)buffer[*cursor + 1] << 48) |
            ((uint64_t)buffer[*cursor + 2] << 40) | ((uint64_t)buffer[*cursor + 3] << 32) |
            ((uint64_t)buffer[*cursor + 4] << 24) | ((uint64_t)buffer[*cursor + 5] << 16) |
            ((uint64_t)buffer[*cursor + 6] << 8) | buffer[*cursor + 7];
    *cursor += 8;
    return val;
}

DLL_EXPORT float readf32(const uint8_t *buffer, sfa_t *cursor) {
    union {
        uint32_t raw;
        float val;
    } cast;
    cast.raw = read32(buffer, cursor);
    return cast.val;
}

DLL_EXPORT double readf64(const uint8_t *buffer, sfa_t *cursor) {
    union {
        uint64_t raw;
        double val;
    } cast;
    cast.raw = read64(buffer, cursor);
    return cast.val;
}

static uint8_t* read_file(FILE *file) {
    if(!file)
        return 0;
    fseek(file, 0L, SEEK_END);
    long flen = ftell(file);
    rewind(file);
    if(flen < MIN_HEADER_LEN)
        return 0;
    uint8_t *buffer = splmalloc(flen);
    fread(buffer, sizeof(uint8_t), flen, file);
    return buffer;
}

DLL_EXPORT void create_file_tag(const uint8_t *buffer, uint8_t *h) {
    sfa_t start, end;
    memcpy(&start, &buffer[TAG_DATA_START_IDX], sizeof(sfa_t));
    memcpy(&end, &buffer[TAG_DATA_END_IDX], sizeof(sfa_t));
    
    uint8_t a = 0x1F, b = 0x3F, c;
    uint64_t s = 0;
    while(start < end) {
        c = buffer[start];
        s = (s ^ a) * 0x5DEECE66DL + b;
        h[s % FILE_TAG_SIZE] += c;
        a = b;
        b = c;
        ++ start;
    }
}

DLL_EXPORT file_data_rt_t* read_file_data_rt(uint8_t *buffer, sfa_t *cursor) {
    // Check to make sure this file is a SPLX executable
    if(!(buffer[0] == EXE_HEADER_B0 && buffer[1] == EXE_HEADER_B1 &&
            buffer[2] == EXE_HEADER_B2 && buffer[3] == EXE_HEADER_B3))
        return 0;
    
    file_data_rt_t *fdata = splmalloc(sizeof(file_data_rt_t));
    fdata->buffer = buffer;
    *cursor = EXE_HEADER_LEN;
    fdata->bytecode_ver = read16(buffer, cursor);
    if(fdata->bytecode_ver != BYTECODE_VERSION) {
        splfree(fdata);
        return 0;
    }
    memcpy(&fdata->tag, &buffer[TAG_START], FILE_TAG_SIZE);
    
    /// Allocate the data for the numeric constants pool
    *cursor = NUM_CPOOL_SIZE_IDX;
    sia_t nps = read_ia(buffer, cursor), off = 0;
#if DEBUG
    printf("Creating numeric constants pool with size %hd\n", nps);
#endif
    fdata->num_pool = (uintptr_t)splmalloc(nps);
    
    // 8-bit entries
    nps = read_ia(buffer, cursor);
#if DEBUG
    printf("Loading %hd 8-bit entries.\n", nps);
#endif
    while(nps > 0) {
        memgetoff(fdata->num_pool, off, uint8_t) = read8(buffer, cursor);
        off += 8;
        -- nps;
    }
    // 16-bit entries
    nps = read_ia(buffer, cursor);
#if DEBUG
    printf("Loading %hd 16-bit entries.\n", nps);
#endif
    while(nps > 0) { 
        memgetoff(fdata->num_pool, off, uint16_t) = read16(buffer, cursor);
        off += 16;
        -- nps;
    }
    // 32-bit entries
    nps = read_ia(buffer, cursor);
#if DEBUG
    printf("Loading %hd 32-bit entries.\n", nps);
#endif
    while(nps > 0) {
        memgetoff(fdata->num_pool, off, uint32_t) = read32(buffer, cursor);
        off += 32;
        -- nps;
    }
    // 64-bit entries
    nps = read_ia(buffer, cursor);
#if DEBUG
    printf("Loading %hd 64-bit entries.\n", nps);
#endif
    while(nps > 0) {
        memgetoff(fdata->num_pool, off, uint64_t) = read64(buffer, cursor);
        off += 64;
        -- nps;
    }
    
    // Loop variable
    sid_t i;
    
    // String constants
    fdata->str_pool_len = read_id(buffer, cursor); // Read the number of string entries
#if DEBUG
    printf("Loading %hd string entries.\n", fdata->str_pool_len);
#endif
    fdata->str_pool = splcalloc(fdata->str_pool_len, sizeof(splstring_t));
    for(i = 0;i < fdata->str_pool_len;++ i) {
        fdata->str_pool[i].len = read16(buffer, cursor);
        fdata->str_pool[i].value = splcalloc(fdata->str_pool[i].len, sizeof(utf8char_t));
        // Decode the string and get the raw byte length
        int64_t blen = decode_utf8(buffer, *cursor, fdata->str_pool[i].value, 0, fdata->str_pool[i].len);
        if(blen < 0) { // Invalid UTF-8 sequence
            for(;i >= 0;-- i)
                splfree(fdata->str_pool[i].value);
            splfree(fdata->str_pool);
            splfree((void*)fdata->num_pool);
            splfree(fdata);
            return 0;
        }
        *cursor += (unsigned)blen;
    }
    
    fdata->num_dependencies = read_id(buffer, cursor);
#if DEBUG
    printf("Loading %hd dependencies.\n", fdata->num_dependencies);
#endif
    fdata->dependencies = splcalloc(fdata->num_dependencies, sizeof(file_data_rt_t));
    for(i = 0;i < fdata->num_dependencies;++ i) {
        
    }
    
    fdata->num_resources = read_id(buffer, cursor);
#if DEBUG
    printf("Loading %hd resources.\n", fdata->num_resources);
#endif
    fdata->resources = splcalloc(fdata->num_resources, sizeof(resource_rt_t));
    for(i = 0;i < fdata->num_resources;++ i) {
        
    }
    
    uint8_t type;
    sia_t param_count;
    sid_t j;
    fdata->num_funcs = read_id(buffer, cursor);
#if DEBUG
    printf("Loading %hd functions.\n", fdata->num_funcs);
#endif
    fdata->funcs = splcalloc(fdata->num_funcs, sizeof(func_spec_rt_t));
    for(i = 0;i < fdata->num_funcs;++ i) {
        fdata->funcs[i].code_pointer = read_fa(buffer, cursor);
        param_count = read_ia(buffer, cursor);
        fdata->funcs[i].param_mem_size = 0;
        for(j = 0;j < param_count;++ j) {
            read_type_ignore_meta(type, buffer, cursor);
            fdata->funcs[i].param_mem_size += SPLTYPE_SIZE[type];
        }
        fdata->funcs[i].stack_size = read_ia(buffer, cursor);
        fdata->funcs[i].local_var_size = read_ia(buffer, cursor);
        skip_type(buffer, cursor); // Return type
        *cursor += sizeof(smodifier_t);
#if DEBUG
        fdata->funcs[i].name_len = read8(buffer, cursor);
        fdata->funcs[i].name = splcalloc(fdata->funcs[i].name_len, sizeof(uint8_t));
        memcpy(fdata->funcs[i].name, &buffer[*cursor], fdata->funcs[i].name_len);
        *cursor += fdata->funcs[i].name_len;
#else
        // Skip the function name
        type = read8(buffer, cursor);
        *cursor += type;
#endif
    }
    
    sid_t count1, count2;
    uint32_t field_offset;
    fdata->num_classes = read_id(buffer, cursor);
#if DEBUG
    printf("Loading %hd classes.\n", fdata->num_classes);
#endif
    fdata->classes = splcalloc(fdata->num_classes, sizeof(class_spec_rt_t));
    for(i = 0;i < fdata->num_classes;++ i) {
        count1 = read_id(buffer, cursor); // Static field count
        count2 = read_id(buffer, cursor); // Total field count
        fdata->classes[i].fields = splcalloc(count2, sizeof(field_spec_rt_t));
        field_offset = 0;
        for(j = 0;j < count2;++ j) {
            if(j == count1) {
                fdata->classes[i].static_fields = (uintptr_t)splmalloc(field_offset);
                field_offset = 0;
            }
            fdata->classes[i].fields[j].offset = field_offset;
            read_type_ignore_meta(type, buffer, cursor);
            fdata->classes[i].fields[j].size = SPLTYPE_SIZE[type];
            field_offset += fdata->classes[i].fields[j].size;
            // Skip the modifiers
            *cursor += sizeof(smodifier_t);
            // Skip the field name
            type = read8(buffer, cursor);
            *cursor += type;
        }
        fdata->classes[i].dynamic_fields_size = field_offset;
        // Skip the function ids
        *cursor += sizeof(sid_t); // Skip the constructor count
        count1 = read_id(buffer, cursor); // Total function count
        *cursor += count1 * sizeof(sid_t);
        // Skip the modifiers
        *cursor += sizeof(smodifier_t);
#if DEBUG
        fdata->classes[i].name_len = read8(buffer, cursor);
        fdata->classes[i].name = splcalloc(fdata->classes[i].name_len, sizeof(uint8_t));
        memcpy(fdata->classes[i].name, &buffer[*cursor], fdata->classes[i].name_len);
        *cursor += fdata->classes[i].name_len;
#else
        // Skip the class name
        type = read8(buffer, cursor);
        *cursor += type;
#endif
    }
    
    return fdata;
}

DLL_EXPORT runtime_t* create_runtime(FILE *file, uint64_t memory) {
    uint8_t* buffer = read_file(file);
    if(!buffer)
        return 0;
    
    runtime_t *rt = splmalloc(sizeof(runtime_t));
    rt->thread_count = 1;
    rt->threads = splmalloc(sizeof(splthread_t));
    rt->fdata = read_file_data_rt(buffer, &rt->threads[0].cursor);
    if(!rt->fdata) {
        splfree(rt->threads);
        splfree(rt);
        splfree(buffer);
        return 0;
    }
    
    rt->threads[0].fdata = rt->fdata;
    rt->threads[0].name = new_splstring("main", 4);
    init_stack(&rt->threads[0].stack, 256);
    rt->threads[0].cursor = ((uint32_t)buffer[rt->threads[0].cursor] << 24) |
            ((uint32_t)buffer[rt->threads[0].cursor + 1] << 16) |
            ((uint32_t)buffer[rt->threads[0].cursor + 2] << 8) | buffer[rt->threads[0].cursor + 3];
    rt->threads[0].alive = TRUE;
    
    init_heap(&rt->heap, 256);

#if DEBUG
    printf("Starting main thread cursor at %u\n", rt->threads[0].cursor);
#endif
    
    return rt;
}