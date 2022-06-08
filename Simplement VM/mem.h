/* 
 * File:   mem.h
 * Author: Ian
 *
 * Created on January 4, 2019, 4:10 AM
 */

#ifndef MEM_H
#define MEM_H

#ifdef __cplusplus
extern "C" {
#endif

#include "spldefs.h"
#include "libexport.h"
#include "utf8.h"
#include <stdio.h>

/* Runtime or Non-Runtime Structs */

typedef union {
    sid_t object_id;
    uint16_t array_spec; // MSBy: type, LSBy: dim
} spl_type_meta_t;

typedef struct {
    uint8_t primitive;
    spl_type_meta_t meta;
} spl_type_t;

typedef struct {
    uint32_t len;
    utf8char_t *value;
} splstring_t;

typedef struct {
    sfa_t code_pointer;
    sid_t num_params;
    spl_type_t *param_types;
    spl_type_t return_type;
    smodifier_t modifiers;
    uint8_t name_len;
    uint8_t *name;
} func_spec_t;

typedef struct {
    spl_type_t type;
    smodifier_t modifiers;
    uint8_t name_len;
    uint8_t *name;
} field_spec_t;

typedef struct {
    sid_t num_static_fields;
    sid_t num_fields; // Total
    field_spec_t *fields; // Static fields then dynamic
    sid_t num_ctrs;
    sid_t num_funcs; // Total
    func_spec_t **funcs; // Constructors then methods
    smodifier_t modifiers;
    uint8_t name_len;
    uint8_t *name;
} class_spec_t;

typedef struct {
    sfa_t code_pointer;
    uint32_t len;
    uint16_t name_len;
    uint8_t *name;
} resource_t;

struct __file_data {
    uint8_t *buffer;
    uint16_t bytecode_ver;
    uint8_t tag[FILE_TAG_SIZE];
    sid_t str_pool_len;
    splstring_t *str_pool;
    sid_t num_dependencies;
    struct __file_data *dependencies;
    sid_t num_resources;
    resource_t *resources;
    sid_t num_funcs;
    func_spec_t *funcs;
    sid_t num_classes;
    class_spec_t *classes;
};

#define file_data_t struct __file_data

/* Runtime-specific Structs */

typedef struct {
    sfa_t code_pointer;
    sia_t param_mem_size;
    
    // Parameters for the stackframe
    sia_t stack_size;
    sia_t local_var_size;
    
#if DEBUG
    uint8_t name_len;
    uint8_t *name;
#endif
} func_spec_rt_t;

typedef struct {
    uint8_t size; // In bytes
    uint32_t offset; // In the field memory block
} field_spec_rt_t;

typedef struct {
    uint32_t dynamic_fields_size;
    field_spec_rt_t *fields; // Static fields then dynamic
    uintptr_t static_fields;

#if DEBUG
    uint8_t name_len;
    uint8_t *name;
#endif
} class_spec_rt_t;

typedef struct {
    sfa_t code_pointer;
    uint32_t len;
} resource_rt_t;

struct __file_data_rt {
    uint8_t *buffer;
    uint16_t bytecode_ver;
    uint8_t tag[FILE_TAG_SIZE];
    
    uintptr_t num_pool;
    sid_t str_pool_len;
    splstring_t *str_pool;
    
    sid_t num_dependencies;
    struct __file_data_rt *dependencies;
    sid_t num_resources;
    resource_rt_t *resources;
    sid_t num_funcs;
    func_spec_rt_t *funcs;
    sid_t num_classes;
    class_spec_rt_t *classes;
};

#define file_data_rt_t struct __file_data_rt

typedef struct {
    class_spec_rt_t *class_spec;
    uintptr_t fields;
} splobject_t;

typedef struct {
    uint32_t len;
    uint8_t dim;
    uint8_t ele_size;
    uintptr_t values;
} splarray_t;

typedef struct {
    uint32_t capacity;
    uintptr_t top;
    uintptr_t rbtm;
    uintptr_t values;
} stack_t;

typedef struct {
    uint64_t capacity;
    uintptr_t values;
    uintptr_t top;
    uint32_t tracker_capacity;
    uint64_t *tracker;
} heap_t;

typedef struct {
    file_data_rt_t *fdata;
    sfa_t cursor;
    stack_t stack;
    splstring_t *name;
    
    bool_t alive;
} splthread_t;

typedef struct {
    heap_t heap;
    file_data_rt_t *fdata;
    uint16_t thread_count;
    splthread_t *threads;
} runtime_t;



#if DEBUG
  DLL_EXPORT void* __splmalloc(size_t);
  DLL_EXPORT void* __splcalloc(size_t, size_t);
  DLL_EXPORT void* __splrealloc(void *, size_t);
  DLL_EXPORT void __splfree(void *);
  #define splmalloc __splmalloc
  #define splcalloc __splcalloc
  #define splrealloc __splrealloc
  #define splfree __splfree
#else
  #define splmalloc malloc
  #define splcalloc calloc
  #define splrealloc realloc
  #define splfree free
#endif

#define memgetoff(ptr,off,type) *((type*)((ptr)+(off)))
#define memget(ptr,type) *((type*)(ptr))
#define topframe(thread) thread->stack.rbtm



DLL_EXPORT splstring_t* new_splstring(const char *str, uint32_t len);
DLL_EXPORT void init_stack(stack_t *stack, uint32_t capacity);
DLL_EXPORT void init_heap(heap_t *heap, uint64_t capacity);



DLL_EXPORT void free_func_spec(func_spec_t *);
DLL_EXPORT void free_field_spec(field_spec_t *);
DLL_EXPORT void free_class_spec(class_spec_t *);
DLL_EXPORT void free_resource(resource_t *);
DLL_EXPORT void free_file_data(file_data_t *);

#if DEBUG
DLL_EXPORT void free_func_spec_rt(func_spec_rt_t *);
#endif
DLL_EXPORT void free_class_spec_rt(class_spec_rt_t *);
DLL_EXPORT void free_file_data_rt(file_data_rt_t *);
DLL_EXPORT void free_stackframe(uintptr_t);
DLL_EXPORT void free_splobject(splobject_t *);
DLL_EXPORT void free_splarray(splarray_t *);
DLL_EXPORT void free_splstring(splstring_t *);
DLL_EXPORT void free_stack(stack_t *);
DLL_EXPORT void free_heap(heap_t *);
DLL_EXPORT void free_splthread(splthread_t *);
DLL_EXPORT void free_runtime(runtime_t *);



DLL_EXPORT void push8(stack_t *, uint8_t);
DLL_EXPORT void push16(stack_t *, uint16_t);
DLL_EXPORT void push32(stack_t *, uint32_t);
DLL_EXPORT void push64(stack_t *, uint64_t);
DLL_EXPORT void push32f(stack_t *, float);
DLL_EXPORT void push64f(stack_t *, double);
DLL_EXPORT void push_intptr(stack_t *, uintptr_t);
DLL_EXPORT void push_ptr(stack_t *, void*);
#define push_fa push32
#define push_ia push16
#define push_id push16

DLL_EXPORT int8_t pop8(stack_t *);
DLL_EXPORT uint8_t pop8u(stack_t *);
DLL_EXPORT int16_t pop16(stack_t *);
DLL_EXPORT uint16_t pop16u(stack_t *);
DLL_EXPORT int32_t pop32(stack_t *);
DLL_EXPORT uint32_t pop32u(stack_t *);
DLL_EXPORT int64_t pop64(stack_t *);
DLL_EXPORT uint64_t pop64u(stack_t *);
DLL_EXPORT float pop32f(stack_t *);
DLL_EXPORT double pop64f(stack_t *);
DLL_EXPORT uintptr_t pop_intptr(stack_t *);
DLL_EXPORT void* pop_ptr(stack_t *);
#define pop_fa pop32u
#define pop_ia pop16u
#define pop_id pop16u

DLL_EXPORT void static_getfield(class_spec_rt_t *clazz, sid_t id, void *dest);
DLL_EXPORT void static_putfield(class_spec_rt_t *clazz, sid_t id, void *src);
DLL_EXPORT void splobject_getfield(splobject_t *obj, sid_t id, void *dest);
DLL_EXPORT void splobject_putfield(splobject_t *obj, sid_t id, void *src);
DLL_EXPORT void splarray_resize(splarray_t *arr, uint32_t new_size);

#ifdef __cplusplus
}
#endif

#endif /* MEM_H */

